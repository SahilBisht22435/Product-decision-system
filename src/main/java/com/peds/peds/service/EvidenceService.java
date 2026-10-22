
package com.peds.peds.service;

import com.peds.peds.model.Evidence;
import com.peds.peds.model.Product;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EvidenceService {

    private final EvidenceValidationService evidenceValidationService;

    public EvidenceService(
            EvidenceValidationService evidenceValidationService) {

        this.evidenceValidationService = evidenceValidationService;
    }

    /**
     * Calculates a heuristic evidence-confidence score from 0 to 60.
     *
     * Each evidence record contributes an equal share of the maximum:
     * - Up to 40 points for valid evidence fields.
     * - Up to 20 points for an accessible source.
     *
     * Rejected evidence contributes no points. Reviewer approval
     * alone does not increase the score.
     *
     * The score is not proof of factual accuracy. Independent
     * confirmation of claims has not been implemented.
     */
    public int calculateConfidenceScore(Product product) {

        if (product == null) {
            return 0;
        }

        List<Evidence> evidenceList = product.getEvidence();

        if (evidenceList == null || evidenceList.isEmpty()) {
            return 0;
        }

        int totalClaims = evidenceList.size();
        double score = 0.0;

        // Cache repeated source checks for this calculation.
        Map<String, Boolean> sourceAvailabilityCache = new HashMap<>();

        for (Evidence evidence : evidenceList) {

            if (evidence == null) {
                continue;
            }

            String reviewStatus = evidence.getReviewStatus();

            if (reviewStatus != null
                    && "REJECTED".equals(
                            reviewStatus.trim().toUpperCase(Locale.ROOT))) {
                continue;
            }

            Map<String, Object> validation =
                    evidenceValidationService.validateEvidence(evidence);

            boolean fieldsValid =
                    Boolean.TRUE.equals(validation.get("fieldsValid"));

            if (!fieldsValid) {
                continue;
            }

            // Valid evidence fields contribute up to 40 points.
            score += 40.0 / totalClaims;

            String sourceUrl = evidence.getSourceUrl();

            if (sourceUrl == null || sourceUrl.isBlank()) {
                continue;
            }

            String cacheKey = sourceUrl.trim();

            boolean reachable;

            if (sourceAvailabilityCache.containsKey(cacheKey)) {
                reachable = sourceAvailabilityCache.get(cacheKey);
            } else {
                Map<String, Object> availability =
                        evidenceValidationService.checkSourceAvailability(
                                cacheKey);

                reachable = Boolean.TRUE.equals(
                        availability.get("reachable"));

                sourceAvailabilityCache.put(cacheKey, reachable);
            }

            // Accessible sources contribute up to another 20 points.
            if (reachable) {
                score += 20.0 / totalClaims;
            }
        }

        return (int) Math.round(Math.min(60.0, score));
    }
}
