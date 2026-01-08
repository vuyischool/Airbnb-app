package com.airbnb.services;

import com.airbnb.models.Review;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Review service
 * Handles review operations and triggers automatic rating updates
 */
public class ReviewService {
    
    /**
     * Get all reviews
     */
    public static List<Review> getAllReviews() {
        List<String> lines = FileService.readAllLines(FileService.REVIEWS_FILE);
        return lines.stream()
            .map(Review::fromFileString)
            .filter(r -> r != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Get review by ID
     */
    public static Review getReviewById(String id) {
        return getAllReviews().stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get reviews by property ID
     */
    public static List<Review> getReviewsByPropertyId(String propertyId) {
        return getAllReviews().stream()
            .filter(r -> r.getPropertyId().equals(propertyId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get reviews by user ID
     */
    public static List<Review> getReviewsByUserId(String userId) {
        return getAllReviews().stream()
            .filter(r -> r.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    /**
     * Add a new review and automatically update property rating
     */
    public static boolean addReview(Review review) {
        if (review.getId() == null || review.getId().isEmpty()) {
            review.setId(UUID.randomUUID().toString());
        }
        
        // Save review to file
        FileService.appendLine(FileService.REVIEWS_FILE, review.toFileString());
        
        // Automatically recalculate and update property rating
        PropertyService.recalculateRating(review.getPropertyId());
        
        return true;
    }
    
    /**
     * Update a review and recalculate rating
     */
    public static boolean updateReview(Review review) {
        boolean updated = FileService.updateLineById(FileService.REVIEWS_FILE, 
                                                    review.getId(), 
                                                    review.toFileString());
        if (updated) {
            // Recalculate rating after update
            PropertyService.recalculateRating(review.getPropertyId());
        }
        return updated;
    }
    
    /**
     * Delete a review and recalculate rating
     */
    public static boolean deleteReview(String id) {
        Review review = getReviewById(id);
        if (review != null) {
            String propertyId = review.getPropertyId();
            boolean deleted = FileService.deleteLineById(FileService.REVIEWS_FILE, id);
            if (deleted) {
                // Recalculate rating after deletion
                PropertyService.recalculateRating(propertyId);
            }
            return deleted;
        }
        return false;
    }
    
    /**
     * Delete all reviews for a property (when property is deleted)
     */
    public static void deleteReviewsByPropertyId(String propertyId) {
        List<Review> reviews = getReviewsByPropertyId(propertyId);
        for (Review review : reviews) {
            FileService.deleteLineById(FileService.REVIEWS_FILE, review.getId());
        }
    }
}

