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
public class GroupTripExpenseResponse {
    private UUID id;
    private UUID groupTripId;
    private String title;
    private double amount;
    private String paidByEmail;
    private List<String> splitAmongEmails;
    private Instant createdAt;
}
