package com.example.Agri_Hub;

import static com.example.Agri_Hub.R.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductDetailActivity extends LanguageBaseActivity {
    String userId;
    LinearLayout buttonSection;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;
    private Button buyButton, cartButton,rateNow,goToCart;
    private String productId;
    private ImageView productImage,infoImage;
    private TextView productName, productPrice, productAvailability, productDescription;
    private TextView packUp,netQuantity,availableTv, organic, pid,type1,chemicalUsedTv,whichChemical;
    private TextView ratingCount, totalRatings, totalRatings1;
    private ProgressBar progress5, progress4, progress3, progress2, progress1;
    private TextView percent5, percent4, percent3, percent2, percent1;
    private RatingBar ratingBar, ratingBar1;
    private ScrollView scrollView;
    private Spinner qtySpinner;
    private String extractedPrice,extractedWeight;
    private TextView weight1,price1tv,weight2,price2tv,weight3,price3tv,weight4,price4tv,weight5,price5tv;
    private CardView cv1,cv2,cv3,cv4,cv5;
    private  LinearLayout l1,l2,l3,l4,l5;
    private List<LinearLayout> variantCards = new ArrayList<>();
    private LinearLayout selectedCardView = null;
    private String basePriceStr = "";
    private String selectedItem = "" ;
    private String textValueToStringPrice = "";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_product_detail);
        productId = getIntent().getStringExtra("productId");



        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Setup Toolbar
        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Product Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        // Initialize Buttons
        buyButton = findViewById(id.btnBuyNow);
        cartButton = findViewById(id.btnAddToCart);
        productImage = findViewById(id.product_image);
        infoImage = findViewById(id.info_img);
        productName = findViewById(id.product_name);
        productPrice = findViewById(id.product_price);
        productAvailability = findViewById(id.product_availability);
        productDescription = findViewById(id.productDescription);
        packUp = findViewById(id.packUp);
        netQuantity = findViewById(id.netQuantity);
        organic = findViewById(id.organic);
        pid = findViewById(id.pid);
        ratingCount = findViewById(id.rating_count);
        totalRatings = findViewById(id.total_ratings);
        totalRatings1 = findViewById(id.total_ratings1);
        ratingBar = findViewById(id.ratingBar);
        ratingBar1 = findViewById(id.ratingBar1);
        progress5 = findViewById(id.progress5);
        progress4 = findViewById(id.progress4);
        progress3 = findViewById(id.progress3);
        progress2 = findViewById(id.progress2);
        progress1 = findViewById(id.progress1);
        percent5 = findViewById(id.percent5);
        percent4 = findViewById(id.percent4);
        percent3 = findViewById(id.percent3);
        percent2 = findViewById(id.percent2);
        percent1 = findViewById(id.percent1);
        rateNow = findViewById(id.rateNow);
        type1 = findViewById(id.type);
        chemicalUsedTv = findViewById(id.chemical);
        whichChemical = findViewById(id.whichChemical);
        qtySpinner = findViewById(id.qtySpinner);
        weight1 = findViewById(id.variantWeight1);
        price1tv = findViewById(id.variantPrice1);
        weight2 = findViewById(id.variantWeight2);
        price2tv = findViewById(id.variantPrice2);
        weight3 = findViewById(id.variantWeight3);
        price3tv = findViewById(id.variantPrice3);
        weight4 = findViewById(id.variantWeight4);
        price4tv = findViewById(id.variantPrice4);
        weight5 = findViewById(id.variantWeight5);
        price5tv = findViewById(id.variantPrice5);
        availableTv = findViewById(id.available);
        cv1 = findViewById(id.variantCard1);
        cv2 = findViewById(id.variantCard2);
        cv3 = findViewById(id.variantCard3);
        cv4 = findViewById(id.variantCard4);
        cv5 = findViewById(id.variantCard5);
        l1 = findViewById(id.cardLayout1);
        l2 = findViewById(id.cardLayout2);
        l3 = findViewById(id.cardLayout3);
        l4 = findViewById(id.cardLayout4);
        l5 = findViewById(id.cardLayout5);

        //add to the list
        variantCards.add(l1);
        variantCards.add(l2);
        variantCards.add(l3);
        variantCards.add(l4);
        variantCards.add(l5);

        //convert the TV value to the string

        //get current userId
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //extract price and weight from the previous activity
        extractedPrice = getIntent().getStringExtra("price");
        extractedWeight = getIntent().getStringExtra("weight");

        //1st set the base_price_str
        basePriceStr = extractedPrice;
        setupSpinner();

        //go to cart section
        goToCart = findViewById(id.goToCart);
        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        buttonSection = findViewById(id.bottomSection);

        LinearLayout ratingsSection = findViewById(id.ratingSection);
        scrollView = findViewById(id.scrollView);

        LinearLayout ratingLayout =findViewById(id.ratingLayout);
        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

