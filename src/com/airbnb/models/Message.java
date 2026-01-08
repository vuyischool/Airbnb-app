package com.airbnb.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message model class
 * Represents a message between users
 */
public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    
    public Message() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
    
    public Message(String id, String senderId, String receiverId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    /**
     * Convert Message to file format string
     * Format: id|senderId|receiverId|content|timestamp|isRead
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return id + "|" + senderId + "|" + receiverId + "|" + 
               (content != null ? content.replace("|", " ").replace("\n", " ") : "") + "|" + 
               (timestamp != null ? timestamp.format(formatter) : LocalDateTime.now().format(formatter)) + "|" + 
               isRead;
    }
    
    /**
     * Create Message from file format string
     */
    public static Message fromFileString(String line) {
        String[] parts = line.split("\\|", 6);
        if (parts.length < 6) {
            return null;
        }
        
        Message message = new Message();
        message.setId(parts[0]);
        message.setSenderId(parts[1]);
        message.setReceiverId(parts[2]);
        message.setContent(parts[3]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            message.setTimestamp(LocalDateTime.parse(parts[4], formatter));
        } catch (Exception e) {
            message.setTimestamp(LocalDateTime.now());
        }
        
        message.setRead(Boolean.parseBoolean(parts[5]));
        
        return message;
    }
    
    @Override
    public String toString() {
        return "From: " + senderId + " - " + content;
    }
}

