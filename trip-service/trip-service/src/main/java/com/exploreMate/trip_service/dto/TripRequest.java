package com.exploreMate.trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {
    @NotBlank(message = "Trip name is required")
    private String tripName;
    
    private String tripDescription;
    
    @NotBlank(message = "Place name is required")
    private String placeName;
    
    private String placeDescription;
    
    private List<String> placePhotos;
}
