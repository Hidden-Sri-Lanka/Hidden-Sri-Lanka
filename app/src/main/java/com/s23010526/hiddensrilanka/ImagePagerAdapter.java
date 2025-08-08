package com.s23010526.hiddensrilanka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * ImagePagerAdapter - ViewPager2 adapter for horizontal scrolling through images
 *
 * This adapter enables users to swipe left/right through full-size images
 * in the location detail view, providing a smooth horizontal scrolling experience.
 */
public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<String> imageUrls;

    public ImagePagerAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pager_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Load image with Glide, handling Google Photos URLs
        if (isGooglePhotosUrl(imageUrl)) {
            GooglePhotosUrlHelper.processImageUrl(context, imageUrl, processedUrl -> {
                loadImageWithGlide(holder.imageView, processedUrl);
            });
        } else {
            loadImageWithGlide(holder.imageView, imageUrl);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * Update the adapter with new image URLs
     */
    public void updateImages(ArrayList<String> newImageUrls) {
        this.imageUrls.clear();
        this.imageUrls.addAll(newImageUrls);
        notifyDataSetChanged();
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
     * Load image using Glide with proper error handling
     */
    private void loadImageWithGlide(ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .into(imageView);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_pager_image);
        }
    }
}
