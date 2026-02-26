package com.exploreMate.ai_service.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage conversation history for each user/session.
 * Stores the last N messages to provide context for AI responses.
 */
@Service
public class ConversationHistoryService {

    // Store conversation history for each session (userId or sessionId)
    // In production, you'd want to persist this to a database
    private final Map<String, List<ChatMessage>> conversationHistory = new ConcurrentHashMap<>();
    
    // Store conversation metadata (title, timestamp)
    private final Map<String, ConversationInfo> conversationInfo = new ConcurrentHashMap<>();
    
    // Maximum number of messages to keep in history
    private static final int MAX_HISTORY_SIZE = 10;
    
    public static class ChatMessage {
        private String role; // "user" or "assistant"
        private String content;
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() {
            return role;
        }
        
        public String getContent() {
            return content;
        }
        
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("role", role);
            map.put("content", content);
            return map;
        }
    }
    
    public static class ConversationInfo {
        private String sessionId;
        private String title;
        private long createdAt;
        private long updatedAt;
        
        public ConversationInfo(String sessionId, String title) {
            this.sessionId = sessionId;
            this.title = title;
            this.createdAt = System.currentTimeMillis();
            this.updatedAt = System.currentTimeMillis();
        }
        
        public String getSessionId() { return sessionId; }
        public String getTitle() { return title; }
        public long getCreatedAt() { return createdAt; }
        public long getUpdatedAt() { return updatedAt; }
        public void setTitle(String title) { this.title = title; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("sessionId", sessionId);
            map.put("title", title);
            map.put("createdAt", createdAt);
            map.put("updatedAt", updatedAt);
            return map;
        }
    }
    
    /**
     * Add a user message to the conversation history
     */
    public void addUserMessage(String sessionId, String message) {
        addMessage(sessionId, "user", message);
    }
    
    /**
     * Add an AI response to the conversation history
     */
    public void addAssistantMessage(String sessionId, String message) {
        addMessage(sessionId, "assistant", message);
        
        // Update conversation title if this is the first assistant message
        if (!conversationInfo.containsKey(sessionId)) {
            String title = generateTitle(message);
            conversationInfo.put(sessionId, new ConversationInfo(sessionId, title));
        } else {
            conversationInfo.get(sessionId).setUpdatedAt(System.currentTimeMillis());
        }
    }
    
    private String generateTitle(String firstResponse) {
        // Generate a title from the first few words of the response
        if (firstResponse == null || firstResponse.isEmpty()) {
            return "New Conversation";
        }
        String title = firstResponse.substring(0, Math.min(50, firstResponse.length()));
        title = title.replaceAll("[\\n\\r]", " ").trim();
        if (title.length() >= 50) {
            title += "...";
        }
        return title;
    }
    
    private void addMessage(String sessionId, String role, String content) {
        conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        List<ChatMessage> history = conversationHistory.get(sessionId);
        history.add(new ChatMessage(role, content));
        
        // Keep only the last MAX_HISTORY_SIZE messages
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }
    
    /**
     * Get conversation history for a session as a list of maps
     * for sending to the Groq API
     */
    public List<Map<String, String>> getHistoryAsList(String sessionId) {
        List<ChatMessage> history = conversationHistory.getOrDefault(sessionId, new ArrayList<>());
        List<Map<String, String>> result = new ArrayList<>();
        for (ChatMessage msg : history) {
            result.add(msg.toMap());
        }
        return result;
    }
    
    /**
     * Get list of all conversations
     */
    public List<Map<String, Object>> getAllConversations() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ConversationInfo info : conversationInfo.values()) {
            result.add(info.toMap());
        }
        // Sort by updated time, most recent first
        result.sort((a, b) -> Long.compare((Long) b.get("updatedAt"), (Long) a.get("updatedAt")));
        return result;
    }
    
    /**
     * Clear conversation history for a session
     */
    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
        conversationInfo.remove(sessionId);
    }
    
    /**
     * Check if a session has any history
     */
    public boolean hasHistory(String sessionId) {
        return conversationHistory.containsKey(sessionId) && 
               !conversationHistory.get(sessionId).isEmpty();
    }
}
