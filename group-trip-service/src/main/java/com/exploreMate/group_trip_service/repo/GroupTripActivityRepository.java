package com.exploreMate.group_trip_service.repo;

import com.exploreMate.group_trip_service.model.GroupTripActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface GroupTripActivityRepository extends MongoRepository<GroupTripActivity, UUID> {
    List<GroupTripActivity> findByGroupTripIdOrderByCreatedAtAsc(UUID groupTripId);
    void deleteByGroupTripId(UUID groupTripId);
}
