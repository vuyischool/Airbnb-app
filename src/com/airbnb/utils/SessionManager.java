package com.airbnb.utils;

import com.airbnb.models.User;

/**
 * Session manager for simulating user sessions
 * Uses static currentUser to maintain session state
 */
public class SessionManager {
    
    private static User currentUser = null;
    
    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user is an admin
     */
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getUserType());
    }
    
    /**
     * Check if current user is a host
     */
    public static boolean isHost() {
        return currentUser != null && "HOST".equals(currentUser.getUserType());
    }
    
    /**
     * Check if current user is a guest
     */
    public static boolean isGuest() {
        return currentUser != null && "GUEST".equals(currentUser.getUserType());
    }
    
    /**
     * Logout the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Get current user ID
     */
    public static String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}

