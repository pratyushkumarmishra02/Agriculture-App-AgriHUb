package com.example.Agri_Hub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressActivity extends  LanguageBaseActivity{

    private EditText nameInput, phoneInput, houseInput, streetInput, pincodeInput, cityInput, districtInput, landmarkInput;
    private Spinner spinnerState, spinnerCountry;
    private Button proceedButton,returnButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String userId;
    private ArrayList<SummarySerializable> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get Current User ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up Toolbar with Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Address");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize Views
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        houseInput = findViewById(R.id.houseInput);
        streetInput = findViewById(R.id.streetInput);
        pincodeInput = findViewById(R.id.pincodeInput);
        cityInput = findViewById(R.id.cityInput);
        districtInput = findViewById(R.id.districtInput);
        landmarkInput = findViewById(R.id.landmarkInput);
        spinnerState = findViewById(R.id.spinner_state);
        spinnerCountry = findViewById(R.id.spinner_country);
        spinnerCountry.setEnabled(false);
        proceedButton = findViewById(R.id.proceedToCheckoutButton);
        progressBar = findViewById(R.id.progressBar);
        returnButton = findViewById(R.id.returnToOrderSummary);

        //initializing
        productList = new ArrayList<>();

        // Populate Country Spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.country_list, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        // Populate State Spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.state_list, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);

        // Check if the user already has an address
        checkExistingAddress();


        boolean isEditing = getIntent().getBooleanExtra("isEditing", false);

        if (isEditing) {
            // Hide Continue button and show Return button
            proceedButton.setVisibility(View.GONE);
            returnButton.setVisibility(View.VISIBLE);
        } else {
            // Show Continue button and hide Return button
            proceedButton.setVisibility(View.VISIBLE);
            returnButton.setVisibility(View.GONE);
        }

        // Continue Button: Save address and go to OrderSummaryActivity
        proceedButton.setOnClickListener(v -> {
            saveAddressToFirestore();
        });

        // Return Button: Save address and return to OrderSummaryActivity
        returnButton.setOnClickListener(v -> {
            saveAddressAndReturn();
        });
    }



    private void checkExistingAddress() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("UserAddresses").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        // Fill the fields with existing address
                        nameInput.setText(documentSnapshot.getString("name"));
                        phoneInput.setText(documentSnapshot.getString("phone"));
                        houseInput.setText(documentSnapshot.getString("house"));
                        streetInput.setText(documentSnapshot.getString("village"));
                        pincodeInput.setText(documentSnapshot.getString("pincode"));
                        cityInput.setText(documentSnapshot.getString("city"));
                        districtInput.setText(documentSnapshot.getString("district"));
                        landmarkInput.setText(documentSnapshot.getString("landmark"));

                        // Set Spinner values
                        String savedState = documentSnapshot.getString("state");
                        String savedCountry = documentSnapshot.getString("country");

                        if (savedState != null) {
                            int statePosition = ((ArrayAdapter<String>) spinnerState.getAdapter()).getPosition(savedState);
                            spinnerState.setSelection(statePosition);
                        }

                        if (savedCountry != null) {
                            int countryPosition = ((ArrayAdapter<String>) spinnerCountry.getAdapter()).getPosition(savedCountry);
                            spinnerCountry.setSelection(countryPosition);
                        }

                        // Ensure this is called on the UI thread
                        runOnUiThread(this::checkProductDetailsAndShowDialog);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddressActivity.this, "Failed to check address!", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkProductDetailsAndShowDialog() {
        String source = getIntent().getStringExtra("source");
        if ("cart".equals(source)) {
            // Extract Multiple Products Data
            ArrayList<String> productIds = getIntent().getStringArrayListExtra("productIds");
            ArrayList<String> names = getIntent().getStringArrayListExtra("names");
            ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("imageUrls");
            ArrayList<String> weights = getIntent().getStringArrayListExtra("weights");
            ArrayList<String> individualPrices = getIntent().getStringArrayListExtra("individualPrices");
            ArrayList<String> individualQuantities = getIntent().getStringArrayListExtra("individualQuantities");
            String price = getIntent().getStringExtra("prices");
            String quantity = getIntent().getStringExtra("quantities");
            Log.d("DEBUG", "price: " + price+ " quantity " +quantity);
            if (productIds != null && !productIds.isEmpty() && names.size() == productIds.size() &&
                    imageUrls != null && imageUrls.size() == productIds.size() && weights != null && weights.size() == productIds.size() && price != null && quantity != null) {
                for (int i = 0; i < productIds.size(); i++) {
                                    SummarySerializable summary = new SummarySerializable(
                                            imageUrls.get(i),
                                            names.get(i),
                                            individualPrices.get(i),
                                            price,
                                            productIds.get(i),
                                            weights.get(i),
                                            quantity,
                                            individualQuantities.get(i)
                                    );

                                    Log.d("DEBUG", "Adding product: " + summary.getProductName() + " - " + summary.getIndividualPrice());
                                    Log.d("DEBUG", "image: " + imageUrls );
                                    Log.d("DEBUG", "name: " + names );
                                    Log.d("DEBUG", "price: " + price );
                                    Log.d("DEBUG", "productId: " + productIds );
                                    Log.d("DEBUG", "weight: " + weights );
                                    Log.d("DEBUG", "quantity: " + quantity );
                                    productList.add(summary);
                }
            } else {
                Log.e("ERROR", "One or more input lists are null, empty, or their sizes do not match.");
            }
        } else if ("productDetails".equals(source)) {
            // Extract Single Product Data
            String productId = getIntent().getStringExtra("productId");
            String name = getIntent().getStringExtra("productName");
            String imageUrl = getIntent().getStringExtra("img");
            String price = getIntent().getStringExtra("individualPrice");
            String totalPrice = getIntent().getStringExtra("totalPrice");
            String weight = getIntent().getStringExtra("weight");
            String quantity = getIntent().getStringExtra("quantity");
            String individualQuantities = getIntent().getStringExtra("individualQuantities");

            if (productId != null && name != null && price != null && imageUrl != null && weight != null && quantity != null) {
                SummarySerializable summary = new SummarySerializable(imageUrl, name, price,totalPrice, productId, weight, quantity,individualQuantities);
                productList.add(summary);
            } else {
                Log.e("ERROR", "One or more product details are null.");
                return;
            }
        }

        // Now `productList` contains all products, whether from cart or product details
        if (!productList.isEmpty()) {
            runOnUiThread(this::showChangeAddressDialog);
        }
    }


    private void showChangeAddressDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Change Address?")
                .setMessage("You already have an address saved. Do you want to update it?")
                .setPositiveButton("Yes", (dialog, which) -> clearAddressFields())
                .setNegativeButton("No", (dialog, which) -> proceedToOrderSummary())
                .setCancelable(false)
                .show();
    }

    private void clearAddressFields() {
        nameInput.setText("");
        phoneInput.setText("");
        houseInput.setText("");
        streetInput.setText("");
        pincodeInput.setText("");
        cityInput.setText("");
        districtInput.setText("");
        landmarkInput.setText("");
        spinnerState.setSelection(0);
        spinnerCountry.setSelection(0);
    }

    private void proceedToOrderSummary() {
        Intent intent = new Intent(AddressActivity.this, OrderSummaryActivity.class);
        intent.putExtra("summary_list", productList);
        passUserAddressData(intent);
        startActivity(intent);
        finish();
    }

    private void saveAddressToFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> address = new HashMap<>();
        address.put("name", nameInput.getText().toString().trim());
        address.put("phone", phoneInput.getText().toString().trim());
        address.put("house", houseInput.getText().toString().trim());
        address.put("village", streetInput.getText().toString().trim());
        address.put("pincode", pincodeInput.getText().toString().trim());
        address.put("city", cityInput.getText().toString().trim());
        address.put("district", districtInput.getText().toString().trim());
        address.put("landmark", landmarkInput.getText().toString().trim());
        address.put("state", spinnerState.getSelectedItem().toString());
        address.put("country", spinnerCountry.getSelectedItem().toString());

        db.collection("UserAddresses").document(userId)
                .set(address)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddressActivity.this, "Address Saved!", Toast.LENGTH_SHORT).show();
                    saveAddressAndProceed();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddressActivity.this, "Failed to save address!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAddressAndProceed() {
        Intent intent = new Intent(AddressActivity.this, OrderSummaryActivity.class);
        intent.putExtra("summary_list", productList);
        passUserAddressData(intent);
        startActivity(intent);
        finish();
    }

    private void saveAddressAndReturn() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("summary_list", productList);
        passUserAddressData(resultIntent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void passUserAddressData(Intent intent) {
        intent.putExtra("userName", nameInput.getText().toString().trim());
        intent.putExtra("userPhone", phoneInput.getText().toString().trim());
        intent.putExtra("userHouse", houseInput.getText().toString().trim());
        intent.putExtra("userStreet", streetInput.getText().toString().trim());
        intent.putExtra("userPincode", pincodeInput.getText().toString().trim());
        intent.putExtra("userCity", cityInput.getText().toString().trim());
        intent.putExtra("userDistrict", districtInput.getText().toString().trim());
        intent.putExtra("userLandmark", landmarkInput.getText().toString().trim());
        intent.putExtra("userState", spinnerState.getSelectedItem().toString());
        intent.putExtra("userCountry", spinnerCountry.getSelectedItem().toString());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
