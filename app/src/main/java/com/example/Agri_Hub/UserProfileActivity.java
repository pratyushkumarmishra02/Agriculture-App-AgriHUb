package com.example.Agri_Hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends LanguageBaseActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView welcome, fullName, email, dob, gender, mobile;
    private ProgressBar pb;
    private ImageView imageViewProfile;
    private FirebaseAuth authProfile;

    private FirebaseFirestore db;
    private String userRole;

    private static final int IMAGE_UPLOAD_REQUEST = 1;
    private static final String TAG = "UserProfileActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_USER_ROLE = "userRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("PROFILE");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Initialize UI elements
        welcome = findViewById(R.id.show_welcome);
        fullName = findViewById(R.id.show_full_name);
        email = findViewById(R.id.show_email);
        dob = findViewById(R.id.show_dob);
        gender = findViewById(R.id.show_gender);
        mobile = findViewById(R.id.show_mobile);
        pb = findViewById(R.id.progressbar);
        imageViewProfile = findViewById(R.id.image_view_profile);

        // Bottom Navigation setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(UserProfileActivity.this, UserDashboard.class);
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
                else if(item.getItemId() == R.id.nav_cart){
                    startActivity(new Intent(UserProfileActivity.this,CartActivity.class));
                    return true;
                }else if (item.getItemId() == R.id.nav_profile) {
                    return true;
                }

                return false;
            }
        });

        authProfile = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "User details unavailable", Toast.LENGTH_LONG).show();
        } else {
            pb.setVisibility(View.VISIBLE);
            fetchUserRole(firebaseUser.getUid());
        }

        // Click listener for profile image upload
        imageViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UploadImageActivity.class);
            intent.putExtra("callerClass", "UserProfileActivity");
            startActivityForResult(intent, IMAGE_UPLOAD_REQUEST);
        });
    }

    // Fetch user role from Firestore or cache
    private void fetchUserRole(String uid) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userRole = sharedPreferences.getString(KEY_USER_ROLE, null);

        if (userRole != null) {
            // Role already cached, proceed to fetch user data
            showUserProfile(uid);
        } else {
            // Fetch role from Firestore
            db.collection("Roles").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userRole = documentSnapshot.getString("userType");
                            if (userRole != null) {
                                // Save role in cache
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USER_ROLE, userRole);
                                editor.apply();

                                // Proceed to show user data
                                showUserProfile(uid);
                            }
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch user role", e);
                        Toast.makeText(this, "Failed to load user role", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // ✅ Load user profile based on role
    private void showUserProfile(String uid) {
        String collectionPath = userRole.equals("farmer") ? "Farmers" : "Users";
        DocumentReference referenceProfile = db.collection(collectionPath).document(uid);

        referenceProfile.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot snapshot = task.getResult();

                String fn = snapshot.getString("user_name");
                String eml = snapshot.getString("email");
                String DOB = snapshot.getString("Dob");
                String sex = snapshot.getString("Gender");
                String phNumber = snapshot.getString("ph_number");

                // Update UI
                welcome.setText("Welcome, " + fn + "!");
                fullName.setText(fn);
                email.setText(eml);
                dob.setText(DOB);
                gender.setText(sex);
                mobile.setText(phNumber);

                // Load profile image
                loadProfileImage(uid);

            } else {
                Toast.makeText(this, "Profile data unavailable", Toast.LENGTH_SHORT).show();
            }
            pb.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch profile data", e);
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_LONG).show();
            pb.setVisibility(View.GONE);
        });
    }

    // Load profile image from Firebase Storage or cache
    private void loadProfileImage(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String cachedImageUrl = sharedPreferences.getString("profile_image_" + userId, null);

        if (cachedImageUrl != null) {
            Picasso.get().load(cachedImageUrl).into(imageViewProfile);
        } else {
            // Fetch from Firebase Storage if no cached image
            StorageReference profileRef = FirebaseStorage.getInstance()
                    .getReference("ProfileImages/" + userId + "/profile.jpg");

            profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Picasso.get().load(imageUrl).into(imageViewProfile);

                // Save the image URL in SharedPreferences for future use
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profile_image_" + userId, imageUrl);
                editor.apply();
            }).addOnFailureListener(e -> {
                if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    imageViewProfile.setImageResource(R.drawable.no_profile_pic);
                } else {
                    Log.e(TAG, "Failed to load profile image", e);
                }
            });
        }
    }

    // Handle image upload result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_UPLOAD_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("uploadedImageUrl")) {
                String uploadedImageUrl = data.getStringExtra("uploadedImageUrl");
                Picasso.get().load(uploadedImageUrl).into(imageViewProfile);

                // Save the new image URL in SharedPreferences
                String userId = authProfile.getCurrentUser().getUid();
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profile_image_" + userId, uploadedImageUrl);
                editor.apply();

                // Get user role from Firestore
                FirebaseFirestore.getInstance().collection("Roles")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userRole = documentSnapshot.getString("userType");

                                // Set collection path based on user role
                                String collectionPath = userRole.equals("farmer") ? "Farmers" : "Users";

                                // Save the new image URL in Firestore based on the role
                                FirebaseFirestore.getInstance().collection(collectionPath)
                                        .document(userId)
                                        .update("profileImage", uploadedImageUrl)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile image updated in Firestore"))
                                        .addOnFailureListener(e -> Log.e(TAG, "Error updating profile image", e));
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch user role", e));

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }

        if (id == R.id.menu_logout) {
            String userId = authProfile.getCurrentUser().getUid();
            authProfile.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the correct item in the bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}


