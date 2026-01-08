package com.airbnb.controller;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;
import com.airbnb.models.User;
import com.airbnb.services.AdminService;
import com.airbnb.services.AuthService;
import com.airbnb.services.PropertyService;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * Admin dashboard controller
 * Provides comprehensive system views and statistics
 */
public class AdminDashboardController implements Initializable {
    
    // Statistics labels
    @FXML private Label totalUsersLabel;
    @FXML private Label totalPropertiesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label activeBookingsLabel;
    
    // Users table
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userIdCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> userTypeCol;
    @FXML private TableColumn<User, String> regDateCol;
    
    // Properties table
    @FXML private TableView<Property> propertiesTable;
    @FXML private TableColumn<Property, String> propIdCol;
    @FXML private TableColumn<Property, String> propTitleCol;
    @FXML private TableColumn<Property, String> propLocationCol;
    @FXML private TableColumn<Property, Double> propPriceCol;
    @FXML private TableColumn<Property, Double> propRatingCol;
    @FXML private TableColumn<Property, String> propOwnerCol;
    
    // Bookings table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdCol;
    @FXML private TableColumn<Booking, String> bookingPropertyCol;
    @FXML private TableColumn<Booking, String> bookingGuestCol;
    @FXML private TableColumn<Booking, String> bookingCheckInCol;
    @FXML private TableColumn<Booking, String> bookingCheckOutCol;
    @FXML private TableColumn<Booking, Double> bookingTotalCol;
    @FXML private TableColumn<Booking, String> bookingStatusCol;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check admin access
        if (!SessionManager.isAdmin()) {
            SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
            return;
        }
        
        // Load statistics
        loadStatistics();
        
        // Set up tables
        setupUsersTable();
        setupPropertiesTable();
        setupBookingsTable();
        
        // Load data
        loadUsers();
        loadProperties();
        loadBookings();
    }
    
    private void loadStatistics() {
        // Use AdminService which uses Streams internally
        totalUsersLabel.setText("Total Users: " + AdminService.getTotalUsers());
        totalPropertiesLabel.setText("Total Properties: " + AdminService.getTotalProperties());
        totalBookingsLabel.setText("Total Bookings: " + AdminService.getTotalBookings());
        
        DecimalFormat df = new DecimalFormat("#.##");
        totalRevenueLabel.setText("Total Revenue: $" + df.format(AdminService.getTotalRevenue()));
        avgRatingLabel.setText("Average Property Rating: " + df.format(AdminService.getAveragePropertyRating()));
        activeBookingsLabel.setText("Active Bookings: " + AdminService.getActiveBookings());
    }
    
    private void setupUsersTable() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        regDateCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                user.getRegistrationDate() != null ? user.getRegistrationDate().toString() : ""
            );
        });
    }
    
    private void setupPropertiesTable() {
        propIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        propTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        propLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        propPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        propRatingCol.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        propOwnerCol.setCellValueFactory(cellData -> {
            Property property = cellData.getValue();
            User owner = AuthService.getUserById(property.getOwnerId());
            return new javafx.beans.property.SimpleStringProperty(
                owner != null ? owner.getUsername() : property.getOwnerId()
            );
        });
        
        // Format price and rating columns
        propPriceCol.setCellFactory(column -> new TableCell<Property, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + new DecimalFormat("#.##").format(price));
                }
            }
        });
        
        propRatingCol.setCellFactory(column -> new TableCell<Property, Double>() {
            @Override
            protected void updateItem(Double rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) {
                    setText(null);
                } else {
                    setText(new DecimalFormat("#.#").format(rating) + "/5.0");
                }
            }
        });
    }
    
    private void setupBookingsTable() {
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingPropertyCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            Property property = PropertyService.getPropertyById(booking.getPropertyId());
            return new javafx.beans.property.SimpleStringProperty(
                property != null ? property.getTitle() : booking.getPropertyId()
            );
        });
        bookingGuestCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            User guest = AuthService.getUserById(booking.getGuestId());
            return new javafx.beans.property.SimpleStringProperty(
                guest != null ? guest.getUsername() : booking.getGuestId()
            );
        });
        bookingCheckInCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                booking.getCheckIn() != null ? booking.getCheckIn().toString() : ""
            );
        });
        bookingCheckOutCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                booking.getCheckOut() != null ? booking.getCheckOut().toString() : ""
            );
        });
        bookingTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        bookingStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format total price column
        bookingTotalCol.setCellFactory(column -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + new DecimalFormat("#.##").format(price));
                }
            }
        });
    }
    
    private void loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(AdminService.getAllUsers());
        usersTable.setItems(users);
    }
    
    private void loadProperties() {
        ObservableList<Property> properties = FXCollections.observableArrayList(AdminService.getAllProperties());
        propertiesTable.setItems(properties);
    }
    
    private void loadBookings() {
        ObservableList<Booking> bookings = FXCollections.observableArrayList(AdminService.getAllBookings());
        bookingsTable.setItems(bookings);
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
}

