package com.exploreMate.group_trip_service.dto;

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
public class GroupTripActivityResponse {
    private UUID id;
    private UUID groupTripId;
    private String title;
    private String description;
    private String scheduledTime;
    private double price;
    private String imageUrl;
    private String status;
    private String proposedByEmail;
    private List<String> votedByEmails;
    private int voteCount;
    private Instant createdAt;
}
