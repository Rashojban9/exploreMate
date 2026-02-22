package com.exploreMate.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private UUID id;
    private String tripName;
    private String tripDescription;
    private String placeName;
    private String placeDescription;
    private List<String> placePhotos;
    private String userEmail;
    private Instant createdAt;
    private Instant updatedAt;
}
