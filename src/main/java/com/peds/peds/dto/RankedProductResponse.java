
package com.peds.peds.dto;

import com.peds.peds.model.Product;
import java.util.List;

public class RankedProductResponse {

    private Product product;
    private int suitabilityScore;
    private List<String> reasons;

    public RankedProductResponse(
            Product product,
            int suitabilityScore,
            List<String> reasons) {
        this.product = product;
        this.suitabilityScore = suitabilityScore;
        this.reasons = reasons;
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
}
