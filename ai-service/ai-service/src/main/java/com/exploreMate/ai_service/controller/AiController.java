package com.exploreMate.ai_service.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.exploreMate.ai_service.dto.AiSuggestionRequest;
import com.exploreMate.ai_service.dto.AiSuggestionResponse;
import com.exploreMate.ai_service.service.ConversationHistoryService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final ConversationHistoryService conversationHistoryService;
    
    // JWT secret - should match the one in auth-service
    @Value("${jwt.secret:my_super_secret_key_that_is_very_long_1234567890}")
    private String JWT_SECRET;
    
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // Auth service URL for fetching user profile
    @Value("${auth.service.url:http://localhost:8080}")
    private String authServiceUrl;
    
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
    
    // Fetch user profile from auth-service
    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserProfile(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        try {
            String url = authServiceUrl + "/auth/profile";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseData = response.getBody();
                // Check if response has data field (wrapped response)
                if (responseData.containsKey("data")) {
                    return (Map<String, Object>) responseData.get("data");
                }
                return responseData;
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch user profile: " + e.getMessage());
        }
        return null;
    }
    
    // Build personalized system prompt based on user profile
    private String buildSystemPrompt(Map<String, Object> userProfile) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("""
            You are ExploreMate AI, a friendly and helpful travel assistant for the ExploreMate travel app.
            
            When users greet you with "hello", "hi", or similar greetings, respond with:
            "Namaste! Welcome to ExploreMate, your AI travel companion. I'm here to help you plan amazing trips, discover hidden gems, and find the best local experiences in Nepal and beyond! How can I help you today?"
            
            For travel advice, provide helpful, detailed recommendations.
            Be friendly, concise, and focused on helping users plan their perfect trip.
            """);
        
        // Add user profile information if available
        if (userProfile != null) {
            promptBuilder.append("\n\nUser Profile Context:\n");
            
            if (userProfile.get("travelStyle") != null) {
                promptBuilder.append("- Travel Style: ").append(userProfile.get("travelStyle")).append("\n");
            }
            
            if (userProfile.get("interests") != null) {
                promptBuilder.append("- Interests: ").append(userProfile.get("interests")).append("\n");
            }
            
            if (userProfile.get("budget") != null) {
                promptBuilder.append("- Total Budget: NPR ").append(userProfile.get("budget")).append("\n");
            }
            
            if (userProfile.get("name") != null) {
                promptBuilder.append("- User Name: ").append(userProfile.get("name")).append("\n");
            }
            
            promptBuilder.append("\nPlease tailor your recommendations based on the user's profile above. ");
            
            // Add specific guidance based on travel style
            String travelStyle = (String) userProfile.get("travelStyle");
            Integer budget = userProfile.get("budget") != null ? (Integer) userProfile.get("budget") : null;
            
            if (travelStyle != null) {
                if (travelStyle.equalsIgnoreCase("Solo")) {
                    promptBuilder.append("Since they prefer traveling solo, suggest experiences that are safe and great for individual travelers. ");
                } else if (travelStyle.equalsIgnoreCase("Couple")) {
                    promptBuilder.append("Since they travel as a couple, the budget is for TWO people combined. Provide per-person cost by dividing the total budget by 2. ");
                } else if (travelStyle.equalsIgnoreCase("Group")) {
                    promptBuilder.append("Since they travel in a group, the budget is for ALL group members combined. Provide per-person cost by dividing the total budget by the number of people in the group. ");
                }
            }
            
            // Budget calculation guidance
            if (budget != null) {
                // Budget from database is NPR per day
                int dailyBudgetNpr = budget;
                
                promptBuilder.append("\n\nBudget Information (IMPORTANT):\n");
                promptBuilder.append("- User's daily budget: NPR ").append(dailyBudgetNpr).append("\n");
                promptBuilder.append("- Use Nepali Rupees (NPR) for all prices.\n");
                promptBuilder.append("- For SOLO travelers: Full daily budget is for one person.\n");
                promptBuilder.append("- For COUPLE travelers: Full daily budget covers both people (divide by 2 for per-person).\n");
                promptBuilder.append("- For GROUP travelers: Full daily budget covers all members (divide by group size for per-person).\n");
            }
            
            // Add interests to the prompt
            if (userProfile != null && userProfile.get("interests") != null) {
                promptBuilder.append("\n\nUser Interests:\n");
                promptBuilder.append("- ").append(userProfile.get("interests")).append("\n");
                promptBuilder.append("- Please tailor recommendations based on these interests.\n");
            }
        }
        
        // Add instruction to respond in Nepali
        promptBuilder.append("\n\nCurrency Instruction:\n");
        promptBuilder.append("- Always show prices in Nepali Rupees (NPR) - नेपाली रुपैयाँ.\n");
        
        return promptBuilder.toString();
    }
    
    // Extract user email from JWT token
    private String extractUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "anonymous";
        }
        try {
            String token = authHeader.substring(7);
            return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return "anonymous";
        }
    }
    
    // Create user-specific session key
    private String getUserSessionKey(String userEmail, String sessionId) {
        return userEmail + "_" + sessionId;
    }

    @PostMapping("/suggestion")
    public ResponseEntity<AiSuggestionResponse> getSuggestion(
            @RequestBody AiSuggestionRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract user from JWT token
        String userEmail = extractUserFromToken(authHeader);
        
        // Fetch user profile for personalized recommendations
        Map<String, Object> userProfile = getUserProfile(authHeader);
        
        String userPrompt = request.getPrompt();
        
        // Get session ID or generate a new one
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        // Create user-specific session key
        String userSessionKey = getUserSessionKey(userEmail, sessionId);
        
        // Check if user wants to clear history
        if (request.getClearHistory() != null && request.getClearHistory()) {
            conversationHistoryService.clearHistory(userSessionKey);
        }
        
        // Get conversation history
        List<Map<String, String>> conversationMessages = conversationHistoryService.getHistoryAsList(userSessionKey);
        
        // Build personalized system prompt based on user profile
        String systemPrompt = buildSystemPrompt(userProfile);
        
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
        
        // Save conversation to history with user-specific key
        conversationHistoryService.addUserMessage(userEmail, sessionId, userPrompt);
        conversationHistoryService.addAssistantMessage(userEmail, sessionId, aiResponse);
        
        return ResponseEntity.ok(new AiSuggestionResponse(aiResponse, false));
    }
    
    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearConversation(
            @RequestBody AiSuggestionRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userEmail = extractUserFromToken(authHeader);
        String sessionId = request.getSessionId();
        
        if (sessionId != null && !sessionId.isEmpty()) {
            String userSessionKey = getUserSessionKey(userEmail, sessionId);
            conversationHistoryService.clearHistory(userSessionKey);
            return ResponseEntity.ok(Map.of("message", "Conversation history cleared"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Session ID required"));
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Map<String, String>>> getHistory(
            @PathVariable String sessionId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userEmail = extractUserFromToken(authHeader);
        String userSessionKey = getUserSessionKey(userEmail, sessionId);
        
        List<Map<String, String>> history = conversationHistoryService.getHistoryAsList(userSessionKey);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userEmail = extractUserFromToken(authHeader);
        
        // Get all conversations for this user
        List<Map<String, Object>> conversations = conversationHistoryService.getConversationsForUser(userEmail);
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
