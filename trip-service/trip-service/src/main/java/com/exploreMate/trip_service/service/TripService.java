package com.exploreMate.trip_service.service;

import com.exploreMate.trip_service.dto.TripRequest;
import com.exploreMate.trip_service.dto.TripResponse;
import com.exploreMate.trip_service.mapper.TripMapper;
import com.exploreMate.trip_service.model.Trip;
import com.exploreMate.trip_service.repo.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
}
