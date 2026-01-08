package com.airbnb.controller;

import com.airbnb.models.Booking;
import com.airbnb.models.Property;
import com.airbnb.services.BookingService;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Booking controller
 * Handles property booking and booking history
 */
public class BookingController implements Initializable {
    
    @FXML private ComboBox<Property> propertyCombo;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Label totalPriceLabel;
    @FXML private Button bookButton;
    @FXML private Label bookingStatusLabel;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> propertyCol;
    @FXML private TableColumn<Booking, LocalDate> checkInCol;
    @FXML private TableColumn<Booking, LocalDate> checkOutCol;
    @FXML private TableColumn<Booking, Double> totalPriceCol;
    @FXML private TableColumn<Booking, String> statusCol;
    
    private ObservableList<Booking> bookingsList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers using lambdas
        bookButton.setOnAction(e -> handleBook());
        propertyCombo.setOnAction(e -> calculateTotalPrice());
        checkInDatePicker.setOnAction(e -> calculateTotalPrice());
        checkOutDatePicker.setOnAction(e -> calculateTotalPrice());
        
        // Load properties into combo box
        loadProperties();
        
        // Set up table columns
        propertyCol.setCellValueFactory(cellData -> {
            Property property = PropertyService.getPropertyById(cellData.getValue().getPropertyId());
            return new javafx.beans.property.SimpleStringProperty(
                property != null ? property.getTitle() : "Unknown"
            );
        });
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format total price column
        totalPriceCol.setCellFactory(column -> new TableCell<Booking, Double>() {
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
        
        // Load bookings
        loadBookings();
    }
    
    private void loadProperties() {
        List<Property> properties = PropertyService.getAllProperties();
        propertyCombo.setItems(FXCollections.observableArrayList(properties));
        propertyCombo.setCellFactory(param -> new ListCell<Property>() {
            @Override
            protected void updateItem(Property property, boolean empty) {
                super.updateItem(property, empty);
                if (empty || property == null) {
                    setText(null);
                } else {
                    setText(property.getTitle() + " - " + property.getLocation() + " ($" + property.getPrice() + "/night)");
                }
            }
        });
    }
    
    private void calculateTotalPrice() {
        Property selectedProperty = propertyCombo.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        
        if (selectedProperty != null && checkIn != null && checkOut != null && checkIn.isBefore(checkOut)) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            double total = selectedProperty.getPrice() * nights;
            totalPriceLabel.setText("Total Price: $" + new DecimalFormat("#.##").format(total));
        } else {
            totalPriceLabel.setText("Total Price: $0.00");
        }
    }
    
    @FXML
    private void handleBook() {
        Property selectedProperty = propertyCombo.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        
        if (selectedProperty == null) {
            showStatus("Please select a property", false);
            return;
        }
        
        if (checkIn == null || checkOut == null) {
            showStatus("Please select check-in and check-out dates", false);
            return;
        }
        
        if (!checkIn.isBefore(checkOut)) {
            showStatus("Check-out date must be after check-in date", false);
            return;
        }
        
        String guestId = SessionManager.getCurrentUserId();
        if (BookingService.createBooking(selectedProperty.getId(), guestId, checkIn, checkOut)) {
            showStatus("Booking confirmed successfully!", true);
            loadBookings();
            // Clear form
            propertyCombo.setValue(null);
            checkInDatePicker.setValue(null);
            checkOutDatePicker.setValue(null);
        } else {
            showStatus("Property not available for selected dates", false);
        }
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
    
    private void loadBookings() {
        String userId = SessionManager.getCurrentUserId();
        
        // Use Streams to filter bookings by user
        List<Booking> bookings = BookingService.getBookingsByGuestId(userId);
        bookingsList = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(bookingsList);
    }
    
    private void showStatus(String message, boolean success) {
        bookingStatusLabel.setText(message);
        bookingStatusLabel.setVisible(true);
        bookingStatusLabel.setTextFill(success ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }
}

