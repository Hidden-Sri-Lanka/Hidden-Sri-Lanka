package com.s23010526.hiddensrilanka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
//This is a RecyclerView adapter that displays a horizontal or grid gallery of images
//loaded from URLs. It supports both viewing mode (just display images) and
//editing mode (with remove buttons) used in home and add location activities respectively.
public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<String> imageUrls;
    private OnImageClickListener listener;
    private boolean showRemoveButton;

    public interface OnImageClickListener {
        void onImageClick(int position, String imageUrl); // user tap on image
        void onImageRemove(int position); // user press on delete button
    }

    public ImageGalleryAdapter(Context context, ArrayList<String> imageUrls, boolean showRemoveButton) {
        this.context = context;
        this.imageUrls = imageUrls; // initial list of img urls
        this.showRemoveButton = showRemoveButton; // true for edit mode, false for view mode
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create new view holder when recycler view needs one
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);// contain image View + floaring action button
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Load image with Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(holder.ivGalleryImage);

        // Show/hide remove button based on mode
        holder.fabRemoveImage.setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);

        // Set click listeners
        holder.ivGalleryImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(position, imageUrl);
            }
        });

        holder.fabRemoveImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void addImage(String imageUrl) { // add new image url to the list
        imageUrls.add(imageUrl);
        notifyItemInserted(imageUrls.size() - 1);
    }

    public void removeImage(int position) {
        if (position >= 0 && position < imageUrls.size()) {
            imageUrls.remove(position);
            notifyItemRemoved(position); // update position fo remaining iterms
            notifyItemRangeChanged(position, imageUrls.size());
        }
    }

    public void updateImages(ArrayList<String> newImageUrls) { // batch update (need when reseving images from firestore db )
        this.imageUrls.clear();
        this.imageUrls.addAll(newImageUrls);
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGalleryImage;
        FloatingActionButton fabRemoveImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.iv_gallery_image);
            fabRemoveImage = itemView.findViewById(R.id.fab_remove_image);
        }
    }
}
