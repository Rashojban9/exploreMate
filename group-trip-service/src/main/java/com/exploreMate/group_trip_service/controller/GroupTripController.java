package com.exploreMate.group_trip_service.controller;

import com.exploreMate.group_trip_service.dto.*;
import com.exploreMate.group_trip_service.service.GroupTripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/group-trips")
@RequiredArgsConstructor
public class GroupTripController {

    private final GroupTripService groupTripService;

    // ─── Group Trip CRUD ──────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<GroupTripResponse> createGroupTrip(
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupTripService.createGroupTrip(userEmail, request));
    }

    @GetMapping
    public ResponseEntity<List<GroupTripResponse>> getGroupTrips(
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(groupTripService.getGroupTripsForUser(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupTripDetailResponse> getGroupTripDetail(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(groupTripService.getGroupTripDetail(id, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupTripResponse> updateGroupTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripRequest request) {
        return ResponseEntity.ok(groupTripService.updateGroupTrip(id, userEmail, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupTrip(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail) {
        groupTripService.deleteGroupTrip(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    // ─── Join by Invite Code ──────────────────────────────────────────────────

    @PostMapping("/join")
    public ResponseEntity<GroupTripResponse> joinByInviteCode(
            @RequestParam String code,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String avatarUrl) {
        return ResponseEntity.ok(groupTripService.joinByInviteCode(code, userEmail, displayName, avatarUrl));
    }

    // ─── Members ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupTripMemberResponse>> getMembers(@PathVariable UUID id) {
        return ResponseEntity.ok(groupTripService.getMembers(id));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<GroupTripMemberResponse> addMember(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupTripService.addMember(id, userEmail, request));
    }

    @DeleteMapping("/{id}/members/{memberEmail}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @PathVariable String memberEmail) {
        groupTripService.removeMember(id, userEmail, memberEmail);
        return ResponseEntity.noContent().build();
    }

    // ─── Activities ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<GroupTripActivityResponse>> getActivities(@PathVariable UUID id) {
        return ResponseEntity.ok(groupTripService.getActivities(id));
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<GroupTripActivityResponse> proposeActivity(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupTripService.proposeActivity(id, userEmail, request));
    }

    @PostMapping("/{id}/activities/{activityId}/vote")
    public ResponseEntity<GroupTripActivityResponse> voteActivity(
            @PathVariable UUID id,
            @PathVariable UUID activityId,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(groupTripService.voteActivity(id, activityId, userEmail));
    }

    @PutMapping("/{id}/activities/{activityId}/confirm")
    public ResponseEntity<GroupTripActivityResponse> confirmActivity(
            @PathVariable UUID id,
            @PathVariable UUID activityId,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(groupTripService.confirmActivity(id, activityId, userEmail));
    }

    @DeleteMapping("/{id}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable UUID id,
            @PathVariable UUID activityId,
            @RequestHeader("X-User-Email") String userEmail) {
        groupTripService.deleteActivity(id, activityId, userEmail);
        return ResponseEntity.noContent().build();
    }

    // ─── Messages ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<GroupTripMessageResponse>> getMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(groupTripService.getMessages(id));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<GroupTripMessageResponse> sendMessage(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupTripService.sendMessage(id, userEmail, request));
    }

    // ─── Expenses & Budget ────────────────────────────────────────────────────

    @GetMapping("/{id}/expenses")
    public ResponseEntity<List<GroupTripExpenseResponse>> getExpenses(@PathVariable UUID id) {
        return ResponseEntity.ok(groupTripService.getExpenses(id));
    }

    @PostMapping("/{id}/expenses")
    public ResponseEntity<GroupTripExpenseResponse> addExpense(
            @PathVariable UUID id,
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody GroupTripExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupTripService.addExpense(id, userEmail, request));
    }

    @GetMapping("/{id}/budget")
    public ResponseEntity<GroupTripDetailResponse.BudgetSummary> getBudgetSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(groupTripService.getBudgetSummary(id));
    }
}
