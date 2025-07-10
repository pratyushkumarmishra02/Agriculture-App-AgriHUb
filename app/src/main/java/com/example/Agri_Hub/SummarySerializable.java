package com.example.Agri_Hub;

import java.io.Serializable;

public class SummarySerializable implements Serializable {
    private String productImage,productName,price,productId,weight,quantity,individualPrice,individualQuantities;

    public SummarySerializable(String productImg, String productName, String individualPrice,String price, String productId, String weight, String quantity,String individualQuantities) {
        this.productImage = productImg;
        this.productName = productName;
        this.individualPrice = individualPrice;
        this.price = price;
        this.productId = productId;
        this.weight = weight;
        this.quantity = quantity;
        this.individualQuantities = individualQuantities;
    }

    public String getProductImage(){
        return productImage;
    }
    public String getProductName(){
        return productName;
    }
    public String getIndividualPrice(){
        return individualPrice;
    }
    public String getPrice(){
        return price;
    }
    public String getProductId(){
        return productId;
    }
    public String getWeight(){
        return weight;
    }
    public String getQuantity(){
        return quantity;
    }
    public String getIndividualQuantities(){ return individualQuantities;}
}