//        ratingLayout.setOnClickListener(v -> {
//            scrollView.post(() -> {
//                int[] location = new int[2];
//                ratingsSection.getLocationInWindow(location); // Get absolute position
//                scrollView.smoothScrollTo(0, location[1] - scrollView.getTop());
//            });
//        });

        // Bottom Navigation setup
        bottomNavigationView = findViewById(id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == id.nav_home) {
                    Intent intent = new Intent(ProductDetailActivity.this, UserDashboard.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    return true;
//                } else if (item.getItemId() == R.id.nav_chat) {
//                    Toast.makeText(UserProfileActivity.this, "Chat box opened", Toast.LENGTH_SHORT).show();
//                    return true;
//                }else if (item.getItemId() == R.id.nav_orders) {
//                    Toast.makeText(UserProfileActivity.this, "Order box opened", Toast.LENGTH_SHORT).show();
//                    return true;
                }
                else if(item.getItemId() == id.nav_cart){
                    startActivity(new Intent(ProductDetailActivity.this,CartActivity.class));
                    return true;
                }else if (item.getItemId() == id.nav_profile) {
                    startActivity(new Intent(ProductDetailActivity.this, UserProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        LinearLayout ratingLayout1 = findViewById(id.ratingLayout1);
        ratingLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this,RatingDetailsActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });

        rateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailActivity.this,RateNowActivity.class);
                intent.putExtra("productId",productId);
                startActivity(intent);
            }
        });

        // Set Click Listeners



        // Load specific product by ID
        if (productId != null) {
            loadProductById(productId);
        } else {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if productId is invalid
        }
    }


    private void loadProductById(String productId) {
        firestore.collection("Uploaded_products")
                .document(productId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String available = document.getString("available");
                        String img = document.getString("img");

                        //Fetch the variant
                        Map<String,Object> variantDetails = (Map<String, Object>) document.get("ProductVariant");
                        String variant1 = "";
                        String price1 = "";
                        String variant2 = "";
                        String price2 = "";
                        String variant3 = "";
                        String price3 = "";
                        String variant4 = "";
                        String price4 = "";
                        String variant5 = "";
                        String price5 = "";

                        if(variantDetails!=null){
                            variant1 = variantDetails.containsKey("selectedVariant1") ? Objects.requireNonNull(variantDetails.get("selectedVariant1")).toString() : "";
                            price1 = variantDetails.containsKey("Price1") ? Objects.requireNonNull(variantDetails.get("Price1")).toString() : "";
                            variant2 = variantDetails.containsKey("selectedVariant2") ? Objects.requireNonNull(variantDetails.get("selectedVariant2")).toString() : "";
                            price2 = variantDetails.containsKey("Price2") ? Objects.requireNonNull(variantDetails.get("Price2")).toString() : "";
                            variant3 = variantDetails.containsKey("selectedVariant3") ? Objects.requireNonNull(variantDetails.get("selectedVariant3")).toString() : "";
                            price3 = variantDetails.containsKey("Price3") ? Objects.requireNonNull(variantDetails.get("Price3")).toString() : "";
                            variant4 = variantDetails.containsKey("selectedVariant4") ? Objects.requireNonNull(variantDetails.get("selectedVariant4")).toString() : "";
                            price4 = variantDetails.containsKey("Price4") ? Objects.requireNonNull(variantDetails.get("Price4")).toString() : "";
                            variant5 = variantDetails.containsKey("selectedVariant5") ? Objects.requireNonNull(variantDetails.get("selectedVariant5")).toString() : "";
                            price5 = variantDetails.containsKey("Price5") ? Objects.requireNonNull(variantDetails.get("Price5")).toString() : "";
                        }

                        // Fetch the productDetails map
                        Map<String, Object> productDetails = (Map<String, Object>) document.get("productDetails");
                        String chemicalDetails = "";
                        String chemicalUsed = "";
                        String description = "";
                        String isOrganic ="";
                        String type ="";

                        if (productDetails != null) {
                            chemicalDetails = productDetails.containsKey("chemicalDetails") ? Objects.requireNonNull(productDetails.get("chemicalDetails")).toString() : "";
                            chemicalUsed = productDetails.containsKey("chemicalUsed") ? Objects.requireNonNull(productDetails.get("chemicalUsed")).toString() : "";
                            description = productDetails.containsKey("description") ? Objects.requireNonNull(productDetails.get("description")).toString() : "";
                            isOrganic =productDetails.containsKey("isOrganic") ? Objects.requireNonNull(productDetails.get("isOrganic")).toString() : "";
                            type =productDetails.containsKey("type") ? Objects.requireNonNull(productDetails.get("type")).toString() : "";
                        }

                        // Load rating and reviews
                        loadRatings(productId, name, available, img, chemicalDetails, chemicalUsed, description,isOrganic,type,variant1,price1,variant2,price2,variant3,price3,variant4,price4,variant5,price5);
                    } else {
                        Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity if product is not found
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load product", Toast.LENGTH_SHORT).show()
                );
    }


    private void loadRatings(String productId, String name, String available, String img,
                             String chemicalDetails, String chemicalUsed, String description,String isOrganic,String type,
                                String variant1,String price1,String variant2,String price2,String variant3,String price3,String variant4,String price4,String variant5,String price5) {
        firestore.collection("ProductRatings")
                .document(productId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    float avgRating = Objects.requireNonNullElse(snapshot.getDouble("averageRating"), 0.0).floatValue();
                    long totalRatingCount = Objects.requireNonNullElse(snapshot.getLong("totalRatings"), 0L);

                    long fiveStar = snapshot.getLong("fiveStar") != null ? snapshot.getLong("fiveStar") : 0;
                    long fourStar = snapshot.getLong("fourStar") != null ? snapshot.getLong("fourStar") : 0;
                    long threeStar = snapshot.getLong("threeStar") != null ? snapshot.getLong("threeStar") : 0;
                    long twoStar = snapshot.getLong("twoStar") != null ? snapshot.getLong("twoStar") : 0;
                    long oneStar = snapshot.getLong("oneStar") != null ? snapshot.getLong("oneStar") : 0;

                    // Calculate percentages for progress bars
                    int fiveStarPercentage = totalRatingCount > 0 ? (int) ((fiveStar * 100) / totalRatingCount) : 0;
                    int fourStarPercentage = totalRatingCount > 0 ? (int) ((fourStar * 100) / totalRatingCount) : 0;
                    int threeStarPercentage = totalRatingCount > 0 ? (int) ((threeStar * 100) / totalRatingCount) : 0;
                    int twoStarPercentage = totalRatingCount > 0 ? (int) ((twoStar * 100) / totalRatingCount) : 0;
                    int oneStarPercentage = totalRatingCount > 0 ? (int) ((oneStar * 100) / totalRatingCount) : 0;

                    //set the details
                    if (img != null && !img.isEmpty()) {
                        Picasso.get().load(img).into(productImage);
                    }

                    runOnUiThread(() -> {
                        if (netQuantity != null) {
                            netQuantity.setText(extractedWeight);
                        }else{
                            netQuantity.setText("N/A");
                        }
                        if (organic != null) {
                            organic.setText(isOrganic);
                        }
                        if (pid != null) {
                            pid.setText(productId);
                        }
                        if(type1!=null){
                            type1.setText(type);
                        }else{
                            type1.setText("N/A");
                        }if(chemicalUsedTv!=null){
                            chemicalUsedTv.setText(chemicalUsed);
                        }else{
                            chemicalUsedTv.setText("N/A");
                        }if(whichChemical!=null){
                            whichChemical.setText(chemicalDetails);
                        }else{
                            whichChemical.setText("N/A");
                        }if(availableTv!=null){
                            availableTv.setText(available);
                        }else{
                            availableTv.setText("N/A");
                        }
                    });
                    productName.setText(name);
                    productPrice.setText(extractedPrice);
                    productAvailability.setText("Available: "+available);
                    ratingCount.setText("("+String.format("%.1f", avgRating)+")");
                    ratingBar.setRating(avgRating);
                    totalRatings.setText(totalRatingCount+" ratings");
                    productDescription.setText(description);
                    ratingBar1.setRating(avgRating);
                    totalRatings1.setText(totalRatingCount+ " ratings");
                    progress5.setProgress((int) fiveStar);
                    progress4.setProgress((int) fourStar);
                    progress3.setProgress((int) threeStar);
                    progress2.setProgress((int) twoStar);
                    progress1.setProgress((int) oneStar);
                    percent5.setText(fiveStarPercentage + "%");
                    percent4.setText(fourStarPercentage + "%");
                    percent3.setText(threeStarPercentage + "%");
                    percent2.setText(twoStarPercentage + "%");
                    percent1.setText(oneStarPercentage + "%");
                    weight1.setText(variant1);
                    price1tv.setText(price1);
                    weight2.setText(variant2);
                    price2tv.setText(price2);
                    weight3.setText(variant3);
                    price3tv.setText(price3);
                    weight4.setText(variant4);
                    price4tv.setText(price4);
                    weight5.setText(variant5);
                    price5tv.setText(price5);


                    setVariantClickListeners(variant1,price1,variant2,price2,variant3,price3,variant4,price4,variant5,price5,img,name);


                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load ratings", Toast.LENGTH_SHORT).show()
                );
    }

    private void setVariantClickListeners(String variant1, String price1, String variant2, String price2,
                                          String variant3, String price3, String variant4, String price4, String variant5, String price5,String img ,String name) {
        for (int i = 0; i < variantCards.size(); i++) {
            final int index = i; // Capture index
            variantCards.get(i).setOnClickListener(v -> handleVariantSelection(variantCards.get(index), index,variant1,price1,variant2,price2,variant3,price3,variant4,price4,variant5,price5,img,name));
        }
    }

    private void handleVariantSelection(LinearLayout linearLayout, int index, String variant1, String price1,
                                        String variant2, String price2, String variant3, String price3, String variant4, String price4, String variant5, String price5,String img, String name) {
        // Reset all CardViews to default background
        for (LinearLayout l : variantCards) {
            l.setBackgroundResource(drawable.search_bg);
        }

        // Apply selected border to the clicked CardView
        linearLayout.setBackgroundResource(drawable.new_border);
        goToCart.setVisibility(View.GONE);
        buttonSection.setVisibility(View.VISIBLE);
        selectedCardView = linearLayout;

        switch (index) {
            case 0:
                extractedWeight=variant1;
                basePriceStr=price1;
                productPrice.setText(price1);
                netQuantity.setText(variant1);
                break;
            case 1:
                extractedWeight=variant2;
                basePriceStr=price2;
                productPrice.setText(price2);
                netQuantity.setText(variant2);
                break;
            case 2:
                extractedWeight=variant3;
                basePriceStr=price3;
                productPrice.setText(price3);
                netQuantity.setText(variant3);
                break;
            case 3:
                extractedWeight=variant4;
                basePriceStr=price4;
                productPrice.setText(price4);
                netQuantity.setText(variant4);
                break;
            case 4:
                extractedWeight=variant5;
                basePriceStr=price5;
                productPrice.setText(price5);
                netQuantity.setText(variant5);
                break;
        }
        updatePrice();
        buyButton.setOnClickListener(v -> handleBuyNow(img,name, textValueToStringPrice,extractedWeight));
        cartButton.setOnClickListener(v -> handleAddToCart(img,name, textValueToStringPrice,extractedWeight));
    }

    //1st convert the basePrice to double (for setting the price according to the selected quantity)
    private void updatePrice() {
        int selectedQuantity = 1;

        try {
            String selectedQuantityStr = qtySpinner.getSelectedItem().toString();
            selectedQuantity = Integer.parseInt(selectedQuantityStr);
            Log.d("DEBUG", "Selected Quantity: " + selectedQuantity);
        } catch (NumberFormatException e) {
            Log.e("ERROR", "Invalid number format", e);
        }

        if (basePriceStr.isEmpty()) {
            Log.e("ERROR", "Base price is not set!");
            return;
        }

        try {
            // Extract numeric part from base price
            Pattern pattern = Pattern.compile("[\\d.]+");
            Matcher matcher = pattern.matcher(basePriceStr);
            StringBuilder numericValue = new StringBuilder();

            while (matcher.find()) {
                numericValue.append(matcher.group());
            }

            if (numericValue.length() == 0) {
                Log.e("ERROR", "No valid price found in base price string");
                return;
            }

            double basePrice = Double.parseDouble(numericValue.toString());
            double totalPrice = basePrice * selectedQuantity;

            // Format price to two decimal places
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedPrice = "₹" + df.format(totalPrice);

            productPrice.setText(formattedPrice);
            textValueToStringPrice = productPrice.getText().toString();
        } catch (NumberFormatException e) {
            Log.e("ERROR", "Failed to parse base price", e);
        }
    }




    private void setupSpinner() {
        String[] numberArray = getResources().getStringArray(array.number_array);
        List<String> numberList = Arrays.asList(numberArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numberList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        qtySpinner.setAdapter(adapter);
        qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                packUp.setText(selectedItem);
                goToCart.setVisibility(View.GONE);
                buttonSection.setVisibility(View.VISIBLE);
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void handleAddToCart(String img,String name,String price,String extractedWeight) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", name);
        cartItem.put("price", price);
        cartItem.put("imageOfProduct", img);
        cartItem.put("userId",userId);
        cartItem.put("quantity",selectedItem);
        cartItem.put("weight",extractedWeight);
        cartItem.put("productId",productId);

        firestore.collection("Cart").document(productId).set(cartItem).addOnSuccessListener(aVoid ->{
                                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                goToCart.setVisibility(View.VISIBLE );
                                buttonSection.setVisibility(View.GONE);

                        })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to Add", Toast.LENGTH_SHORT).show());

    }

    private void handleBuyNow(String img,String name,String price,String extractedWeight) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Purchase")
                .setMessage("Are you sure you want to buy this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(ProductDetailActivity.this, AddressActivity.class);
                    intent.putExtra("img",img);
                    intent.putExtra("productName",name);
                    intent.putExtra("weight",extractedWeight);
                    intent.putExtra("individualPrice",price);
                    intent.putExtra("totalPrice",price);
                    intent.putExtra("quantity",selectedItem);
                    intent.putExtra("individualQuantities",selectedItem);
                    intent.putExtra("productId", productId);
                    intent.putExtra("source", "productDetails");
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
