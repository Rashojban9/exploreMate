package com.exploreMate.group_trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group_trip_members")
@CompoundIndex(name = "trip_user_idx", def = "{'groupTripId': 1, 'userEmail': 1}", unique = true)
public class GroupTripMember {
    @Id
    private UUID id;
    private UUID groupTripId;
    private String userEmail;
    private String displayName;
    private String avatarUrl;
    private String role; // OWNER, MEMBER
    private Instant joinedAt;
}
