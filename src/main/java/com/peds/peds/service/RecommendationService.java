
package com.peds.peds.service;

import com.peds.peds.model.Product;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RecommendationService {

    public int calculateScore(Product product) {

        if (product == null || product.getCategory() == null) {
            return 0;
        }

        String category = product.getCategory()
                .trim()
                .toUpperCase(Locale.ROOT);

        int score;

        switch (category) {
            case "LAPTOP":
                score = calculateLaptopScore(product);
                break;

            case "SMARTPHONE":
                score = calculateSmartphoneScore(product);
                break;

            case "CAR":
                score = calculateCarScore(product);
                break;

            default:
                return 0;
        }

        return Math.max(0, Math.min(100, score));
    }

    // Laptop score: maximum 100
    private int calculateLaptopScore(Product product) {

        int score = 0;

        // RAM: maximum 30
        double ram = product.getRam();

        if (Double.isFinite(ram) && ram >= 16) {
            score += 30;
        } else if (Double.isFinite(ram) && ram >= 8) {
            score += 20;
        }

        // Storage: maximum 25
        double storage = product.getStorage();

        if (Double.isFinite(storage) && storage >= 1024) {
            score += 25;
        } else if (Double.isFinite(storage) && storage >= 512) {
            score += 20;
        } else if (Double.isFinite(storage) && storage > 0) {
            score += 10;
        }

        // Battery life: maximum 25
        double batteryHours = product.getBatteryHours();

        if (Double.isFinite(batteryHours) && batteryHours >= 8) {
            score += 25;
        } else if (Double.isFinite(batteryHours) && batteryHours >= 6) {
            score += 20;
        } else if (Double.isFinite(batteryHours) && batteryHours > 0) {
            score += 10;
        }

        // Weight: maximum 20
        double weight = product.getWeight();

        if (Double.isFinite(weight) && weight > 0 && weight <= 1.5) {
            score += 20;
        } else if (Double.isFinite(weight) && weight > 1.5 && weight <= 1.8) {
            score += 15;
        } else if (Double.isFinite(weight) && weight > 1.8) {
            score += 10;
        }

        return score;
    }

    // Smartphone score: maximum 100
    private int calculateSmartphoneScore(Product product) {

        int score = 0;

        // RAM: maximum 20
        double ram = product.getRam();

        if (Double.isFinite(ram) && ram >= 12) {
            score += 20;
        } else if (Double.isFinite(ram) && ram >= 8) {
            score += 15;
        } else if (Double.isFinite(ram) && ram >= 4) {
            score += 10;
        }

        // Storage: maximum 20
        double storage = product.getStorage();

        if (Double.isFinite(storage) && storage >= 512) {
            score += 20;
        } else if (Double.isFinite(storage) && storage >= 256) {
            score += 15;
        } else if (Double.isFinite(storage) && storage >= 128) {
            score += 10;
        } else if (Double.isFinite(storage) && storage > 0) {
            score += 5;
        }

        // Camera resolution: maximum 20
        double cameraMP = product.getCameraMP();

        if (Double.isFinite(cameraMP) && cameraMP >= 100) {
            score += 20;
        } else if (Double.isFinite(cameraMP) && cameraMP >= 50) {
            score += 15;
        } else if (Double.isFinite(cameraMP) && cameraMP >= 12) {
            score += 10;
        } else if (Double.isFinite(cameraMP) && cameraMP > 0) {
            score += 5;
        }

        // Battery capacity: maximum 25
        double batteryMah = product.getBatteryMah();

        if (Double.isFinite(batteryMah) && batteryMah >= 6000) {
            score += 25;
        } else if (Double.isFinite(batteryMah) && batteryMah >= 5000) {
            score += 20;
        } else if (Double.isFinite(batteryMah) && batteryMah >= 4000) {
            score += 15;
        } else if (Double.isFinite(batteryMah) && batteryMah > 0) {
            score += 5;
        }

        // Screen size: maximum 15
        double screenSize = product.getScreenSize();

        if (Double.isFinite(screenSize)
                && screenSize >= 6.5
                && screenSize <= 6.9) {
            score += 15;
        } else if (Double.isFinite(screenSize) && screenSize > 0) {
            score += 10;
        }

        return score;
    }

    // Car score: maximum 100
    private int calculateCarScore(Product product) {

        int score = 0;

        // Mileage: maximum 40
        double mileage = product.getMileage();

        if (Double.isFinite(mileage) && mileage >= 25) {
            score += 40;
        } else if (Double.isFinite(mileage) && mileage >= 20) {
            score += 30;
        } else if (Double.isFinite(mileage) && mileage >= 15) {
            score += 20;
        } else if (Double.isFinite(mileage) && mileage > 0) {
            score += 10;
        }

        // Seating capacity: maximum 25
        double seatingCapacity = product.getSeatingCapacity();

        if (Double.isFinite(seatingCapacity) && seatingCapacity >= 7) {
            score += 25;
        } else if (Double.isFinite(seatingCapacity) && seatingCapacity >= 5) {
            score += 20;
        } else if (Double.isFinite(seatingCapacity) && seatingCapacity > 0) {
            score += 10;
        }

        // Fuel type: maximum 20
        String fuelType = product.getFuelType();

        if (fuelType != null) {
            String fuel = fuelType.trim().toLowerCase(Locale.ROOT);

            switch (fuel) {
                case "cng":
                case "electric":
                    score += 20;
                    break;

                case "hybrid":
                    score += 18;
                    break;

                case "petrol":
                case "diesel":
                    score += 15;
                    break;

                default:
                    break;
            }
        }

        // Transmission: maximum 15
        String transmission = product.getTransmission();

        if (transmission != null) {
            String type = transmission.trim().toLowerCase(Locale.ROOT);

            if ("automatic".equals(type)) {
                score += 15;
            } else if ("manual".equals(type)) {
                score += 10;
            }
        }

        return score;
    }
}
