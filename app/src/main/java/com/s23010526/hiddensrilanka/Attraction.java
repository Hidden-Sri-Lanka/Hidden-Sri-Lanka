package com.s23010526.hiddensrilanka;

import androidx.annotation.NonNull;
import java.util.List;
import java.util.ArrayList;


public class Attraction { // class
    // Core identification and content fields
    // followings will store allthe data foreach attractions
    private String documentId;      // Firebase document ID for database operations
    private String name;           // Primary attraction name (eg: "Udawalawe National Park","Open University Sri Lanka")
    private String category;       // Type classification (Historical, Waterfall, Beach, etc.)
    private String description;    // Detailed description for display
    private String youtubeUrl;     // Optional video content URL
    private List<String> images;   // Collection of image URLs (supports multiple photos)

    // Contributor tracking fields - demonstrates community aspect
    private String contributorName; // User who added this attraction
    private long contributedAt;     // Timestamp of contribution (Unix timestamp)

    // Geographic classification fields - supports location-based queries
    private String city;           // City/town location (e.g., "Embilipitiya")
    private String province;       // Administrative province in Sri Lanka

    // Enhanced location fields for navigation and mapping
    private String imageUrl;       // Primary image URL (for backward compatibility)
    private double latitude;       // GPS coordinate for precise location
    private double longitude;      // GPS coordinate for precise location

    // Special state field for UI placeholder handling
    private boolean isPlaceholder; // Indicates "grow our database" entries


    public Attraction() { // this is the defoult container (runs wehn new attraction is created without no aguments )
        // never be null
        // Initialize collections to prevent null pointer exceptions
        this.images = new ArrayList<>();
    }

   //constructer overload
    public Attraction(String documentId,
                      String name,
                      String category,
                      String description,
                      String youtubeUrl,
                      List<String> images,
                      String contributorName,
                      long contributedAt,
                      String city,
                      String province
    ) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        // Null-safe initialization - prevents runtime errors
        this.images = images != null ? images : new ArrayList<>();
        this.contributorName = contributorName;
        this.contributedAt = contributedAt;
        this.city = city;
        this.province = province;
    }

    public Attraction(String documentId,
                      String name,
                      String category,
                      String description,
                      String youtubeUrl,
                      List<String> images,
                      String contributorName,
                      long contributedAt
    ) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images != null ? images : new ArrayList<>();
        this.contributorName = contributorName;
        this.contributedAt = contributedAt;
    }

    public Attraction(String documentId,
                      String name,
                      String category,
                      String description,
                      String youtubeUrl,
                      List<String> images
    ) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images != null ? images : new ArrayList<>();
    }

    // Getters and Setters
    // getter let us read privet fealds
    // setter let us change privet fealds
    // this is encaptiulation fealds stay privet but we can expose controlledaccess
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public List<String> getImages() {
        return images;
    }


//      Setter for images that handles both old String format and new List<String> format
//      This ensures backward compatibility with existing Firestore data
//     Reson for this old db data i have created with manual data entry but after i have implimented form for that and i have to support both
    @SuppressWarnings("unchecked")
    public void setImages(Object images) {
        if (images instanceof List) {
            this.images = (List<String>) images;
        } else if (images instanceof String) {
            String imageUrl = (String) images;
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                this.images = new ArrayList<>();
                this.images.add(imageUrl);
            }
        } else {
            // Fallback: Initialize empty list
            this.images = new ArrayList<>();
        }
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public long getContributedAt() {
        return contributedAt;
    }

    public void setContributedAt(long contributedAt) {
        this.contributedAt = contributedAt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    // Backward compatibility getter for single image URL
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0); // Return first image for backward compatibility
        }
        return imageUrl; // Fallback to old field
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        // Also add to images list if not already there
        if (images == null) {
            images = new ArrayList<>();
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty() && !images.contains(imageUrl)) {
            images.add(imageUrl);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(boolean placeholder) {
        isPlaceholder = placeholder;
    }


//      Utility method to get the primary image URL
//      Returns the first image from the list, or null if no images
//
    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }


//      Utility method to check if the attraction has any images
//
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }


//      Utility method to get the number of images
//
    public int getImageCount() {
        return images != null ? images.size() : 0;
    }

    public boolean hasLocation() {
        return latitude != 0.0 && longitude != 0.0;
    }

    @NonNull
    @Override
    public String toString() { // how objects are shown in logcat and debugger
        return "Attraction{" +
                "documentId='" + documentId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}