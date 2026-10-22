
package com.peds.peds.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EvidenceReviewRequest {

    @NotBlank(message = "Decision is required")
    @Pattern(
            regexp = "(?i)VERIFIED|REJECTED|PENDING_REVIEW",
            message = "Decision must be VERIFIED, REJECTED, or PENDING_REVIEW"
    )
    private String decision;

    @NotBlank(message = "Review notes are required")
    private String notes;

    public EvidenceReviewRequest() {
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
