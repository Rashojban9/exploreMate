package com.exploreMate.group_trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group_trip_messages")
public class GroupTripMessage {
    @Id
    private UUID id;
    private UUID groupTripId;
    private String senderEmail;
    private String senderName;
    private String text;
    private Instant sentAt;
}
