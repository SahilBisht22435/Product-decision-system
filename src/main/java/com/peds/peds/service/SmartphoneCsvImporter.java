
package com.peds.peds.service;

import com.peds.peds.model.Product;
import com.peds.peds.repository.ProductRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class SmartphoneCsvImporter {

    private final ProductRepository productRepository;

    public SmartphoneCsvImporter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Object> importSmartphones() throws Exception {

        Set<String> existingProducts = new HashSet<>();

        for (Product product : productRepository.findAll()) {
            if ("SMARTPHONE".equalsIgnoreCase(product.getCategory())) {
                existingProducts.add(
                        createKey(product.getBrand(), product.getName()));
            }
        }

        List<Product> productsToSave = new ArrayList<>();
        int skipped = 0;

        ClassPathResource resource =
                new ClassPathResource("datasets/smartphones.csv");

        try (
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            resource.getInputStream(),
                            StandardCharsets.UTF_8));

            CSVParser parser = CSVParser.parse(
                    reader,
                    CSVFormat.DEFAULT.builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setAllowMissingColumnNames(true)
                            .setTrim(true)
                            .get())
        ) {

            for (CSVRecord row : parser) {

                // First column has no header and contains the brand.
                String brand = row.get(0).trim();

                String model = value(row, "model");
                double price = decimalValue(row, "price_inr");

                if (brand.isBlank() || model.isBlank() || price <= 0) {
                    skipped++;
                    continue;
                }

                String key = createKey(brand, model);

                if (!existingProducts.add(key)) {
                    skipped++;
                    continue;
                }

                Product product = new Product();

                // Common product details
                product.setName(model);
                product.setBrand(brand);
                product.setPrice(price);
                product.setCategory("SMARTPHONE");

                // Rating score (0-100 scale)
                product.setRating(
                        nullableDecimal(row, "rating_score"));

                // Processor specifications
                product.setProcessorName(
                        value(row, "processor_name"));
                product.setProcessorBrand(
                        value(row, "processor_brand"));

                product.setCoreCount(
                        nullableInteger(row, "core_count"));
                product.setClockSpeedGhz(
                        nullableDecimal(row, "clock_speed_ghz"));

                // Memory specifications
                product.setRam(integerValue(row, "ram_gb"));
                product.setStorage(integerValue(row, "storage_gb"));

                // Network and connectivity
                product.setHas5G(
                        nullableBoolean(row, "has_5g"));
                product.setHasNfc(
                        nullableBoolean(row, "has_nfc"));
                product.setHasIrBlaster(
                        nullableBoolean(row, "has_ir_blaster"));

                // Display specifications
                Double displaySize =
                        nullableDecimal(row, "display_inches");

                if (displaySize != null) {
                    product.setScreenSize(displaySize);
                    product.setDisplaySizeInch(displaySize);
                }

                product.setDisplayWidthPx(
                        nullableInteger(row, "res_width_px"));
                product.setDisplayHeightPx(
                        nullableInteger(row, "res_height_px"));

                product.setRefreshRateHz(
                        nullableInteger(row, "refresh_rate_hz"));

                // Battery and charging
                product.setBatteryMah(
                        nullableInteger(row, "battery_mah"));

                product.setFastCharging(
                        nullableBoolean(row, "fast_charging"));

                product.setChargingWatt(
                        nullableInteger(row, "charging_watt"));

                // Camera specifications
                product.setRearCameraCount(
                        nullableInteger(row, "rear_camera_count"));

                product.setFrontCameraCount(
                        nullableInteger(row, "front_camera_count"));

                product.setCameraMP(
                        nullableInteger(row, "rear_camera_main_mp"));

                product.setFrontCameraMP(
                        nullableDecimal(row, "front_camera_main_mp"));

                // Operating system
                product.setOperatingSystem(
                        value(row, "os_name"));

                // Memory card support
                product.setMemoryCardSupported(
                        nullableBoolean(row, "memory_card_supported"));

                product.setMemoryCardType(
                        value(row, "memory_card_type"));

                // Dataset information
                product.setDataSource("smartphones.csv");

                product.setEvidenceSource(
                        "Unverified dataset import: smartphones.csv");

                product.setVerified(false);

                productsToSave.add(product);
            }
        }

        productRepository.saveAll(productsToSave);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("imported", productsToSave.size());
        result.put("skipped", skipped);
        result.put("message", "Smartphone CSV import completed.");

        return result;
    }

    private String value(CSVRecord row, String column) {
        String result = row.get(column);
        return result == null ? "" : result.trim();
    }

    private double decimalValue(CSVRecord row, String column) {
        try {
            String raw = value(row, column)
                    .replace(",", "")
                    .replace("₹", "")
                    .trim();

            return raw.isBlank() ? 0 : Double.parseDouble(raw);

        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private int integerValue(CSVRecord row, String column) {
        Integer result = nullableInteger(row, column);
        return result == null ? 0 : result;
    }

    private Integer nullableInteger(CSVRecord row, String column) {
        try {
            String raw = value(row, column);

            return raw.isBlank()
                    ? null
                    : (int) Double.parseDouble(raw);

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double nullableDecimal(CSVRecord row, String column) {
        try {
            String raw = value(row, column);

            return raw.isBlank()
                    ? null
                    : Double.parseDouble(raw);

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Boolean nullableBoolean(CSVRecord row, String column) {
        String raw = value(row, column);

        if (raw.isBlank()) {
            return null;
        }

        if (raw.equalsIgnoreCase("true")) {
            return true;
        }

        if (raw.equalsIgnoreCase("false")) {
            return false;
        }

        return null;
    }

    private String createKey(String brand, String name) {

        String normalizedBrand = brand == null
                ? "" : brand.trim().toLowerCase(Locale.ROOT);

        String normalizedName = name == null
                ? "" : name.trim().toLowerCase(Locale.ROOT);

        return normalizedBrand + "|" + normalizedName;
    }
}
