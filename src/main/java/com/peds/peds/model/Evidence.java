//Each evidence record stores a source name, its URL, the claim it supports, and whether that claim has been verified.

package com.peds.peds.model;

public class Evidence {

    private String sourceName;
    private String sourceUrl;
    private String claim;
    private boolean verified;
    private String reviewStatus = "PENDING_REVIEW";
    public Evidence() {
    }

    public Evidence(String sourceName, String sourceUrl,
                    String claim, boolean verified) {
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.claim = claim;
        this.verified = verified;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getReviewStatus() {
    return reviewStatus;
}

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
