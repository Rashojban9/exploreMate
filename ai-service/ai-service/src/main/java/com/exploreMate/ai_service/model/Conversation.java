package com.exploreMate.ai_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document for storing AI conversation history
 */
@Document(collection = "conversations")
public class Conversation {
    
    @Id
    private String id;
    private String userEmail;
    private String sessionId;
    private String title;
    private List<ChatMessage> messages = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    
    public Conversation() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    public Conversation(String userEmail, String sessionId, String title) {
        this.userEmail = userEmail;
        this.sessionId = sessionId;
        this.title = title;
        this.messages = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.updatedAt = Instant.now();
    }
}
