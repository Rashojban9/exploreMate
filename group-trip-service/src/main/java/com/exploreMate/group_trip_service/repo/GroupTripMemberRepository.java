package com.exploreMate.group_trip_service.repo;

import com.exploreMate.group_trip_service.model.GroupTripMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupTripMemberRepository extends MongoRepository<GroupTripMember, UUID> {
    List<GroupTripMember> findByGroupTripId(UUID groupTripId);
    List<GroupTripMember> findByUserEmail(String userEmail);
    Optional<GroupTripMember> findByGroupTripIdAndUserEmail(UUID groupTripId, String userEmail);
    boolean existsByGroupTripIdAndUserEmail(UUID groupTripId, String userEmail);
    void deleteByGroupTripId(UUID groupTripId);
    long countByGroupTripId(UUID groupTripId);
}
