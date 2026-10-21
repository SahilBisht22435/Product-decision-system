
package com.peds.peds.service;
import java.time.LocalDate;
import com.peds.peds.model.Product;
import com.peds.peds.dto.RankedProductResponse;
import com.peds.peds.model.Evidence;
import com.peds.peds.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.peds.peds.model.VerificationHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RecommendationService recommendationService;
    private final EvidenceService evidenceService;
    public ProductService(
        ProductRepository productRepository,
        RecommendationService recommendationService,
        EvidenceService evidenceService) {

    this.productRepository = productRepository;
    this.recommendationService = recommendationService;
    this.evidenceService = evidenceService;
}

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

   



    // Filter and rank products by suitability score
    // public List<Product> getRankedProductsByBudget(
    //         String category, double budget) {

    //     return productRepository.findByCategoryIgnoreCase(category)
    //             .stream()
    //             .filter(product -> product.getPrice() <= budget)
    //             .sorted((p1, p2) -> Integer.compare(
    //                     recommendationService.calculateScore(p2),
    //                     recommendationService.calculateScore(p1)))
    //             .toList();
    // }

 // Filter products by category and budget

public List<RankedProductResponse> getRankedProductsByBudget(
        String category, double budget) {

    List<Product> products =
            productRepository.findByCategoryAndPriceRange(
                    category, 0, budget);

    return products.stream()
            .filter(this::hasValidSpecifications)
            .map(product -> new RankedProductResponse(
                    product,
                    recommendationService.calculateScore(product),
                    getRecommendationReasons(product),
                    evidenceService.calculateConfidenceScore(product)))
            .sorted((p1, p2) -> Integer.compare(
                    p2.getOverallScore(),
                    p1.getOverallScore()))
            .toList();
}

public List<Product> getRecommendedProductsByBudget(
        String category, double budget) {

    return productRepository.findByCategoryAndPriceRange(
                    category, 0, budget)
            .stream()
            .filter(this::hasValidSpecifications)
            .toList();
}

private boolean hasValidSpecifications(Product product) {

    if (product.getPrice() <= 0) {
        return false;
    }

    String category = product.getCategory();

    if (category == null) {
        return false;
    }

    switch (category.trim().toUpperCase()) {
        case "LAPTOP":
            return product.getRam() > 0
                    && product.getStorage() > 0
                    && product.getBatteryHours() > 0
                    && product.getWeight() > 0;

        case "SMARTPHONE":
            return product.getScreenSize() > 0
                    && product.getCameraMP() > 0
                    && product.getBatteryMah() > 0;

        case "CAR":
            return product.getMileage() > 0
                    && product.getSeatingCapacity() > 0
                    && product.getFuelType() != null
                    && !product.getFuelType().isBlank()
                    && product.getTransmission() != null
                    && !product.getTransmission().isBlank();

        default:
            return false;
    }
}


