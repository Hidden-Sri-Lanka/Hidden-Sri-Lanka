package com.s23010526.hiddensrilanka;

import java.util.List;
import java.util.ArrayList;

public class Attraction {
    private String documentId;
    private String name;
    private String category;
    private String description;
    private String youtubeUrl;
    private List<String> images;

    // New fields for contributor information
    private String contributorName;
    private long contributedAt;

    // Field to identify placeholder entries
    private boolean isPlaceholder;

    // Firebase requires a public, no-argument constructor
    public Attraction() {
    }

    // Constructor with all fields including contributor info
    public Attraction(String documentId, String name, String category, String description,
                     String youtubeUrl, List<String> images, String contributorName, long contributedAt) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images;
        this.contributorName = contributorName;
        this.contributedAt = contributedAt;
    }

    // Original constructor for backward compatibility
    public Attraction(String documentId, String name, String category, String description, String youtubeUrl, List<String> images) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images;
        this.contributorName = "Unknown";
        this.contributedAt = 0;
    }

    // Getters and Setters
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

    @SuppressWarnings("unchecked")
    public void setImages(Object imagesData) {
        if (imagesData == null) {
            this.images = new ArrayList<>();
        } else if (imagesData instanceof List) {
            // If it's already a List, cast it with proper type checking
            List<?> rawList = (List<?>) imagesData;
            this.images = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof String) {
                    this.images.add((String) item);
                }
            }
        } else if (imagesData instanceof String) {
            // If it's a String, convert it to a List
            String imageString = (String) imagesData;
            if (imageString.trim().isEmpty()) {
                this.images = new ArrayList<>();
            } else if (imageString.contains(",")) {
                // Handle comma-separated string
                this.images = new ArrayList<>();
                String[] urlArray = imageString.split(",");
                for (String url : urlArray) {
                    this.images.add(url.trim());
                }
            } else {
                // Single URL
                this.images = new ArrayList<>();
                this.images.add(imageString.trim());
            }
        } else {
            // Fallback for any other data type
            this.images = new ArrayList<>();
        }
    }

    // New getters and setters for contributor information
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

    // Getter and setter for isPlaceholder
    public boolean isPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(boolean placeholder) {
        isPlaceholder = placeholder;
    }

    // Helper method to get formatted contribution date
    public String getFormattedContributionDate() {
        if (contributedAt == 0) return "Unknown";
        return new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date(contributedAt));
    }
}