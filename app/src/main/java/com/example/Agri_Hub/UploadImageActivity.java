package com.example.Agri_Hub;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UploadImageActivity extends LanguageBaseActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUpload;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> openFileLauncher;
    private String callerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Upload Image");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Initialize UI elements
        Button buttonChoose = findViewById(R.id.button_upload_pic_choose);
        Button buttonUpload = findViewById(R.id.upload_pic_button);
        imageViewUpload = findViewById(R.id.imageview_profile_dp);
        progressBar = findViewById(R.id.pb);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Determine caller class and set storage reference accordingly
        callerClass = getIntent().getStringExtra("callerClass");
        String userId = firebaseUser.getUid();
        if ("UserProfileActivity".equals(callerClass)) {
            storageReference = FirebaseStorage.getInstance().getReference("ProfileImages/" + userId);
        } else if ("UploadProduceActivity".equals(callerClass)) {
            storageReference = FirebaseStorage.getInstance().getReference("ProductImages/" + userId);
        }

        // Initialize ActivityResultLauncher for picking a single image
        openFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        updateImagePreview();
                    } else {
                        Log.e("UploadImageActivity", "Image selection failed");
                    }
                }
        );

        buttonChoose.setOnClickListener(v -> openFileChooser());

        // Upload single image on button click
        buttonUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirestore();
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Toolbar back button functionality
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open file chooser for a single image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        openFileLauncher.launch(intent);
    }

    // Upload a single image to Firebase Storage
    private void uploadImageToFirestore() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileExtension = getFileExtension(selectedImageUri);
        String fileName = System.currentTimeMillis() + "." + fileExtension;
        StorageReference fileReference = storageReference.child(fileName);

        fileReference.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    sendImageUrlBack(uri.toString());
                })
        ).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadImageActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Send uploaded image URL back to UploadProduceActivity
    private void sendImageUrlBack(String imageUrl) {
        progressBar.setVisibility(View.GONE);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("uploadedImageUrl", imageUrl);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // Update UI to show the selected image
    private void updateImagePreview() {
        if (selectedImageUri != null) {
            Picasso.get().load(selectedImageUri).into(imageViewUpload);
        }
    }

    // Get the file extension of the selected image
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
