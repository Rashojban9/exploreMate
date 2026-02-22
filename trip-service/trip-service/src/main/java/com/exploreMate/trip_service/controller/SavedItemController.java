package com.exploreMate.trip_service.controller;

import com.exploreMate.trip_service.dto.SavedItemRequest;
import com.exploreMate.trip_service.dto.SavedItemResponse;
import com.exploreMate.trip_service.service.SavedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved")
@RequiredArgsConstructor
public class SavedItemController {

    private final SavedItemService savedItemService;

    @GetMapping
    public ResponseEntity<List<SavedItemResponse>> getSavedItems(@RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(savedItemService.getSavedItemsByUser(userEmail));
    }

    @PostMapping
    public ResponseEntity<SavedItemResponse> createSavedItem(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody SavedItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedItemService.createSavedItem(userEmail, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedItem(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String userEmail) {
        savedItemService.deleteSavedItem(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
