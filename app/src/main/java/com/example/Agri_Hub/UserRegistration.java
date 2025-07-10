package com.example.Agri_Hub;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Agri_Hub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistration extends LanguageBaseActivity {

    private EditText flnm, eml, ph_number, Dob, pwd, cpwd;
    private ProgressBar pb;
    private RadioGroup rdogrp, rdogrp1;
    private RadioButton rdobtn;
    private Button rbtn;
    private DatePickerDialog picker;
    private FirebaseAuth authProfile;
    private FirebaseFirestore firestore;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//            Objects.requireNonNull(getSupportActionBar()).setTitle("Registration");
//        }
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//        }

        Toast.makeText(UserRegistration.this, "You can register now", Toast.LENGTH_LONG).show();

        // Initialize all the UI elements
        flnm = findViewById(R.id.full_name);
        eml = findViewById(R.id.email_id);
        ph_number = findViewById(R.id.phone_number);
        Dob = findViewById(R.id.dob);
        pwd = findViewById(R.id.password);
        cpwd = findViewById(R.id.cpassword);
        pb = findViewById(R.id.progressBar);
        rbtn = findViewById(R.id.button);
        rdogrp = findViewById(R.id.for_radio_button);


        firestore = FirebaseFirestore.getInstance();

        // Setting up the date picker for Date of Birth
        Dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Date picker dialogue
                picker = new DatePickerDialog(UserRegistration.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Dob.setText(format("%d/%d/%d", dayOfMonth, month + 1, year));
                    }
                }, year, month, day);
                picker.show();
            }
        });

        // Set the Register Button OnClickListener
        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedid = rdogrp.getCheckedRadioButtonId();
                rdobtn = findViewById(checkedid);

                String textflname = flnm.getText().toString();
                String texteml = eml.getText().toString();
                String textph_number = ph_number.getText().toString();
                String textDob = Dob.getText().toString();
                String textpwd = pwd.getText().toString();
                String textcpwd = cpwd.getText().toString();
                String textGender;

                // Validate mobile number using matcher/pattern (RegEx)
                String MobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(MobileRegex);
                mobileMatcher = mobilePattern.matcher(textph_number);

                // Input validation
                if (TextUtils.isEmpty(textflname)) {
                    flnm.setError("Full name is required");
                    flnm.requestFocus();
                } else if (TextUtils.isEmpty(texteml)) {
                    eml.setError("Email is required");
                    eml.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(texteml).matches()) {
                    eml.setError("Valid email is required");
                    eml.requestFocus();
                } else if (TextUtils.isEmpty(textDob)) {
                    Dob.setError("Date of birth is required");
                    Dob.requestFocus();
                } else if (rdogrp.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(UserRegistration.this, "Please select your gender", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(textph_number)) {
                    ph_number.setError("Phone number is required");
                    ph_number.requestFocus();
                } else if (textph_number.length() != 10 || !mobileMatcher.find()) {
                    ph_number.setError("Valid phone number is required");
                    ph_number.requestFocus();
                } else if (TextUtils.isEmpty(textpwd)) {
                    pwd.setError("Password is required");
                    pwd.requestFocus();
                } else if (textpwd.length() < 6) {
                    pwd.setError("Password should be at least 6 characters");
                    pwd.requestFocus();
                } else if (TextUtils.isEmpty(textcpwd)) {
                    cpwd.setError("Confirm password is required");
                    cpwd.requestFocus();
                } else if (!textpwd.equals(textcpwd)) {
                    cpwd.setError("Passwords do not match");
                    cpwd.requestFocus();
                } else {
                    textGender = rdobtn.getText().toString();
                    pb.setVisibility(View.VISIBLE);
                    registerUser(textflname, texteml, textDob, textph_number, textpwd, textGender);
                }
            }
        });

        // Login link to redirect back to login screen
        TextView lgn = findViewById(R.id._login);
        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRegistration.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(String textflname, String texteml, String textDob, String textph_number, String textpwd, String textGender) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(texteml, textpwd).addOnCompleteListener(UserRegistration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseuser = auth.getCurrentUser();

                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(textflname).build();
                    firebaseuser.updateProfile(userProfileChangeRequest);
                    String userId = firebaseuser.getUid();


                    // Store additional user details in the firestore
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("user_name", textflname);
                    userData.put("email", texteml);
                    userData.put("Dob", textDob);
                    userData.put("Gender", textGender);
                    userData.put("ph_number", textph_number);
                    userData.put("userId",userId);

                    SharedData.getInstance().setFarmerId(userId);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Users").document(firebaseuser.getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseuser.sendEmailVerification();
                                Toast.makeText(UserRegistration.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();
                                Intent intent= new Intent(UserRegistration.this, UserLoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(UserRegistration.this, "User registration failed. Please try again", Toast.LENGTH_LONG).show();
                            }
                            pb.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {
                        pwd.setError("Your password is too weak. Kindly use a stronger password");
                        pwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        eml.setError("Your email is invalid or already in use");
                        eml.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        eml.setError("User is already registered with this email");
                        eml.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(UserRegistration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    // Creating Actionbar Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate menu items
//        getMenuInflater().inflate(R.menu.only_refresh_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    // When any menu item is selected
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        // Navigate back to the previous screen
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        // Refresh activity
//        if (id == R.id.menu_refresh) {
//            startActivity(getIntent());
//            finish();
//            overridePendingTransition(0, 0);
//        } else {
//            Toast.makeText(registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}

