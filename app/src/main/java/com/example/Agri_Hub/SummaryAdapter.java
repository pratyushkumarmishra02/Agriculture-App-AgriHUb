package com.example.Agri_Hub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ProductViewHolder> {
    private ArrayList<SummarySerializable> productList;

    public SummaryAdapter(ArrayList<SummarySerializable> productList){
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SummarySerializable product = productList.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getIndividualPrice());
        holder.productWeight.setText("Weight: " + product.getWeight());
        holder.productQuantity.setText("Quantity: " + product.getIndividualQuantities());

        Glide.with(holder.itemView.getContext())
                .load(product.getProductImage())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productWeight, productQuantity;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.final_price);
            productWeight = itemView.findViewById(R.id.final_weight);
            productQuantity = itemView.findViewById(R.id.final_quantity);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
