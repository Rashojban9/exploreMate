package com.exploreMate.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedItemResponse {
    private Long id;
    private String type;
    private String title;
    private String location;
    private String imageUrl;
    private String description;
    private String dateAdded;
}
