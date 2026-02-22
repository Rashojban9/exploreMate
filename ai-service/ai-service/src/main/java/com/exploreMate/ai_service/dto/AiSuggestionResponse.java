package com.exploreMate.ai_service.dto;

public class AiSuggestionResponse {
    private String text;
    private boolean simulated;

    public AiSuggestionResponse() {}

    public AiSuggestionResponse(String text, boolean simulated) {
        this.text = text;
        this.simulated = simulated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSimulated() {
        return simulated;
    }

    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }
}
