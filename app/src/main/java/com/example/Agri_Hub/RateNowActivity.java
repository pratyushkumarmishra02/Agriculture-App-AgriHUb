package com.example.Agri_Hub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RateNowActivity extends LanguageBaseActivity {

    private RatingBar userRatingBar;
    private EditText reviewInput;
    private Button submitRating;

    private FirebaseFirestore firestore;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_now);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Review");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        // Initialize Firebase Firestore
        productId = getIntent().getStringExtra("productId");
        firestore = FirebaseFirestore.getInstance();

        // Bind UI components
        userRatingBar = findViewById(R.id.user_rating_bar);
        reviewInput = findViewById(R.id.review_input);
        submitRating = findViewById(R.id.submit_rating);

        // Set submit button listener
        submitRating.setOnClickListener(v -> submitUserRating());
    }

    private void submitUserRating() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        float rating = userRatingBar.getRating();
        String review = reviewInput.getText().toString();
        String username = currentUser.getDisplayName();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Store individual review in Firestore ("Reviews" Collection)
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("userId", userId);
        reviewData.put("rating", rating);
        reviewData.put("review", review);
        reviewData.put("userName", username);
        reviewData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("Reviews")
                .document(productId)
                .collection("UserReviews")
                .document(userId)
                .set(reviewData)
                .addOnSuccessListener(aVoid -> {
                    updateTotalRatings(rating); // Update overall ratings
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show());
    }

    /**
     * Update total ratings and star count in Firestore.
     */
    private void updateTotalRatings(float newRating) {
        DocumentReference productRatingRef = firestore.collection("ProductRatings").document(productId);

        firestore.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(productRatingRef);

            // Get current values (set default if null)
            double currentAverage = snapshot.getDouble("averageRating") != null ? snapshot.getDouble("averageRating") : 0;
            long currentTotalRatings = snapshot.getLong("totalRatings") != null ? snapshot.getLong("totalRatings") : 0;

            long fiveStar = snapshot.getLong("fiveStar") != null ? snapshot.getLong("fiveStar") : 0;
            long fourStar = snapshot.getLong("fourStar") != null ? snapshot.getLong("fourStar") : 0;
            long threeStar = snapshot.getLong("threeStar") != null ? snapshot.getLong("threeStar") : 0;
            long twoStar = snapshot.getLong("twoStar") != null ? snapshot.getLong("twoStar") : 0;
            long oneStar = snapshot.getLong("oneStar") != null ? snapshot.getLong("oneStar") : 0;

            // Increment the count for the respective rating
            switch ((int) newRating) {
                case 5: fiveStar++; break;
                case 4: fourStar++; break;
                case 3: threeStar++; break;
                case 2: twoStar++; break;
                case 1: oneStar++; break;
            }

            // Calculate new average rating
            double newAverage = ((currentAverage * currentTotalRatings) + newRating) / (currentTotalRatings + 1);
            long newTotalRatings = currentTotalRatings + 1;

            // Update Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("averageRating", newAverage);
            updateData.put("totalRatings", newTotalRatings);
            updateData.put("fiveStar", fiveStar);
            updateData.put("fourStar", fourStar);
            updateData.put("threeStar", threeStar);
            updateData.put("twoStar", twoStar);
            updateData.put("oneStar", oneStar);

            transaction.set(productRatingRef, updateData);
            return null;
        }).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Rating submitted!", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to update rating", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
