package com.example.Agri_Hub;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UserDashboard extends LanguageBaseActivity {
    private static final int SPEECH_REQUEST_CODE = 1;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ProductsAdapterForUser adapter;
    private List<ProductsForUser> productList;
    private List<ProductsForUser> filteredList;
    private SearchView searchView;
    private TextView noResultsText;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileIcon,micIcon,cartIcon;
    private ImageView profileImageView;
    private TextView userNameTextView,userEmailTextView;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        View rootView = findViewById(R.id.mainContent);
        rootView.setOnTouchListener((v, event) -> {
                                 searchView.clearFocus();
            return false;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        loadImage();


        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchbar);
        noResultsText = findViewById(R.id.noResultsText);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        profileIcon = findViewById(R.id.profile_icon);
        micIcon = findViewById(R.id.mic_icon);

        micIcon.setOnClickListener(v -> startVoiceRecognition());
        cartIcon = findViewById(R.id.cart_icon);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this,CartActivity.class);
                startActivity(intent);
            }
        });


        // Access the header view of the navigation drawer to display profile info
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.nav_profile_image);
        userNameTextView = headerView.findViewById(R.id.textViewName);
        userEmailTextView  = headerView.findViewById(R.id.textViewEmailEmail);

        // Open Drawer when profile icon is clicked
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id =item.getItemId();
                Map<Integer, Class<?>> activityMap = new HashMap<>();
                activityMap.put(R.id.nav_profile, UserProfileActivity.class);

                // Get the corresponding activity class for the selected menu item
                Class<?> activityClass = activityMap.get(id);
                if (activityClass != null) {
                    Intent intent = new Intent(UserDashboard.this, activityClass);
                    startActivity(intent);
                }
                return true;
            }
        });

        // RecyclerView setup
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacing = 8;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing, includeEdge));
        recyclerView.setHasFixedSize(true);

        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ProductsAdapterForUser(this, filteredList);
        recyclerView.setAdapter(adapter);

        // Load products from Firebase
        loadProductsFromFirebase();

        // Initialize SearchView
        setupSearchView();

        // Bottom Navigation setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
        bottomNavigationView.post(() -> bottomNavigationView.setSelectedItemId(R.id.nav_home));
    }

    private void loadImage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "UserId: " + userId);

            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get profile image URL
                            String profileImageUrl = documentSnapshot.getString("profileImage");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .into(profileImageView);
                            }if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .into(profileIcon);
                            }

                            // Get and set user name
                            String userName = documentSnapshot.getString("user_name");
                            if (userNameTextView != null && userName != null) {
                                userNameTextView.setText(userName);
                            }

                            // Get and set user email
                            String userEmail = documentSnapshot.getString("email");
                            if (userEmailTextView != null && userEmail != null) {
                                userEmailTextView.setText(userEmail);
                            }

                            Log.d(TAG, "Profile Data: " + userName + ", " + userEmail);
                        } else {
                            Log.e(TAG, "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch user details: " + e.getMessage());
                        Toast.makeText(UserDashboard.this, "Failed to load profile details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "User not authenticated");
        }
    }



    // Handle bottom navigation clicks
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
//        } else if (item.getItemId() == R.id.nav_chat) {
//            Toast.makeText(this, "Chat box opened", Toast.LENGTH_SHORT).show();
//            return true;
//        }else if (item.getItemId() == R.id.nav_orders) {
//            Toast.makeText(this, "Order box opened", Toast.LENGTH_SHORT).show();
//            return true;
        }else if(item.getItemId() == R.id.nav_cart){
            startActivity(new Intent(this,CartActivity.class));
            return true;
        }
        else if (item.getItemId() == R.id.nav_home) {
            return true;
        }
        return false;
    }

    // Load products from Firestore
    private void loadProductsFromFirebase() {
        FirebaseFirestore.getInstance()
                .collection("Uploaded_products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String productName = document.getString("name");
                        String imageUrl = document.getString("img");
                        String available = document.getString("available");
                        String productId = document.getString("productId");
                        //String price = document.getString("price");
                        String farmerName=document.getString("farmerName");

                        //to fetch the variants
                        Map<String, Object> variantDetails = (Map<String, Object>) document.get("ProductVariant");
                        String price = "";
                        String weight = "";
                        if(variantDetails!=null){
                            price = variantDetails.containsKey("Price1") ? Objects.requireNonNull(variantDetails.get("Price1")).toString() : "";
                            weight = variantDetails.containsKey("selectedVariant1") ? Objects.requireNonNull(variantDetails.get("selectedVariant1")).toString() : "";
                        }


                        if (!isProductAlreadyAdded(productId)) {
                            loadProductRatings(productId, productName, imageUrl, available, price,weight,farmerName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Load product ratings separately
    private void loadProductRatings(String productId, String productName, String imageUrl, String available, String price,String weight,String farmerName) {
        FirebaseFirestore.getInstance()
                .collection("ProductRatings")
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

                    // nly add if product is not already in list
                    if (!isProductAlreadyAdded(productId)) {
                        ProductsForUser product = new ProductsForUser(productId, productName, imageUrl, price,weight, available,farmerName, rating, ratingCount);
                        productList.add(product);
                    }

                    // Update filtered list and notify adapter once all data is loaded
                    filteredList.clear();
                    filteredList.addAll(productList);
                    adapter.notifyDataSetChanged();

                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load product ratings", Toast.LENGTH_SHORT).show();
                });
    }

    // method to prevent duplicates based on productId
    private boolean isProductAlreadyAdded(String productId) {
        for (ProductsForUser product : productList) {
            if (product.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    // Setup search functionality
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    // Filter products based on search query
    private void filterProducts(String query) {
        filteredList.clear();


        if (query.isEmpty()) {
            filteredList.addAll(productList);
            loadProductsFromFirebase();
        } else {
            String searchText = query.toLowerCase();
            for (ProductsForUser product : productList) {
                if (product.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(product);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    // Update visibility of RecyclerView and "No Results" message
    private void updateUI() {
        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noResultsText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the product name");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                searchView.setQuery(spokenText, false);
                searchView.clearFocus();
                filterProducts(spokenText);
            }
        }
    }

    // Refresh products and everything  when activity resumes
    @Override
    protected void onResume() {
        searchView.clearFocus();



         //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EXIT");
            builder.setMessage("Do you want to really exit ?");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity(); // Close all activities in the stack (exit app)
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


}
