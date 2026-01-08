package com.airbnb.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * User model class
 * Represents a user in the system (Guest, Host, or Admin)
 */
public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash; // SHA-256 hashed password
    private String userType; // GUEST, HOST, or ADMIN
    private LocalDate registrationDate;
    
    public User() {
    }
    
    public User(String id, String username, String email, String passwordHash, String userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.registrationDate = LocalDate.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    /**
     * Convert User to file format string
     * Format: id|username|email|passwordHash|userType|registrationDate
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return id + "|" + username + "|" + email + "|" + passwordHash + "|" + 
               userType + "|" + (registrationDate != null ? registrationDate.format(formatter) : "");
    }
    
    /**
     * Create User from file format string
     */
    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) {
            return null;
        }
        
        User user = new User();
        user.setId(parts[0]);
        user.setUsername(parts[1]);
        user.setEmail(parts[2]);
        user.setPasswordHash(parts[3]);
        user.setUserType(parts[4]);
        
        if (parts.length > 5 && !parts[5].isEmpty()) {
            try {
                user.setRegistrationDate(LocalDate.parse(parts[5], DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception e) {
                user.setRegistrationDate(LocalDate.now());
            }
        } else {
            user.setRegistrationDate(LocalDate.now());
        }
        
        return user;
    }
    
    @Override
    public String toString() {
        return username + " (" + userType + ")";
    }
}

