package com.example.Agri_Hub;

public class CartItem {
    private String productId, name, imageUrl,price,weight,quantity;
    private float rating;
    private int totalRatings;

    public CartItem() { }

    public CartItem(String productId, String productName, String imageUrl, String price, String quantity, String weight, float rating, int ratingCount) {
        this.productId = productId;
        this.name =productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.weight = weight;
        this.quantity = quantity;
        this.rating = rating;
        this.totalRatings = ratingCount;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getPrice() { return price; }
    public String getWeight(){ return weight;}
    public String getQuantity(){return quantity;}
    public float getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
}
