package com.exploreMate.group_trip_service.mapper;

import com.exploreMate.group_trip_service.dto.GroupTripActivityResponse;
import com.exploreMate.group_trip_service.model.GroupTripActivity;
import org.springframework.stereotype.Component;

@Component
public class GroupTripActivityMapper {

    public GroupTripActivityResponse toResponse(GroupTripActivity activity) {
        return GroupTripActivityResponse.builder()
                .id(activity.getId())
                .groupTripId(activity.getGroupTripId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .scheduledTime(activity.getScheduledTime())
                .price(activity.getPrice())
                .imageUrl(activity.getImageUrl())
                .status(activity.getStatus())
                .proposedByEmail(activity.getProposedByEmail())
                .votedByEmails(activity.getVotedByEmails())
                .voteCount(activity.getVotedByEmails() != null ? activity.getVotedByEmails().size() : 0)
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
