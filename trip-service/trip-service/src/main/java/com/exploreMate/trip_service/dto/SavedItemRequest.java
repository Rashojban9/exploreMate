package com.exploreMate.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedItemRequest {
    private String type; // DESTINATION, ITINERARY, ARTICLE
    private String title;
    private String location;
    private String imageUrl;
    private String description;
}
