package com.exploreMate.group_trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group_trips")
public class GroupTrip {
    @Id
    private UUID id;
    private String tripName;
    private String destination;
    private String description;
    private String startDate;
    private String endDate;
    @Indexed
    private String creatorEmail;
    @Indexed(unique = true)
    private String inviteCode;
    private String coverImage;
    private Instant createdAt;
    private Instant updatedAt;
}
