package com.exploreMate.group_trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTripResponse {
    private UUID id;
    private String tripName;
    private String destination;
    private String description;
    private String startDate;
    private String endDate;
    private String creatorEmail;
    private String inviteCode;
    private String coverImage;
    private int memberCount;
    private Instant createdAt;
    private Instant updatedAt;
}
