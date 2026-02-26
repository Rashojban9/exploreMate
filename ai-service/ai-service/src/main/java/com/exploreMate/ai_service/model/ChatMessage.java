package com.exploreMate.ai_service.model;

/**
 * MongoDB embedded document for chat messages
 */
public class ChatMessage {
    private String role; // "user" or "assistant"
    private String content;
    
    public ChatMessage() {}
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
