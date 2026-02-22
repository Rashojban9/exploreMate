package com.exploreMate.ai_service.controller;

import com.exploreMate.ai_service.dto.AiSuggestionRequest;
import com.exploreMate.ai_service.dto.AiSuggestionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final Random random = new Random();

    @PostMapping("/suggestion")
    public ResponseEntity<AiSuggestionResponse> getSuggestion(@RequestBody AiSuggestionRequest request) {
        String prompt = request.getPrompt().toLowerCase();
        
        String suggestion = generateSuggestion(prompt);
        
        return ResponseEntity.ok(new AiSuggestionResponse(suggestion, true));
    }

    private String generateSuggestion(String prompt) {
        // Simulated AI suggestions based on user prompts
        if (prompt.contains("trip") || prompt.contains("travel") || prompt.contains("destination")) {
            String[] tripSuggestions = {
                "Based on your interests, I recommend visiting the mountains for a peaceful retreat. The scenic views and fresh air will rejuvenate your spirit.",
                "Consider exploring coastal areas with beautiful beaches. Water activities and sunset walks could be wonderful experiences.",
                "A cultural heritage tour could be enriching. Visit historical sites and museums to learn about local traditions.",
                "Adventure tourism might interest you. Try hiking, rock climbing, or zip-lining for an adrenaline rush.",
                "A rural countryside escape offers tranquility. Enjoy farm stays, local cuisine, and nature walks."
            };
            return tripSuggestions[random.nextInt(tripSuggestions.length)];
        } 
        else if (prompt.contains("itinerary") || prompt.contains("plan") || prompt.contains("schedule")) {
            String[] itinerarySuggestions = {
                "Start your day early with a hearty breakfast, then visit the main attractions. Take a break for lunch at a local restaurant. In the afternoon, explore neighborhoods and markets. End the day with dinner and entertainment.",
                "For a balanced trip, allocate mornings for outdoor activities, afternoons for indoor attractions, and evenings for relaxation or cultural experiences.",
                "Consider a flexible schedule with buffer time. Don't overplan - leave room for spontaneous discoveries and rest."
            };
            return itinerarySuggestions[random.nextInt(itinerarySuggestions.length)];
        }
        else if (prompt.contains("budget") || prompt.contains("cost") || prompt.contains("cheap")) {
            String[] budgetSuggestions = {
                "Travel during off-season for better deals. Book accommodations in advance and consider hostels or budget hotels.",
                "Use public transportation instead of taxis. Eat at local street food stalls and markets for affordable meals.",
                "Look for free attractions and walking tours. Many cities offer free entry to museums on specific days."
            };
            return budgetSuggestions[random.nextInt(budgetSuggestions.length)];
        }
        else if (prompt.contains("food") || prompt.contains("restaurant") || prompt.contains("cuisine")) {
            String[] foodSuggestions = {
                "Explore local street food for authentic flavors. Don't miss the regional specialties - they're often the most delicious and affordable.",
                "Find restaurants away from tourist areas for better prices and authentic experiences. Ask locals for recommendations.",
                "Consider cooking some meals yourself if you have kitchen access. Visit local grocery stores for fresh ingredients."
            };
            return foodSuggestions[random.nextInt(foodSuggestions.length)];
        }
        else {
            String[] generalSuggestions = {
                "Travel is about exploration and discovery. Be open to new experiences, meet locals, and embrace the unexpected.",
                "Pack light and travel smart. Bring comfortable shoes, a good camera, and an open mind.",
                "Document your journey through photos and journal. These memories will be priceless.",
                "Stay connected with loved ones but also take time to disconnect and fully immerse yourself in the experience.",
                "Remember: the journey is as important as the destination. Enjoy every moment!"
            };
            return generalSuggestions[random.nextInt(generalSuggestions.length)];
        }
    }
}
