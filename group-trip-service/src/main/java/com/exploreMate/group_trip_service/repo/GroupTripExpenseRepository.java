package com.exploreMate.group_trip_service.repo;

import com.exploreMate.group_trip_service.model.GroupTripExpense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface GroupTripExpenseRepository extends MongoRepository<GroupTripExpense, UUID> {
    List<GroupTripExpense> findByGroupTripId(UUID groupTripId);
    void deleteByGroupTripId(UUID groupTripId);
}
