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

    // Location fields
    private String city;
    private String province;

    // Additional fields for LocationDetailActivity
    private String imageUrl;
    private double latitude;
    private double longitude;

    // Field to identify placeholder entries
    private boolean isPlaceholder;

    // Firebase requires a public, no-argument constructor
    public Attraction() {
        this.images = new ArrayList<>();
    }

    // Constructor with all fields including contributor info and location
    public Attraction(String documentId, String name, String category, String description,
                     String youtubeUrl, List<String> images, String contributorName, long contributedAt,
                     String city, String province) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images != null ? images : new ArrayList<>();
        this.contributorName = contributorName;
        this.contributedAt = contributedAt;
        this.city = city;
        this.province = province;
    }

    // Constructor with all fields including contributor info
    public Attraction(String documentId, String name, String category, String description,
                     String youtubeUrl, List<String> images, String contributorName, long contributedAt) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images != null ? images : new ArrayList<>();
        this.contributorName = contributorName;
        this.contributedAt = contributedAt;
    }

    // Constructor for basic attraction
    public Attraction(String documentId, String name, String category, String description,
                     String youtubeUrl, List<String> images) {
        this.documentId = documentId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.youtubeUrl = youtubeUrl;
        this.images = images != null ? images : new ArrayList<>();
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

    public void setImages(List<String> images) {
        this.images = images != null ? images : new ArrayList<>();
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

    // New getters and setters for missing fields
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    // Utility methods
    public String getFirstImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public void addImage(String imageUrl) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(imageUrl);
    }

    public boolean hasImages() {
        return (imageUrl != null && !imageUrl.isEmpty()) ||
               (images != null && !images.isEmpty());
    }

    public boolean hasLocation() {
        return latitude != 0.0 && longitude != 0.0;
    }

    @Override
    public String toString() {
        return "Attraction{" +
                "documentId='" + documentId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", contributorName='" + contributorName + '\'' +
                ", hasImages=" + hasImages() +
                ", hasLocation=" + hasLocation() +
                '}';
    }
}