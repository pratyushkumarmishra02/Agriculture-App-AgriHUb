package com.example.Agri_Hub;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProduceActivity extends LanguageBaseActivity {

    private EditText editTextProductAvailable;
    private ImageView imageViewProduct;
    private Button btnUpload,buttonForVariant;
    private Spinner spinnerProductName;
    private FirebaseFirestore db;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private String uploadedImageUrl = "";
    private CollectionReference productCollection;
    private static final int IMAGE_UPLOAD_REQUEST = 1;
    private static final int DESCRIPTION_REQUEST = 2;
    private static final int VARIANT_REQUEST = 3;
    private Map<String, Object> productDetailsMap = new HashMap<>();
    private Map<String,Object> variantPriceMap = new HashMap<>();
    TextView productDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_produce);

        // Initialize Firebase
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        productCollection = db.collection("Uploaded_products");

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Upload and Produce");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Initialize UI Elements
        spinnerProductName = findViewById(R.id.spinner);
        editTextProductAvailable = findViewById(R.id.editTextProductAvailable);
        imageViewProduct = findViewById(R.id.image_view_profile);
        btnUpload = findViewById(R.id.btnUpload);


        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Setup Spinner
        setupSpinner();

        // Handle Image Upload
        imageViewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(UploadProduceActivity.this, UploadImageActivity.class);
            intent.putExtra("callerClass", "UploadProduceActivity");
            startActivityForResult(intent, IMAGE_UPLOAD_REQUEST);
        });

        // Handle Upload Button Click
        btnUpload.setOnClickListener(v -> uploadProduct());

        // Format product availability input
        editTextProductAvailable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().endsWith(" Kg")) {
                    editTextProductAvailable.setText(s.toString().replace(" Kg", "") + " Kg");
                    editTextProductAvailable.setSelection(editTextProductAvailable.getText().length() - 3);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(UploadProduceActivity.this, UserProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                Toast.makeText(UploadProduceActivity.this, "Chat opened", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation);

        productDetails =findViewById(R.id.productDetails);
        productDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(UploadProduceActivity.this,ProductDescriptionActivity.class);
                startActivityForResult(intent,DESCRIPTION_REQUEST);
            }
        });

        buttonForVariant = findViewById(R.id.buttonForVariant);
        buttonForVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(UploadProduceActivity.this,VariantActivity.class);
                startActivityForResult(intent,VARIANT_REQUEST);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinner() {
        String[] vegetableArray = getResources().getStringArray(R.array.vegitable);
        List<String> vegetableList = Arrays.asList(vegetableArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vegetableList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProductName.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_UPLOAD_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadedImageUrl = data.getStringExtra("uploadedImageUrl");

            if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()) {
                Picasso.get().load(uploadedImageUrl).into(imageViewProduct);
            }
        }else if (requestCode == DESCRIPTION_REQUEST && resultCode == RESULT_OK && data != null) {
            // Receiving description details
            String productDescription = data.getStringExtra("description");
            String type = data.getStringExtra("type");
            boolean isOrganic = data.getBooleanExtra("isOrganic", false);
            boolean chemicalUsed = data.getBooleanExtra("chemicalUsed", false);
            String chemicalDetails = data.getStringExtra("chemicalDetails");

            // Store details in a map
            productDetailsMap.put("productDetails", new HashMap<String, Object>() {{
                put("description", productDescription);
                put("type", type);
                put("isOrganic", isOrganic ? "Yes" : "No");
                put("chemicalUsed", chemicalUsed ? "Yes" : "No");
                put("chemicalDetails", chemicalDetails);
            }});
            Log.d("UploadProduceActivity", "Product Details Updated: " + productDetailsMap.toString());
        }else if(requestCode == VARIANT_REQUEST && resultCode == RESULT_OK && data != null){
            String etItem1 = data.getStringExtra("etItem1");
            String etItem2 = data.getStringExtra("etItem2");
            String etItem3 = data.getStringExtra("etItem3");
            String etItem4 = data.getStringExtra("etItem4");
            String etItem5 = data.getStringExtra("etItem5");
            String selectedVariant1 = data.getStringExtra("selectedVariant1");
            String selectedVariant2 = data.getStringExtra("selectedVariant2");
            String selectedVariant3 = data.getStringExtra("selectedVariant3");
            String selectedVariant4 = data.getStringExtra("selectedVariant4");
            String selectedVariant5 = data.getStringExtra("selectedVariant5");
            variantPriceMap.put("ProductVariant",new HashMap<String,Object>(){{
                put("selectedVariant1",selectedVariant1);
                put("Price1",etItem1);
                put("selectedVariant2",selectedVariant2);
                put("Price2",etItem2);
                put("selectedVariant3",selectedVariant3);
                put("Price3",etItem3);
                put("selectedVariant4",selectedVariant4);
                put("Price4",etItem4);
                put("selectedVariant5",selectedVariant5);
                put("Price5",etItem5);
            }});
            Log.d("UploadProduceActivity", "Variants Details Updated: " + variantPriceMap.toString());
        }
    }

    private void uploadProduct() {
        String productName = spinnerProductName.getSelectedItem().toString();
        String productAvailable = editTextProductAvailable.getText().toString();
        String farmerId = getFarmerIdFromSharedPreferences();
        String farmerName=firebaseUser.getDisplayName();

        if (TextUtils.isEmpty(productName) || productName.equals("Select a product")) {
            Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productAvailable)) {
            editTextProductAvailable.setError("Enter available quantity");
            return;
        }
        if (uploadedImageUrl == null || uploadedImageUrl.isEmpty()) {
            Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show();
            return;
        }if (productDetailsMap.isEmpty() || !productDetailsMap.containsKey("productDetails")) {
            productDetails.setError("Please provide product details before uploading");
            return;
        }if(variantPriceMap.isEmpty() || !variantPriceMap.containsKey("ProductVariant")){
            buttonForVariant.setError("Please choose the variants");
            return;
        }

        progressDialog.show();

        // Create a unique ID based on farmerId + productName to avoid duplication
        String productId = FirebaseFirestore.getInstance().collection("Uploaded_products").document().getId();
        productId = productId + "_" + productName;
        DocumentReference productRef = productCollection.document(productId);
        Map<String, Object> product = new HashMap<>();
        product.put("productId", productId);
        product.put("name", productName);
        product.put("available", productAvailable);
        product.put("img", uploadedImageUrl);
        product.put("timestamp", System.currentTimeMillis());
        product.put("farmerId", farmerId);
        product.put("farmerName",farmerName);
        // Add description details
        product.put("productDetails", productDetailsMap.get("productDetails"));
        product.put("ProductVariant",variantPriceMap.get("ProductVariant"));

        // First check if the document exists
        productRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // If it exists, update the existing product data
                        productRef.update(product)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadProduceActivity.this, "Product Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UploadProduceActivity.this, FarmerDashboard.class));
                                    clearFields();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadProduceActivity.this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If it doesn’t exist, create a new product record
                        productRef.set(product)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadProduceActivity.this, "Product Uploaded", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UploadProduceActivity.this, FarmerDashboard.class));
                                    clearFields();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadProduceActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private String getFarmerIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("FarmerId", MODE_PRIVATE);
        return sharedPreferences.getString("farmerId", "");
    }

    private void clearFields() {
        editTextProductAvailable.setText("");
        uploadedImageUrl = "";
        imageViewProduct.setImageResource(R.drawable.no_profile_pic);
        spinnerProductName.setSelection(0);
    }
}
