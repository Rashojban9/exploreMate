package com.exploreMate.trip_service.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.exploreMate.trip_service.model.Trip;

@Repository
public interface TripRepository extends MongoRepository<Trip, UUID> {
    List<Trip> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Trip> findByPlaceNameContainingIgnoreCase(String placeName);
    void deleteByIdAndUserEmail(UUID id, String userEmail);
    
    // Count methods for user stats
    long countByUserEmail(String userEmail);
    long countByUserEmailAndStatus(String userEmail, String status);
}
