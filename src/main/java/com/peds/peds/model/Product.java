
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
    private int ram;
    private int storage;
    private double batteryHours;
    private double weight;

    // Smartphone specifications
    private double screenSize;
    private int cameraMP;
    private int batteryMah;

    // Car specifications
    private String fuelType;
    private String transmission;
    private double mileage;
    private int seatingCapacity;

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
}
