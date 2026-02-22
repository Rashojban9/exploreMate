package com.exploreMate.trip_service.repo;

import com.exploreMate.trip_service.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends MongoRepository<Trip, UUID> {
    List<Trip> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Trip> findByPlaceNameContainingIgnoreCase(String placeName);
    void deleteByIdAndUserEmail(UUID id, String userEmail);
}
