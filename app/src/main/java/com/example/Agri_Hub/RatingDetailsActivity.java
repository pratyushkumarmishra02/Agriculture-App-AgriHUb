package com.example.Agri_Hub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RatingDetailsActivity extends LanguageBaseActivity {

    private RatingBar averageRatingBar;
    private TextView totalRatingCount;
    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<RatingModel> ratingList;

    private FirebaseFirestore firestore;
    private String productId;
    private float totalRating = 0;
    private int ratingCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_details);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Rating Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Get productId from Intent
        productId = getIntent().getStringExtra("productId");

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        averageRatingBar = findViewById(R.id.averageRatingBar);
        totalRatingCount = findViewById(R.id.totalRatingCount);
        ratingRecyclerView = findViewById(R.id.ratingRecyclerView);

        // Setup RecyclerView
        ratingList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(this, ratingList);
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.setAdapter(ratingAdapter);

        // Load Ratings from Firestore
        loadRatingsFromFirebase();
    }

    private void loadRatingsFromFirebase() {
        firestore.collection("Reviews")
                .document(productId)
                .collection("UserReviews")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ratingList.clear();
                    totalRating = 0;
                    ratingCount = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        float rating = document.getDouble("rating").floatValue();
                        String review = document.getString("review");
                        String userName = document.getString("userName");

                        ratingList.add(new RatingModel(userName, rating, review));
                        totalRating += rating;
                        ratingCount++;
                    }

                    if (ratingCount > 0) {
                        float averageRating = totalRating / ratingCount;
                        averageRatingBar.setRating(averageRating);
                        totalRatingCount.setText("Total Ratings: " + ratingCount);
                    } else {
                        averageRatingBar.setRating(0);
                        totalRatingCount.setText("No Ratings Yet");
                    }

                    ratingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> totalRatingCount.setText("Failed to load ratings"));
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
