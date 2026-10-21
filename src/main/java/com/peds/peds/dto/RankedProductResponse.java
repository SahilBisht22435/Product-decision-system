
package com.peds.peds.dto;

import com.peds.peds.model.Product;
import com.peds.peds.model.Evidence;
import java.util.List;

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
        this.suitabilityScore = suitabilityScore;
        this.reasons = reasons;
        this.evidenceConfidenceScore = evidenceConfidenceScore;

        this.suitabilityContribution = Math.round(suitabilityScore * 0.70 * 100.0) / 100.0;
        this.evidenceContribution = Math.round(evidenceConfidenceScore * 0.30 * 100.0) / 100.0;
        this.overallScore = (int) Math.round(
                suitabilityContribution + evidenceContribution);

        this.evidenceStatus = determineEvidenceStatus(product);
        this.overallExplanation = generateOverallExplanation();
        this.rankingExplanation = generateRankingExplanation();
    }

    private String determineEvidenceStatus(Product product) {
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

            if (!"VERIFIED".equals(status) || !evidence.isVerified()) {
                allVerified = false;
            }

            if (!"REJECTED".equals(status)) {
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
        explanation += " All evidence records are rejected, "
                + "so evidence confidence is zero.";
    } else if ("NO_EVIDENCE".equals(evidenceStatus)) {
        explanation += " No evidence records are available "
                + "for this product.";
    } else if ("PENDING_REVIEW".equals(evidenceStatus)) {
        explanation += " Evidence is pending review or has "
                + "mixed review statuses.";
    } else if ("VERIFIED".equals(evidenceStatus)) {
        explanation += " All evidence records have verified "
                + "review status; this does not independently "
                + "guarantee that every claim is factually correct.";
    }

    return explanation;
}

public String getOverallExplanation() {
    return overallExplanation;
}

private String generateRankingExplanation() {
    if (evidenceConfidenceScore == 0) {
        return "This product receives no evidence-confidence points. "
                + "Its ranking depends entirely on suitability.";
    }

    if (evidenceContribution > suitabilityContribution) {
        return "Evidence confidence contributes more to the overall "
                + "score than suitability.";
    }

    return "Suitability contributes more to the overall score "
            + "than evidence confidence.";
}

public String getRankingExplanation() {
    return rankingExplanation;
}
}
