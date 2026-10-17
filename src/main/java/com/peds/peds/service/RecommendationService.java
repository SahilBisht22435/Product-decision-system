
package com.peds.peds.service;

import com.peds.peds.model.Product;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public int calculateScore(Product product) {

        String category = product.getCategory();

        if (category == null) {
            return 0;
        }

        switch (category.toUpperCase()) {
            case "LAPTOP":
                return calculateLaptopScore(product);

            case "SMARTPHONE":
                return calculateSmartphoneScore(product);

            case "CAR":
                return calculateCarScore(product);

            default:
                return 0;
        }
    }

    // Laptop score: maximum 100
    private int calculateLaptopScore(Product product) {
        int score = 0;

        // RAM: maximum 30
        if (product.getRam() >= 16) {
            score += 30;
        } else if (product.getRam() >= 8) {
            score += 20;
        }

        // Storage: maximum 25
        if (product.getStorage() >= 1024) {
            score += 25;
        } else if (product.getStorage() >= 512) {
            score += 20;
        } else if (product.getStorage() > 0) {
            score += 10;
        }

        // Battery life: maximum 25
        if (product.getBatteryHours() >= 8) {
            score += 25;
        } else if (product.getBatteryHours() >= 6) {
            score += 20;
        } else if (product.getBatteryHours() > 0) {
            score += 10;
        }

        // Weight: maximum 20
        if (product.getWeight() > 0 && product.getWeight() <= 1.5) {
            score += 20;
        } else if (product.getWeight() > 1.5
                && product.getWeight() <= 1.8) {
            score += 15;
        } else if (product.getWeight() > 1.8) {
            score += 10;
        }

        return score;
    }

    // Smartphone score: maximum 100
    private int calculateSmartphoneScore(Product product) {
        int score = 0;

        // RAM: maximum 20
        if (product.getRam() >= 12) {
            score += 20;
        } else if (product.getRam() >= 8) {
            score += 15;
        } else if (product.getRam() >= 4) {
            score += 10;
        }

        // Storage: maximum 20
        if (product.getStorage() >= 512) {
            score += 20;
        } else if (product.getStorage() >= 256) {
            score += 15;
        } else if (product.getStorage() >= 128) {
            score += 10;
        } else if (product.getStorage() > 0) {
            score += 5;
        }

        // Camera resolution: maximum 20
        if (product.getCameraMP() >= 100) {
            score += 20;
        } else if (product.getCameraMP() >= 50) {
            score += 15;
        } else if (product.getCameraMP() >= 12) {
            score += 10;
        } else if (product.getCameraMP() > 0) {
            score += 5;
        }

        // Battery capacity: maximum 25
        if (product.getBatteryMah() >= 6000) {
            score += 25;
        } else if (product.getBatteryMah() >= 5000) {
            score += 20;
        } else if (product.getBatteryMah() >= 4000) {
            score += 15;
        } else if (product.getBatteryMah() > 0) {
            score += 5;
        }

        // Screen size: maximum 15
        if (product.getScreenSize() >= 6.5
                && product.getScreenSize() <= 6.9) {
            score += 15;
        } else if (product.getScreenSize() > 0) {
            score += 10;
        }

        return score;
    }

    // Car score: maximum 100
    private int calculateCarScore(Product product) {
        int score = 0;

        // Mileage: maximum 40
        if (product.getMileage() >= 25) {
            score += 40;
        } else if (product.getMileage() >= 20) {
            score += 30;
        } else if (product.getMileage() >= 15) {
            score += 20;
        } else if (product.getMileage() > 0) {
            score += 10;
        }

        // Seating capacity: maximum 25
        if (product.getSeatingCapacity() >= 7) {
            score += 25;
        } else if (product.getSeatingCapacity() >= 5) {
            score += 20;
        } else if (product.getSeatingCapacity() > 0) {
            score += 10;
        }

        // Fuel type: maximum 20
        if (product.getFuelType() != null) {
            String fuel = product.getFuelType().toLowerCase();

            if (fuel.equals("cng") || fuel.equals("electric")) {
                score += 20;
            } else if (fuel.equals("hybrid")) {
                score += 18;
            } else if (fuel.equals("petrol") || fuel.equals("diesel")) {
                score += 15;
            }
        }

        // Transmission: maximum 15
        if (product.getTransmission() != null) {
            String transmission =
                    product.getTransmission().toLowerCase();

            if (transmission.equals("automatic")) {
                score += 15;
            } else if (transmission.equals("manual")) {
                score += 10;
            }
        }

        return score;
    }
}
