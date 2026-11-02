
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
public class LaptopCsvImporter {

    private final ProductRepository productRepository;

    public LaptopCsvImporter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Object> importLaptops() throws Exception {

        // Prevent duplicate imports, including products already in MongoDB.
        Set<String> existingProducts = new HashSet<>();

        for (Product product : productRepository.findAll()) {
            if ("LAPTOP".equalsIgnoreCase(product.getCategory())) {
                existingProducts.add(
                        createKey(product.getBrand(), product.getName()));
            }
        }

        List<Product> productsToSave = new ArrayList<>();
        int skipped = 0;

        // The CSV is stored in src/main/resources/dataset/.
        ClassPathResource resource =
                new ClassPathResource("datasets/laptops_cleaned.csv");

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
                            .get())
        ) {
            for (CSVRecord row : parser) {

                String brand = value(row, "brand");
                String model = value(row, "model");
                double price = decimalValue(row, "price");

                // Skip records without essential catalog information.
                if (brand.isBlank() || model.isBlank() || price <= 0) {
                    skipped++;
                    continue;
                }

                String name = (brand + " " + model).trim();
                String key = createKey(brand, name);

                if (!existingProducts.add(key)) {
                    skipped++;
                    continue;
                }

                Product product = new Product();

                product.setName(name);
                product.setBrand(brand);
                product.setPrice(price);
                product.setCategory("LAPTOP");

                product.setRam(integerValue(row, "ram_gb"));
                product.setStorage(integerValue(row, "storage_gb"));

                product.setCpuBrand(value(row, "cpu_brand"));
                product.setCpuFamily(value(row, "cpu_family"));
                product.setCpuModel(value(row, "cpu_model"));
                product.setCpuCores(
                        nullableInteger(row, "cpu_core_count"));
                product.setCpuThreads(
                        nullableInteger(row, "cpu_thread_count"));

                product.setDisplayWidthPx(
                        nullableInteger(row, "display_width_px"));
                product.setDisplayHeightPx(
                        nullableInteger(row, "display_height_px"));
                product.setDisplaySizeInch(
                        nullableDecimal(row, "display_size_inch"));

                product.setGpuBrand(value(row, "gpu_brand"));
                product.setGpuModel(value(row, "gpu_model"));
                product.setGpuVramGb(
                        nullableDecimal(row, "gpu_vram_gb"));
                product.setGpuType(value(row, "gpu_type"));

                product.setOperatingSystem(value(row, "os_name"));
                product.setWarrantyYears(
                        nullableInteger(row, "warranty_years"));
                product.setRating(nullableDecimal(row, "rating"));

                product.setDataSource(
                        "GitHub: abhinavflac/laptops-specs-dataset");

                // Imported records are not independently verified.
                product.setVerified(false);
                product.setEvidenceSource(
                        "Unverified dataset import: "
                        + "abhinavflac/laptops-specs-dataset");

                // Battery hours and weight are not present in this dataset.
                // Leave both at their default value rather than inventing data.

                productsToSave.add(product);
            }
        }

        productRepository.saveAll(productsToSave);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("imported", productsToSave.size());
        result.put("skipped", skipped);
        result.put("message", "Laptop CSV import completed.");

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
            return raw.isBlank() ? null : (int) Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double nullableDecimal(CSVRecord row, String column) {
        try {
            String raw = value(row, column);
            return raw.isBlank() ? null : Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String createKey(String brand, String name) {
        String normalizedBrand = brand == null
                ? "" : brand.trim().toLowerCase(Locale.ROOT);

        String normalizedName = name == null
                ? "" : name.trim().toLowerCase(Locale.ROOT);

        return normalizedBrand + "|" + normalizedName;
    }
}
