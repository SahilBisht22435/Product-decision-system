
package com.peds.peds.model;

import java.util.Locale;

public class Evidence {

    private String sourceName;
    private String sourceUrl;
    private String claim;

    private Boolean verified = false;
    private String reviewStatus = "PENDING_REVIEW";

    public Evidence() {
    }

    public Evidence(
            String sourceName,
            String sourceUrl,
            String claim,
            Boolean verified) {

        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.claim = claim;
        setVerified(verified);
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

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public Boolean isVerified() {
        return Boolean.TRUE.equals(verified);
    }

    public void setVerified(Boolean verified) {
        this.verified = verified != null ? verified : false;

        if (Boolean.TRUE.equals(this.verified)) {
            this.reviewStatus = "VERIFIED";
        } else if ("VERIFIED".equalsIgnoreCase(this.reviewStatus)) {
            this.reviewStatus = "PENDING_REVIEW";
        }
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {

        if (reviewStatus == null || reviewStatus.isBlank()) {
            this.reviewStatus = "PENDING_REVIEW";
            this.verified = false;
            return;
        }

        String normalizedStatus =
                reviewStatus.trim().toUpperCase(Locale.ROOT);

        switch (normalizedStatus) {
            case "VERIFIED":
                this.reviewStatus = "VERIFIED";
                this.verified = true;
                break;

            case "REJECTED":
                this.reviewStatus = "REJECTED";
                this.verified = false;
                break;

            case "PENDING_REVIEW":
                this.reviewStatus = "PENDING_REVIEW";
                this.verified = false;
                break;

            default:
                this.reviewStatus = "PENDING_REVIEW";
                this.verified = false;
                break;
        }
    }
}
