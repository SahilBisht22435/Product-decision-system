
package com.peds.peds.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    // Evidence and verification details
    private String evidenceSource;
    private boolean verified;
    private String lastVerifiedDate;

    private List<Evidence> evidence = new ArrayList<>();
    private List<VerificationHistory> verificationHistory =
            new ArrayList<>();

    // Common product details
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Category cannot be blank")
    @Pattern(
            regexp = "(?i)\\s*(LAPTOP|SMARTPHONE|CAR)\\s*",
            message = "Category must be LAPTOP, SMARTPHONE, or CAR"
    )
    private String category = "LAPTOP";

    // Laptop specifications
    
    // Laptop specifications
    private int ram;
    private int storage;
    private double batteryHours;
    private double weight;

    // Additional laptop specifications from imported datasets
    private String cpuBrand;
    private String cpuFamily;
    private String cpuModel;
    private Integer cpuCores;
    private Integer cpuThreads;

    private Integer displayWidthPx;
    private Integer displayHeightPx;
    private Double displaySizeInch;

    private String gpuBrand;
    private String gpuModel;
    private Double gpuVramGb;
    private String gpuType;

    private String operatingSystem;
    private Integer warrantyYears;
    private Double rating;

    // Dataset provenance
    private String dataSource;


    // Smartphone specifications
    private double screenSize;
    private int cameraMP;
    private int batteryMah;

    // Car specifications
   // Car specifications
    private String fuelType;
    private String transmission;
    private double mileage;
    private int seatingCapacity;
    private Integer year;
    private Integer engineCC;
    private Double serviceCost;
    // Default constructor required for data binding
    public Product() {
    }

    // Original constructor retained for compatibility
    public Product(
            String name,
            String brand,
            double price,
            int ram,
            int storage,
            double batteryHours,
            double weight) {

        this.name = name;
        this.brand = brand;
        this.price = price;
        this.ram = ram;
        this.storage = storage;
        this.batteryHours = batteryHours;
        this.weight = weight;
    }

    // Constructor with category retained for compatibility
    public Product(
            String name,
            String brand,
            double price,
            int ram,
            int storage,
            double batteryHours,
            double weight,
            String category) {

        this(name, brand, price, ram, storage, batteryHours, weight);
        setCategory(category);
    }

    // Evidence source
    public String getEvidenceSource() {
        return evidenceSource;
    }

    public void setEvidenceSource(String evidenceSource) {
        this.evidenceSource = evidenceSource;
    }

    // Evidence collection
    public List<Evidence> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<Evidence> evidence) {
        this.evidence = evidence == null
                ? new ArrayList<>()
                : new ArrayList<>(evidence);
    }

    // Verification history
    public List<VerificationHistory> getVerificationHistory() {
        return verificationHistory;
    }

    public void setVerificationHistory(
            List<VerificationHistory> verificationHistory) {

        this.verificationHistory = verificationHistory == null
                ? new ArrayList<>()
                : new ArrayList<>(verificationHistory);
    }

    // Product verification status
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLastVerifiedDate() {
        return lastVerifiedDate;
    }

    public void setLastVerifiedDate(String lastVerifiedDate) {
        this.lastVerifiedDate = lastVerifiedDate;
    }

    // ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Common product details
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {

        if (category == null || category.isBlank()) {
            this.category = category;
            return;
        }

        this.category = category.trim().toUpperCase(Locale.ROOT);
    }

    // Laptop specifications
    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public double getBatteryHours() {
        return batteryHours;
    }

    public void setBatteryHours(double batteryHours) {
        this.batteryHours = batteryHours;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Smartphone specifications
    public double getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(double screenSize) {
        this.screenSize = screenSize;
    }

    public int getCameraMP() {
        return cameraMP;
    }

    public void setCameraMP(int cameraMP) {
        this.cameraMP = cameraMP;
    }

    public int getBatteryMah() {
        return batteryMah;
    }

    public void setBatteryMah(int batteryMah) {
        this.batteryMah = batteryMah;
    }

    // Car specifications
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }
    public Integer getYear() {
    return year;
}

public void setYear(Integer year) {
    this.year = year;
}

public Integer getEngineCC() {
    return engineCC;
}

public void setEngineCC(Integer engineCC) {
    this.engineCC = engineCC;
}

public Double getServiceCost() {
    return serviceCost;
}

public void setServiceCost(Double serviceCost) {
    this.serviceCost = serviceCost;
}
    
    // Additional laptop specifications
    public String getCpuBrand() { return cpuBrand; }
    public void setCpuBrand(String cpuBrand) { this.cpuBrand = cpuBrand; }

    public String getCpuFamily() { return cpuFamily; }
    public void setCpuFamily(String cpuFamily) { this.cpuFamily = cpuFamily; }

    public String getCpuModel() { return cpuModel; }
    public void setCpuModel(String cpuModel) { this.cpuModel = cpuModel; }

    public Integer getCpuCores() { return cpuCores; }
    public void setCpuCores(Integer cpuCores) { this.cpuCores = cpuCores; }

    public Integer getCpuThreads() { return cpuThreads; }
    public void setCpuThreads(Integer cpuThreads) { this.cpuThreads = cpuThreads; }

    public Integer getDisplayWidthPx() { return displayWidthPx; }
    public void setDisplayWidthPx(Integer displayWidthPx) { this.displayWidthPx = displayWidthPx; }

    public Integer getDisplayHeightPx() { return displayHeightPx; }
    public void setDisplayHeightPx(Integer displayHeightPx) { this.displayHeightPx = displayHeightPx; }

    public Double getDisplaySizeInch() { return displaySizeInch; }
    public void setDisplaySizeInch(Double displaySizeInch) { this.displaySizeInch = displaySizeInch; }

    public String getGpuBrand() { return gpuBrand; }
    public void setGpuBrand(String gpuBrand) { this.gpuBrand = gpuBrand; }

    public String getGpuModel() { return gpuModel; }
    public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }

    public Double getGpuVramGb() { return gpuVramGb; }
    public void setGpuVramGb(Double gpuVramGb) { this.gpuVramGb = gpuVramGb; }

    public String getGpuType() { return gpuType; }
    public void setGpuType(String gpuType) { this.gpuType = gpuType; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public Integer getWarrantyYears() { return warrantyYears; }
    public void setWarrantyYears(Integer warrantyYears) { this.warrantyYears = warrantyYears; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

}
