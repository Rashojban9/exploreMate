package com.exploreMate.qr_guide_service.controller;

import com.exploreMate.qr_guide_service.dto.QrArtifactRequest;
import com.exploreMate.qr_guide_service.dto.QrArtifactResponse;
import com.exploreMate.qr_guide_service.dto.ScanHistoryResponse;
import com.exploreMate.qr_guide_service.service.QrGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qr-guides")
@RequiredArgsConstructor
public class QrGuideController {

    private final QrGuideService service;

    @GetMapping
    public ResponseEntity<List<QrArtifactResponse>> getAllArtifacts() {
        return ResponseEntity.ok(service.getAllArtifacts());
    }

    @PostMapping
    public ResponseEntity<QrArtifactResponse> createArtifact(@RequestBody QrArtifactRequest request) {
        return new ResponseEntity<>(service.createArtifact(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QrArtifactResponse> getArtifact(@PathVariable String id) {
        return ResponseEntity.ok(service.getArtifact(id));
    }

    @PostMapping("/{id}/scan")
    public ResponseEntity<ScanHistoryResponse> recordScan(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.recordScan(id, userEmail));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ScanHistoryResponse>> getUserHistory(
            @RequestHeader("X-User-Email") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.getUserHistory(userEmail));
    }
}
