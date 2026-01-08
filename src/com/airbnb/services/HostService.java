package com.airbnb.services;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;
import com.airbnb.models.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Host service
 * Provides filtered views for host users (only their own data)
 */
public class HostService {
    
    /**
     * Get properties owned by a host
     * Uses Streams for filtering
     */
    public static List<Property> getMyProperties(String hostId) {
        return PropertyService.getAllProperties().stream()
            .filter(p -> p.getOwnerId().equals(hostId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get bookings for properties owned by a host
     * Uses Streams with nested filtering
     */
    public static List<Booking> getMyBookings(String hostId) {
        return BookingService.getAllBookings().stream()
            .filter(b -> {
                Property property = PropertyService.getPropertyById(b.getPropertyId());
                return property != null && property.getOwnerId().equals(hostId);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get reviews for properties owned by a host
     * Uses Streams with nested filtering
     */
    public static List<Review> getMyReviews(String hostId) {
        List<Property> myProperties = getMyProperties(hostId);
        List<String> propertyIds = myProperties.stream()
            .map(Property::getId)
            .collect(Collectors.toList());
        
        return ReviewService.getAllReviews().stream()
            .filter(r -> propertyIds.contains(r.getPropertyId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate total earnings from completed bookings
     * Uses Streams for calculation
     */
    public static double getTotalEarnings(String hostId) {
        return getMyBookings(hostId).stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();
    }
    
    /**
     * Calculate pending earnings from confirmed bookings
     * Uses Streams for calculation
     */
    public static double getPendingEarnings(String hostId) {
        return getMyBookings(hostId).stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();
    }
    
    /**
     * Get number of bookings for host's properties
     */
    public static long getBookingCount(String hostId) {
        return getMyBookings(hostId).stream().count();
    }
    
    /**
     * Get average rating for host's properties
     * Uses Streams for calculation
     */
    public static double getAverageRating(String hostId) {
        List<Property> myProperties = getMyProperties(hostId);
        if (myProperties.isEmpty()) {
            return 0.0;
        }
        
        return myProperties.stream()
            .mapToDouble(Property::getAverageRating)
            .average()
            .orElse(0.0);
    }
}

