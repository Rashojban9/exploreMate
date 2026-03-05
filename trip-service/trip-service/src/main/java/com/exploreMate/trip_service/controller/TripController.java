package com.exploreMate.trip_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exploreMate.trip_service.dto.TripRequest;
import com.exploreMate.trip_service.dto.TripResponse;
import com.exploreMate.trip_service.dto.UserTripStatsDto;
import com.exploreMate.trip_service.service.TripService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips(@RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(tripService.getTripsByUser(userEmail));
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody TripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tripService.createTrip(userEmail, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody TripRequest request) {
        return ResponseEntity.ok(tripService.updateTrip(id, userEmail, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail) {
        tripService.deleteTrip(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(@RequestParam String place) {
        return ResponseEntity.ok(tripService.searchTripsByPlace(place));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<UserTripStatsDto> getUserTripStats(@RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(tripService.getUserTripStats(userEmail));
    }
}
