
package com.peds.peds.service;

import com.peds.peds.model.Evidence;
import com.peds.peds.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EvidenceService {

    private final EvidenceValidationService evidenceValidationService;

    public EvidenceService(
            EvidenceValidationService evidenceValidationService) {
        this.evidenceValidationService = evidenceValidationService;
    }


public int calculateConfidenceScore(Product product) {
    List<Evidence> evidenceList = product.getEvidence();

    if (evidenceList == null || evidenceList.isEmpty()) {
        return 0;
    }

    int totalClaims = evidenceList.size();
    double score = 0;

    for (Evidence evidence : evidenceList) {
        if (evidence == null) {
            continue;
        }

        String reviewStatus = evidence.getReviewStatus();

        // Rejected evidence contributes no confidence points.
        if ("REJECTED".equalsIgnoreCase(reviewStatus)) {
            continue;
        }

        Map<String, Object> validation =
                evidenceValidationService.validateEvidence(evidence);

        boolean fieldsValid = Boolean.TRUE.equals(
                validation.get("fieldsValid"));

        // Invalid evidence fields contribute no points.
        if (!fieldsValid) {
            continue;
        }

        // Valid evidence fields contribute up to 40 points.
        score += 40.0 / totalClaims;

        Map<String, Object> availability =
                evidenceValidationService.checkSourceAvailability(
                        evidence.getSourceUrl());

        boolean reachable = Boolean.TRUE.equals(
                availability.get("reachable"));

        // Only an accessible source contributes these 20 points.
        if (reachable) {
            score += 20.0 / totalClaims;
        }

        /*
         * Reviewer approval alone does not prove claim accuracy.
         * Therefore, do not award the remaining 40 points
         * based only on VERIFIED status and the verified flag.
         *
         * Those points should be awarded only after PEDS has
         * an explicit independent-confirmation field.
         */
    }

    return (int) Math.round(Math.min(100, score));
}

}
