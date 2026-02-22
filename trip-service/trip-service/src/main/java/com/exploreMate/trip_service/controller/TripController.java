package com.exploreMate.trip_service.controller;

import com.exploreMate.trip_service.dto.TripRequest;
import com.exploreMate.trip_service.dto.TripResponse;
import com.exploreMate.trip_service.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
}
