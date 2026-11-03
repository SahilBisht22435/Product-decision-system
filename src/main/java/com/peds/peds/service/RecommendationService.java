
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

    // RAM: maximum 15
    Integer ram = product.getRam();

    if (ram != null && ram >= 12) {
        score += 15;
    } else if (ram != null && ram >= 8) {
        score += 12;
    } else if (ram != null && ram >= 6) {
        score += 9;
    } else if (ram != null && ram >= 4) {
        score += 6;
    }

    // Storage: maximum 15
    Integer storage = product.getStorage();

    if (storage != null && storage >= 512) {
        score += 15;
    } else if (storage != null && storage >= 256) {
        score += 12;
    } else if (storage != null && storage >= 128) {
        score += 9;
    } else if (storage != null && storage > 0) {
        score += 5;
    }

    // Rear camera: maximum 15
    Integer cameraMP = product.getCameraMP();

    if (cameraMP != null && cameraMP >= 100) {
        score += 15;
    } else if (cameraMP != null && cameraMP >= 50) {
        score += 12;
    } else if (cameraMP != null && cameraMP >= 12) {
        score += 9;
    } else if (cameraMP != null && cameraMP > 0) {
        score += 5;
    }

    // Battery: maximum 20
    Integer batteryMah = product.getBatteryMah();

    if (batteryMah != null && batteryMah >= 6000) {
        score += 20;
    } else if (batteryMah != null && batteryMah >= 5000) {
        score += 16;
    } else if (batteryMah != null && batteryMah >= 4000) {
        score += 12;
    } else if (batteryMah != null && batteryMah > 0) {
        score += 5;
    }

    // Display size: maximum 10
    Double screenSize = product.getScreenSize();

    if (screenSize != null
            && screenSize >= 6.5
            && screenSize <= 6.9) {
        score += 10;
    } else if (screenSize != null && screenSize > 0) {
        score += 7;
    }

    // User rating: maximum 25
    Double rating = product.getRating();

    if (rating != null && rating > 0) {
        score += (int) Math.round(
                Math.min(rating, 100) * 25 / 100.0);
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
