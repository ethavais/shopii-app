package com.example.shopii;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopii.adapters.ProductAdapter;
import com.example.shopii.models.Product;
import com.example.shopii.models.ProductCategory;
import com.example.shopii.repos.ProductRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** @noinspection ALL*/
public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProductRepository productRepository;
    private Spinner sortSpinner, filterSpinner;
    private List<Product> allProducts; // Danh sách chứa tất cả sản phẩm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize UI components
        RecyclerView recyclerView = findViewById(R.id.recycler_view_products);
        sortSpinner = findViewById(R.id.sort_spinner);
        filterSpinner = findViewById(R.id.filter_spinner);
        Button cartButton = findViewById(R.id.cart_button);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        // Initialize ProductRepository
        productRepository = new ProductRepository(this);

        // Fetch products from API
        fetchProducts();

        // Set up sorting and filtering options
        setupSortSpinner();
        setupFilterSpinner();

        // Set click listener for Cart button
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchProducts() {
        new Thread(() -> {
            List<Product> products = productRepository.getProducts();
            runOnUiThread(() -> {
                if (products != null) {
                    allProducts = new ArrayList<>(products); // Lưu trữ tất cả sản phẩm
                    productList.clear();
                    productList.addAll(products);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ProductListActivity", "Failed to fetch products");
                }
            });
        }).start();
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = parent.getItemAtPosition(position).toString();
                sortProducts(selectedSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupFilterSpinner() {
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                handleFilterSelection(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void handleFilterSelection(String filterOption) {
        switch (filterOption) {
            case "Electronics":
                filterProductsByCategory(ProductCategory.ELECTRONICS);
                break;
            case "Clothing":
                filterProductsByCategory(ProductCategory.CLOTHING);
                break;
            case "Books":
                filterProductsByCategory(ProductCategory.BOOKS);
                break;
            case "Home":
                filterProductsByCategory(ProductCategory.HOME);
                break;
            case "Price: Under $50":
                filterProductsByPriceRange(0, 50);
                break;
            case "Price: $50 - $100":
                filterProductsByPriceRange(50, 100);
                break;
            case "Price: Over $100":
                filterProductsByPriceRange(100, Double.MAX_VALUE);
                break;
            case "Rating: 4+":
                filterProductsByRating();
                break;
            default:
                resetProductList(); // Hiển thị lại tất cả sản phẩm
                break;
        }
    }

    private void filterProductsByCategory(ProductCategory category) {
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> product.getCategory() == category)
                .collect(Collectors.toList());
        updateProductList(filteredProducts);
    }

    private void filterProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        updateProductList(filteredProducts);
    }

    private void filterProductsByRating() {
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> product.getRating() >= (double) 4)
                .collect(Collectors.toList());
        updateProductList(filteredProducts);
    }

    private void resetProductList() {
        if (allProducts != null) {
            updateProductList(allProducts);
        } else {
            Log.e("ProductListActivity", "allProducts is null, cannot reset product list");
            // Có thể thêm thông báo cho người dùng hoặc thử fetch lại dữ liệu
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateProductList(List<Product> products) {
        if (products != null) {
            productList.clear();
            productList.addAll(products);
            productAdapter.notifyDataSetChanged();
        } else {
            Log.e("ProductListActivity", "Attempted to update product list with null data");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortProducts(String sortOption) {
        switch (sortOption) {
            case "Price: Low to High":
                productList.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "Price: High to Low":
                productList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "Rating: High to Low":
                productList.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));
                break;
            case "Popularity":
                // Implement popularity sorting logic if available
                break;
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
} 