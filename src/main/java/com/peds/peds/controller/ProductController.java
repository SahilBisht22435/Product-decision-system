
package com.peds.peds.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.peds.peds.dto.EvidenceReviewRequest;
import com.peds.peds.dto.RankedProductResponse;
import com.peds.peds.model.Evidence;
import com.peds.peds.model.Product;
import com.peds.peds.model.VerificationHistory;
import com.peds.peds.repository.ProductRepository;
import com.peds.peds.service.EvidenceService;
import com.peds.peds.service.EvidenceValidationService;
import com.peds.peds.service.ProductService;
import com.peds.peds.service.RecommendationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final RecommendationService recommendationService;
    private final EvidenceService evidenceService;
    private final EvidenceValidationService evidenceValidationService;
    private final ProductRepository productRepository;

    public ProductController(
            ProductService productService,
            RecommendationService recommendationService,
            EvidenceService evidenceService,
            EvidenceValidationService evidenceValidationService,
            ProductRepository productRepository) {

        this.productService = productService;
        this.recommendationService = recommendationService;
        this.evidenceService = evidenceService;
        this.evidenceValidationService = evidenceValidationService;
        this.productRepository = productRepository;
    }

    // Retrieve all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Validate evidence fields and source information
    @PostMapping("/validate-evidence")
    public Map<String, Object> validateEvidence(
            @RequestBody Evidence evidence) {

        if (evidence == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Evidence data is required");
        }

        return evidenceValidationService.validateEvidence(evidence);
    }

    // Retrieve products within a maximum budget
    @GetMapping("/budget/{budget}")
    public List<Product> getProductsByBudget(
            @PathVariable double budget) {

        return productService.getProductsByBudget(budget);
    }

    // Compare products by their names
    @GetMapping("/compare")
    public List<Product> compareProducts(
            @RequestParam List<String> names) {

        return productService.compareProductsByName(names);
    }

    // Validate category-specific specifications before creating a product
    private void validateProductSpecifications(Product product) {

        String category = product.getCategory();

        if (category == null || category.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product category is required");
        }

        category = category.trim().toUpperCase(Locale.ROOT);
        product.setCategory(category);

        switch (category) {

            case "LAPTOP":

                if (product.getRam() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "RAM must be greater than zero");
                }

                if (product.getStorage() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Storage must be greater than zero");
                }

                if (product.getBatteryHours() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Battery hours must be greater than zero");
                }

                if (product.getWeight() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Weight must be greater than zero");
                }

                break;

            case "SMARTPHONE":

                if (product.getScreenSize() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Screen size must be greater than zero");
                }

                if (product.getCameraMP() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Camera megapixels must be greater than zero");
                }

                if (product.getBatteryMah() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Battery capacity must be greater than zero");
                }

                break;

            case "CAR":

                if (product.getMileage() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Mileage must be greater than zero");
                }

                if (product.getSeatingCapacity() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Seating capacity must be greater than zero");
                }

                if (product.getFuelType() == null
                        || product.getFuelType().isBlank()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Fuel type is required");
                }

                if (product.getTransmission() == null
                        || product.getTransmission().isBlank()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Transmission is required");
                }

                break;

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Category must be LAPTOP, SMARTPHONE, or CAR");
        }
    }

    // Create a product
    @PostMapping
    public Product addProduct(
            @Valid @RequestBody Product product) {

        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product data is required");
        }

        validateProductSpecifications(product);

        return productService.addProduct(product);
    }

    // Retrieve products by category
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(
            @PathVariable String category) {

        if (category == null
                || !category.trim().matches(
                        "(?i)LAPTOP|SMARTPHONE|CAR")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category must be LAPTOP, SMARTPHONE, or CAR");
        }

        return productService.getProductsByCategory(
                category.trim().toUpperCase(Locale.ROOT));
    }

    // Recommend eligible products within a budget
    @GetMapping("/recommend-by-budget")
    public List<Product> recommendByBudget(
            @RequestParam String category,
            @RequestParam double budget) {

        return productService.getRecommendedProductsByBudget(
                category, budget);
    }

    // Retrieve ranked products
    @GetMapping("/ranked-by-budget")
    public List<RankedProductResponse> getRankedProductsByBudget(
            @RequestParam String category,
            @RequestParam double budget) {

        return productService.getRankedProductsByBudget(
                category, budget);
    }

    // Explain the ranking of the top products
    @GetMapping("/ranking-explanation")
    public String getRankingExplanation(
            @RequestParam String category,
            @RequestParam double budget) {

        return productService.explainTopRankedProducts(
                category, budget);
    }

    // Retrieve an individual product's evidence-confidence score
    @GetMapping("/evidence-score/{id}")
    public Map<String, Object> getEvidenceScore(
            @PathVariable String id) {

        Product product = findProductOrThrow(id);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("productName", product.getName());
        response.put("evidenceSource", product.getEvidenceSource());
        response.put("verified", product.isVerified());
        response.put("lastVerifiedDate", product.getLastVerifiedDate());
        response.put(
                "evidenceConfidenceScore",
                evidenceService.calculateConfidenceScore(product));

        return response;
    }

    // Add an evidence claim to an existing product
    @PostMapping("/{productId}/evidence")
    public Product addEvidenceToProduct(
            @PathVariable String productId,
            @RequestBody Evidence evidence) {

        if (evidence == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Evidence data is required");
        }

        Product product = findProductOrThrow(productId);

        if (product.getEvidence() == null) {
            product.setEvidence(new ArrayList<>());
        }

        // New evidence must be reviewed before approval.
        evidence.setVerified(false);
        evidence.setReviewStatus("PENDING_REVIEW");

        product.getEvidence().add(evidence);

        // Adding pending evidence invalidates the previous
        // product-level verification state.
        product.setVerified(false);
        product.setLastVerifiedDate(null);

        return productRepository.save(product);
    }

    // Review an evidence claim
    @PatchMapping("/{productId}/evidence/{index}/verify")
    public Evidence verifyEvidence(
            @PathVariable String productId,
            @PathVariable int index,
            @Valid @RequestBody EvidenceReviewRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Evidence review request is required");
        }

        return productService.verifyEvidence(
                productId,
                index,
                request.getDecision(),
                request.getNotes());
    }

    // Recommend products by their names and return suitability reasons
    @GetMapping("/recommend")
    public List<Map<String, Object>> recommendProducts(
            @RequestParam List<String> names) {

        List<Product> products =
                productService.compareProductsByName(names);

        return products.stream().map(product -> {

            int score =
                    recommendationService.calculateScore(product);

            Map<String, Object> result = new LinkedHashMap<>();

            result.put("name", product.getName());
            result.put("brand", product.getBrand());
            result.put("price", product.getPrice());
            result.put("ram", product.getRam());
            result.put("storage", product.getStorage());
            result.put("batteryHours", product.getBatteryHours());
            result.put("weight", product.getWeight());
            result.put("suitabilityScore", score);

            List<String> reasons = new ArrayList<>();

            if (product.getRam() >= 16) {
                reasons.add("Good RAM capacity: 16 GB or more");
            }

            if (product.getStorage() >= 512) {
                reasons.add("At least 512 GB storage");
            }

            if (product.getBatteryHours() >= 7) {
                reasons.add("Battery rating of 7 hours or more");
            }

            if (product.getWeight() > 0
                    && product.getWeight() <= 1.8) {
                reasons.add("Weight is 1.8 kg or less");
            }

            result.put("reasons", reasons);

            return result;
        }).toList();
    }

    // Retrieve one product by ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return findProductOrThrow(id);
    }

    // Retrieve review history
    @GetMapping("/{productId}/verification-history")
    public List<VerificationHistory> getVerificationHistory(
            @PathVariable String productId) {

        return productService.getVerificationHistory(productId);
    }

    // Check the availability of an evidence source
    @GetMapping("/check-source")
    public Map<String, Object> checkSource(
            @RequestParam String url) {

        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Source URL is required");
        }

        Map<String, Object> result =
                evidenceValidationService.checkSourceAvailability(
                        url.trim());

        result.put("controllerVersion", "PEDS-CHECK-SOURCE-V2");

        return result;
    }

    // Generate a detailed evidence report for a product
    @GetMapping("/{productId}/evidence-report")
    public Map<String, Object> getEvidenceReport(
            @PathVariable String productId) {

        Product product = findProductOrThrow(productId);

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("productId", product.getId());
        report.put("productName", product.getName());

        List<Map<String, Object>> evidenceReports = new ArrayList<>();
        List<Evidence> evidenceList = product.getEvidence();

        boolean allEvidenceApprovedAndAvailable =
                evidenceList != null && !evidenceList.isEmpty();

        if (evidenceList != null) {

            for (int i = 0; i < evidenceList.size(); i++) {

                Evidence evidence = evidenceList.get(i);
                Map<String, Object> item = new LinkedHashMap<>();

                item.put("evidenceIndex", i);

                if (evidence == null) {
                    item.put("validationStatus", "INVALID");
                    item.put(
                            "sourceAvailabilityStatus",
                            "SOURCE_UNAVAILABLE");
                    item.put("reviewStatus", "PENDING_REVIEW");
                    item.put("recordedVerifiedFlag", false);
                    item.put(
                            "claimAccuracyIndependentlyConfirmed",
                            false);
                    item.put(
                            "independentConfirmationWarning",
                            "Claim accuracy has not been independently confirmed.");

                    evidenceReports.add(item);
                    allEvidenceApprovedAndAvailable = false;
                    continue;
                }

                item.put("claim", evidence.getClaim());
                item.put("sourceName", evidence.getSourceName());
                item.put("sourceUrl", evidence.getSourceUrl());
                item.put("verified", evidence.isVerified());

                Map<String, Object> validation =
                        evidenceValidationService.validateEvidence(
                                evidence);

                Map<String, Object> availability =
                        evidenceValidationService.checkSourceAvailability(
                                evidence.getSourceUrl());

                boolean fieldsValid =
                        Boolean.TRUE.equals(
                                validation.get("fieldsValid"));

                boolean reachable =
                        Boolean.TRUE.equals(
                                availability.get("reachable"));

                Object statusValue = availability.get("httpStatus");

                Integer httpStatus =
                        statusValue instanceof Number
                                ? ((Number) statusValue).intValue()
                                : null;

                String sourceAvailabilityStatus;

                if (!fieldsValid) {
                    sourceAvailabilityStatus = "INVALID";
                } else if (httpStatus != null && httpStatus == 403) {
                    sourceAvailabilityStatus = "ACCESS_DENIED";
                } else if (httpStatus != null && httpStatus == 404) {
                    sourceAvailabilityStatus = "PAGE_NOT_FOUND";
                } else if (!reachable) {
                    sourceAvailabilityStatus = "SOURCE_UNAVAILABLE";
                } else {
                    sourceAvailabilityStatus = "AVAILABLE";
                }

                String reviewStatus = evidence.getReviewStatus();

                if (reviewStatus == null || reviewStatus.isBlank()) {
                    reviewStatus = "PENDING_REVIEW";
                } else {
                    reviewStatus =
                            reviewStatus.trim().toUpperCase(Locale.ROOT);
                }

                boolean approved =
                        evidence.isVerified()
                                && "VERIFIED".equals(reviewStatus);

                if (!approved) {
                    allEvidenceApprovedAndAvailable = false;
                }

                if (!fieldsValid || !reachable) {
                    allEvidenceApprovedAndAvailable = false;
                }

                item.put("validation", validation);
                item.put("sourceAvailability", availability);
                item.put(
                        "sourceAvailabilityStatus",
                        sourceAvailabilityStatus);
                item.put("reviewStatus", reviewStatus);
                item.put(
                        "recordedVerifiedFlag",
                        evidence.isVerified());
                item.put(
                        "claimAccuracyIndependentlyConfirmed",
                        false);
                item.put(
                        "independentConfirmationWarning",
                        "Review status indicates the review decision only. "
                                + "Claim accuracy has not been independently confirmed.");

                evidenceReports.add(item);
            }
        }

        boolean productVerified =
                allEvidenceApprovedAndAvailable
                        && product.isVerified();

        report.put("productVerified", productVerified);
        report.put(
                "lastVerifiedDate",
                productVerified ? product.getLastVerifiedDate() : null);
        report.put(
                "evidenceConfidenceScore",
                evidenceService.calculateConfidenceScore(product));
        report.put("evidenceReports", evidenceReports);

        List<VerificationHistory> history =
                product.getVerificationHistory();

        report.put(
                "verificationHistory",
                history == null ? new ArrayList<>() : history);

        return report;
    }

    // Shared lookup method so endpoints do not load every product.
    private Product findProductOrThrow(String productId) {

        if (productId == null || productId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product ID is required");
        }

        return productRepository.findById(productId.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"));
    }
}
