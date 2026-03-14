package com.exploreMate.group_trip_service.mapper;

import com.exploreMate.group_trip_service.dto.GroupTripMemberResponse;
import com.exploreMate.group_trip_service.model.GroupTripMember;
import org.springframework.stereotype.Component;

@Component
public class GroupTripMemberMapper {

    public GroupTripMemberResponse toResponse(GroupTripMember member) {
        return GroupTripMemberResponse.builder()
                .id(member.getId())
                .groupTripId(member.getGroupTripId())
                .userEmail(member.getUserEmail())
                .displayName(member.getDisplayName())
                .avatarUrl(member.getAvatarUrl())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
