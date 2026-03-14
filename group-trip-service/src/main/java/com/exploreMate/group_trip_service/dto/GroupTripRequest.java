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
public class GroupTripRequest {
    @NotBlank(message = "Trip name is required")
    private String tripName;

    @NotBlank(message = "Destination is required")
    private String destination;

    private String description;
    private String startDate;
    private String endDate;
    private String coverImage;
}
