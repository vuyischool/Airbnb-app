package com.airbnb.services;

import com.airbnb.models.Message;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Message service
 * Handles messaging between users
 */
public class MessageService {
    
    /**
     * Get all messages
     */
    public static List<Message> getAllMessages() {
        List<String> lines = FileService.readAllLines(FileService.MESSAGES_FILE);
        return lines.stream()
            .map(Message::fromFileString)
            .filter(m -> m != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages between two users
     */
    public static List<Message> getMessagesBetweenUsers(String userId1, String userId2) {
        return getAllMessages().stream()
            .filter(m -> (m.getSenderId().equals(userId1) && m.getReceiverId().equals(userId2)) ||
                        (m.getSenderId().equals(userId2) && m.getReceiverId().equals(userId1)))
            .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages for a user (both sent and received)
     */
    public static List<Message> getMessagesForUser(String userId) {
        return getAllMessages().stream()
            .filter(m -> m.getSenderId().equals(userId) || m.getReceiverId().equals(userId))
            .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())) // Most recent first
            .collect(Collectors.toList());
    }
    
    /**
     * Get unread messages for a user
     */
    public static List<Message> getUnreadMessages(String userId) {
        return getAllMessages().stream()
            .filter(m -> m.getReceiverId().equals(userId) && !m.isRead())
            .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    /**
     * Send a message
     */
    public static boolean sendMessage(String senderId, String receiverId, String content) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        
        FileService.appendLine(FileService.MESSAGES_FILE, message.toFileString());
        return true;
    }
    
    /**
     * Mark message as read
     */
    public static boolean markAsRead(String messageId) {
        Message message = getAllMessages().stream()
            .filter(m -> m.getId().equals(messageId))
            .findFirst()
            .orElse(null);
        
        if (message != null) {
            message.setRead(true);
            return FileService.updateLineById(FileService.MESSAGES_FILE, 
                                            messageId, 
                                            message.toFileString());
        }
        return false;
    }
}

