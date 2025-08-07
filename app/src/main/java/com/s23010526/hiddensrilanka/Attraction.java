package com.s23010526.hiddensrilanka;

import java.util.List;
import java.util.ArrayList;

/**
 * Attraction - Data model class for tourist attractions in Sri Lanka
 *
 * This class demonstrates key object-oriented programming and Android development concepts:
 * 1. Data encapsulation with private fields and public getters/setters
 * 2. Firebase integration with proper constructor patterns
 * 3. Flexible data structure supporting multiple use cases
 * 4. Location-based data modeling with coordinates and administrative divisions
 * 5. Support for placeholder entries and dynamic content loading
 *
 * Academic Learning Outcomes:
 * - Object-oriented design principles
 * - Database integration patterns
 * - Data validation and type safety
 * - Memory-efficient collection usage
 * - Flexible architecture for different data sources
 */
public class Attraction {
    // Core identification and content fields
    private String documentId;      // Firebase document ID for database operations
    private String name;           // Primary attraction name (e.g., "Udawalawe National Park")
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

    /**
     * Default constructor required by Firebase
     *
     * Firebase requires a public, no-argument constructor for automatic
     * object serialization/deserialization from Firestore documents.
     *
     * Academic Learning: Understanding framework requirements and
     * designing classes that work with external libraries.
     */
    public Attraction() {
        // Initialize collections to prevent null pointer exceptions
        this.images = new ArrayList<>();
    }

    /**
     * Comprehensive constructor for creating complete attraction objects
     *
     * This constructor demonstrates proper object initialization with
     * all required fields and null-safety for optional parameters.
     *
     * @param documentId Firebase document identifier
     * @param name Attraction name
     * @param category Attraction type/category
     * @param description Detailed description
     * @param youtubeUrl Optional video URL
     * @param images List of image URLs
     * @param contributorName User who added this attraction
     * @param contributedAt Timestamp of contribution
     * @param city City location
     * @param province Province location
     */
    public Attraction(String documentId, String name, String category, String description,
                     String youtubeUrl, List<String> images, String contributorName, long contributedAt,
                     String city, String province) {
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

    /**
     * Constructor with all fields including contributor info
     *
     * This constructor is used when contributor information is available,
     * but detailed location data is not required.
     *
     * @param documentId Firebase document identifier
     * @param name Attraction name
     * @param category Attraction type/category
     * @param description Detailed description
     * @param youtubeUrl Optional video URL
     * @param images List of image URLs
     * @param contributorName User who added this attraction
     * @param contributedAt Timestamp of contribution
     */
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

    /**
     * Constructor for basic attraction
     *
     * This constructor is used for creating attraction objects with
     * only the essential information. Ideal for placeholder entries
     * or simplified views.
     *
     * @param documentId Firebase document identifier
     * @param name Attraction name
     * @param category Attraction type/category
     * @param description Detailed description
     * @param youtubeUrl Optional video URL
     * @param images List of image URLs
     */
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