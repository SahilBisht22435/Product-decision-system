
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
public class CarCsvImporter {

    private final ProductRepository productRepository;

    public CarCsvImporter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Object> importCars() throws Exception {

        Set<String> existingCars = new HashSet<>();

        for (Product product : productRepository.findAll()) {
            if ("CAR".equalsIgnoreCase(product.getCategory())) {
                existingCars.add(createKey(
                        product.getBrand(), product.getName()));
            }
        }

        List<Product> productsToSave = new ArrayList<>();
        int skipped = 0;

        ClassPathResource resource =
                new ClassPathResource("datasets/car_dataset_india.csv");

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

                String brand = value(row, "Brand");
                String model = value(row, "Model");
                double price = decimalValue(row, "Price");

                if (brand.isBlank() || model.isBlank() || price <= 0) {
                    skipped++;
                    continue;
                }

                String name = (brand + " " + model).trim();
                String key = createKey(brand, name);

                if (!existingCars.add(key)) {
                    skipped++;
                    continue;
                }

                Product product = new Product();

                product.setName(name);
                product.setBrand(brand);
                product.setPrice(price);
                product.setCategory("CAR");

                product.setFuelType(value(row, "Fuel_Type"));
                product.setTransmission(value(row, "Transmission"));
                product.setMileage(decimalValue(row, "Mileage"));
                product.setSeatingCapacity(
                        integerValue(row, "Seating_Capacity"));

                product.setYear(nullableInteger(row, "Year"));
                product.setEngineCC(nullableInteger(row, "Engine_CC"));
                product.setServiceCost(
                        nullableDecimal(row, "Service_Cost"));

                product.setDataSource(
                        "GitHub dataset: car_dataset_india.csv");
                product.setEvidenceSource(
                        "Unverified dataset import: car_dataset_india.csv");
                product.setVerified(false);

                // Dataset values have not been independently verified.
                productsToSave.add(product);
            }
        }

        productRepository.saveAll(productsToSave);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("imported", productsToSave.size());
        result.put("skipped", skipped);
        result.put("message", "Car CSV import completed.");

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

    private Integer nullableInteger(CSVRecord row, String column) {
        try {
            String raw = value(row, column);
            return raw.isBlank() ? null : (int) Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private int integerValue(CSVRecord row, String column) {
        Integer result = nullableInteger(row, column);
        return result == null ? 0 : result;
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
