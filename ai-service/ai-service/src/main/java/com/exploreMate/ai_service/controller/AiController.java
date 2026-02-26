package com.exploreMate.ai_service.controller;

import com.exploreMate.ai_service.dto.AiSuggestionRequest;
import com.exploreMate.ai_service.dto.AiSuggestionResponse;
import com.exploreMate.ai_service.service.ConversationHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final ConversationHistoryService conversationHistoryService;

    // Groq API configuration
    @Value("${groq.api-key:${GROQ_API_KEY:}}")
    private String groqApiKey;

    // Using llama-3.3-70b-versatile model for travel advice
    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String modelName;

    private final RestTemplate restTemplate;

    public AiController(RestTemplateBuilder restTemplateBuilder, 
                        ConversationHistoryService conversationHistoryService) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        this.conversationHistoryService = conversationHistoryService;
    }

    @PostMapping("/suggestion")
    public ResponseEntity<AiSuggestionResponse> getSuggestion(@RequestBody AiSuggestionRequest request) {
        String userPrompt = request.getPrompt();
        
        // Get session ID or generate a new one
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        // Check if user wants to clear history
        if (request.getClearHistory() != null && request.getClearHistory()) {
            conversationHistoryService.clearHistory(sessionId);
        }
        
        // Get conversation history
        List<Map<String, String>> conversationMessages = conversationHistoryService.getHistoryAsList(sessionId);
        
        // Enhance the prompt for better travel tips
        String systemPrompt = """
            You are ExploreMate AI, a friendly and helpful travel assistant for the ExploreMate travel app.
            
            When users greet you with "hello", "hi", or similar greetings, respond with:
            "Namaste! Welcome to ExploreMate, your AI travel companion. I'm here to help you plan amazing trips, discover hidden gems, and find the best local experiences in Nepal and beyond! How can I help you today?"
            
            For travel advice, provide helpful, detailed recommendations.
            Be friendly, concise, and focused on helping users plan their perfect trip.
            """;;
        
        // Build messages list with history
        List<Map<String, String>> messages = new ArrayList<>();
        
        // Add system message
        messages.add(Map.of("role", "system", "content", systemPrompt));
        
        // Add conversation history
        messages.addAll(conversationMessages);
        
        // Add current user message
        messages.add(Map.of("role", "user", "content", userPrompt));
        
        // Call Groq API with conversation history
        String aiResponse = callGroqWithHistory(messages);
        
        // Save conversation to history
        conversationHistoryService.addUserMessage(sessionId, userPrompt);
        conversationHistoryService.addAssistantMessage(sessionId, aiResponse);
        
        return ResponseEntity.ok(new AiSuggestionResponse(aiResponse, false));
    }
    
    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearConversation(@RequestBody AiSuggestionRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId != null && !sessionId.isEmpty()) {
            conversationHistoryService.clearHistory(sessionId);
            return ResponseEntity.ok(Map.of("message", "Conversation history cleared"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Session ID required"));
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Map<String, String>>> getConversationHistory(@PathVariable String sessionId) {
        List<Map<String, String>> history = conversationHistoryService.getHistoryAsList(sessionId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getAllConversations() {
        List<Map<String, Object>> conversations = conversationHistoryService.getAllConversations();
        return ResponseEntity.ok(conversations);
    }

    private String callGroqWithHistory(List<Map<String, String>> messages) {
        try {
            String apiKey = groqApiKey;
            
            // Also check environment variable
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("GROQ_API_KEY");
            }
            
            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: GROQ_API_KEY not configured. Please set the GROQ_API_KEY environment variable.";
            }
            
            String url = "https://api.groq.com/openai/v1/chat/completions";
            System.out.println("Calling Groq API with model: " + modelName);
            System.out.println("Number of messages in conversation: " + messages.size());
            
            Map<String, Object> requestBody = Map.of(
                "model", modelName,
                "messages", messages,
                "temperature", 0.7,
                "max_tokens", 1024,
                "top_p", 0.9
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            if (response != null && response.get("choices") != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.get("content") != null) {
                        return message.get("content").toString();
                    }
                }
            }
            
            return "I apologize, but I couldn't generate a response at this moment. Please try again.";
            
        } catch (Exception e) {
            return "AI service error: " + e.getMessage();
        }
    }
}
