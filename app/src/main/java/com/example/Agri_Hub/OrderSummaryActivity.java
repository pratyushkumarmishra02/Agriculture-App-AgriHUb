package com.example.Agri_Hub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OrderSummaryActivity extends LanguageBaseActivity {

    private TextView userNameText, userPhoneText, userCityText, productNameText, finalPriceText, price1, packOf, totalPrice, finalPriceOfProduct;
    private Button continueButton;
    private ArrayList<SummarySerializable> productList;
    private RecyclerView recyclerViewSummary;
    private SummaryAdapter summaryAdapter;
    private ScrollView svSummary;
    private SummarySerializable summary;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Order Summary");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize UI Elements
        userNameText = findViewById(R.id.user_name);
        userPhoneText = findViewById(R.id.user_phone);
        userCityText = findViewById(R.id.user_city);
        productNameText = findViewById(R.id.product_name);
        //finalPriceText = findViewById(R.id.final_price);
        continueButton = findViewById(R.id.continue_button);
        price1 = findViewById(R.id.price);
        packOf = findViewById(R.id.quantity);
        totalPrice = findViewById(R.id.total);
        finalPriceOfProduct = findViewById(R.id.totalPrice);

        //smooth scrolling
        svSummary = findViewById(R.id.svSummary);
        svSummary.setSmoothScrollingEnabled(true);


        productList = (ArrayList<SummarySerializable>)getIntent().getSerializableExtra("summary_list") ;
        if(productList==null){
            productList = new ArrayList<>();
        }
        //setting of recyclerview
        recyclerViewSummary = findViewById(R.id.recyclerViewOrderSummary);
        Log.d("DEBUG", "RecyclerView: " + recyclerViewSummary);
        recyclerViewSummary.setLayoutManager(new LinearLayoutManager(this));
        summaryAdapter = new SummaryAdapter(productList);
        recyclerViewSummary.setAdapter(summaryAdapter);
        summaryAdapter.notifyDataSetChanged();

        // Set values outside RecyclerView
        if (!productList.isEmpty()) {
            summary = productList.get(0);
            price1.setText(summary.getPrice());
            totalPrice.setText(summary.getPrice());
            finalPriceOfProduct.setText(summary.getPrice());
            packOf.setText(summary.getQuantity());
        }


        LinearLayout address = findViewById(R.id.address);
        address.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSummaryActivity.this, AddressActivity.class);
            intent.putExtra("isEditing", true);
            activityResultLauncher.launch(intent);
        });


        // Load initial user address details
        updateAddressFromIntent(getIntent());

        // Continue Button Click Listener
        continueButton.setOnClickListener(v -> showConfirmationDialog());
    }

    // Show Confirmation Dialog
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Order")
                .setMessage("Are you sure you want to continue with this product?")
                .setPositiveButton("Yes", (dialog, which) -> proceedToPayment())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Proceed to Payment Activity
    private void proceedToPayment() {
        Intent intent = new Intent(OrderSummaryActivity.this, PaymentActivity.class);
        intent.putExtra("TOTAL_AMOUNT", summary.getPrice());
        startActivity(intent);
    }

    // ActivityResultLauncher for AddressActivity
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    updateAddressFromIntent(result.getData());
                }
            }
    );

    // Method to update the address details
    private void updateAddressFromIntent(Intent intent) {
        String userName = intent.getStringExtra("userName");
        String userPhone = intent.getStringExtra("userPhone");
        String userHouse = intent.getStringExtra("userHouse");
        String userStreet = intent.getStringExtra("userStreet");
        String userPincode = intent.getStringExtra("userPincode");
        String userCity = intent.getStringExtra("userCity");
        String userDistrict = intent.getStringExtra("userDistrict");
        String userLandmark = intent.getStringExtra("userLandmark");
        String userState = intent.getStringExtra("userState");
        String userCountry = intent.getStringExtra("userCountry");

        userNameText.setText(userName);
        userPhoneText.setText(userPhone);
        userCityText.setText(userHouse + ", " + userStreet + ", " + userCity + ", " + userState + ", " + userCountry + " - " + userPincode + "," + userDistrict + "," + userLandmark);

    }
}
