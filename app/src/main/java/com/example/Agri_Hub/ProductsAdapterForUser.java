package com.example.Agri_Hub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProductsAdapterForUser extends RecyclerView.Adapter<ProductsAdapterForUser.ProductViewHolder> {

    private WeakReference<Context> contextRef;
    private List<ProductsForUser> productList;

    // Constructor
    public ProductsAdapterForUser(Context context, List<ProductsForUser> productList) {
        this.contextRef = new WeakReference<>(context);
        this.productList = productList;
        setHasStableIds(true); // Performance improvement for stable IDs
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_product_user, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductsForUser product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        holder.weight.setText("("+product.getWeight()+")");
        holder.productAvailability.setText("Available: "+product.getAvailability());
        holder.farmerName.setText("Product of: "+product.getFarmerName());

        // Load image using Glide with error and placeholder handling
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_refresh_white)
                .error(R.drawable.ic_upload)
                .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
                .into(holder.productImage);

        // Set the rating
        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setRating(product.getRating());

        // Set rating count
        holder.ratingCount.setText("(" + String.format("%.1f", product.getRating()) + ")");


        holder.itemView.setOnClickListener(v -> {
            if (contextRef.get() != null) {
                Intent intent = new Intent(contextRef.get(), ProductDetailActivity.class);
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("price",product.getPrice());
                intent.putExtra("weight",product.getWeight());
                contextRef.get().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    // ViewHolder Class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productAvailability,ratingCount,farmerName,weight;
        ImageView productImage;
        RatingBar ratingBar;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productAvailability = itemView.findViewById(R.id.product_availability);
            productImage = itemView.findViewById(R.id.product_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingCount = itemView.findViewById(R.id.rating_count);
            farmerName =itemView.findViewById(R.id.farmerName);
            weight = itemView.findViewById(R.id.productWeight);
        }
    }
}
