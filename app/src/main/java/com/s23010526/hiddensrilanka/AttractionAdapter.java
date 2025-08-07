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

/**
 * AttractionAdapter - RecyclerView adapter for displaying attraction cards
 *
 * This adapter demonstrates key Android development concepts:
 * 1. RecyclerView implementation for efficient list scrolling
 * 2. ViewHolder pattern for memory optimization
 * 3. Intent data passing between activities
 * 4. Image loading with Google Photos URL processing
 * 5. Dynamic UI updates based on data state
 *
 * Key Bug Fix: Resolved intent data passing issue that caused
 * empty location details pages due to mismatched intent keys.
 */
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    // List of attractions to display - could include real data or placeholders
    private List<Attraction> attractionList;

    /**
     * Constructor - Initialize adapter with attraction data
     * @param attractionList List of attractions to display in RecyclerView
     */
    public AttractionAdapter(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    /**
     * Create new ViewHolder instances for RecyclerView items
     * Called when RecyclerView needs a new item view
     */
    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each attraction card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    /**
     * Bind data to ViewHolder for each item in the list
     * This method is called for each visible item and handles:
     * 1. Text data binding
     * 2. Image loading with Google Photos processing
     * 3. Placeholder vs real attraction handling
     * 4. Click listener setup for navigation
     */
    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction currentAttraction = attractionList.get(position);

        // Set basic text data for the attraction card
        holder.nameTextView.setText(currentAttraction.getName());
        holder.categoryTextView.setText(currentAttraction.getCategory());

        // Handle placeholder entries vs real attractions differently
        if (currentAttraction.isPlaceholder()) {
            // PLACEHOLDER HANDLING: Show "grow our database" image for empty states
            holder.imageViewAttraction.setImageResource(R.drawable.grow_our_database);
            holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Visual styling to indicate this is a call-to-action placeholder
            holder.itemView.setAlpha(0.9f);
            holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.DeepForestGreen));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.GoldYellow));
        } else {
            // REAL ATTRACTION HANDLING: Load actual attraction data and images
            holder.itemView.setAlpha(1.0f);
            holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.NavyBlue));
            holder.categoryTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
            holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // SMART IMAGE LOADING: Handle different types of image URLs
            if (currentAttraction.getImages() != null && !currentAttraction.getImages().isEmpty()) {
                String imageUrl = currentAttraction.getImages().get(0);

                // Process Google Photos URLs before loading with Glide
                if (isGooglePhotosUrl(imageUrl)) {
                    Log.d("GlideDebug", "Processing Google Photos URL: " + imageUrl);
                    // Convert Google Photos share link to direct image URL
                    GooglePhotosUrlHelper.processImageUrl(holder.itemView.getContext(), imageUrl, processedUrl -> {
                        loadImageWithGlide(holder, processedUrl);
                    });
                } else {
                    // Direct image URL - load immediately
                    loadImageWithGlide(holder, imageUrl);
                }
            } else {
                // No image available - show placeholder
                Log.w("GlideDebug", "Image URL list is null or empty for: " + currentAttraction.getName());
                holder.imageViewAttraction.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        // CRITICAL FIX: Click listener with proper intent data passing
        // This was the source of the major bug where details page showed empty data
        setupClickListener(holder, currentAttraction);
    }

    /**
     * Set up click listeners for attraction cards
     *
     * MAJOR BUG FIX: This method contains the solution to the intent data passing
     * issue that prevented location details from displaying properly.
     */
    private void setupClickListener(AttractionViewHolder holder, Attraction currentAttraction) {
        holder.itemView.setOnClickListener(v -> {
            if (currentAttraction.isPlaceholder()) {
                // PLACEHOLDER CLICK: Redirect to add location form
                Intent intent = new Intent(holder.itemView.getContext(), AddLocationActivity.class);
                holder.itemView.getContext().startActivity(intent);

                Toast.makeText(holder.itemView.getContext(),
                        "Let's add some attractions! 🚀",
                        Toast.LENGTH_SHORT).show();
            } else {
                // REAL ATTRACTION CLICK: Navigate to location details

                // CRITICAL FIX: Corrected intent extra keys to match LocationDetailActivity
                // Previously used mismatched keys that caused empty details page
                Intent intent = new Intent(holder.itemView.getContext(), LocationDetailActivity.class);

                // BEFORE (BROKEN): Used inconsistent key names
                // intent.putExtra("location_name", name);
                // intent.putExtra("category", category);

                // AFTER (FIXED): Standardized key names that match receiving activity
                intent.putExtra("attraction_name", currentAttraction.getName());
                intent.putExtra("attraction_city", currentAttraction.getCity());
                intent.putExtra("attraction_category", currentAttraction.getCategory());
                intent.putExtra("attraction_description", currentAttraction.getDescription());
                intent.putExtra("contributor_name", currentAttraction.getContributorName());
                intent.putExtra("youtube_url", currentAttraction.getYoutubeUrl());

                // IMPORTANT: Pass coordinate data for directions functionality
                intent.putExtra("attraction_latitude", currentAttraction.getLatitude());
                intent.putExtra("attraction_longitude", currentAttraction.getLongitude());

                // Pass image URL with correct key name
                if (currentAttraction.getImages() != null && !currentAttraction.getImages().isEmpty()) {
                    intent.putExtra("attraction_image_url", currentAttraction.getImages().get(0));
                }

                // Start the location details activity with all required data
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    /**
     * Return the total number of items in the dataset
     * Required method for RecyclerView.Adapter
     */
    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    /**
     * ViewHolder class - Implements the ViewHolder pattern for RecyclerView
     *
     * This pattern improves performance by caching view references
     * instead of calling findViewById repeatedly for each item.
     *
     * Academic Learning: Demonstrates memory optimization techniques
     * and efficient list rendering in Android applications.
     */
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        // Cached view references for efficient access
        public ImageView imageViewAttraction;   // Main attraction image
        public TextView nameTextView;          // Attraction name
        public TextView categoryTextView;      // Attraction category

        /**
         * Constructor - Cache view references to avoid repeated findViewById calls
         * @param itemView The inflated layout for each list item
         */
        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cache all view references during initialization
            imageViewAttraction = itemView.findViewById(R.id.imageView_attraction);
            nameTextView = itemView.findViewById(R.id.textView_attraction_name);
            categoryTextView = itemView.findViewById(R.id.textView_attraction_category);
        }
    }

    /**
     * Utility method to detect Google Photos URLs that require special processing
     *
     * Google Photos share links need to be converted to direct image URLs
     * before they can be loaded by image loading libraries like Glide.
     *
     * @param url The image URL to check
     * @return true if URL is a Google Photos link, false otherwise
     */
    private boolean isGooglePhotosUrl(String url) {
        return url != null && (
                url.contains("photos.app.goo.gl") ||       // Shortened share links
                url.contains("photos.google.com/share") ||  // Direct share links
                url.contains("photos.google.com/u/") ||     // User photo links
                url.contains("photos.google.com/album")     // Album links
        );
    }

    /**
     * Load image using Glide library with comprehensive error handling
     *
     * This method demonstrates professional image loading practices:
     * 1. Placeholder images while loading
     * 2. Error handling with fallback images
     * 3. Debug logging for troubleshooting
     * 4. Performance monitoring
     *
     * Academic Learning: Shows integration of third-party libraries
     * and proper error handling in production applications.
     *
     * @param holder The ViewHolder containing the ImageView
     * @param imageUrl The processed image URL to load
     */
    private void loadImageWithGlide(AttractionViewHolder holder, String imageUrl) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)  // Show while loading
                .error(R.drawable.ic_image_placeholder)        // Show if loading fails
                .listener(new RequestListener<Drawable>() {    // Monitor loading process
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                               Target<Drawable> target, boolean isFirstResource) {
                        // Log failure for debugging purposes
                        Log.w("GlideDebug", "Failed to load image: " + imageUrl);
                        if (e != null) {
                            Log.w("GlideDebug", "Error: " + e.getMessage());
                        }
                        return false; // Let Glide handle the error image
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                 Target<Drawable> target, DataSource dataSource,
                                                 boolean isFirstResource) {
                        // Log success for debugging and performance monitoring
                        Log.d("GlideDebug", "Successfully loaded image: " + imageUrl);
                        return false; // Let Glide display the image
                    }
                })
                .into(holder.imageViewAttraction); // Load into the ImageView
    }
}