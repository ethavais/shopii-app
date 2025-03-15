package com.example.shopii.repos;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.shopii.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_KEY = "cart_items";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public CartRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addToCart(CartItem cartItem) {
        List<CartItem> cartItems = getCartItems();
        cartItems.add(cartItem);
        saveCartItems(cartItems);
    }

    public List<CartItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void removeFromCart(CartItem cartItem) {
        List<CartItem> cartItems = getCartItems();
        cartItems.remove(cartItem);
        saveCartItems(cartItems);
    }

    public void clearCart() {
        sharedPreferences.edit().remove(CART_ITEMS_KEY).apply();
    }

    private void saveCartItems(List<CartItem> cartItems) {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply();
    }
} 