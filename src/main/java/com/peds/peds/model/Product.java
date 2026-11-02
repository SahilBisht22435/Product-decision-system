
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
    private Integer ram;
    private Integer storage;
    private double batteryHours;
    private double weight;

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
    private Integer cameraMP;
    private Integer batteryMah;
    private Double screenSize;

    private String processorName;
    private String processorBrand;
    private Integer coreCount;
    private Double clockSpeedGhz;

    private Boolean has5G;
    private Boolean hasNfc;
    private Boolean hasIrBlaster;

    private Integer refreshRateHz;
    private Integer chargingWatt;
    private Boolean fastCharging;

    private Integer rearCameraCount;
    private Integer frontCameraCount;
    private Double frontCameraMP;

    private String memoryCardType;
    private Boolean memoryCardSupported;

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
    public Integer getRam() {
    return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
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
    public Double getScreenSize() {
    return screenSize;
}

    public void setScreenSize(Double screenSize) {
        this.screenSize = screenSize;
    }

    public Integer getCameraMP() {
        return cameraMP;
    }

    public void setCameraMP(Integer cameraMP) {
        this.cameraMP = cameraMP;
    }

    public Integer getBatteryMah() {
        return batteryMah;
    }

    public void setBatteryMah(Integer batteryMah) {
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
    public String getProcessorName() { return processorName; }
    public void setProcessorName(String processorName) { this.processorName = processorName; }

    public String getProcessorBrand() { return processorBrand; }
    public void setProcessorBrand(String processorBrand) { this.processorBrand = processorBrand; }

    public Integer getCoreCount() { return coreCount; }
    public void setCoreCount(Integer coreCount) { this.coreCount = coreCount; }

    public Double getClockSpeedGhz() { return clockSpeedGhz; }
    public void setClockSpeedGhz(Double clockSpeedGhz) { this.clockSpeedGhz = clockSpeedGhz; }

    public Boolean getHas5G() { return has5G; }
    public void setHas5G(Boolean has5G) { this.has5G = has5G; }

    public Boolean getHasNfc() { return hasNfc; }
    public void setHasNfc(Boolean hasNfc) { this.hasNfc = hasNfc; }

    public Boolean getHasIrBlaster() { return hasIrBlaster; }
    public void setHasIrBlaster(Boolean hasIrBlaster) { this.hasIrBlaster = hasIrBlaster; }

    public Integer getRefreshRateHz() { return refreshRateHz; }
    public void setRefreshRateHz(Integer refreshRateHz) { this.refreshRateHz = refreshRateHz; }

    public Integer getChargingWatt() { return chargingWatt; }
    public void setChargingWatt(Integer chargingWatt) { this.chargingWatt = chargingWatt; }

    public Integer getRearCameraCount() { return rearCameraCount; }
    public void setRearCameraCount(Integer rearCameraCount) { this.rearCameraCount = rearCameraCount; }

    public Integer getFrontCameraCount() { return frontCameraCount; }
    public void setFrontCameraCount(Integer frontCameraCount) { this.frontCameraCount = frontCameraCount; }

    public Double getFrontCameraMP() { return frontCameraMP; }
    public void setFrontCameraMP(Double frontCameraMP) { this.frontCameraMP = frontCameraMP; }

    public String getMemoryCardType() { return memoryCardType; }
    public void setMemoryCardType(String memoryCardType) { this.memoryCardType = memoryCardType; }

    public Boolean getMemoryCardSupported() { return memoryCardSupported; }
    public void setMemoryCardSupported(Boolean memoryCardSupported) { this.memoryCardSupported = memoryCardSupported; }

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
    public Boolean getFastCharging() {
        return fastCharging;
    }

    public void setFastCharging(Boolean fastCharging) {
        this.fastCharging = fastCharging;
    }
}
