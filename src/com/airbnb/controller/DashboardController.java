package com.airbnb.controller;

import com.airbnb.models.User;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main dashboard controller
 * Handles navigation based on user role
 */
public class DashboardController implements Initializable {
    
    @FXML private Label userLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button logoutButton;
    @FXML private Button searchButton;
    @FXML private Button myBookingsButton;
    @FXML private Button messagesButton;
    @FXML private Button hostDashboardButton;
    @FXML private Button addPropertyButton;
    @FXML private Button adminDashboardButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to login
            SceneManager.switchScene("/com/airbnb/views/Login.fxml", "Airbnb System - Login");
            return;
        }
        
        // Update UI based on user role
        userLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getUserType() + ")");
        welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        
        // Show/hide buttons based on role
        if (SessionManager.isAdmin()) {
            adminDashboardButton.setVisible(true);
            hostDashboardButton.setVisible(false);
            addPropertyButton.setVisible(false);
            roleLabel.setText("You are logged in as an Administrator");
        } else if (SessionManager.isHost()) {
            hostDashboardButton.setVisible(true);
            addPropertyButton.setVisible(true);
            adminDashboardButton.setVisible(false);
            roleLabel.setText("You are logged in as a Host");
        } else {
            hostDashboardButton.setVisible(false);
            addPropertyButton.setVisible(false);
            adminDashboardButton.setVisible(false);
            roleLabel.setText("You are logged in as a Guest");
        }
        
        // Set up event handlers using lambdas
        logoutButton.setOnAction(e -> handleLogout());
        searchButton.setOnAction(e -> handleSearchProperties());
        myBookingsButton.setOnAction(e -> handleMyBookings());
        messagesButton.setOnAction(e -> handleMessages());
        hostDashboardButton.setOnAction(e -> handleHostDashboard());
        addPropertyButton.setOnAction(e -> handleAddProperty());
        adminDashboardButton.setOnAction(e -> handleAdminDashboard());
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.logout();
        SceneManager.switchScene("/com/airbnb/views/Login.fxml", "Airbnb System - Login");
    }
    
    @FXML
    private void handleSearchProperties() {
        SceneManager.switchScene("/com/airbnb/views/PropertyList.fxml", "Airbnb System - Search Properties");
    }
    
    @FXML
    private void handleMyBookings() {
        SceneManager.switchScene("/com/airbnb/views/Booking.fxml", "Airbnb System - My Bookings");
    }
    
    @FXML
    private void handleMessages() {
        SceneManager.switchScene("/com/airbnb/views/Messages.fxml", "Airbnb System - Messages");
    }
    
    @FXML
    private void handleHostDashboard() {
        SceneManager.switchScene("/com/airbnb/views/HostDashboard.fxml", "Airbnb System - Host Dashboard");
    }
    
    @FXML
    private void handleAddProperty() {
        SceneManager.switchScene("/com/airbnb/views/PropertyDetail.fxml", "Airbnb System - Add Property");
    }
    
    @FXML
    private void handleAdminDashboard() {
        SceneManager.switchScene("/com/airbnb/views/AdminDashboard.fxml", "Airbnb System - Admin Dashboard");
    }
}

