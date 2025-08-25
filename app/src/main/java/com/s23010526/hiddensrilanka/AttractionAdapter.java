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

import java.util.ArrayList;
import java.util.List;


//  AttractionAdapter - RecyclerView adapter for displaying attraction cards in Hidden Sri Lanka app

// Tells this is an adapter for recycler view
// Binds attraction data to the item views in the RecyclerView
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private static final String TAG = "AttractionAdapter";

//        List of attractions to display - immutable after initialization
//      Contains both real attraction data and placeholder entries for user engagement


//     hold the attractions to display
    private final List<Attraction> attractionList;
    public AttractionAdapter(List<Attraction> attractionList) {
        if (attractionList == null) {
            throw new IllegalArgumentException("Attraction list cannot be null");
        }
        this.attractionList = attractionList;
    }

//      Create new ViewHolder instances for RecyclerView items
//      Called when RecyclerView needs a new item view that doesn't exist in the cache

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each attraction card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction currentAttraction = attractionList.get(position);

        // Set basic text data for the attraction card
        holder.nameTextView.setText(currentAttraction.getName());
        holder.categoryTextView.setText(currentAttraction.getCategory());

        // Handle placeholder entries vs real attractions with different visual treatments
        if (currentAttraction.isPlaceholder()) {
            setupPlaceholderView(holder);
        } else {
            setupAttractionView(holder, currentAttraction);
        }

        // Setup click listener with proper intent data passing
        setupClickListener(holder, currentAttraction);
    }

    private void setupPlaceholderView(AttractionViewHolder holder) {
        // Show "grow our database" promotional image
        holder.imageViewAttraction.setImageResource(R.drawable.grow_our_database);
        holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Apply distinctive visual styling to indicate call-to-action nature
        holder.itemView.setAlpha(0.9f);
        holder.nameTextView.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), R.color.DeepForestGreen));
        holder.categoryTextView.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), R.color.GoldYellow));
    }

    private void setupAttractionView(AttractionViewHolder holder, Attraction attraction) {
        // Apply standard visual styling for real attractions
        holder.itemView.setAlpha(1.0f);
        holder.nameTextView.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), R.color.NavyBlue));
        holder.categoryTextView.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), R.color.light_gray));
        holder.imageViewAttraction.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Handle image loading with smart URL processing
        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            String imageUrl = attraction.getImages().get(0);

            // Process Google Photos URLs for direct image access
            if (isGooglePhotosUrl(imageUrl)) {
                Log.d(TAG, "Processing Google Photos URL for: " + attraction.getName());
                GooglePhotosUrlHelper.processImageUrl(holder.itemView.getContext(),
                        imageUrl, processedUrl -> loadImageWithGlide(holder, processedUrl));
            } else {
                // Load direct image URLs immediately
                loadImageWithGlide(holder, imageUrl);
            }
        } else {
            // No image available - show placeholder
            Log.w(TAG, "No images available for attraction: " + attraction.getName());
            holder.imageViewAttraction.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

//      Set up click listeners for attraction cards with proper navigation handling
    private void setupClickListener(AttractionViewHolder holder, Attraction currentAttraction) {
        holder.itemView.setOnClickListener(v -> {
            if (currentAttraction.isPlaceholder()) {
                // Handle placeholder clicks - redirect to add location form
                navigateToAddLocation(holder);
            } else {
                // Handle real attraction clicks - navigate to location details
                navigateToLocationDetails(holder, currentAttraction);
            }
        });
    }

//      Navigate to AddLocationActivity when placeholder card is clicked
    private void navigateToAddLocation(AttractionViewHolder holder) {
        Intent intent = new Intent(holder.itemView.getContext(), AddLocationActivity.class);
        holder.itemView.getContext().startActivity(intent);

        // Show encouraging message to motivate user contribution
        Toast.makeText(holder.itemView.getContext(),
                "Let's add some attractions! 🚀",
                Toast.LENGTH_SHORT).show();
    }

//      Navigate to LocationDetailActivity with complete attraction data
    private void navigateToLocationDetails(AttractionViewHolder holder, Attraction attraction) {
        Intent intent = new Intent(holder.itemView.getContext(), LocationDetailActivity.class);

        // Pass all attraction data with standardized key names
        intent.putExtra("attraction_name", attraction.getName());
        intent.putExtra("attraction_city", attraction.getCity());
        intent.putExtra("attraction_category", attraction.getCategory());
        intent.putExtra("attraction_description", attraction.getDescription());
        intent.putExtra("contributor_name", attraction.getContributorName());
        intent.putExtra("youtube_url", attraction.getYoutubeUrl());

        // Pass coordinate data for directions functionality
        intent.putExtra("attraction_latitude", attraction.getLatitude());
        intent.putExtra("attraction_longitude", attraction.getLongitude());

        // Pass image data for gallery functionality
        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            // Primary image for backward compatibility
            intent.putExtra("attraction_image_url", attraction.getImages().get(0));

            // Full image list for horizontal gallery
            ArrayList<String> imagesList = new ArrayList<>(attraction.getImages());
            intent.putStringArrayListExtra("attraction_images", imagesList);

            Log.d(TAG, "Passing " + attraction.getImages().size() +
                    " images to detail view for: " + attraction.getName());
        }

        // Start the location details activity
        holder.itemView.getContext().startActivity(intent);
    }

