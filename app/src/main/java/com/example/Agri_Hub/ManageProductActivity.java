package com.example.Agri_Hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageProductActivity extends LanguageBaseActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_product);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Products");
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEditClick(Product product) {
                Toast.makeText(ManageProductActivity.this, "Edit " + product.getProductName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(Product product) {
                Toast.makeText(ManageProductActivity.this, "Clicked on " + product.getProductName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(productAdapter);

        // Fetch Data
        fetchProducts();

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(ManageProductActivity.this, UserProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_chat) {
                Toast.makeText(ManageProductActivity.this, "Chat box opened", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
    }

    private void fetchProducts() {
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String farmerUid=currentUser.getUid();
        CollectionReference productsRef = db.collection("Uploaded_products");

        productsRef.whereEqualTo("farmerId", farmerUid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String productName = document.getString("name");
                    String imageUrl = document.getString("img");
                    String available = document.getString("available");
                    String farmerId = document.getString("farmerId");
                    String price =document.getString("price");

                    Product product = new Product(productName, imageUrl, available, farmerId,price);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}