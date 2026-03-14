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
public class GroupTripMemberResponse {
    private UUID id;
    private UUID groupTripId;
    private String userEmail;
    private String displayName;
    private String avatarUrl;
    private String role;
    private Instant joinedAt;
}
