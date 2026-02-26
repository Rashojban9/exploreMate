package com.exploreMate.ai_service.service;

import com.exploreMate.ai_service.model.ChatMessage;
import com.exploreMate.ai_service.model.Conversation;
import com.exploreMate.ai_service.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to manage conversation history for each user/session.
 * Uses MongoDB for persistent storage.
 */
@Service
public class ConversationHistoryService {

    @Autowired
    private ConversationRepository conversationRepository;
    
    // Maximum number of messages to keep in history
    private static final int MAX_HISTORY_SIZE = 10;
    
    /**
     * Add a user message to the conversation history
     * Session ID format: userEmail_sessionId
     */
    public void addUserMessage(String sessionKey, String message) {
        // Parse the sessionKey to get userEmail
        String[] parts = parseSessionKey(sessionKey);
        String userEmail = parts[0];
        
        Conversation conversation = conversationRepository.findById(sessionKey)
            .orElseGet(() -> {
                Conversation c = new Conversation();
                c.setId(sessionKey);
                c.setUserEmail(userEmail);
                c.setSessionId(sessionKey);
                c.setTitle("New Conversation");
                return c;
            });
        
        conversation.addMessage(new ChatMessage("user", message));
        
        // Keep only the last MAX_HISTORY_SIZE messages
        if (conversation.getMessages().size() > MAX_HISTORY_SIZE) {
            conversation.getMessages().remove(0);
        }
        
        conversationRepository.save(conversation);
    }
    
    /**
     * Add an AI response to the conversation history
     */
    public void addAssistantMessage(String sessionKey, String message) {
        // Parse the sessionKey to get userEmail
        String[] parts = parseSessionKey(sessionKey);
        String userEmail = parts[0];
        
        Conversation conversation = conversationRepository.findById(sessionKey)
            .orElseGet(() -> {
                Conversation c = new Conversation();
                c.setId(sessionKey);
                c.setUserEmail(userEmail);
                c.setSessionId(sessionKey);
                c.setTitle(generateTitle(message));
                return c;
            });
        
        // Update title if it's a new conversation
        if (conversation.getTitle().equals("New Conversation") && conversation.getMessages().isEmpty()) {
            conversation.setTitle(generateTitle(message));
        }
        
        conversation.addMessage(new ChatMessage("assistant", message));
        
        // Keep only the last MAX_HISTORY_SIZE messages
        if (conversation.getMessages().size() > MAX_HISTORY_SIZE) {
            conversation.getMessages().remove(0);
        }
        
        conversationRepository.save(conversation);
    }
    
    private String generateTitle(String firstResponse) {
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
    
    /**
     * Parse the composite session key into userEmail and sessionId
     * Format: userEmail_sessionId
     */
    private String[] parseSessionKey(String sessionKey) {
        int lastUnderscore = sessionKey.lastIndexOf('_');
        if (lastUnderscore > 0) {
            String userEmail = sessionKey.substring(0, lastUnderscore);
            String sessionId = sessionKey.substring(lastUnderscore + 1);
            return new String[]{userEmail, sessionId};
        }
        return new String[]{"anonymous", sessionKey};
    }
    
    /**
     * Get conversation history for a session as a list of maps
     * for sending to the Groq API
     */
    public List<Map<String, String>> getHistoryAsList(String sessionKey) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(sessionKey);
        
        if (conversationOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        Conversation conversation = conversationOpt.get();
        List<Map<String, String>> result = new ArrayList<>();
        
        for (ChatMessage msg : conversation.getMessages()) {
            Map<String, String> map = new HashMap<>();
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            result.add(map);
        }
        
        return result;
    }
    
    /**
     * Get list of all conversations (for admin)
     */
    public List<Map<String, Object>> getAllConversations() {
        return conversationRepository.findAll().stream()
            .map(this::conversationToMap)
            .collect(Collectors.toList());
    }
    
    /**
     * Get list of conversations for a specific user
     */
    public List<Map<String, Object>> getConversationsForUser(String userEmail) {
        // Find all conversations for this user by userEmail field, sorted by most recent
        return conversationRepository.findByUserEmailOrderByUpdatedAtDesc(userEmail).stream()
            .map(this::conversationToMap)
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> conversationToMap(Conversation conversation) {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", conversation.getSessionId());
        map.put("title", conversation.getTitle());
        map.put("createdAt", conversation.getCreatedAt().toEpochMilli());
        map.put("updatedAt", conversation.getUpdatedAt().toEpochMilli());
        return map;
    }
    
    /**
     * Clear conversation history for a session
     */
    public void clearHistory(String sessionKey) {
        conversationRepository.deleteById(sessionKey);
    }
    
    /**
     * Check if a session has any history
     */
    public boolean hasHistory(String sessionKey) {
        Optional<Conversation> conversation = conversationRepository.findById(sessionKey);
        
        return conversation.isPresent() && !conversation.get().getMessages().isEmpty();
    }
}
