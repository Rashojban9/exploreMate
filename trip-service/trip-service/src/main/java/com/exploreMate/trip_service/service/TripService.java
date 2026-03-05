package com.exploreMate.trip_service.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.exploreMate.trip_service.dto.TripRequest;
import com.exploreMate.trip_service.dto.TripResponse;
import com.exploreMate.trip_service.dto.UserTripStatsDto;
import com.exploreMate.trip_service.mapper.TripMapper;
import com.exploreMate.trip_service.model.Trip;
import com.exploreMate.trip_service.repo.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public List<TripResponse> getTripsByUser(String userEmail) {
        List<Trip> trips = tripRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return trips.stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    public TripResponse createTrip(String userEmail, TripRequest request) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .tripName(request.getTripName())
                .tripDescription(request.getTripDescription())
                .placeName(request.getPlaceName())
                .placeDescription(request.getPlaceDescription())
                .placePhotos(request.getPlacePhotos())
                .userEmail(userEmail)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus().toUpperCase() : "UPCOMING")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toResponse(savedTrip);
    }

    public void deleteTrip(UUID tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Unauthorized to delete this trip");
        }

        tripRepository.delete(trip);
    }

    public TripResponse updateTrip(UUID tripId, String userEmail, TripRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Unauthorized to update this trip");
        }

        trip.setTripName(request.getTripName());
        trip.setTripDescription(request.getTripDescription());
        trip.setPlaceName(request.getPlaceName());
        trip.setPlaceDescription(request.getPlaceDescription());
        trip.setPlacePhotos(request.getPlacePhotos());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            trip.setStatus(request.getStatus().toUpperCase());
        }
        trip.setUpdatedAt(Instant.now());

        Trip updatedTrip = tripRepository.save(trip);
        return tripMapper.toResponse(updatedTrip);
    }

    public List<TripResponse> searchTripsByPlace(String placeName) {
        List<Trip> trips = tripRepository.findByPlaceNameContainingIgnoreCase(placeName);
        return trips.stream()
                .map(tripMapper::toResponse)
                .toList();
    }
    
    public UserTripStatsDto getUserTripStats(String userEmail) {
        // Get all trips to calculate stats (excluding drafts from total)
        List<Trip> allTrips = tripRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        
        // Total trips excludes draft
        long totalTrips = allTrips.stream()
                .filter(trip -> !"DRAFT".equals(trip.getStatus()))
                .count();
        
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        int missedTrips = 0;
        int completedTrips = 0;
        int upcomingCount = 0;
        
        for (Trip trip : allTrips) {
            // Skip draft
            if ("DRAFT".equals(trip.getStatus())) {
                continue;
            }
            
            if (trip.getEndDate() != null && !trip.getEndDate().isEmpty()) {
                try {
                    LocalDate endDate = LocalDate.parse(trip.getEndDate(), formatter);
                    
                    if (endDate.isBefore(now)) {
                        // End date is in the past - this is missed
                        missedTrips++;
                    } else {
                        // End date is in the future
                        if ("PAST".equals(trip.getStatus())) {
                            completedTrips++;
                        } else {
                            upcomingCount++;
                        }
                    }
                } catch (Exception e) {
                    // If date parsing fails
                    if ("PAST".equals(trip.getStatus())) {
                        completedTrips++;
                    }
                }
            } else {
                // No endDate
                if ("PAST".equals(trip.getStatus())) {
                    completedTrips++;
                } else if ("UPCOMING".equals(trip.getStatus())) {
                    upcomingCount++;
                }
            }
        }
        
        return UserTripStatsDto.builder()
                .totalTrips((int) totalTrips)
                .completedTrips(completedTrips)
                .upcomingTrips(upcomingCount)
                .missedTrips(missedTrips)
                .draftTrips(0)
                .build();
    }
}
