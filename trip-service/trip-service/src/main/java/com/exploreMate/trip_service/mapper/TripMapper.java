package com.exploreMate.trip_service.mapper;

import com.exploreMate.trip_service.dto.TripResponse;
import com.exploreMate.trip_service.model.Trip;
import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .tripName(trip.getTripName())
                .tripDescription(trip.getTripDescription())
                .placeName(trip.getPlaceName())
                .placeDescription(trip.getPlaceDescription())
                .placePhotos(trip.getPlacePhotos())
                .userEmail(trip.getUserEmail())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }
}
