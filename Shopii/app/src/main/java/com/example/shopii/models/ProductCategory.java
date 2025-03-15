package com.example.shopii.models;

public enum ProductCategory {
    ELECTRONICS(0),
    CLOTHING(1),
    BOOKS(2),
    HOME(3),
    OTHER(4);

    private final int value;

    ProductCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProductCategory fromValue(int value) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.value == value) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid ProductCategory value: " + value);
    }
} 