//      Return the total number of items in the dataset
    @Override
    public int getItemCount() {
        return attractionList.size();
    }

//      ViewHolder class - Implements the ViewHolder pattern for RecyclerView
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {

        // Cached view references for efficient access during binding
        public final ImageView imageViewAttraction;   // Main attraction image
        public final TextView nameTextView;          // Attraction name display
        public final TextView categoryTextView;      // Attraction category display

//          Constructor - Cache view references to avoid repeated findViewById calls
        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cache all view references during initialization for performance
            imageViewAttraction = itemView.findViewById(R.id.imageView_attraction);
            nameTextView = itemView.findViewById(R.id.textView_attraction_name);
            categoryTextView = itemView.findViewById(R.id.textView_attraction_category);
        }
    }

//      Utility method to detect Google Photos URLs that require special processing
//      Google Photos share links cannot be loaded directly by image libraries.
//      They must be processed through GooglePhotosUrlHelper to extract direct image URLs.
//
//      Supported Google Photos URL patterns:
//      - photos.app.goo.gl/* (shortened share links)
//      - photos.google.com/share/* (direct share links)
//      - photos.google.com/u/* (user photo links)
//      - photos.google.com/album/* (album links)
//

    private boolean isGooglePhotosUrl(String url) {
        return url != null && (
                url.contains("photos.app.goo.gl") ||       // Shortened share links
                url.contains("photos.google.com/share") ||  // Direct share links
                url.contains("photos.google.com/u/") ||     // User photo links
                url.contains("photos.google.com/album")     // Album links
        );
    }

//      Load image using Glide library with comprehensive error handling
    private void loadImageWithGlide(AttractionViewHolder holder, String imageUrl) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)  // Show while loading
                .error(R.drawable.ic_image_placeholder)        // Show if loading fails
                .listener(new RequestListener<>() {            // Monitor loading process
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                               Target<Drawable> target, boolean isFirstResource) {
                        // Log failure details for debugging
                        Log.w(TAG, "Failed to load image: " + imageUrl);
                        if (e != null) {
                            Log.w(TAG, "Glide error details: " + e.getMessage());
                        }
                        return false; // Let Glide handle the error image display
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                 Target<Drawable> target, DataSource dataSource,
                                                 boolean isFirstResource) {
                        // Log success for debugging and performance monitoring
                        Log.d(TAG, "Successfully loaded image from " + dataSource + ": " + imageUrl);
                        return false; // Let Glide display the loaded image
                    }
                })
                .into(holder.imageViewAttraction); // Load into the target ImageView
    }

//      Update the adapter's data set and refresh the RecyclerView efficiently
    public void updateAttractions(List<Attraction> newAttractions) {
        if (newAttractions != null) {
            int oldSize = attractionList.size();
            attractionList.clear();
            attractionList.addAll(newAttractions);

            // Use more efficient notification methods
            if (oldSize == newAttractions.size()) {
                notifyItemRangeChanged(0, newAttractions.size());
            } else {
                notifyDataSetChanged(); // Only when size changes
            }

            Log.d(TAG, "Attraction list updated with " + newAttractions.size() + " items");
        }
    }

//      Add a single attraction to the list efficiently

    public void addAttraction(Attraction attraction) {
        if (attraction != null) {
            attractionList.add(attraction);
            notifyItemInserted(attractionList.size() - 1);
            Log.d(TAG, "Added attraction: " + attraction.getName());
        }
    }

//      Remove an attraction at the specified position

    public void removeAttraction(int position) {
        if (position >= 0 && position < attractionList.size()) {
            Attraction removed = attractionList.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "Removed attraction: " + removed.getName());
        }
    }
}