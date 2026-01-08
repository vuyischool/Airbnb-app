package com.airbnb.services;

import com.airbnb.models.User;
import com.airbnb.utils.SecurityUtil;
import com.airbnb.utils.ValidationUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication service
 * Handles user registration and login
 */
public class AuthService {
    
    /**
     * Register a new user
     */
    public static boolean register(String username, String email, String password, String userType) {
        // Validate inputs
        if (!ValidationUtil.isValidUsername(username)) {
            return false;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return false;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return false;
        }
        
        // Check if username or email already exists
        List<User> users = getAllUsers();
        boolean exists = users.stream()
            .anyMatch(u -> u.getUsername().equalsIgnoreCase(username) || 
                         u.getEmail().equalsIgnoreCase(email));
        
        if (exists) {
            return false;
        }
        
        // Create new user
        String id = UUID.randomUUID().toString();
        String passwordHash = SecurityUtil.hashPassword(password);
        User user = new User(id, username, email, passwordHash, userType);
        
        // Save to file
        FileService.appendLine(FileService.USERS_FILE, user.toFileString());
        return true;
    }
    
    /**
     * Login a user
     */
    public static User login(String username, String password) {
        List<User> users = getAllUsers();
        
        // Use Streams to find matching user
        return users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .filter(u -> SecurityUtil.verifyPassword(password, u.getPasswordHash()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all users
     */
    public static List<User> getAllUsers() {
        List<String> lines = FileService.readAllLines(FileService.USERS_FILE);
        return lines.stream()
            .map(User::fromFileString)
            .filter(u -> u != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user by ID
     */
    public static User getUserById(String id) {
        return getAllUsers().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get user by username
     */
    public static User getUserByUsername(String username) {
        return getAllUsers().stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null);
    }
}

