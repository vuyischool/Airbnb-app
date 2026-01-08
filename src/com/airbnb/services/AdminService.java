package com.airbnb.services;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;
import com.airbnb.models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin service
 * Provides comprehensive system views and statistics for admin users
 */
public class AdminService {
    
    /**
     * Get all users (for admin dashboard)
     */
    public static List<User> getAllUsers() {
        return AuthService.getAllUsers();
    }
    
    /**
     * Get all properties (for admin dashboard)
     */
    public static List<Property> getAllProperties() {
        return PropertyService.getAllProperties();
    }
    
    /**
     * Get all bookings (for admin dashboard)
     */
    public static List<Booking> getAllBookings() {
        return BookingService.getAllBookings();
    }
    
    /**
     * Get total number of users
     */
    public static long getTotalUsers() {
        return getAllUsers().stream().count();
    }
    
    /**
     * Get total number of properties
     */
    public static long getTotalProperties() {
        return getAllProperties().stream().count();
    }
    
    /**
     * Get total number of bookings
     */
    public static long getTotalBookings() {
        return getAllBookings().stream().count();
    }
    
    /**
     * Get number of active bookings (CONFIRMED status)
     * Uses Streams for filtering
     */
    public static long getActiveBookings() {
        return getAllBookings().stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()))
            .count();
    }
    
    /**
     * Get total revenue from completed bookings
     * Uses Streams for calculation
     */
    public static double getTotalRevenue() {
        return getAllBookings().stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();
    }
    
    /**
     * Get number of users by type
     */
    public static long getUsersByType(String userType) {
        return getAllUsers().stream()
            .filter(u -> u.getUserType().equals(userType))
            .count();
    }
    
    /**
     * Get average property rating across all properties
     * Uses Streams for calculation
     */
    public static double getAveragePropertyRating() {
        List<Property> properties = getAllProperties();
        if (properties.isEmpty()) {
            return 0.0;
        }
        
        return properties.stream()
            .mapToDouble(Property::getAverageRating)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Delete a user (admin only)
     */
    public static boolean deleteUser(String userId) {
        // Also delete user's properties, bookings, reviews, and messages
        List<Property> userProperties = PropertyService.getPropertiesByOwnerId(userId);
        for (Property property : userProperties) {
            PropertyService.deleteProperty(property.getId());
        }
        
        List<Booking> userBookings = BookingService.getBookingsByGuestId(userId);
        for (Booking booking : userBookings) {
            BookingService.deleteBooking(booking.getId());
        }
        
        return FileService.deleteLineById(FileService.USERS_FILE, userId);
    }
}

