
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

            Map<String, Object> validation =
                    evidenceValidationService.validateEvidence(evidence);

            boolean fieldsValid = Boolean.TRUE.equals(
                    validation.get("fieldsValid"));

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

            // An accessible source contributes up to 20 points.
            if (reachable) {
                score += 20.0 / totalClaims;
            }

            // Only an explicitly VERIFIED review earns review points.
            // The Boolean flag alone is not enough.
            if ("VERIFIED".equals(evidence.getReviewStatus())
                    && evidence.isVerified()) {
                score += 40.0 / totalClaims;
            }
        }

        return (int) Math.round(Math.min(100, score));
    }
}
