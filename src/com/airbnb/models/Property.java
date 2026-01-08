package com.airbnb.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Property model class
 * Represents a property listing with automatic rating calculation
 */
public class Property {
    private String id;
    private String title;
    private String description;
    private String location;
    private double price;
    private String ownerId;
    private double averageRating; // Automatically calculated
    private String imagePath;
    private String availableDates; // Format: "2024-01-01,2024-01-15" or "all"
    private List<Review> reviews; // For in-memory rating calculation
    
    public Property() {
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
    }
    
    public Property(String id, String title, String description, String location, 
                   double price, String ownerId, String imagePath, String availableDates) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.ownerId = ownerId;
        this.imagePath = imagePath;
        this.availableDates = availableDates;
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
    }
    
    /**
     * Calculate average rating from reviews using Streams
     * This is called automatically when a review is added
     */
    public void calculateAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            this.averageRating = 0.0;
            return;
        }
        
        // Use Java Streams to calculate average
        this.averageRating = reviews.stream()
            .mapToDouble(Review::getRating)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Add a review and automatically update the average rating
     */
    public void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        calculateAverageRating(); // Auto-update rating
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getAvailableDates() {
        return availableDates;
    }
    
    public void setAvailableDates(String availableDates) {
        this.availableDates = availableDates;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        calculateAverageRating(); // Recalculate when reviews are set
    }
    
    /**
     * Convert Property to file format string
     * Format: id|title|description|location|price|ownerId|averageRating|imagePath|availableDates
     */
    public String toFileString() {
        return id + "|" + title + "|" + description + "|" + location + "|" + 
               price + "|" + ownerId + "|" + averageRating + "|" + 
               (imagePath != null ? imagePath : "") + "|" + 
               (availableDates != null ? availableDates : "all");
    }
    
    /**
     * Create Property from file format string
     */
    public static Property fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            return null;
        }
        
        Property property = new Property();
        property.setId(parts[0]);
        property.setTitle(parts[1]);
        property.setDescription(parts[2]);
        property.setLocation(parts[3]);
        
        try {
            property.setPrice(Double.parseDouble(parts[4]));
        } catch (NumberFormatException e) {
            property.setPrice(0.0);
        }
        
        property.setOwnerId(parts[5]);
        
        // Parse averageRating if present
        if (parts.length > 6 && !parts[6].isEmpty()) {
            try {
                property.setAverageRating(Double.parseDouble(parts[6]));
            } catch (NumberFormatException e) {
                property.setAverageRating(0.0);
            }
        }
        
        // Parse imagePath if present
        if (parts.length > 7) {
            property.setImagePath(parts[7]);
        }
        
        // Parse availableDates if present
        if (parts.length > 8) {
            property.setAvailableDates(parts[8]);
        } else {
            property.setAvailableDates("all");
        }
        
        return property;
    }
    
    @Override
    public String toString() {
        return title + " - " + location + " ($" + price + "/night)";
    }
}

