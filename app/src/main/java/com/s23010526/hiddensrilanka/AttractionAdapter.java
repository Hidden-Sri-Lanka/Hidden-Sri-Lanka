package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private List<Attraction> attractionList;

    public AttractionAdapter(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction currentAttraction = attractionList.get(position);

        holder.nameTextView.setText(currentAttraction.getName());
        holder.categoryTextView.setText(currentAttraction.getCategory());

        // Handle placeholder entries differently
        if (currentAttraction.isPlaceholder()) {
            // For placeholder entries, show your original PNG image
            holder.imageViewAttraction.setImageResource(R.drawable.grow_our_database);
            holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Change appearance to indicate it's clickable
            holder.itemView.setAlpha(0.9f);
            holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.DeepForestGreen));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.GoldYellow));
        } else {
            // Normal attraction entries
            holder.itemView.setAlpha(1.0f);
            holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.NavyBlue));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
            holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Load image for normal attractions
            if (currentAttraction.getImages() != null && !currentAttraction.getImages().isEmpty()) {
                String imageUrl = currentAttraction.getImages().get(0);

                // Process Google Photos URLs before loading
                if (isGooglePhotosUrl(imageUrl)) {
                    Log.d("GlideDebug", "Processing Google Photos URL: " + imageUrl);
                    GooglePhotosUrlHelper.processImageUrl(holder.itemView.getContext(), imageUrl, processedUrl -> {
                        loadImageWithGlide(holder, processedUrl);
                    });
                } else {
                    // Direct image URL - load directly
                    loadImageWithGlide(holder, imageUrl);
                }
            } else {
                Log.w("GlideDebug", "Image URL list is null or empty for: " + currentAttraction.getName());
                holder.imageViewAttraction.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        // Click listener with placeholder handling
        holder.itemView.setOnClickListener(v -> {
            if (currentAttraction.isPlaceholder()) {
                // Redirect to AddLocationActivity for placeholder entries
                Intent intent = new Intent(holder.itemView.getContext(), AddLocationActivity.class);
                holder.itemView.getContext().startActivity(intent);

                Toast.makeText(holder.itemView.getContext(),
                        "Let's add some attractions! 🚀",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to LocationDetailActivity with attraction data
                Intent intent = new Intent(holder.itemView.getContext(), LocationDetailActivity.class);

                // Pass all attraction data as extras
                intent.putExtra("location_name", currentAttraction.getName());
                intent.putExtra("category", currentAttraction.getCategory());
                intent.putExtra("description", currentAttraction.getDescription());
                intent.putExtra("contributor_name", currentAttraction.getContributorName());
                intent.putExtra("youtube_url", currentAttraction.getYoutubeUrl());

                // Pass first image URL if available
                if (currentAttraction.getImages() != null && !currentAttraction.getImages().isEmpty()) {
                    intent.putExtra("image_url", currentAttraction.getImages().get(0));
                }

                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }


    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAttraction;
        public TextView nameTextView;
        public TextView categoryTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAttraction = itemView.findViewById(R.id.imageView_attraction);
            nameTextView = itemView.findViewById(R.id.textView_attraction_name);
            categoryTextView = itemView.findViewById(R.id.textView_attraction_category);
        }
    }

    /**
     * Check if URL is a Google Photos URL that needs processing
     */
    private boolean isGooglePhotosUrl(String url) {
        return url != null && (
                url.contains("photos.app.goo.gl") ||
                url.contains("photos.google.com/share") ||
                url.contains("photos.google.com/u/") ||
                url.contains("photos.google.com/album")
        );
    }

    /**
     * Load image with Glide using the processed URL
     */
    private void loadImageWithGlide(AttractionViewHolder holder, String imageUrl) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.w("GlideDebug", "Failed to load image: " + imageUrl);
                        if (e != null) {
                            Log.w("GlideDebug", "Error: " + e.getMessage());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideDebug", "Successfully loaded image: " + imageUrl);
                        return false;
                    }
                })
                .into(holder.imageViewAttraction);
    }
}