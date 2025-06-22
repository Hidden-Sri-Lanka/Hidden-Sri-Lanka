package com.s23010526.hiddensrilanka;

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

        // --- THIS IS THE UPDATED SECTION WITH IMAGE LOADING AND DEBUGGING ---
        if (currentAttraction.getImages() != null && !currentAttraction.getImages().isEmpty()) {
            String imageUrl = currentAttraction.getImages().get(0); // Get the URL from my  database list


            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false; // Return false so Glide can handle loading an error placeholder
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false; // Return false to allow Glide to continue its normal process
                        }
                    })
                    .into(holder.imageViewAttraction);
        } else {
            Log.w("GlideDebug", "Image URL list is null or empty for: " + currentAttraction.getName());
        }

//______________________________________________________________________________________________________________--------_
        // Click listener code
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(),
                    currentAttraction.getName() + " clicked!",
                    Toast.LENGTH_SHORT).show();
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
}