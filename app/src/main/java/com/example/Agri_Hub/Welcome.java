package com.example.Agri_Hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Welcome extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";
    private static final String KEY_USER_ROLE = "userRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            boolean isFirstTime = sharedPreferences.getBoolean(KEY_FIRST_TIME, true);
            FirebaseUser currentUser = auth.getCurrentUser();

            if (isFirstTime) {
                // If user is opening the app for the first time, go to WelcomeActivity
                startActivity(new Intent(Welcome.this, MainActivity.class));

                // Set first-time flag to false
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_FIRST_TIME, false);
                editor.apply();
            } else if (currentUser != null) {
                // If user is already signed in, check role and redirect accordingly
                fetchUserRoleAndRedirect(currentUser.getUid());
            } else {
                // Otherwise, go to LoginActivity
                startActivity(new Intent(Welcome.this, MainActivity.class));
                finish();
            }
        }, 2000); // 2 seconds delay
    }

    // Fetch role and save farmerId if the user is a farmer
    private void fetchUserRoleAndRedirect(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String cachedRole = sharedPreferences.getString(KEY_USER_ROLE, null);

        if (cachedRole != null) {
            // If role is cached in SharedPreferences, use it
            if ("farmer".equals(cachedRole)) {
                saveFarmerId(userId); // Save farmerId if it's a farmer
            }
            redirectToDashboard(cachedRole);
        } else {
            // If not cached, fetch from Firestore
            db.collection("Roles").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userRole = documentSnapshot.getString("userType");
                            if (userRole != null) {
                                // Cache role in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USER_ROLE, userRole);
                                editor.apply();

                                if ("farmer".equals(userRole)) {
                                    // Save farmerId for farmers
                                    saveFarmerId(userId);
                                }

                                // Redirect to the correct dashboard
                                redirectToDashboard(userRole);
                            } else {
                                redirectToLogin(); // Role missing in Firestore
                            }
                        } else {
                            redirectToLogin(); // Document doesn't exist
                        }
                    })
                    .addOnFailureListener(e -> redirectToLogin()); // Firestore fetch failed
        }
    }

    // Save farmerId in SharedPreferences
    private void saveFarmerId(String farmerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("FarmerId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("farmerId", farmerId);
        editor.apply();

        // Log to verify it's saving
        Log.d("FarmerID", "Saved Farmer ID: " + farmerId);
    }

    // Redirect user to the correct dashboard based on role
    private void redirectToDashboard(String userRole) {
        if ("farmer".equals(userRole)) {
            startActivity(new Intent(Welcome.this, FarmerDashboard.class));
        } else if ("buyer".equals(userRole)) {
            startActivity(new Intent(Welcome.this, UserDashboard.class));
        } else {
            // If role is unknown, go to login
            redirectToLogin();
        }
        finish();
    }

    // Redirect user to login if something goes wrong
    private void redirectToLogin() {
        startActivity(new Intent(Welcome.this, MainActivity.class));
        finish();
    }
}