private List<String> getRecommendationReasons(Product product) {
    List<String> reasons = new ArrayList<>();

    if (product.getCategory() == null) {
        reasons.add("Category is unavailable");
        return reasons;
    }

    switch (product.getCategory().toUpperCase()) {
        case "LAPTOP":
            if (product.getRam() >= 16) {
                reasons.add("Strong RAM capacity: 16 GB or more");
            } else if (product.getRam() >= 8) {
                reasons.add("Suitable RAM capacity: at least 8 GB");
            }

            if (product.getStorage() >= 1024) {
                reasons.add("Large storage capacity: 1 TB or more");
            } else if (product.getStorage() >= 512) {
                reasons.add("Good storage capacity: 512 GB or more");
            } else if (product.getStorage() > 0) {
                reasons.add("Storage available: " + product.getStorage() + " GB");
            }

            if (product.getBatteryHours() >= 8) {
                reasons.add("Long battery life: 8 hours or more");
            } else if (product.getBatteryHours() >= 6) {
                reasons.add("Battery life: at least 6 hours");
            }

            if (product.getWeight() > 0 && product.getWeight() <= 1.5) {
                reasons.add("Lightweight design: 1.5 kg or less");
            } else if (product.getWeight() > 1.5
                    && product.getWeight() <= 1.8) {
                reasons.add("Moderate weight: 1.5–1.8 kg");
            }
            break;

        case "SMARTPHONE":
            if (product.getRam() >= 12) {
                reasons.add("High RAM capacity: 12 GB or more");
            } else if (product.getRam() >= 8) {
                reasons.add("Good RAM capacity: 8 GB or more");
            } else if (product.getRam() >= 4) {
                reasons.add("At least 4 GB RAM");
            }

            if (product.getStorage() >= 512) {
                reasons.add("Large storage: 512 GB or more");
            } else if (product.getStorage() >= 256) {
                reasons.add("Good storage: 256 GB or more");
            } else if (product.getStorage() >= 128) {
                reasons.add("Storage: 128 GB or more");
            }

            if (product.getCameraMP() >= 50) {
                reasons.add("High-resolution camera: 50 MP or more");
            }

            if (product.getBatteryMah() >= 5000) {
                reasons.add("Large battery: 5000 mAh or more");
            }

            if (product.getScreenSize() >= 6.5
                    && product.getScreenSize() <= 6.9) {
                reasons.add("Screen size in the 6.5–6.9 inch range");
            }
            break;

        case "CAR":
            if (product.getMileage() >= 25) {
                reasons.add("High mileage: 25 km/l or more");
            } else if (product.getMileage() >= 20) {
                reasons.add("Good mileage: 20 km/l or more");
            } else if (product.getMileage() >= 15) {
                reasons.add("Mileage: 15 km/l or more");
            }

            if (product.getSeatingCapacity() >= 7) {
                reasons.add("Seating for 7 or more people");
            } else if (product.getSeatingCapacity() >= 5) {
                reasons.add("Seating for at least 5 people");
            }

            if (product.getFuelType() != null) {
                String fuel = product.getFuelType().toLowerCase();

                if (fuel.equals("electric") || fuel.equals("cng")) {
                    reasons.add("Fuel type: " + fuel.toUpperCase());
                } else if (fuel.equals("hybrid")) {
                    reasons.add("Hybrid powertrain");
                } else if (fuel.equals("petrol") || fuel.equals("diesel")) {
                    reasons.add("Fuel type: " + fuel);
                }
            }

            if (product.getTransmission() != null) {
                String transmission = product.getTransmission().toLowerCase();

                if (transmission.equals("automatic")) {
                    reasons.add("Automatic transmission");
                } else if (transmission.equals("manual")) {
                    reasons.add("Manual transmission");
                }
            }
            break;

        default:
            reasons.add("Score based on available product specifications");
    }

    if (reasons.isEmpty()) {
        reasons.add("Limited specifications available for recommendation");
    }

    return reasons;
}



    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> getProductsByBudget(double budget) {
        return productRepository.findByPriceLessThanEqual(budget);
    }

    public List<Product> compareProducts(List<String> ids) {
        return productRepository.findAllById(ids).stream().toList();
    }

    public List<Product> compareProductsByName(List<String> names) {
        return productRepository.findAll().stream()
                .filter(product -> names.stream()
                        .anyMatch(name -> product.getName()
                                .toLowerCase()
                                .contains(name.toLowerCase())))
                .toList();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    
public Product updateProductVerificationStatus(String productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found"));

    boolean allVerified = product.getEvidence() != null
            && !product.getEvidence().isEmpty()
            && product.getEvidence().stream()
                    .allMatch(Evidence::isVerified);

    product.setVerified(allVerified);

    if (allVerified) {
        product.setLastVerifiedDate(
                java.time.LocalDate.now().toString());
    } else {
        product.setLastVerifiedDate(null);
    }

    return productRepository.save(product);
}


//This method finds the product, checks that the evidence index exists, marks only that claim as verified, and saves the updated product to MongoDB.
    


public Evidence verifyEvidence(
        String productId,
        int index,
        String decision,
        String notes) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found"));

    if (product.getEvidence() == null
            || index < 0
            || index >= product.getEvidence().size()) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Evidence claim not found");
    }

    Evidence evidence = product.getEvidence().get(index);

    if (decision == null
            || !(decision.equals("VERIFIED")
                || decision.equals("REJECTED")
                || decision.equals("PENDING_REVIEW"))) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Decision must be VERIFIED, REJECTED, or PENDING_REVIEW");
    }

    if (notes == null || notes.trim().isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Review notes are required");
    }

    // Only record VERIFIED when the reviewer explicitly confirms the claim.
    evidence.setVerified("VERIFIED".equals(decision));
    evidence.setReviewStatus(decision);

    if (product.getVerificationHistory() == null) {
    product.setVerificationHistory(new ArrayList<>());
}

