package com.airbnb.controller;

import com.airbnb.models.User;
import com.airbnb.services.AuthService;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import com.airbnb.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for authentication screens (Login and Register)
 */
public class AuthController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private Label regErrorLabel;
    @FXML private Button registerSubmitButton;
    @FXML private Button backToLoginButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBox with user types - ensure it's always initialized
        initializeUserTypeCombo();
        
        // Set up event handlers using lambdas
        if (loginButton != null) {
            loginButton.setOnAction(e -> handleLogin());
        }
        if (registerButton != null) {
            registerButton.setOnAction(e -> handleRegister());
        }
        if (registerSubmitButton != null) {
            registerSubmitButton.setOnAction(e -> handleRegisterSubmit());
        }
        if (backToLoginButton != null) {
            backToLoginButton.setOnAction(e -> handleBackToLogin());
        }
        
        // Ensure ComboBox is initialized after JavaFX injection is complete
        javafx.application.Platform.runLater(() -> {
            initializeUserTypeCombo();
        });
    }
    
    /**
     * Initialize the user type ComboBox with all available user types
     * This method is called multiple times to ensure initialization
     * Made package-private so SceneManager can access it
     */
    void initializeUserTypeCombo() {
        if (userTypeCombo != null) {
            // Clear any existing items first
            userTypeCombo.getItems().clear();
            // Set items with all three user types including ADMIN
            userTypeCombo.setItems(FXCollections.observableArrayList("GUEST", "HOST", "ADMIN"));
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Please enter username and password");
            return;
        }
        
        User user = AuthService.login(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(user);
            navigateToDashboard();
        } else {
            showError(errorLabel, "Invalid username or password");
        }
    }
    
    @FXML
    private void handleRegister() {
        SceneManager.switchScene("/com/airbnb/views/Register.fxml", "Airbnb System - Register");
    }
    
    @FXML
    private void handleRegisterSubmit() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();
        String userType = userTypeCombo.getValue();
        
        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(regErrorLabel, "Please fill in all fields");
            return;
        }
        
        if (!ValidationUtil.isValidUsername(username)) {
            showError(regErrorLabel, "Username must be at least 3 characters (alphanumeric and underscore only)");
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            showError(regErrorLabel, "Invalid email format");
            return;
        }
        
        if (!ValidationUtil.isValidPassword(password)) {
            showError(regErrorLabel, "Password must be at least 6 characters");
            return;
        }
        
        if (userType == null) {
            showError(regErrorLabel, "Please select a user type");
            return;
        }
        
        // Attempt registration
        if (AuthService.register(username, email, password, userType)) {
            // Registration successful, show success message and redirect to login
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Registration Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Your account has been created successfully! Please login with your credentials.");
            successAlert.showAndWait();
            
            // Redirect to login screen
            handleBackToLogin();
        } else {
            showError(regErrorLabel, "Username or email already exists");
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        SceneManager.switchScene("/com/airbnb/views/Login.fxml", "Airbnb System - Login");
    }
    
    private void navigateToDashboard() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && "ADMIN".equals(currentUser.getUserType())) {
            SceneManager.switchScene("/com/airbnb/views/AdminDashboard.fxml", "Airbnb System - Admin Dashboard");
        } else {
            SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
        }
    }
    
    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }
}

