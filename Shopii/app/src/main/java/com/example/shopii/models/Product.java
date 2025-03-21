package com.example.shopii.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import android.util.Log;

public class Product implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private double price;
    private List<String> imageUrls;
    private String brand;
    private int category;
    private double rating;
    private int stockQuantity;

    // Constructors
    public Product() {
        this.id = UUID.randomUUID();
    }

    public Product(String name, String description, double price, List<String> imageUrls, String brand, ProductCategory category, double rating, int stockQuantity) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.brand = brand;
        this.category = category.getValue();
        this.rating = rating;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public ProductCategory getCategory() {
        Log.d("Product", "Converting category value: " + category + " to enum");
        return ProductCategory.fromValue(category);
    }

    public void setCategory(ProductCategory category) {
        this.category = category.getValue();
    }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 