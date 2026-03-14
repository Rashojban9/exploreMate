package com.exploreMate.group_trip_service.repo;

import com.exploreMate.group_trip_service.model.GroupTripMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface GroupTripMessageRepository extends MongoRepository<GroupTripMessage, UUID> {
    List<GroupTripMessage> findByGroupTripIdOrderBySentAtAsc(UUID groupTripId);
    void deleteByGroupTripId(UUID groupTripId);
}