// Prevent an identical review from being recorded twice.
String cleanedNotes = notes.trim();

boolean duplicateReview = product.getVerificationHistory()
        .stream()
        .anyMatch(existing ->
                java.util.Objects.equals(
                        existing.getClaim(), evidence.getClaim())
                && java.util.Objects.equals(
                        existing.getSourceUrl(), evidence.getSourceUrl())
                && java.util.Objects.equals(
                        existing.getAction(), decision)
                && java.util.Objects.equals(
                        existing.getNotes(), cleanedNotes)
        );

if (duplicateReview) {
    productRepository.save(product);
    return evidence;
}

VerificationHistory history = new VerificationHistory(
            evidence.getClaim(),
            evidence.getSourceName(),
            evidence.getSourceUrl(),
            decision,
            LocalDateTime.now(),
            notes.trim()
    );

    product.getVerificationHistory().add(history);

    boolean allVerified = product.getEvidence() != null
            && !product.getEvidence().isEmpty()
            && product.getEvidence().stream()
                    .allMatch(item ->
                            item != null
                            && item.isVerified()
                            && "VERIFIED".equals(item.getReviewStatus())
                    );

    product.setVerified(allVerified);

    if (allVerified) {
        product.setLastVerifiedDate(LocalDate.now().toString());
    } else {
        product.setLastVerifiedDate(null);
    }

    productRepository.save(product);

    return evidence;

}



public List<VerificationHistory> getVerificationHistory(
        String productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found"));

    if (product.getVerificationHistory() == null) {
        return List.of();
    }

    return product.getVerificationHistory();
}

public String explainTopRankedProducts(
        String category, double budget) {

    List<RankedProductResponse> products =
            getRankedProductsByBudget(category, budget);

    if (products.isEmpty()) {
        return "No products found for the selected category and budget.";
    }

    if (products.size() == 1) {
        return "Only one product matches the selected category and budget: "
                + products.get(0).getProduct().getName()
                + ". There is no second product to compare.";
    }

    RankedProductResponse first = products.get(0);
    RankedProductResponse second = products.get(1);

    String firstName = first.getProduct().getName();
    String secondName = second.getProduct().getName();

    int scoreDifference =
            first.getOverallScore() - second.getOverallScore();

    String explanation = firstName + " ranks above " + secondName
            + " with an overall score of " + first.getOverallScore()
            + " compared with " + second.getOverallScore()
            + ". The score difference is " + scoreDifference + " points.";

    if (first.getSuitabilityScore() > second.getSuitabilityScore()) {
        explanation += " " + firstName
                + " has the higher suitability score.";
    } else if (first.getSuitabilityScore() < second.getSuitabilityScore()) {
        explanation += " " + secondName
                + " has the higher suitability score.";
    } else {
        explanation += " Both products have equal suitability scores.";
    }

    if (first.getEvidenceConfidenceScore()
            > second.getEvidenceConfidenceScore()) {
        explanation += " " + firstName
                + " has the higher evidence-confidence score.";
    } else if (first.getEvidenceConfidenceScore()
            < second.getEvidenceConfidenceScore()) {
        explanation += " " + secondName
                + " has the higher evidence-confidence score.";
    } else {
        explanation += " Both products have equal evidence-confidence scores.";
    }

    return explanation;
}
}
