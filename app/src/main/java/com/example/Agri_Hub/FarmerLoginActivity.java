package com.example.Agri_Hub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FarmerLoginActivity extends LanguageBaseActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, forgetPasswordButton;
    private TextView signupNow;
    private ImageView eyeIcon;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private FirebaseAuth authProfile;
    private FirebaseFirestore db;

    // SharedPreferences keys
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_ROLE = "userRole";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);

        // Initialize Firebase Auth & Firestore
        authProfile = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        forgetPasswordButton = findViewById(R.id.forget_password);
        signupNow = findViewById(R.id.signupNow);
        eyeIcon = findViewById(R.id.eyeIcon);
        progressBar = findViewById(R.id.progressbar);

        // Handle Login Button Click
        loginButton.setOnClickListener(v -> loginUser());

        // Handle Password Visibility Toggle
        eyeIcon.setOnClickListener(v -> togglePasswordVisibility());

        // Handle Signup Navigation
        signupNow.setOnClickListener(v -> {
            Intent intent = new Intent(FarmerLoginActivity.this, FarmerRegistration.class);
            startActivity(intent);
        });

        // Handle Forgot Password Action
        forgetPasswordButton =findViewById(R.id.forget_password);
        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FarmerLoginActivity.this, ForgotPasswordForFarmerActivity.class));
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eyeIcon.setImageResource(R.drawable.baseline_visibility_off_24);
        } else {
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            eyeIcon.setImageResource(R.drawable.baseline_visibility_24);
        }
        passwordEditText.setSelection(passwordEditText.length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = authProfile.getCurrentUser();
                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                    Toast.makeText(FarmerLoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Save user role and redirect
                    saveUserRoleToFirestore(firebaseUser.getUid());
                } else {
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification();
                    }
                    authProfile.signOut();
                    showEmailVerificationDialog();
                }
            } else {
                handleLoginError(task);
            }
        });
    }

    private void handleLoginError(@NonNull Task<AuthResult> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthInvalidUserException e) {
            emailEditText.setError("User doesn't exist. Please register.");
            emailEditText.requestFocus();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            passwordEditText.setError("Invalid credentials. Please try again.");
            passwordEditText.requestFocus();
        } catch (Exception e) {
            Toast.makeText(FarmerLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserRoleToFirestore(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userRole = sharedPreferences.getString(KEY_USER_ROLE, null);

        if (userRole != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userType", userRole);

            db.collection("Roles").document(userId)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        saveFarmerId(userId);

                        // Redirect based on role
                        if ("farmer".equals(userRole)) {
                            startActivity(new Intent(FarmerLoginActivity.this, FarmerDashboard.class));
                        } else if ("buyer".equals(userRole)) {
                            startActivity(new Intent(FarmerLoginActivity.this, UserDashboard.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(FarmerLoginActivity.this, "Failed to save role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User role missing", Toast.LENGTH_SHORT).show();
        }
    }

    // Save farmerId in SharedPreferences
    private void saveFarmerId(String farmerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("FarmerId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("farmerId", farmerId);
        editor.apply();

        Log.d("FarmerID", "Saved Farmer ID: " + farmerId);
    }

    private void showEmailVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FarmerLoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email before logging in.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
