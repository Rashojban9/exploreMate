package com.exploreMate.group_trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group_trip_expenses")
public class GroupTripExpense {
    @Id
    private UUID id;
    private UUID groupTripId;
    private String title;
    private double amount;
    private String paidByEmail;
    @Builder.Default
    private List<String> splitAmongEmails = new ArrayList<>();
    private Instant createdAt;
}
