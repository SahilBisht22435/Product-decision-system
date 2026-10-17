package com.peds.peds.controller;
import com.peds.peds.model.Product;
import com.peds.peds.service.ProductService;
import com.peds.peds.service.RecommendationService;
import com.peds.peds.dto.RankedProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
// import com.peds.peds.model.Product;
import com.peds.peds.service.EvidenceService;

import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

import com.peds.peds.dto.EvidenceReviewRequest;
import com.peds.peds.model.Evidence;
import java.util.ArrayList;
import com.peds.peds.model.VerificationHistory;
import com.peds.peds.repository.ProductRepository;
import com.peds.peds.service.EvidenceValidationService;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final RecommendationService recommendationService;

    public ProductController(
            ProductService productService,
            RecommendationService recommendationService) {
        this.productService = productService;
        this.recommendationService = recommendationService;
    }
    @Autowired
    private EvidenceValidationService evidenceValidationService;

    @Autowired
    private ProductRepository productRepository;
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/validate-evidence")
    public Map<String, Object> validateEvidence(
        @RequestBody Evidence evidence) {

        return evidenceValidationService.validateEvidence(evidence);
    }
    @GetMapping("/budget/{budget}")
    public List<Product> getProductsByBudget(
            @PathVariable double budget) {
        return productService.getProductsByBudget(budget);
    }

    
    @GetMapping("/compare")
    public List<Product> compareProducts(
            @RequestParam List<String> names) {
        return productService.compareProductsByName(names);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }


    
@GetMapping("/category/{category}")
public List<Product> getProductsByCategory(
        @PathVariable String category) {
    return productService.getProductsByCategory(category);
}


@GetMapping("/recommend-by-budget")
public List<Product> recommendByBudget(
        @RequestParam String category,
        @RequestParam double budget) {

    return productService.getRecommendedProductsByBudget(
            category, budget);
}


@GetMapping("/ranked-by-budget")
public List<RankedProductResponse> getRankedProductsByBudget(
        @RequestParam String category,
        @RequestParam double budget) {

    return productService.getRankedProductsByBudget(
            category, budget);
}

@Autowired
private EvidenceService evidenceService;

@GetMapping("/evidence-score/{id}")
public Map<String, Object> getEvidenceScore(
        @PathVariable String id) {

    Product product = productService.getAllProducts()
            .stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Product not found"));

    int score = evidenceService.calculateConfidenceScore(product);

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("productName", product.getName());
    response.put("evidenceSource", product.getEvidenceSource());
    response.put("verified", product.isVerified());
    response.put("lastVerifiedDate", product.getLastVerifiedDate());
    response.put("evidenceConfidenceScore", score);

    return response;
}


