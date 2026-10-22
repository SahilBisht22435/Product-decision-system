
package com.peds.peds.dto;

import com.peds.peds.model.Evidence;
import com.peds.peds.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RankedProductResponse {

    private Product product;
    private int suitabilityScore;
    private List<String> reasons;
    private int evidenceConfidenceScore;
    private int overallScore;
    private double suitabilityContribution;
    private double evidenceContribution;
    private String evidenceStatus;
    private String overallExplanation;
    private String rankingExplanation;

    public RankedProductResponse(
            Product product,
            int suitabilityScore,
            List<String> reasons,
            int evidenceConfidenceScore) {

        this.product = product;

        this.suitabilityScore = clampScore(suitabilityScore);
        this.evidenceConfidenceScore = clampScore(evidenceConfidenceScore);

        this.reasons = reasons == null
                ? new ArrayList<>()
                : new ArrayList<>(reasons);

        this.suitabilityContribution =
                roundToTwoDecimals(this.suitabilityScore * 0.70);

        this.evidenceContribution =
                roundToTwoDecimals(this.evidenceConfidenceScore * 0.30);

        this.overallScore = clampScore(
                (int) Math.round(
                        suitabilityContribution + evidenceContribution
                )
        );

        this.evidenceStatus = determineEvidenceStatus(product);
        this.overallExplanation = generateOverallExplanation();
        this.rankingExplanation = generateRankingExplanation();
    }

    private int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String determineEvidenceStatus(Product product) {

        if (product == null) {
            return "NO_EVIDENCE";
        }

        List<Evidence> evidenceList = product.getEvidence();

        if (evidenceList == null || evidenceList.isEmpty()) {
            return "NO_EVIDENCE";
        }

        boolean allVerified = true;
        boolean allRejected = true;

        for (Evidence evidence : evidenceList) {

            if (evidence == null) {
                allVerified = false;
                allRejected = false;
                continue;
            }

            String status = evidence.getReviewStatus();

            String normalizedStatus = status == null
                    ? ""
                    : status.trim().toUpperCase(Locale.ROOT);

            if (!"VERIFIED".equals(normalizedStatus)
                    || !evidence.isVerified()) {
                allVerified = false;
            }

            if (!"REJECTED".equals(normalizedStatus)) {
                allRejected = false;
            }
        }

        if (allRejected) {
            return "REJECTED";
        }

        if (allVerified) {
            return "VERIFIED";
        }

        return "PENDING_REVIEW";
    }

    public Product getProduct() {
        return product;
    }

    public int getSuitabilityScore() {
        return suitabilityScore;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public int getEvidenceConfidenceScore() {
        return evidenceConfidenceScore;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public String getEvidenceStatus() {
        return evidenceStatus;
    }

    public double getSuitabilityContribution() {
        return suitabilityContribution;
    }

    public double getEvidenceContribution() {
        return evidenceContribution;
    }

    private String generateOverallExplanation() {

        String explanation = "Suitability contributes "
                + suitabilityContribution
                + " points and evidence contributes "
                + evidenceContribution
                + " points to the overall score of "
                + overallScore + ".";

        if ("REJECTED".equals(evidenceStatus)) {
            explanation += " All evidence records are rejected.";
        } else if ("NO_EVIDENCE".equals(evidenceStatus)) {
            explanation += " No evidence records are available.";
        } else if ("PENDING_REVIEW".equals(evidenceStatus)) {
            explanation += " Evidence is pending review, incomplete, "
                    + "or has mixed review statuses.";
        } else if ("VERIFIED".equals(evidenceStatus)) {
            explanation += " All evidence records have verified "
                    + "review status. This does not independently "
                    + "guarantee claim accuracy.";
        }

        return explanation;
    }

    public String getOverallExplanation() {
        return overallExplanation;
    }

    private String generateRankingExplanation() {

        if (evidenceConfidenceScore == 0) {
            return "This product receives no evidence-confidence "
                    + "points. Its ranking depends entirely on "
                    + "suitability.";
        }

        if (evidenceContribution > suitabilityContribution) {
            return "Evidence confidence contributes more to the "
                    + "overall score than suitability.";
        }

        if (evidenceContribution < suitabilityContribution) {
            return "Suitability contributes more to the overall "
                    + "score than evidence confidence.";
        }

        return "Suitability and evidence confidence contribute "
                + "equally to the overall score.";
    }

    public String getRankingExplanation() {
        return rankingExplanation;
    }
}
