package com.airbnb.services;

import com.airbnb.models.Property;
import com.airbnb.models.Review;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Property service
 * Handles property CRUD operations and rating calculations
 */
public class PropertyService {
    
    /**
     * Get all properties
     */
    public static List<Property> getAllProperties() {
        List<String> lines = FileService.readAllLines(FileService.LISTINGS_FILE);
        List<Property> properties = lines.stream()
            .map(Property::fromFileString)
            .filter(p -> p != null)
            .collect(Collectors.toList());
        
        // Load reviews for each property to calculate ratings
        for (Property property : properties) {
            List<Review> reviews = ReviewService.getReviewsByPropertyId(property.getId());
            property.setReviews(reviews);
            property.calculateAverageRating();
        }
        
        return properties;
    }
    
    /**
     * Get property by ID
     */
    public static Property getPropertyById(String id) {
        return getAllProperties().stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get properties by owner ID
     */
    public static List<Property> getPropertiesByOwnerId(String ownerId) {
        return getAllProperties().stream()
            .filter(p -> p.getOwnerId().equals(ownerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Search properties by location, price range, and rating
     * Uses Java Streams for filtering
     */
    public static List<Property> searchProperties(String location, Double maxPrice, Double minRating) {
        return getAllProperties().stream()
            .filter(p -> location == null || location.isEmpty() || 
                        p.getLocation().toLowerCase().contains(location.toLowerCase()))
            .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
            .filter(p -> minRating == null || p.getAverageRating() >= minRating)
            .collect(Collectors.toList());
    }
    
    /**
     * Add a new property
     */
    public static boolean addProperty(Property property) {
        if (property.getId() == null || property.getId().isEmpty()) {
            property.setId(UUID.randomUUID().toString());
        }
        FileService.appendLine(FileService.LISTINGS_FILE, property.toFileString());
        return true;
    }
    
    /**
     * Update a property
     */
    public static boolean updateProperty(Property property) {
        return FileService.updateLineById(FileService.LISTINGS_FILE, 
                                         property.getId(), 
                                         property.toFileString());
    }
    
    /**
     * Delete a property
     */
    public static boolean deleteProperty(String id) {
        // Also delete associated reviews and bookings
        ReviewService.deleteReviewsByPropertyId(id);
        BookingService.deleteBookingsByPropertyId(id);
        return FileService.deleteLineById(FileService.LISTINGS_FILE, id);
    }
    
    /**
     * Update property rating (called automatically when review is added)
     */
    public static void updateRating(String propertyId, double averageRating) {
        Property property = getPropertyById(propertyId);
        if (property != null) {
            property.setAverageRating(averageRating);
            updateProperty(property);
        }
    }
    
    /**
     * Recalculate and update rating for a property
     * This is called after adding a new review
     */
    public static void recalculateRating(String propertyId) {
        Property property = getPropertyById(propertyId);
        if (property != null) {
            List<Review> reviews = ReviewService.getReviewsByPropertyId(propertyId);
            property.setReviews(reviews);
            property.calculateAverageRating();
            updateProperty(property);
        }
    }
}

