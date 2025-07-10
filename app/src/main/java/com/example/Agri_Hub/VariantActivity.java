package com.example.Agri_Hub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class VariantActivity extends AppCompatActivity {
    private EditText et1,et2,et3,et4,et5;
    private Spinner vSpinner1,vSpinner2,vSpinner3,vSpinner4,vSpinner5;
    private Button goBack;
    private String selectedVariant1,selectedVariant2,selectedVariant3,selectedVariant4,selectedVariant5;
    private FirebaseFirestore db;
    private String productId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_variant);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        vSpinner1 = findViewById(R.id.vSpinner1);
        vSpinner2 = findViewById(R.id.vSpinner2);
        vSpinner3 = findViewById(R.id.vSpinner3);
        vSpinner4 = findViewById(R.id.vSpinner4);
        vSpinner5 = findViewById(R.id.vSpinner5);
        goBack = findViewById(R.id.goBack);

        //populate the items in the spinner
        ArrayAdapter<CharSequence> weight1 = ArrayAdapter.createFromResource(this,
                R.array.weights, android.R.layout.simple_spinner_item);
        weight1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinner1.setAdapter(weight1);

        ArrayAdapter<CharSequence> weight2 = ArrayAdapter.createFromResource(this,
                R.array.weights, android.R.layout.simple_spinner_item);
        weight2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinner2.setAdapter(weight2);

        ArrayAdapter<CharSequence> weight3 = ArrayAdapter.createFromResource(this,
                R.array.weights, android.R.layout.simple_spinner_item);
        weight3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinner3.setAdapter(weight3);

        ArrayAdapter<CharSequence> weight4 = ArrayAdapter.createFromResource(this,
                R.array.weights, android.R.layout.simple_spinner_item);
        weight4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinner4.setAdapter(weight4);

        ArrayAdapter<CharSequence> weight5 = ArrayAdapter.createFromResource(this,
                R.array.weights, android.R.layout.simple_spinner_item);
        weight5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinner5.setAdapter(weight5);

        // Format price input for et1
        et1.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et1.getText().toString().isEmpty()) {
                et1.setText("₹ ");
                et1.setSelection(et1.getText().length());
            }
        });
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("₹ ")) {
                    et1.setText("₹ " + s.toString().replace("₹ ", ""));
                    et1.setSelection(et1.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Format price input for et2
        et2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et2.getText().toString().isEmpty()) {
                et2.setText("₹ ");
                et2.setSelection(et2.getText().length());
            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("₹ ")) {
                    et2.setText("₹ " + s.toString().replace("₹ ", ""));
                    et2.setSelection(et2.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Format price input for et3
        et3.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et3.getText().toString().isEmpty()) {
                et3.setText("₹ ");
                et3.setSelection(et3.getText().length());
            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("₹ ")) {
                    et3.setText("₹ " + s.toString().replace("₹ ", ""));
                    et3.setSelection(et3.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Format price input for et4
        et4.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et4.getText().toString().isEmpty()) {
                et4.setText("₹ ");
                et4.setSelection(et4.getText().length());
            }
        });
        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("₹ ")) {
                    et4.setText("₹ " + s.toString().replace("₹ ", ""));
                    et4.setSelection(et4.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Format price input for et5
        et5.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et5.getText().toString().isEmpty()) {
                et5.setText("₹ ");
                et5.setSelection(et5.getText().length());
            }
        });
        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("₹ ")) {
                    et5.setText("₹ " + s.toString().replace("₹ ", ""));
                    et5.setSelection(et5.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        vSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVariant1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVariant2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVariant3 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVariant4 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVariant5 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        goBack.setOnClickListener(v -> {

            uploadVariants();

        });

    }

    private void uploadVariants() {
        String etItem1 = et1.getText().toString().trim();
        String etItem2 = et2.getText().toString().trim();
        String etItem3 = et3.getText().toString().trim();
        String etItem4 = et4.getText().toString().trim();
        String etItem5 = et5.getText().toString().trim();
        if (et1.getText().toString().trim().equals("₹") || et1.getText().toString().trim().isEmpty()) {
            et1.setError("Price required");
            et1.requestFocus();
        }
        if (et2.getText().toString().trim().equals("₹") || et2.getText().toString().trim().isEmpty()) {
            et2.setError("Price required");
            et2.requestFocus();
        }
        if (et3.getText().toString().trim().equals("₹") || et3.getText().toString().trim().isEmpty()) {
            et3.setError("Price required");
            et3.requestFocus();
        }
        if (et4.getText().toString().trim().equals("₹") || et4.getText().toString().trim().isEmpty()) {
            et4.setError("Price required");
            et4.requestFocus();
        }
        if (et5.getText().toString().trim().equals("₹") || et5.getText().toString().trim().isEmpty()) {
            et5.setError("Price required");
            et5.requestFocus();
        }



//        // Upload to Firestore
//        db.collection("ProductVariants").document(productId).set(variants)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(VariantActivity.this, "Variants uploaded successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("etItem1",etItem1);
                    resultIntent.putExtra("etItem2",etItem2);
                    resultIntent.putExtra("etItem3",etItem3);
                    resultIntent.putExtra("etItem4",etItem4);
                    resultIntent.putExtra("etItem5",etItem5);
                    resultIntent.putExtra("selectedVariant1",selectedVariant1);
                    resultIntent.putExtra("selectedVariant2",selectedVariant2);
                    resultIntent.putExtra("selectedVariant3",selectedVariant3);
                    resultIntent.putExtra("selectedVariant4",selectedVariant4);
                    resultIntent.putExtra("selectedVariant5",selectedVariant5);

                    // Set result and finish
                    setResult(RESULT_OK, resultIntent);
                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(VariantActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }


}