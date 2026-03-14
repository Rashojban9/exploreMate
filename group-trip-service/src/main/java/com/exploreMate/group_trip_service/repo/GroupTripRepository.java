package com.exploreMate.group_trip_service.repo;

import com.exploreMate.group_trip_service.model.GroupTrip;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupTripRepository extends MongoRepository<GroupTrip, UUID> {
    List<GroupTrip> findByCreatorEmailOrderByCreatedAtDesc(String creatorEmail);
    Optional<GroupTrip> findByInviteCode(String inviteCode);
}
