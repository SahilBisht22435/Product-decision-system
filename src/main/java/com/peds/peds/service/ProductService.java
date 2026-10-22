
package com.peds.peds.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.peds.peds.dto.RankedProductResponse;
import com.peds.peds.model.Evidence;
import com.peds.peds.model.Product;
import com.peds.peds.model.VerificationHistory;
import com.peds.peds.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    private void validateBudget(double budget) {
        if (!Double.isFinite(budget) || budget <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Budget must be a positive, finite number");
        }
    }

    private boolean hasValidSpecifications(Product product) {

        if (product == null
                || !Double.isFinite(product.getPrice())
                || product.getPrice() <= 0) {
            return false;
        }

        String category = product.getCategory();

        if (category == null || category.isBlank()) {
            return false;
        }

        switch (category.trim().toUpperCase(Locale.ROOT)) {

            case "LAPTOP":
                return product.getRam() > 0
                        && product.getStorage() > 0
                        && Double.isFinite(product.getBatteryHours())
                        && product.getBatteryHours() > 0
                        && Double.isFinite(product.getWeight())
                        && product.getWeight() > 0;

            case "SMARTPHONE":
                return Double.isFinite(product.getScreenSize())
                        && product.getScreenSize() > 0
                        && Double.isFinite(product.getCameraMP())
                        && product.getCameraMP() > 0
                        && product.getBatteryMah() > 0;

            case "CAR":
                return Double.isFinite(product.getMileage())
                        && product.getMileage() > 0
                        && product.getSeatingCapacity() > 0
                        && product.getFuelType() != null
                        && !product.getFuelType().isBlank()
                        && product.getTransmission() != null
                        && !product.getTransmission().isBlank();

            default:
                return false;
        }
    }

    public List<RankedProductResponse> getRankedProductsByBudget(
            String category, double budget) {

        validateBudget(budget);
        String normalizedCategory = validateCategory(category);

        List<Product> products =
                productRepository.findByCategoryAndPriceRange(
                        normalizedCategory, 0, budget);

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

        validateBudget(budget);
        String normalizedCategory = validateCategory(category);

        return productRepository.findByCategoryAndPriceRange(
                        normalizedCategory, 0, budget)
                .stream()
                .filter(this::hasValidSpecifications)
                .toList();
    }

    private String validateCategory(String category) {

        if (category == null || category.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category is required");
        }

        String normalizedCategory =
                category.trim().toUpperCase(Locale.ROOT);

        if (!List.of("LAPTOP", "SMARTPHONE", "CAR")
                .contains(normalizedCategory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category must be LAPTOP, SMARTPHONE, or CAR");
        }

        return normalizedCategory;
    }

    private List<String> getRecommendationReasons(Product product) {

        List<String> reasons = new ArrayList<>();

        if (product.getCategory() == null
                || product.getCategory().isBlank()) {
            reasons.add("Category is unavailable");
            return reasons;
        }

        switch (product.getCategory().trim().toUpperCase(Locale.ROOT)) {

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
                    reasons.add(
                            "Storage available: "
                                    + product.getStorage() + " GB");
                }

                if (product.getBatteryHours() >= 8) {
                    reasons.add("Long battery life: 8 hours or more");
                } else if (product.getBatteryHours() >= 6) {
                    reasons.add("Battery life: at least 6 hours");
                }

                if (product.getWeight() > 0
                        && product.getWeight() <= 1.5) {
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
                    reasons.add(
                            "High-resolution camera: 50 MP or more");
                }

                if (product.getBatteryMah() >= 5000) {
                    reasons.add("Large battery: 5000 mAh or more");
                }

                if (product.getScreenSize() >= 6.5
                        && product.getScreenSize() <= 6.9) {
                    reasons.add(
                            "Screen size in the 6.5–6.9 inch range");
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
                    String fuel = product.getFuelType()
                            .trim().toLowerCase(Locale.ROOT);

                    if (fuel.equals("electric") || fuel.equals("cng")) {
                        reasons.add("Fuel type: "
                                + fuel.toUpperCase(Locale.ROOT));
                    } else if (fuel.equals("hybrid")) {
                        reasons.add("Hybrid powertrain");
                    } else if (fuel.equals("petrol")
                            || fuel.equals("diesel")) {
                        reasons.add("Fuel type: " + fuel);
                    }
                }

                if (product.getTransmission() != null) {
                    String transmission = product.getTransmission()
                            .trim().toLowerCase(Locale.ROOT);

                    if (transmission.equals("automatic")) {
                        reasons.add("Automatic transmission");
                    } else if (transmission.equals("manual")) {
                        reasons.add("Manual transmission");
                    }
                }

                break;

            default:
                reasons.add(
                        "Score based on available product specifications");
        }

        if (reasons.isEmpty()) {
            reasons.add(
                    "Limited specifications available for recommendation");
        }

        return reasons;
    }

    public List<Product> getProductsByCategory(String category) {
        String normalizedCategory = validateCategory(category);

        return productRepository.findByCategoryIgnoreCase(
                normalizedCategory);
    }

    public List<Product> getProductsByBudget(double budget) {
        validateBudget(budget);
        return productRepository.findByPriceLessThanEqual(budget);
    }

    public List<Product> compareProducts(List<String> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one product ID is required");
        }

        return productRepository.findAllById(ids).stream().toList();
    }

    public List<Product> compareProductsByName(List<String> names) {

        if (names == null
                || names.isEmpty()
                || names.stream().noneMatch(
                        name -> name != null && !name.isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one valid product name is required");
        }

        List<String> normalizedNames = names.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .toList();

        return productRepository.findAll().stream()
                .filter(product -> product.getName() != null)
                .filter(product -> normalizedNames.stream()
                        .anyMatch(name -> product.getName()
                                .toLowerCase(Locale.ROOT)
                                .contains(name)))
                .toList();
    }

    public Product addProduct(Product product) {

        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product data is required");
        }

        if (product.getName() == null
                || product.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name is required");
        }

        String normalizedCategory = validateCategory(
                product.getCategory());

        if (!Double.isFinite(product.getPrice())
                || product.getPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product price must be a positive, finite number");
        }

        product.setName(product.getName().trim());
        product.setCategory(normalizedCategory);

        return productRepository.save(product);
    }

    private boolean areAllEvidenceReviewedAndApproved(Product product) {

        return product != null
                && product.getEvidence() != null
                && !product.getEvidence().isEmpty()
                && product.getEvidence().stream()
                        .allMatch(item -> {
                            if (item == null || !item.isVerified()) {
                                return false;
                            }

                            String status = item.getReviewStatus();

                            return status != null
                                    && "VERIFIED".equals(
                                            status.trim()
                                                    .toUpperCase(Locale.ROOT));
                        });
    }

    public Product updateProductVerificationStatus(String productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"));

        boolean allApproved =
                areAllEvidenceReviewedAndApproved(product);

        product.setVerified(allApproved);
        product.setLastVerifiedDate(
                allApproved ? LocalDate.now().toString() : null);

        return productRepository.save(product);
    }

    public Evidence verifyEvidence(
            String productId,
            int index,
            String decision,
            String notes) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"));

        if (product.getEvidence() == null
                || index < 0
                || index >= product.getEvidence().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Evidence claim not found");
        }

        if (decision == null || decision.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Decision must be VERIFIED, REJECTED, or PENDING_REVIEW");
        }

        String normalizedDecision =
                decision.trim().toUpperCase(Locale.ROOT);

        if (!normalizedDecision.equals("VERIFIED")
                && !normalizedDecision.equals("REJECTED")
                && !normalizedDecision.equals("PENDING_REVIEW")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Decision must be VERIFIED, REJECTED, or PENDING_REVIEW");
        }

        if (notes == null || notes.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review notes are required");
        }

        Evidence evidence = product.getEvidence().get(index);

        if (evidence == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Evidence claim not found");
        }

        String cleanedNotes = notes.trim();

        evidence.setReviewStatus(normalizedDecision);
        evidence.setVerified("VERIFIED".equals(normalizedDecision));

        if (product.getVerificationHistory() == null) {
            product.setVerificationHistory(new ArrayList<>());
        }

        VerificationHistory history = new VerificationHistory(
                evidence.getClaim(),
                evidence.getSourceName(),
                evidence.getSourceUrl(),
                normalizedDecision,
                LocalDateTime.now(),
                cleanedNotes);

        product.getVerificationHistory().add(history);

        boolean allApproved =
                areAllEvidenceReviewedAndApproved(product);

        product.setVerified(allApproved);
        product.setLastVerifiedDate(
                allApproved ? LocalDate.now().toString() : null);

        productRepository.save(product);

        return evidence;
    }

    public List<VerificationHistory> getVerificationHistory(
            String productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"));

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
                + ". The score difference is "
                + scoreDifference + " points.";

        if (first.getSuitabilityScore()
                > second.getSuitabilityScore()) {
            explanation += " " + firstName
                    + " has the higher suitability score.";
        } else if (first.getSuitabilityScore()
                < second.getSuitabilityScore()) {
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
