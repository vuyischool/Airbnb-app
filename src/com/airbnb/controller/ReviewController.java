package com.airbnb.controller;

import com.airbnb.models.Property;
import com.airbnb.models.Review;
import com.airbnb.services.PropertyService;
import com.airbnb.services.ReviewService;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import com.airbnb.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Review controller
 * Handles review submission and display
 */
public class ReviewController implements Initializable {
    
    @FXML private ComboBox<Property> propertyCombo;
    @FXML private ComboBox<Integer> ratingCombo;
    @FXML private TextArea commentField;
    @FXML private Button submitReviewButton;
    @FXML private Label reviewStatusLabel;
    @FXML private ListView<String> reviewsList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers using lambdas
        submitReviewButton.setOnAction(e -> handleSubmitReview());
        
        // Load properties
        loadProperties();
        
        // Set up rating combo (1-5)
        ratingCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        
        // Load reviews
        loadReviews();
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
                    setText(property.getTitle() + " - " + property.getLocation());
                }
            }
        });
    }
    
    @FXML
    private void handleSubmitReview() {
        Property selectedProperty = propertyCombo.getValue();
        Integer rating = ratingCombo.getValue();
        String comment = commentField.getText().trim();
        
        if (selectedProperty == null) {
            showStatus("Please select a property", false);
            return;
        }
        
        if (rating == null) {
            showStatus("Please select a rating", false);
            return;
        }
        
        if (!ValidationUtil.isValidRating(rating)) {
            showStatus("Rating must be between 1 and 5", false);
            return;
        }
        
        Review review = new Review();
        review.setPropertyId(selectedProperty.getId());
        review.setUserId(SessionManager.getCurrentUserId());
        review.setRating(rating);
        review.setComment(comment);
        
        if (ReviewService.addReview(review)) {
            showStatus("Review submitted successfully! Rating updated automatically.", true);
            loadReviews();
            // Clear form
            propertyCombo.setValue(null);
            ratingCombo.setValue(null);
            commentField.clear();
        } else {
            showStatus("Failed to submit review", false);
        }
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
    
    private void loadReviews() {
        List<Review> reviews = ReviewService.getAllReviews();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        // Use Streams to format reviews for display
        List<String> reviewStrings = reviews.stream()
            .map(review -> {
                Property property = PropertyService.getPropertyById(review.getPropertyId());
                String propertyName = property != null ? property.getTitle() : "Unknown";
                String stars = "⭐".repeat(review.getRating());
                return String.format("%s - %s\n%s\n%s - %s",
                    propertyName,
                    stars,
                    review.getComment(),
                    review.getDate() != null ? review.getDate().format(formatter) : "",
                    review.getUserId()
                );
            })
            .collect(java.util.stream.Collectors.toList());
        
        reviewsList.setItems(FXCollections.observableArrayList(reviewStrings));
    }
    
    private void showStatus(String message, boolean success) {
        reviewStatusLabel.setText(message);
        reviewStatusLabel.setVisible(true);
        reviewStatusLabel.setTextFill(success ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }
}

