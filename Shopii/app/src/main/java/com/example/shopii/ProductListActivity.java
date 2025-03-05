package com.example.shopii;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopii.adapters.ProductAdapter;
import com.example.shopii.models.Product;
import com.example.shopii.models.ProductCategory;
import com.example.shopii.repos.ProductRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProductRepository productRepository;
    private Spinner sortSpinner, filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view_products);
        sortSpinner = findViewById(R.id.sort_spinner);
        filterSpinner = findViewById(R.id.filter_spinner);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // Initialize ProductRepository
        productRepository = new ProductRepository(this);

        // Fetch products from API
        fetchProducts();

        // Set up sorting and filtering options
        setupSortSpinner();
        setupFilterSpinner();
    }

    private void fetchProducts() {
        new Thread(() -> {
            List<Product> products = productRepository.getProducts();
            runOnUiThread(() -> {
                if (products != null) {
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
                filterProducts(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void sortProducts(String sortOption) {
        switch (sortOption) {
            case "Price: Low to High":
                Collections.sort(productList, Comparator.comparingDouble(Product::getPrice));
                break;
            case "Price: High to Low":
                Collections.sort(productList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "Rating: High to Low":
                Collections.sort(productList, (p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));
                break;
            case "Popularity":
                // Implement popularity sorting logic if available
                break;
        }
        productAdapter.notifyDataSetChanged();
    }

    private void filterProducts(String filterOption) {
        List<Product> filteredList = new ArrayList<>();
        switch (filterOption) {
            case "All":
                filteredList.addAll(productList);
                break;
            case "Electronics":
                filteredList.addAll(filterByCategory(ProductCategory.ELECTRONICS));
                break;
            case "Clothing":
                filteredList.addAll(filterByCategory(ProductCategory.CLOTHING));
                break;
            case "Books":
                filteredList.addAll(filterByCategory(ProductCategory.BOOKS));
                break;
            case "Home":
                filteredList.addAll(filterByCategory(ProductCategory.HOME));
                break;
            case "Price: Under $50":
                filteredList.addAll(filterByPriceRange(0, 50));
                break;
            case "Price: $50 - $100":
                filteredList.addAll(filterByPriceRange(50, 100));
                break;
            case "Price: Over $100":
                filteredList.addAll(filterByPriceRange(100, Double.MAX_VALUE));
                break;
            case "Rating: 4+":
                filteredList.addAll(filterByRating(4));
                break;
        }
        productAdapter.updateList(filteredList);
    }

    private List<Product> filterByCategory(ProductCategory category) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory() == category) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }

    private List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }

    private List<Product> filterByRating(double minRating) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getRating() >= minRating) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }
} 