package com.airbnb.controller;

import com.airbnb.models.Message;
import com.airbnb.models.User;
import com.airbnb.services.AuthService;
import com.airbnb.services.MessageService;
import com.airbnb.utils.SceneManager;
import com.airbnb.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Message controller
 * Handles messaging between users
 */
public class MessageController implements Initializable {
    
    @FXML private ListView<String> conversationsList;
    @FXML private Label conversationLabel;
    @FXML private VBox messagesContainer;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    
    private String selectedUserId = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers using lambdas
        sendButton.setOnAction(e -> handleSend());
        conversationsList.setOnMouseClicked(e -> {
            String selected = conversationsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadConversation(selected);
            }
        });
        
        loadConversations();
    }
    
    private void loadConversations() {
        String currentUserId = SessionManager.getCurrentUserId();
        List<Message> messages = MessageService.getMessagesForUser(currentUserId);
        
        // Use Streams to get unique conversation partners
        Set<String> conversationPartners = messages.stream()
            .map(m -> m.getSenderId().equals(currentUserId) ? m.getReceiverId() : m.getSenderId())
            .collect(Collectors.toSet());
        
        List<String> partnerNames = conversationPartners.stream()
            .map(userId -> {
                User user = AuthService.getUserById(userId);
                return user != null ? user.getUsername() : userId;
            })
            .collect(Collectors.toList());
        
        conversationsList.setItems(FXCollections.observableArrayList(partnerNames));
    }
    
    private void loadConversation(String partnerName) {
        String currentUserId = SessionManager.getCurrentUserId();
        User partner = AuthService.getUserByUsername(partnerName);
        
        if (partner == null) {
            return;
        }
        
        selectedUserId = partner.getId();
        conversationLabel.setText("Conversation with " + partnerName);
        
        // Load messages between current user and partner
        List<Message> messages = MessageService.getMessagesBetweenUsers(currentUserId, selectedUserId);
        
        messagesContainer.getChildren().clear();
        
        // Use Streams to process and display messages
        messages.forEach(message -> {
            VBox messageBox = createMessageBox(message, message.getSenderId().equals(currentUserId));
            messagesContainer.getChildren().add(messageBox);
        });
        
        // Scroll to bottom
        messagesScrollPane.setVvalue(1.0);
    }
    
    private VBox createMessageBox(Message message, boolean isSent) {
        VBox messageBox = new VBox(5);
        messageBox.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5));
        
        HBox contentBox = new HBox(10);
        contentBox.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        Label contentLabel = new Label(message.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setStyle("-fx-background-color: " + (isSent ? "#3498db" : "#ecf0f1") + "; " +
                             "-fx-text-fill: " + (isSent ? "white" : "black") + "; " +
                             "-fx-padding: 10; -fx-background-radius: 10;");
        contentLabel.setFont(Font.font(14));
        
        Label timeLabel = new Label(message.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10;");
        
        if (isSent) {
            contentBox.getChildren().addAll(timeLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(contentLabel, timeLabel);
        }
        
        messageBox.getChildren().add(contentBox);
        
        return messageBox;
    }
    
    @FXML
    private void handleSend() {
        if (selectedUserId == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Conversation Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a conversation first");
            alert.showAndWait();
            return;
        }
        
        String content = messageField.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        
        String senderId = SessionManager.getCurrentUserId();
        if (MessageService.sendMessage(senderId, selectedUserId, content)) {
            messageField.clear();
            loadConversation(AuthService.getUserById(selectedUserId).getUsername());
        }
    }
    
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/com/airbnb/views/MainDashboard.fxml", "Airbnb System - Dashboard");
    }
}

