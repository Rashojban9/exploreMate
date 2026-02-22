package com.exploreMate.trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "saved_items")
public class SavedItem {
    @Id
    private String id;
    private String userEmail;
    private String type; // DESTINATION, ITINERARY, ARTICLE
    private String title;
    private String location;
    private String imageUrl;
    private String description;
    private LocalDateTime dateAdded;
}
