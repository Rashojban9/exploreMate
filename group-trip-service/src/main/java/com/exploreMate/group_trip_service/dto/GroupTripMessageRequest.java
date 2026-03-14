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
public class GroupTripMessageRequest {
    @NotBlank(message = "Message text is required")
    private String text;

    private String senderName;
}
