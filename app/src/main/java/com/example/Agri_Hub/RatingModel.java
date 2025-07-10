package com.example.Agri_Hub;

public class RatingModel {
    private String userName;
    private float rating;
    private String review;

    public RatingModel(String userName, float rating, String review) {
        this.userName = userName;
        this.rating = rating;
        this.review = review;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}
