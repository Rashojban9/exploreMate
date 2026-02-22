package com.exploreMate.trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trips")
public class Trip {
    @Id
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
