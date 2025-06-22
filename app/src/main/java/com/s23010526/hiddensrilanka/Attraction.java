package com.s23010526.hiddensrilanka;

import java.util.List;

public class Attraction {

    // --- NEW FIELD ---
    private String documentId;

    // --- Existing Fields ---
    private String name;
    private String category;
    private String description;
    private String youtubeUrl;
    private List<String> images;

    // Firebase requires a public, no-argument constructor
    public Attraction() {
    }

    // --- NEW GETTER AND SETTER METHODS ---
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    // --- END OF NEW METHODS ---


    // --- Existing Getters and Setters ---
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
        this.images = images;
    }
}