@PatchMapping("/{productId}/evidence/{index}/verify")
public Evidence verifyEvidence(
        @PathVariable String productId,
        @PathVariable int index,
        @Valid @RequestBody EvidenceReviewRequest request) {

    return productService.verifyEvidence(
            productId,
            index,
            request.getDecision(),
            request.getNotes()
    );
    
}

    
@GetMapping("/recommend")
public List<Map<String, Object>> recommendProducts(
        @RequestParam List<String> names) {

    List<Product> products =
            productService.compareProductsByName(names);

    return products.stream().map(product -> {
        int score = recommendationService.calculateScore(product);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", product.getName());
        result.put("brand", product.getBrand());
        result.put("price", product.getPrice());
        result.put("ram", product.getRam());
        result.put("storage", product.getStorage());
        result.put("batteryHours", product.getBatteryHours());
        result.put("weight", product.getWeight());
        result.put("suitabilityScore", score);

        List<String> reasons = new java.util.ArrayList<>();

        if (product.getRam() >= 16) {
            reasons.add("Good RAM capacity: 16 GB or more");
        }
        if (product.getStorage() >= 512) {
            reasons.add("At least 512 GB storage");
        }
        if (product.getBatteryHours() >= 7) {
            reasons.add("Battery rating of 7 hours or more");
        }
        if (product.getWeight() <= 1.8) {
            reasons.add("Weight is 1.8 kg or less");
        }

        result.put("reasons", reasons);
        return result;
    }).toList();
}
@GetMapping("/{id}")
public Product getProductById(@PathVariable String id) {
    return productService.getAllProducts().stream()
            .filter(product -> id.equals(product.getId()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found"));
}

@GetMapping("/{productId}/verification-history")
public List<VerificationHistory> getVerificationHistory(
        @PathVariable String productId) {

    return productService.getVerificationHistory(productId);
}

@GetMapping("/check-source")
public Map<String, Object> checkSource(
        @RequestParam String url) {

    Map<String, Object> result =
            evidenceValidationService.checkSourceAvailability(url);

    result.put("controllerVersion", "PEDS-CHECK-SOURCE-V2");

    return result;
}



@GetMapping("/{productId}/evidence-report")
public Map<String, Object> getEvidenceReport(
        @PathVariable String productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            ));

    Map<String, Object> report = new LinkedHashMap<>();

    report.put("productId", product.getId());
    report.put("productName", product.getName());

    List<Map<String, Object>> evidenceReports = new ArrayList<>();
    List<Evidence> evidenceList = product.getEvidence();

    // double totalScore = 0;
    // int totalClaims = evidenceList == null ? 0 : evidenceList.size();

    boolean allSourcesAvailable =
            evidenceList != null && !evidenceList.isEmpty();

    if (evidenceList != null) {
        for (int i = 0; i < evidenceList.size(); i++) {

            Evidence evidence = evidenceList.get(i);

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("evidenceIndex", i);
            item.put("claim", evidence.getClaim());
            item.put("sourceName", evidence.getSourceName());
            item.put("sourceUrl", evidence.getSourceUrl());
            item.put("verified", evidence.isVerified());

            Map<String, Object> validation =
                    evidenceValidationService.validateEvidence(evidence);

            Map<String, Object> availability =
                    evidenceValidationService.checkSourceAvailability(
                            evidence.getSourceUrl()
                    );

            boolean fieldsValid =
                    Boolean.TRUE.equals(validation.get("fieldsValid"));

            boolean reachable =
                    Boolean.TRUE.equals(availability.get("reachable"));

            if (!reachable) {
                allSourcesAvailable = false;
            }

            


String sourceAvailabilityStatus;

Object httpStatusValue = availability.get("httpStatus");

Integer httpStatus = httpStatusValue instanceof Number
        ? ((Number) httpStatusValue).intValue()
        : null;

// Determine source availability independently of the review decision.
if (!fieldsValid) {
    sourceAvailabilityStatus = "INVALID";
} else if (httpStatus != null && httpStatus == 403) {
    sourceAvailabilityStatus = "ACCESS_DENIED";
} else if (httpStatus != null && httpStatus == 404) {
    sourceAvailabilityStatus = "PAGE_NOT_FOUND";
} else if (httpStatus == null || !reachable) {
    sourceAvailabilityStatus = "SOURCE_UNAVAILABLE";
} else {
    sourceAvailabilityStatus = "AVAILABLE";
}

// Read the review decision recorded for this evidence.
String reviewStatus = evidence.getReviewStatus();

if (reviewStatus == null || reviewStatus.isBlank()
        || ("PENDING_REVIEW".equals(reviewStatus)
            && evidence.isVerified())) {
    reviewStatus = evidence.isVerified()
            ? "VERIFIED"
            : "PENDING_REVIEW";
}

if ("VERIFIED".equals(reviewStatus) && !evidence.isVerified()) {
    reviewStatus = "PENDING_REVIEW";
}
item.put("validation", validation);
item.put("sourceAvailability", availability);
item.put("sourceAvailabilityStatus", sourceAvailabilityStatus);
item.put("reviewStatus", reviewStatus);
item.put("recordedVerifiedFlag", evidence.isVerified());
item.put("claimAccuracyIndependentlyConfirmed", false);


// if (fieldsValid && reachable) {
//     totalScore += 40.0 / totalClaims;

//     if (evidence.isVerified()) {
//         totalScore += 60.0 / totalClaims;
//     }
// }

evidenceReports.add(item);


    }
}

    // int confidenceScore = (int) Math.round(totalScore);
    int confidenceScore =
        evidenceService.calculateConfidenceScore(product);

    report.put(
            "productVerified",
            allSourcesAvailable && product.isVerified()
    );

    report.put(
            "lastVerifiedDate",
            allSourcesAvailable ? product.getLastVerifiedDate() : null
    );

    report.put("evidenceConfidenceScore", confidenceScore);
    report.put("evidenceReports", evidenceReports);

    List<VerificationHistory> history =
            product.getVerificationHistory();

    report.put(
            "verificationHistory",
            history == null ? new ArrayList<>() : history
    );

    return report;
}


}
