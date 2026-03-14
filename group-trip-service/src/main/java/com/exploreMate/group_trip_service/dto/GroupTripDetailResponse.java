package com.exploreMate.group_trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTripDetailResponse {
    private GroupTripResponse trip;
    private List<GroupTripMemberResponse> members;
    private List<GroupTripActivityResponse> activities;
    private BudgetSummary budget;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetSummary {
        private double totalCost;
        private int memberCount;
        private double perPersonCost;
    }
}
