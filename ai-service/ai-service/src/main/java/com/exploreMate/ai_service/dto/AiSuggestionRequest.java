package com.exploreMate.ai_service.dto;

public class AiSuggestionRequest {
    private String prompt;
    private String sessionId; // To track conversation history per user
    
    // Optional: clear previous conversation
    private Boolean clearHistory;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Boolean getClearHistory() {
        return clearHistory;
    }
    
    public void setClearHistory(Boolean clearHistory) {
        this.clearHistory = clearHistory;
    }
}
