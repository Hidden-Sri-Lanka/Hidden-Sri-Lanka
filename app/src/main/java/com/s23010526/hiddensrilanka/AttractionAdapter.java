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

/**
 * AttractionAdapter - RecyclerView adapter for displaying attraction cards in Hidden Sri Lanka app
 *
 * This adapter demonstrates key Android development concepts:
 * 1. RecyclerView implementation for efficient list scrolling
 * 2. ViewHolder pattern for memory optimization and smooth scrolling
 * 3. Intent data passing between activities with proper key management
 * 4. Image loading with Google Photos URL processing and Glide integration
 * 5. Dynamic UI updates based on data state (placeholder vs real data)
 * 6. Click handling for different card types (placeholder vs attraction)
 *
 * Key Features:
 * - Handles both real attraction data and placeholder cards
 * - Processes Google Photos share URLs for direct image loading
 * - Provides smooth navigation to LocationDetailActivity with complete data
 * - Implements proper error handling for image loading failures
 * - Uses consistent color theming based on app design system
 *
 * Recent Bug Fix: Resolved intent data passing issue that caused
 * empty location details pages due to mismatched intent keys between
 * this adapter and LocationDetailActivity.
 *
 * @author Hidden Sri Lanka Development Team
 * @version 2.0.0
 * @since 1.0.0
 */
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private static final String TAG = "AttractionAdapter";

    /**
     * List of attractions to display - immutable after initialization
     * Contains both real attraction data and placeholder entries for user engagement
     */
    private final List<Attraction> attractionList;

    /**
     * Constructor - Initialize adapter with attraction data
     *
     * @param attractionList List of attractions to display in RecyclerView
     *                      Must not be null, can contain placeholder entries
     * @throws IllegalArgumentException if attractionList is null
     */
    public AttractionAdapter(List<Attraction> attractionList) {
        if (attractionList == null) {
            throw new IllegalArgumentException("Attraction list cannot be null");
        }
        this.attractionList = attractionList;
    }

    /**
     * Create new ViewHolder instances for RecyclerView items
     * Called when RecyclerView needs a new item view that doesn't exist in the cache
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (unused in this implementation)
     * @return A new AttractionViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each attraction card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    /**
     * Bind data to ViewHolder for each item in the list
     * This method is called for each visible item and handles:
     * 1. Text data binding (name, category)
     * 2. Image loading with Google Photos URL processing
     * 3. Visual styling differences between placeholder and real attractions
     * 4. Click listener setup for proper navigation
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item
     * @param position The position of the item within the adapter's data set
     */
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

    /**
     * Configure visual appearance for placeholder cards
     * Placeholder cards encourage users to contribute new locations to the database
     *
     * @param holder The ViewHolder to configure for placeholder display
     */
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

    /**
     * Configure visual appearance and load images for real attraction cards
     * Handles image loading with fallback mechanisms and proper error handling
     *
     * @param holder The ViewHolder to configure for attraction display
     * @param attraction The attraction data to display
     */
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

    /**
     * Set up click listeners for attraction cards with proper navigation handling
     *
     * CRITICAL BUG FIX: This method contains the solution to the intent data passing
     * issue that prevented location details from displaying properly.
     * Intent extra keys now match exactly with those expected by LocationDetailActivity.
     *
     * @param holder The ViewHolder containing the clickable item
     * @param currentAttraction The attraction data to pass to the detail activity
     */
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

    /**
     * Navigate to AddLocationActivity when placeholder card is clicked
     * Encourages user contribution to expand the database
     *
     * @param holder The ViewHolder containing context for navigation
     */
    private void navigateToAddLocation(AttractionViewHolder holder) {
        Intent intent = new Intent(holder.itemView.getContext(), AddLocationActivity.class);
        holder.itemView.getContext().startActivity(intent);

        // Show encouraging message to motivate user contribution
        Toast.makeText(holder.itemView.getContext(),
                "Let's add some attractions! 🚀",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to LocationDetailActivity with complete attraction data
     *
     * MAJOR BUG FIX: Intent extra keys now match exactly with LocationDetailActivity
     * Previously mismatched keys caused empty detail pages
     *
     * @param holder The ViewHolder containing context for navigation
     * @param attraction The attraction data to pass to detail activity
     */
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

    /**
     * Return the total number of items in the dataset
     * Required method for RecyclerView.Adapter
     *
     * @return Total number of items (attractions + placeholders) to display
     */
    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    /**
     * ViewHolder class - Implements the ViewHolder pattern for RecyclerView
     *
     * This pattern improves performance by caching view references instead of
     * calling findViewById repeatedly for each item during scrolling.
     *
     * Academic Learning: Demonstrates memory optimization techniques and
     * efficient list rendering in Android applications. Essential pattern
     * for creating performant list interfaces.
     */
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {

        // Cached view references for efficient access during binding
        public final ImageView imageViewAttraction;   // Main attraction image
        public final TextView nameTextView;          // Attraction name display
        public final TextView categoryTextView;      // Attraction category display

        /**
         * Constructor - Cache view references to avoid repeated findViewById calls
         * Called once when ViewHolder is created, improving scrolling performance
         *
         * @param itemView The inflated layout for each list item
         */
        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cache all view references during initialization for performance
            imageViewAttraction = itemView.findViewById(R.id.imageView_attraction);
            nameTextView = itemView.findViewById(R.id.textView_attraction_name);
            categoryTextView = itemView.findViewById(R.id.textView_attraction_category);
        }
    }

    /**
     * Utility method to detect Google Photos URLs that require special processing
     *
     * Google Photos share links cannot be loaded directly by image libraries.
     * They must be processed through GooglePhotosUrlHelper to extract direct image URLs.
     *
     * Supported Google Photos URL patterns:
     * - photos.app.goo.gl/* (shortened share links)
     * - photos.google.com/share/* (direct share links)
     * - photos.google.com/u/* (user photo links)
     * - photos.google.com/album/* (album links)
     *
     * @param url The image URL to analyze
     * @return true if URL requires Google Photos processing, false for direct loading
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
     * 1. Placeholder images while loading for better UX
     * 2. Error handling with fallback images
     * 3. Debug logging for development troubleshooting
     * 4. Performance monitoring through load callbacks
     * 5. Memory efficient loading with Glide's caching
     *
     * Academic Learning: Shows integration of third-party libraries
     * and proper error handling in production applications.
     *
     * @param holder The ViewHolder containing the target ImageView
     * @param imageUrl The processed, direct image URL to load
     */
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

    /**
     * Update the adapter's data set and refresh the RecyclerView efficiently
     * Useful for dynamic content updates without recreating the adapter
     *
     * @param newAttractions The new list of attractions to display
     */
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

    /**
     * Add a single attraction to the list efficiently
     *
     * @param attraction The attraction to add
     */
    public void addAttraction(Attraction attraction) {
        if (attraction != null) {
            attractionList.add(attraction);
            notifyItemInserted(attractionList.size() - 1);
            Log.d(TAG, "Added attraction: " + attraction.getName());
        }
    }

    /**
     * Remove an attraction at the specified position
     *
     * @param position The position of the attraction to remove
     */
    public void removeAttraction(int position) {
        if (position >= 0 && position < attractionList.size()) {
            Attraction removed = attractionList.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "Removed attraction: " + removed.getName());
        }
    }
}