package com.example.shopii.services;

import com.example.shopii.models.Product;
import com.example.shopii.models.ProductCategory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface ProductService {
    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products")
    Call<List<Product>> getProductsByCategory(@Query("category") ProductCategory category);

    @GET("products")
    Call<List<Product>> getProductsByPriceRange(@Query("minPrice") double minPrice, @Query("maxPrice") double maxPrice);

    @GET("products")
    Call<List<Product>> getProductsByRating(@Query("minRating") double minRating);
} 