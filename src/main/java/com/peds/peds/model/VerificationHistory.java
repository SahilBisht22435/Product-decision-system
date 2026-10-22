
package com.peds.peds.model;

import java.time.LocalDateTime;

public class VerificationHistory {

    private String claim;
    private String sourceName;
    private String sourceUrl;
    private String action;
    private LocalDateTime verifiedAt;
    private String notes;

    public VerificationHistory() {
    }

    public VerificationHistory(
            String claim,
            String sourceName,
            String sourceUrl,
            String action,
            LocalDateTime verifiedAt,
            String notes) {

        this.claim = claim;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.action = action;
        this.verifiedAt = verifiedAt != null
                ? verifiedAt
                : LocalDateTime.now();
        this.notes = notes;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
