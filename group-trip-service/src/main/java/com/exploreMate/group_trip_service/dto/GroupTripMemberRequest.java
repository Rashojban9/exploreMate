package com.exploreMate.group_trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTripMemberRequest {
    private String userEmail;
    private String displayName;
    private String avatarUrl;
}
