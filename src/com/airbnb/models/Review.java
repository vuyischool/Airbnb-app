package com.airbnb.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Review model class
 * Represents a review and rating for a property
 */
public class Review {
    private String id;
    private String propertyId;
    private String userId;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDate date;
    
    public Review() {
        this.date = LocalDate.now();
    }
    
    public Review(String id, String propertyId, String userId, int rating, String comment) {
        this.id = id;
        this.propertyId = propertyId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDate.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Convert Review to file format string
     * Format: id|propertyId|userId|rating|comment|date
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return id + "|" + propertyId + "|" + userId + "|" + rating + "|" + 
               (comment != null ? comment.replace("|", " ") : "") + "|" + 
               (date != null ? date.format(formatter) : LocalDate.now().format(formatter));
    }
    
    /**
     * Create Review from file format string
     */
    public static Review fromFileString(String line) {
        String[] parts = line.split("\\|", 6);
        if (parts.length < 6) {
            return null;
        }
        
        Review review = new Review();
        review.setId(parts[0]);
        review.setPropertyId(parts[1]);
        review.setUserId(parts[2]);
        
        try {
            review.setRating(Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            review.setRating(5);
        }
        
        review.setComment(parts[4]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            review.setDate(LocalDate.parse(parts[5], formatter));
        } catch (Exception e) {
            review.setDate(LocalDate.now());
        }
        
        return review;
    }
    
    @Override
    public String toString() {
        return rating + " stars - " + comment;
    }
}

