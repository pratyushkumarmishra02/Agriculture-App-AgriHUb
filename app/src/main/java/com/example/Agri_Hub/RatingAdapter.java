package com.example.Agri_Hub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private Context context;
    private List<RatingModel> ratingList;

    public RatingAdapter(Context context, List<RatingModel> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatingModel rating = ratingList.get(position);
        holder.userName.setText(rating.getUserName());
        holder.userRating.setRating(rating.getRating());
        holder.userReview.setText(rating.getReview());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userReview;
        RatingBar userRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userRating = itemView.findViewById(R.id.userRating);
            userReview = itemView.findViewById(R.id.userReview);
        }
    }
}
