package com.example.Agri_Hub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.cartItemName.setText(item.getName());
        holder.cartItemPrice.setText(item.getPrice());
        holder.ratingCount.setText("(" + String.format("%.1f", item.getRating()) + ")");
        holder.totalRatings.setText("(" + item.getTotalRatings() + " ratings)");
        holder.ratingBar.setRating((float) item.getRating());
        holder.weight.setText("Weight: "+item.getWeight());
        holder.qtyTv.setText(item.getQuantity());


        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.no_profile_pic)
                .into(holder.imgProduct);

        // Remove button functionality
        holder.removeBtn.setOnClickListener(v -> {
            db.collection("Cart")
                    .document(item.getProductId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < cartItemList.size()) {
                            cartItemList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartItemList.size());
                        }
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                        // Restart activity
                        Intent intent = new Intent(context, CartActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();

                    });
        });

        // Buy Now button functionality
        holder.buyNowBtn.setOnClickListener(v ->
                Toast.makeText(context, "Buy Now clicked for " + item.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() { return cartItemList.size(); }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        LinearLayout removeBtn, buyNowBtn;
        TextView cartItemName, cartItemPrice, ratingCount, totalRatings,weight;
        RatingBar ratingBar;
        TextView qtyTv;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            ratingCount = itemView.findViewById(R.id.rating_count);
            totalRatings = itemView.findViewById(R.id.total_ratings);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            buyNowBtn = itemView.findViewById(R.id.buyNowBtn);
            qtyTv = itemView.findViewById(R.id.qtyTv);
            weight = itemView.findViewById(R.id.cartItemWeight);
        }
    }

}
