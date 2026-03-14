package com.exploreMate.group_trip_service.mapper;

import com.exploreMate.group_trip_service.dto.GroupTripMessageResponse;
import com.exploreMate.group_trip_service.model.GroupTripMessage;
import org.springframework.stereotype.Component;

@Component
public class GroupTripMessageMapper {

    public GroupTripMessageResponse toResponse(GroupTripMessage message) {
        return GroupTripMessageResponse.builder()
                .id(message.getId())
                .groupTripId(message.getGroupTripId())
                .senderEmail(message.getSenderEmail())
                .senderName(message.getSenderName())
                .text(message.getText())
                .sentAt(message.getSentAt())
                .build();
    }
}
