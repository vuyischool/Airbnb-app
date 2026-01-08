package com.airbnb.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for managing scene transitions in JavaFX
 */
public class SceneManager {
    
    private static Stage primaryStage;
    
    /**
     * Set the primary stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
    
    /**
     * Switch to a new scene
     * @param fxmlPath Path to FXML file (e.g., "/com/airbnb/views/Login.fxml")
     * @param title Window title
     */
    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    /**
     * Get the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

