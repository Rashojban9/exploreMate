package com.exploreMate.group_trip_service.mapper;

import com.exploreMate.group_trip_service.dto.GroupTripResponse;
import com.exploreMate.group_trip_service.model.GroupTrip;
import org.springframework.stereotype.Component;

@Component
public class GroupTripMapper {

    public GroupTripResponse toResponse(GroupTrip trip, int memberCount) {
        return GroupTripResponse.builder()
                .id(trip.getId())
                .tripName(trip.getTripName())
                .destination(trip.getDestination())
                .description(trip.getDescription())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .creatorEmail(trip.getCreatorEmail())
                .inviteCode(trip.getInviteCode())
                .coverImage(trip.getCoverImage())
                .memberCount(memberCount)
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }
}
