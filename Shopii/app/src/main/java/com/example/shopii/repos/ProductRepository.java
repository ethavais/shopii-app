package com.example.shopii.repos;

import android.content.Context;
import android.util.Log;

import com.example.shopii.R;
import com.example.shopii.config.RetrofitConfig;
import com.example.shopii.models.Product;
import com.example.shopii.models.ProductCategory;
import com.example.shopii.services.ProductService;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductRepository {
    private final ProductService productService;

    public ProductRepository(Context context) {
        // Sử dụng RetrofitConfig để lấy instance của Retrofit
        String baseUrl = context.getString(R.string.api_product_base_url);

        productService = RetrofitConfig.getRetrofitInstance(baseUrl).create(ProductService.class);
    }

    public List<Product> getProducts() {
        try {
            Call<List<Product>> call = productService.getProducts();
            Response<List<Product>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                try (ResponseBody errorBody = response.errorBody()) {
                    if (errorBody != null) {
                        Log.e("ProductRepository", "Failed to fetch products: " + errorBody.string());
                    }
                } catch (IOException e) {
                    Log.e("ProductRepository", "Error reading error body", e);
                }
            }
        } catch (Exception e) {
            Log.e("ProductRepository", "Error fetching products", e);
        }
        return null;
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        try {
            Log.d("ProductRepository", "Requesting products for category: " + category);
            Call<List<Product>> call = productService.getProductsByCategory(category);
            Response<List<Product>> response = call.execute();

            if (response.isSuccessful()) {
                Log.d("ProductRepository", "Successfully fetched products for category: " + category);
                return response.body();
            } else {
                Log.e("ProductRepository", "Failed to fetch products by category: " + response.code());
                try (ResponseBody errorBody = response.errorBody()) {
                    if (errorBody != null) {
                        Log.e("ProductRepository", "Error body: " + errorBody.string());
                    }
                } catch (IOException e) {
                    Log.e("ProductRepository", "Error reading error body", e);
                }
            }
        } catch (Exception e) {
            Log.e("ProductRepository", "Error fetching products by category", e);
        }
        return null;
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        try {
            Call<List<Product>> call = productService.getProductsByPriceRange(minPrice, maxPrice);
            Response<List<Product>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                try (ResponseBody errorBody = response.errorBody()) {
                    if (errorBody != null) {
                        Log.e("ProductRepository", "Failed to fetch products by price range: " + errorBody.string());
                    }
                } catch (IOException e) {
                    Log.e("ProductRepository", "Error reading error body", e);
                }
            }
        } catch (Exception e) {
            Log.e("ProductRepository", "Error fetching products by price range", e);
        }
        return null;
    }

    public List<Product> getProductsByRating(double minRating) {
        try {
            Call<List<Product>> call = productService.getProductsByRating(minRating);
            Response<List<Product>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                try (ResponseBody errorBody = response.errorBody()) {
                    if (errorBody != null) {
                        Log.e("ProductRepository", "Failed to fetch products by rating: " + errorBody.string());
                    }
                } catch (IOException e) {
                    Log.e("ProductRepository", "Error reading error body", e);
                }
            }
        } catch (Exception e) {
            Log.e("ProductRepository", "Error fetching products by rating", e);
        }
        return null;
    }
} 