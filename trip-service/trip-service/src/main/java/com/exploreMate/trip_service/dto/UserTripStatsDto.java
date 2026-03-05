package com.exploreMate.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTripStatsDto {
    private int totalTrips;
    private int completedTrips;
    private int upcomingTrips;
    private int missedTrips;
    private int draftTrips;
}
