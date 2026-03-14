package com.exploreMate.group_trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTripExpenseRequest {
    @NotBlank(message = "Expense title is required")
    private String title;

    @Positive(message = "Amount must be positive")
    private double amount;

    private List<String> splitAmongEmails;
}
