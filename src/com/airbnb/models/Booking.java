package com.airbnb.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Booking model class
 * Represents a booking made by a guest
 */
public class Booking {
    private String id;
    private String propertyId;
    private String guestId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    
    public Booking() {
        this.status = "PENDING";
    }
    
    public Booking(String id, String propertyId, String guestId, 
                  LocalDate checkIn, LocalDate checkOut, double totalPrice) {
        this.id = id;
        this.propertyId = propertyId;
        this.guestId = guestId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }
    
    /**
     * Calculate total price based on property price and number of nights
     */
    public void calculateTotalPrice(double propertyPrice) {
        if (checkIn != null && checkOut != null && checkIn.isBefore(checkOut)) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            this.totalPrice = propertyPrice * nights;
        }
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
    
    public String getGuestId() {
        return guestId;
    }
    
    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }
    
    public LocalDate getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    
    public LocalDate getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Convert Booking to file format string
     * Format: id|propertyId|guestId|checkIn|checkOut|totalPrice|status
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return id + "|" + propertyId + "|" + guestId + "|" + 
               (checkIn != null ? checkIn.format(formatter) : "") + "|" + 
               (checkOut != null ? checkOut.format(formatter) : "") + "|" + 
               totalPrice + "|" + status;
    }
    
    /**
     * Create Booking from file format string
     */
    public static Booking fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 7) {
            return null;
        }
        
        Booking booking = new Booking();
        booking.setId(parts[0]);
        booking.setPropertyId(parts[1]);
        booking.setGuestId(parts[2]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            if (!parts[3].isEmpty()) {
                booking.setCheckIn(LocalDate.parse(parts[3], formatter));
            }
            if (!parts[4].isEmpty()) {
                booking.setCheckOut(LocalDate.parse(parts[4], formatter));
            }
        } catch (Exception e) {
            // Invalid date format
        }
        
        try {
            booking.setTotalPrice(Double.parseDouble(parts[5]));
        } catch (NumberFormatException e) {
            booking.setTotalPrice(0.0);
        }
        
        booking.setStatus(parts[6]);
        
        return booking;
    }
    
    @Override
    public String toString() {
        return "Booking #" + id + " - " + checkIn + " to " + checkOut + " ($" + totalPrice + ")";
    }
}

