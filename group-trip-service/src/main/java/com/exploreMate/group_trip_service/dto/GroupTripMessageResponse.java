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
public class GroupTripMessageResponse {
    private UUID id;
    private UUID groupTripId;
    private String senderEmail;
    private String senderName;
    private String text;
    private Instant sentAt;
}
