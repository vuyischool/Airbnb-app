package com.airbnb.controller;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;
import com.airbnb.models.Review;
import com.airbnb.services.AuthService;
import com.airbnb.services.HostService;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * Host dashboard controller
 * Provides filtered views for host users (only their own data)
 */
public class HostDashboardController implements Initializable {
    
    // Properties table
    @FXML private TableView<Property> propertiesTable;
    @FXML private TableColumn<Property, String> propTitleCol;
    @FXML private TableColumn<Property, String> propLocationCol;
    @FXML private TableColumn<Property, Double> propPriceCol;
    @FXML private TableColumn<Property, Double> propRatingCol;
    @FXML private TableColumn<Property, String> propActionsCol;
    
    // Bookings table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingPropertyCol;
    @FXML private TableColumn<Booking, String> bookingGuestCol;
    @FXML private TableColumn<Booking, String> bookingCheckInCol;
    @FXML private TableColumn<Booking, String> bookingCheckOutCol;
    @FXML private TableColumn<Booking, Double> bookingTotalCol;
    @FXML private TableColumn<Booking, String> bookingStatusCol;
    
    // Reviews list
    @FXML private ListView<String> reviewsList;
    
    // Earnings labels
    @FXML private Label totalEarningsLabel;
    @FXML private Label pendingEarningsLabel;
    @FXML private Label bookingCountLabel;
    @FXML private Label avgRatingLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check host access
        if (!SessionManager.isHost()) {
            SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
            return;
        }
        
        String hostId = SessionManager.getCurrentUserId();
        
        // Set up tables
        setupPropertiesTable();
        setupBookingsTable();
        
        // Load data (filtered by host)
        loadMyProperties(hostId);
        loadMyBookings(hostId);
        loadMyReviews(hostId);
        loadEarnings(hostId);
    }
    
    private void setupPropertiesTable() {
        propTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        propLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        propPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        propRatingCol.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        propActionsCol.setCellValueFactory(cellData -> {
            Property property = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty("Edit | Delete");
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
        bookingPropertyCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            Property property = PropertyService.getPropertyById(booking.getPropertyId());
            return new javafx.beans.property.SimpleStringProperty(
                property != null ? property.getTitle() : booking.getPropertyId()
            );
        });
        bookingGuestCol.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            var guest = AuthService.getUserById(booking.getGuestId());
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
    
    private void loadMyProperties(String hostId) {
        // Use HostService which filters by host ID using Streams
        ObservableList<Property> properties = FXCollections.observableArrayList(
            HostService.getMyProperties(hostId)
        );
        propertiesTable.setItems(properties);
    }
    
    private void loadMyBookings(String hostId) {
        // Use HostService which filters bookings for host's properties using Streams
        ObservableList<Booking> bookings = FXCollections.observableArrayList(
            HostService.getMyBookings(hostId)
        );
        bookingsTable.setItems(bookings);
    }
    
    private void loadMyReviews(String hostId) {
        // Use HostService which filters reviews for host's properties using Streams
        List<Review> reviews = HostService.getMyReviews(hostId);
        
        List<String> reviewStrings = reviews.stream()
            .map(review -> {
                Property property = PropertyService.getPropertyById(review.getPropertyId());
                String propertyName = property != null ? property.getTitle() : "Unknown";
                String stars = "⭐".repeat(review.getRating());
                return String.format("%s - %s\n%s\n%s",
                    propertyName,
                    stars,
                    review.getComment(),
                    review.getDate()
                );
            })
            .collect(java.util.stream.Collectors.toList());
        
        reviewsList.setItems(FXCollections.observableArrayList(reviewStrings));
    }
    
    private void loadEarnings(String hostId) {
        // Use HostService which calculates earnings using Streams
        DecimalFormat df = new DecimalFormat("#.##");
        
        totalEarningsLabel.setText("Total Earnings: $" + df.format(HostService.getTotalEarnings(hostId)));
        pendingEarningsLabel.setText("Pending Earnings: $" + df.format(HostService.getPendingEarnings(hostId)));
        bookingCountLabel.setText("Total Bookings: " + HostService.getBookingCount(hostId));
        avgRatingLabel.setText("Average Rating: " + df.format(HostService.getAverageRating(hostId)));
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
}

