package com.airbnb.services;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Booking service
 * Handles booking operations
 */
public class BookingService {
    
    /**
     * Get all bookings
     */
    public static List<Booking> getAllBookings() {
        List<String> lines = FileService.readAllLines(FileService.BOOKINGS_FILE);
        return lines.stream()
            .map(Booking::fromFileString)
            .filter(b -> b != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Get booking by ID
     */
    public static Booking getBookingById(String id) {
        return getAllBookings().stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get bookings by guest ID
     */
    public static List<Booking> getBookingsByGuestId(String guestId) {
        return getAllBookings().stream()
            .filter(b -> b.getGuestId().equals(guestId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get bookings for a property
     */
    public static List<Booking> getBookingsByPropertyId(String propertyId) {
        return getAllBookings().stream()
            .filter(b -> b.getPropertyId().equals(propertyId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get bookings for properties owned by a host
     * Uses Streams with nested filtering
     */
    public static List<Booking> getBookingsByHostId(String hostId) {
        return getAllBookings().stream()
            .filter(b -> {
                Property property = PropertyService.getPropertyById(b.getPropertyId());
                return property != null && property.getOwnerId().equals(hostId);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Check if property is available for date range
     */
    public static boolean isPropertyAvailable(String propertyId, LocalDate checkIn, LocalDate checkOut) {
        Property property = PropertyService.getPropertyById(propertyId);
        if (property == null) {
            return false;
        }
        
        // Check existing bookings
        List<Booking> bookings = getBookingsByPropertyId(propertyId);
        return bookings.stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()) || "PENDING".equals(b.getStatus()))
            .noneMatch(b -> {
                LocalDate bCheckIn = b.getCheckIn();
                LocalDate bCheckOut = b.getCheckOut();
                return !(checkOut.isBefore(bCheckIn) || checkIn.isAfter(bCheckOut));
            });
    }
    
    /**
     * Create a new booking
     */
    public static boolean createBooking(String propertyId, String guestId, 
                                       LocalDate checkIn, LocalDate checkOut) {
        // Check availability
        if (!isPropertyAvailable(propertyId, checkIn, checkOut)) {
            return false;
        }
        
        Property property = PropertyService.getPropertyById(propertyId);
        if (property == null) {
            return false;
        }
        
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());
        booking.setPropertyId(propertyId);
        booking.setGuestId(guestId);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.calculateTotalPrice(property.getPrice());
        booking.setStatus("CONFIRMED");
        
        FileService.appendLine(FileService.BOOKINGS_FILE, booking.toFileString());
        return true;
    }
    
    /**
     * Update booking status
     */
    public static boolean updateBooking(Booking booking) {
        return FileService.updateLineById(FileService.BOOKINGS_FILE, 
                                         booking.getId(), 
                                         booking.toFileString());
    }
    
    /**
     * Delete booking
     */
    public static boolean deleteBooking(String id) {
        return FileService.deleteLineById(FileService.BOOKINGS_FILE, id);
    }
    
    /**
     * Delete all bookings for a property (when property is deleted)
     */
    public static void deleteBookingsByPropertyId(String propertyId) {
        List<Booking> bookings = getBookingsByPropertyId(propertyId);
        for (Booking booking : bookings) {
            deleteBooking(booking.getId());
        }
    }
}

