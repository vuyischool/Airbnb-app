package com.airbnb.controller;

import com.airbnb.models.Property;
import com.airbnb.services.PropertyService;
import com.airbnb.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Property controller
 * Handles property listing, search, and display
 */
public class PropertyController implements Initializable {
    
    @FXML private TextField locationField;
    @FXML private TextField maxPriceField;
    @FXML private TextField minRatingField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private VBox propertiesContainer;
    @FXML private Label noResultsLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers using lambdas
        searchButton.setOnAction(e -> handleSearch());
        clearButton.setOnAction(e -> handleClear());
        
        // Load all properties on startup
        loadProperties(PropertyService.getAllProperties());
    }
    
    @FXML
    private void handleSearch() {
        String location = locationField.getText().trim();
        Double maxPrice = null;
        Double minRating = null;
        
        try {
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            // Invalid price, ignore
        }
        
        try {
            if (!minRatingField.getText().trim().isEmpty()) {
                minRating = Double.parseDouble(minRatingField.getText().trim());
            }
        } catch (NumberFormatException e) {
            // Invalid rating, ignore
        }
        
        // Use Streams for filtering (already done in PropertyService)
        List<Property> results = PropertyService.searchProperties(
            location.isEmpty() ? null : location,
            maxPrice,
            minRating
        );
        
        loadProperties(results);
    }
    
    @FXML
    private void handleClear() {
        locationField.clear();
        maxPriceField.clear();
        minRatingField.clear();
        loadProperties(PropertyService.getAllProperties());
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
    
    private void loadProperties(List<Property> properties) {
        propertiesContainer.getChildren().clear();
        
        if (properties.isEmpty()) {
            noResultsLabel.setVisible(true);
            return;
        }
        
        noResultsLabel.setVisible(false);
        
        // Create property cards using Streams (for demonstration)
        properties.forEach(property -> {
            VBox card = createPropertyCard(property);
            propertiesContainer.getChildren().add(card);
        });
    }
    
    private VBox createPropertyCard(Property property) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(800);
        
        HBox header = new HBox(10);
        Label titleLabel = new Label(property.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Display average rating
        DecimalFormat df = new DecimalFormat("#.#");
        String ratingText = "Rating: " + df.format(property.getAverageRating()) + "/5.0";
        if (property.getAverageRating() > 0) {
            ratingText += " ⭐".repeat((int) Math.round(property.getAverageRating()));
        } else {
            ratingText += " (No ratings yet)";
        }
        Label ratingLabel = new Label(ratingText);
        ratingLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        
        header.getChildren().addAll(titleLabel, spacer, ratingLabel);
        
        Label locationLabel = new Label("📍 " + property.getLocation());
        Label priceLabel = new Label("$" + property.getPrice() + " per night");
        priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14;");
        
        Label descLabel = new Label(property.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(750);
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewButton.setOnAction(e -> viewPropertyDetails(property));
        
        card.getChildren().addAll(header, locationLabel, priceLabel, descLabel, viewButton);
        VBox.setMargin(card, new Insets(5));
        
        return card;
    }
    
    private void viewPropertyDetails(Property property) {
        // Store property in a way that PropertyDetailController can access it
        // For simplicity, we'll pass it via a static method or navigate with ID
        SceneManager.switchScene("/com/airbnb/views/PropertyDetail.fxml", "Airbnb System - Property Details");
        // Note: In a real app, you'd use a shared state or pass parameters
        // For now, PropertyDetailController will need to load by ID if editing
    }
}

