/*
 * Airbnb-Like Property Rental System
 * Main Application Entry Point
 * 
 * This application is built using:
 * - JavaFX for GUI framework
 * - SHA-256 for password hashing (Java Security API)
 * 
 * @author vuyis
 */
package com.airbnb;

import com.airbnb.utils.SceneManager;
import com.airbnb.services.FileService;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class
 * Initializes the data directory and loads the login screen
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize data directory
        FileService.initializeDataDirectory();
        
        // Set up scene manager
        SceneManager.setPrimaryStage(primaryStage);
        
        // Load login screen
        SceneManager.switchScene("/com/airbnb/views/Login.fxml", "Airbnb System - Login");
        
        // Set window properties
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);
    }

    /**
     * Main method to launch the application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
