package com.airbnb.controller;

import com.airbnb.models.Property;
import com.airbnb.services.PropertyService;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Property detail controller
 * Handles property creation, editing, and booking
 */
public class PropertyDetailController implements Initializable {
    
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField priceField;
    @FXML private TextField imagePathField;
    @FXML private TextField availableDatesField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button bookButton;
    @FXML private Button reviewsButton;
    @FXML private Label statusLabel;
    
    private Property currentProperty;
    private boolean isEditMode = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers using lambdas
        saveButton.setOnAction(e -> handleSave());
        deleteButton.setOnAction(e -> handleDelete());
        bookButton.setOnAction(e -> handleBook());
        reviewsButton.setOnAction(e -> handleViewReviews());
        
        // Check if user is host (can add/edit) or guest (can book)
        if (SessionManager.isHost()) {
            bookButton.setVisible(false);
            deleteButton.setVisible(true);
        } else if (SessionManager.isGuest()) {
            deleteButton.setVisible(false);
            bookButton.setVisible(true);
        }
    }
    
    @FXML
    private void handleSave() {
        if (!SessionManager.isHost()) {
            showStatus("Only hosts can add/edit properties", false);
            return;
        }
        
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String location = locationField.getText().trim();
        String priceStr = priceField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        String availableDates = availableDatesField.getText().trim();
        
        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || priceStr.isEmpty()) {
            showStatus("Please fill in all required fields", false);
            return;
        }
        
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                showStatus("Price must be positive", false);
                return;
            }
            
            if (currentProperty == null) {
                // Create new property
                currentProperty = new Property();
                currentProperty.setId(java.util.UUID.randomUUID().toString());
                currentProperty.setOwnerId(SessionManager.getCurrentUserId());
            }
            
            currentProperty.setTitle(title);
            currentProperty.setDescription(description);
            currentProperty.setLocation(location);
            currentProperty.setPrice(price);
            currentProperty.setImagePath(imagePath.isEmpty() ? null : imagePath);
            currentProperty.setAvailableDates(availableDates.isEmpty() ? "all" : availableDates);
            
            if (isEditMode) {
                PropertyService.updateProperty(currentProperty);
                showStatus("Property updated successfully!", true);
            } else {
                PropertyService.addProperty(currentProperty);
                showStatus("Property added successfully!", true);
                // Clear fields for next property
                clearFields();
            }
        } catch (NumberFormatException e) {
            showStatus("Invalid price format", false);
        }
    }
    
    @FXML
    private void handleDelete() {
        if (currentProperty != null && SessionManager.isHost()) {
            PropertyService.deleteProperty(currentProperty.getId());
            showStatus("Property deleted successfully!", true);
            clearFields();
        }
    }
    
    @FXML
    private void handleBook() {
        if (currentProperty == null) {
            showStatus("Please select a property first", false);
            return;
        }
        // Navigate to booking screen with property ID
        SceneManager.switchScene("/com/airbnb/views/Booking.fxml", "Airbnb System - Book Property");
    }
    
    @FXML
    private void handleViewReviews() {
        if (currentProperty == null) {
            showStatus("Please select a property first", false);
            return;
        }
        SceneManager.switchScene("/com/airbnb/views/Reviews.fxml", "Airbnb System - Reviews");
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
    
    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        priceField.clear();
        imagePathField.clear();
        availableDatesField.clear();
        currentProperty = null;
        isEditMode = false;
    }
    
    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setTextFill(success ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }
}

