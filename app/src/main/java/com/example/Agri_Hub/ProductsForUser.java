package com.example.Agri_Hub;

public class ProductsForUser {
    private String productId;
    private String name;
    private String imageUrl;
    private String price;
    private String availability;
    private String farmerName;
    private float rating;
    private int ratingCount;
    private String weight;

    // Empty constructor for Firestore
    public ProductsForUser() {
    }

    // Constructor
    public ProductsForUser(String productId,String name, String imageUrl, String price,String weight, String availability,String farmerName, float rating, int ratingCount) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.availability = availability;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.productId=productId;
        this.farmerName=farmerName;
        this.weight = weight;
    }

    // Getters
    public String getProductId(){
        return productId;
    }
    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }
    public float getRating() {
        return rating;
    }
    public String getFarmerName(){
        return farmerName;
    }

    public String getWeight(){ return weight;}

    public int getRatingCount() {
        return ratingCount;
    }
}
