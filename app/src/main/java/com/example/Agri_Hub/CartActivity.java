package com.example.Agri_Hub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CartActivity extends LanguageBaseActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private String userId;
    private Button placeOrderBtn;
    private TextView packOf, price1, totalPrice, finalPrice;
    private ScrollView sv;
    private int quantity = 0;
    private double itemPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //full screen
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), android.graphics.PorterDuff.Mode.SRC_ATOP);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("CART");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase Setup
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setAdapter(cartAdapter);

        packOf = findViewById(R.id.quantity);
        price1 = findViewById(R.id.price);
        totalPrice = findViewById(R.id.total);
        finalPrice = findViewById(R.id.totalPrice);
        sv = findViewById(R.id.sv);
        sv.setSmoothScrollingEnabled(true);



        fetchCartItems();


        // Bottom Navigation setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
    }

    // Handle bottom navigation clicks
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(this, UserDashboard.class));
            return true;
        } else if (item.getItemId() == R.id.nav_cart) {
            return true;
        }
        return false;
    }

    private void fetchCartItems() {
        db.collection("Cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItemList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String productName = document.getString("name");
                        String imageUrl = document.getString("imageOfProduct");
                        String productId = document.getString("productId");
                        String price = document.getString("price");
                        String quantity = document.getString("quantity") ;
                        String weight = document.getString("weight");

                        if (!isProductAlreadyAdded(productId)) {
                            loadProductRatings(productId, productName, imageUrl, price, quantity,weight);
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Load product ratings separately
    private void loadProductRatings(String productId, String productName, String imageUrl, String price, String quantity,String weight) {
        db.collection("ProductRatings")
                .document(productId)
                .get()
                .addOnSuccessListener(ratingSnapshot -> {
                    float rating = 0;
                    int ratingCount = 0;

                    if (ratingSnapshot.exists()) {
                        rating = ratingSnapshot.getDouble("averageRating") != null ?
                                ratingSnapshot.getDouble("averageRating").floatValue() : 0;

                        ratingCount = ratingSnapshot.getLong("totalRatings") != null ?
                                ratingSnapshot.getLong("totalRatings").intValue() : 0;
                    }

                    // Add product to cart if not already added
                    if (!isProductAlreadyAdded(productId)) {
                        CartItem product = new CartItem(productId, productName, imageUrl, price,quantity,weight ,rating, ratingCount);
                        cartItemList.add(product);
                    }




                    cartAdapter.notifyDataSetChanged();
                    calculateCartTotals();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load product ratings", Toast.LENGTH_SHORT).show();
                });
    }



    private void calculateCartTotals() {
        int totalQuantity = 0;
        double totalAmount = 0;

        for (CartItem item : cartItemList) {
            String quantityStr = item.getQuantity();
            String priceStr = item.getPrice();
            Log.d("Quantity","Quantity= "+quantityStr);
            Log.d("Price","Price= "+priceStr);
            if (quantityStr == null || quantityStr.isEmpty()) quantityStr = "0";
            if (priceStr == null || priceStr.isEmpty()) priceStr = "0";

            Pattern pattern = Pattern.compile("[\\d.]+");
            Matcher matcher = pattern.matcher(priceStr);
            StringBuilder numericValue = new StringBuilder();

            while (matcher.find()) {
                numericValue.append(matcher.group());
            }

            if (numericValue.length() == 0) {
                Log.e("ERROR", "No valid price found in base price string");
                return;
            }



            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Log.e("CartDebug", "Invalid quantity: " + quantityStr);
            }
            try {
                itemPrice = Double.parseDouble(numericValue.toString());
            } catch (NumberFormatException e) {
                Log.e("CartDebug", "Invalid price: " + priceStr);
            }
            totalQuantity += quantity;
            totalAmount += itemPrice;
        }

        // Debugging logs to check values
        Log.d("CartDebug", "Total Quantity: " + totalQuantity);
        Log.d("CartDebug", "Total Amount: " + totalAmount);

        if (packOf != null) {
            packOf.setText(String.valueOf(totalQuantity));
        } else {
            Log.e("CartDebug", "packOf TextView is null!");
        }

        if (totalPrice != null) {
            totalPrice.setText(String.format("%.2f", totalAmount));
        } else {
            Log.e("CartDebug", "totalPrice TextView is null!");
        }

        if (finalPrice != null) {
            finalPrice.setText(String.format("%.2f", totalAmount));
        } else {
            Log.e("CartDebug", "finalPrice TextView is null!");
        }

        if (price1 != null) {
            price1.setText(String.format("%.2f", totalAmount));
        } else {
            Log.e("CartDebug", "price1 TextView is null!");
        }

        double finalTotalAmount = totalAmount;
        int finalTotalQuantity = totalQuantity;
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder(String.valueOf(finalTotalQuantity),String.format("%.2f", finalTotalAmount));
            }
        });
    }


    private void placeOrder(String quantity,String price) {
        Intent intent = new Intent(CartActivity.this, AddressActivity.class);
        ArrayList<String> productIds = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<String> weights = new ArrayList<>();
        ArrayList<String> individualPrices = new ArrayList<>();
        ArrayList<String> individualQuantities = new ArrayList<>();

        for (CartItem item : cartItemList) {
            productIds.add(item.getProductId());
            names.add(item.getName());
            imageUrls.add(item.getImageUrl());
            weights.add(item.getWeight());
            individualPrices.add(item.getPrice());
            individualQuantities.add(item.getQuantity());
        }

        Log.d("CartActivity", "Product IDs: " + productIds);
        Log.d("CartActivity", "Names: " + names);
        Log.d("CartActivity", "Prices: " + price);


        intent.putStringArrayListExtra("productIds", productIds);
        intent.putStringArrayListExtra("names", names);
        intent.putStringArrayListExtra("imageUrls", imageUrls);
        intent.putStringArrayListExtra("individualPrices",individualPrices);
        intent.putExtra("prices", price);
        intent.putStringArrayListExtra("weights", weights);
        intent.putExtra("quantities", quantity);
        intent.putStringArrayListExtra("individualQuantities",individualQuantities);
        intent.putExtra("source", "cart");


        startActivity(intent);


    }


    // method to prevent duplicates based on productId
    private boolean isProductAlreadyAdded(String productId) {
        for (CartItem product : cartItemList) {
            if (product.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
