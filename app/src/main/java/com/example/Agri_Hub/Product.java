package com.example.Agri_Hub;

public class Product {
    private String productName;
    private String imageUrl;
    private String available;
    private String farmerId;
    private  String price;

    public Product() {}

    public Product(String productName, String imageUrl, String available, String farmerId,String price) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.available = available;
        this.farmerId = farmerId;
        this.price=price;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAvailable() {
        return available;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public String getPrice(){
        return price;
    }
}
