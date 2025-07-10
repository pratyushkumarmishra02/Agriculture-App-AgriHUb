package com.example.Agri_Hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;

public class ProductDescriptionActivity extends LanguageBaseActivity {

    private TextInputEditText etDescription, etChemical;
    private RadioGroup radioGroupType, radioGroupOrganic, radioGroupChemicalUsed;
    private RadioButton radioVegetable, radioFruit, radioYes, radioNo, radioYes1, radioNo1;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Initialize Firestore

        // Initialize Views
        etDescription = findViewById(R.id.et_description);
        etChemical = findViewById(R.id.et_chemical);
        radioGroupType = findViewById(R.id.radioGroup);
        radioGroupOrganic = findViewById(R.id.radioGroup1);
        radioGroupChemicalUsed = findViewById(R.id.radioGroup2);
        radioVegetable = findViewById(R.id.radio_vegetable);
        radioFruit = findViewById(R.id.radio_fruit);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);
        radioYes1 = findViewById(R.id.radio_yes1);
        radioNo1 = findViewById(R.id.radio_no1);
        btnSubmit = findViewById(R.id.btn_submit);

        // Hide chemical input initially
        etChemical.setVisibility(View.GONE);

        // Show `et_chemical` if "Yes" is selected for "Chemical Used?"
        radioGroupChemicalUsed.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_yes1) {
                etChemical.setVisibility(View.VISIBLE);
            } else {
                etChemical.setVisibility(View.GONE);
            }
        });


        btnSubmit.setOnClickListener(v -> {
            // Collecting data
            String description = etDescription.getText().toString().trim();
            String type = radioVegetable.isChecked() ? "Vegetable" : "Fruit";
            boolean isOrganic = radioYes.isChecked();  // true if Yes is selected, false otherwise
            boolean chemicalUsed = radioYes1.isChecked();  // true if Yes is selected, false otherwise
            String chemicalDetails = etChemical.getVisibility() == View.VISIBLE ? etChemical.getText().toString().trim() : "N/A";

            // Creating intent to send back data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("description", description);
            resultIntent.putExtra("type", type);
            resultIntent.putExtra("isOrganic", isOrganic);
            resultIntent.putExtra("chemicalUsed", chemicalUsed);
            resultIntent.putExtra("chemicalDetails", chemicalDetails);

            // Set result and finish
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
