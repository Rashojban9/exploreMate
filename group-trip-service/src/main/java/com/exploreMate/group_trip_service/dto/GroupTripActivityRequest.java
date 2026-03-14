package com.exploreMate.group_trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTripActivityRequest {
    @NotBlank(message = "Activity title is required")
    private String title;

    private String description;
    private String scheduledTime;
    private double price;
    private String imageUrl;
}
