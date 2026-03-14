package com.exploreMate.group_trip_service.service;

import com.exploreMate.group_trip_service.dto.*;
import com.exploreMate.group_trip_service.mapper.*;
import com.exploreMate.group_trip_service.model.*;
import com.exploreMate.group_trip_service.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupTripService {

    private final GroupTripRepository groupTripRepo;
    private final GroupTripMemberRepository memberRepo;
    private final GroupTripActivityRepository activityRepo;
    private final GroupTripMessageRepository messageRepo;
    private final GroupTripExpenseRepository expenseRepo;
    private final GroupTripMapper tripMapper;
    private final GroupTripMemberMapper memberMapper;
    private final GroupTripActivityMapper activityMapper;
    private final GroupTripMessageMapper messageMapper;
    private final GroupTripExpenseMapper expenseMapper;

    // ─── Group Trip CRUD ──────────────────────────────────────────────────────

    public GroupTripResponse createGroupTrip(String userEmail, GroupTripRequest request) {
        GroupTrip trip = GroupTrip.builder()
                .id(UUID.randomUUID())
                .tripName(request.getTripName())
                .destination(request.getDestination())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .creatorEmail(userEmail)
                .inviteCode(generateInviteCode())
                .coverImage(request.getCoverImage())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        GroupTrip saved = groupTripRepo.save(trip);

        // Auto-add creator as OWNER member
        GroupTripMember owner = GroupTripMember.builder()
                .id(UUID.randomUUID())
                .groupTripId(saved.getId())
                .userEmail(userEmail)
                .displayName(userEmail.split("@")[0])
                .role("OWNER")
                .joinedAt(Instant.now())
                .build();
        memberRepo.save(owner);

        return tripMapper.toResponse(saved, 1);
    }

    public List<GroupTripResponse> getGroupTripsForUser(String userEmail) {
        // Find all trips where user is a member
        List<GroupTripMember> memberships = memberRepo.findByUserEmail(userEmail);
        List<GroupTripResponse> responses = new ArrayList<>();

        for (GroupTripMember membership : memberships) {
            groupTripRepo.findById(membership.getGroupTripId()).ifPresent(trip -> {
                int memberCount = (int) memberRepo.countByGroupTripId(trip.getId());
                responses.add(tripMapper.toResponse(trip, memberCount));
            });
        }

        return responses;
    }

    public GroupTripDetailResponse getGroupTripDetail(UUID tripId, String userEmail) {
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        // Verify user is a member
        if (!memberRepo.existsByGroupTripIdAndUserEmail(tripId, userEmail)) {
            throw new RuntimeException("You are not a member of this group trip");
        }

        List<GroupTripMemberResponse> members = memberRepo.findByGroupTripId(tripId)
                .stream().map(memberMapper::toResponse).toList();

        List<GroupTripActivityResponse> activities = activityRepo
                .findByGroupTripIdOrderByCreatedAtAsc(tripId)
                .stream().map(activityMapper::toResponse).toList();

        // Budget summary
        List<GroupTripExpense> expenses = expenseRepo.findByGroupTripId(tripId);
        double totalCost = expenses.stream().mapToDouble(GroupTripExpense::getAmount).sum();
        int memberCount = members.size();
        double perPerson = memberCount > 0 ? totalCost / memberCount : 0;

        GroupTripDetailResponse.BudgetSummary budget = GroupTripDetailResponse.BudgetSummary.builder()
                .totalCost(totalCost)
                .memberCount(memberCount)
                .perPersonCost(Math.round(perPerson * 100.0) / 100.0)
                .build();

        return GroupTripDetailResponse.builder()
                .trip(tripMapper.toResponse(trip, memberCount))
                .members(members)
                .activities(activities)
                .budget(budget)
                .build();
    }

    public GroupTripResponse updateGroupTrip(UUID tripId, String userEmail, GroupTripRequest request) {
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        if (!trip.getCreatorEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Only the trip owner can update the trip");
        }

        trip.setTripName(request.getTripName());
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        if (request.getCoverImage() != null) {
            trip.setCoverImage(request.getCoverImage());
        }
        trip.setUpdatedAt(Instant.now());

        GroupTrip updated = groupTripRepo.save(trip);
        int memberCount = (int) memberRepo.countByGroupTripId(tripId);
        return tripMapper.toResponse(updated, memberCount);
    }

    public void deleteGroupTrip(UUID tripId, String userEmail) {
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        if (!trip.getCreatorEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Only the trip owner can delete the trip");
        }

        // Cascade delete all related data
        memberRepo.deleteByGroupTripId(tripId);
        activityRepo.deleteByGroupTripId(tripId);
        messageRepo.deleteByGroupTripId(tripId);
        expenseRepo.deleteByGroupTripId(tripId);
        groupTripRepo.delete(trip);
    }

    // ─── Join by Invite Code ──────────────────────────────────────────────────

    public GroupTripResponse joinByInviteCode(String code, String userEmail, String displayName, String avatarUrl) {
        GroupTrip trip = groupTripRepo.findByInviteCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        if (memberRepo.existsByGroupTripIdAndUserEmail(trip.getId(), userEmail)) {
            throw new RuntimeException("You are already a member of this trip");
        }

        GroupTripMember member = GroupTripMember.builder()
                .id(UUID.randomUUID())
                .groupTripId(trip.getId())
                .userEmail(userEmail)
                .displayName(displayName != null ? displayName : userEmail.split("@")[0])
                .avatarUrl(avatarUrl)
                .role("MEMBER")
                .joinedAt(Instant.now())
                .build();
        memberRepo.save(member);

        int memberCount = (int) memberRepo.countByGroupTripId(trip.getId());
        return tripMapper.toResponse(trip, memberCount);
    }

    // ─── Members ──────────────────────────────────────────────────────────────

    public List<GroupTripMemberResponse> getMembers(UUID tripId) {
        return memberRepo.findByGroupTripId(tripId)
                .stream().map(memberMapper::toResponse).toList();
    }

    public GroupTripMemberResponse addMember(UUID tripId, String userEmail, GroupTripMemberRequest request) {
        // Verify trip exists
        groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        String targetEmail = request.getUserEmail();
        if (memberRepo.existsByGroupTripIdAndUserEmail(tripId, targetEmail)) {
            throw new RuntimeException("User is already a member");
        }

        GroupTripMember member = GroupTripMember.builder()
                .id(UUID.randomUUID())
                .groupTripId(tripId)
                .userEmail(targetEmail)
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : targetEmail.split("@")[0])
                .avatarUrl(request.getAvatarUrl())
                .role("MEMBER")
                .joinedAt(Instant.now())
                .build();

        return memberMapper.toResponse(memberRepo.save(member));
    }

    public void removeMember(UUID tripId, String userEmail, String memberEmail) {
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        if (!trip.getCreatorEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Only the trip owner can remove members");
        }

        if (memberEmail.equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Cannot remove yourself as the owner");
        }

        GroupTripMember member = memberRepo.findByGroupTripIdAndUserEmail(tripId, memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        memberRepo.delete(member);
    }

    // ─── Activities ───────────────────────────────────────────────────────────

    public List<GroupTripActivityResponse> getActivities(UUID tripId) {
        return activityRepo.findByGroupTripIdOrderByCreatedAtAsc(tripId)
                .stream().map(activityMapper::toResponse).toList();
    }

    public GroupTripActivityResponse proposeActivity(UUID tripId, String userEmail, GroupTripActivityRequest request) {
        // Verify membership
        if (!memberRepo.existsByGroupTripIdAndUserEmail(tripId, userEmail)) {
            throw new RuntimeException("You are not a member of this group trip");
        }

        GroupTripActivity activity = GroupTripActivity.builder()
                .id(UUID.randomUUID())
                .groupTripId(tripId)
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledTime(request.getScheduledTime())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .status("PROPOSED")
                .proposedByEmail(userEmail)
                .votedByEmails(new ArrayList<>(List.of(userEmail))) // Auto-vote by proposer
                .createdAt(Instant.now())
                .build();

        return activityMapper.toResponse(activityRepo.save(activity));
    }

    public GroupTripActivityResponse voteActivity(UUID tripId, UUID activityId, String userEmail) {
        GroupTripActivity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getGroupTripId().equals(tripId)) {
            throw new RuntimeException("Activity does not belong to this trip");
        }

        List<String> votes = activity.getVotedByEmails();
        if (votes == null) {
            votes = new ArrayList<>();
        }

        if (votes.contains(userEmail)) {
            votes.remove(userEmail); // Toggle off
        } else {
            votes.add(userEmail); // Toggle on
        }
        activity.setVotedByEmails(votes);

        return activityMapper.toResponse(activityRepo.save(activity));
    }

    public GroupTripActivityResponse confirmActivity(UUID tripId, UUID activityId, String userEmail) {
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        if (!trip.getCreatorEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Only the trip owner can confirm activities");
        }

        GroupTripActivity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        activity.setStatus("CONFIRMED");
        return activityMapper.toResponse(activityRepo.save(activity));
    }

    public void deleteActivity(UUID tripId, UUID activityId, String userEmail) {
        GroupTripActivity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getGroupTripId().equals(tripId)) {
            throw new RuntimeException("Activity does not belong to this trip");
        }

        // Only owner or proposer can delete
        GroupTrip trip = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Group trip not found"));

        if (!trip.getCreatorEmail().equalsIgnoreCase(userEmail)
                && !activity.getProposedByEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Only the trip owner or activity proposer can delete");
        }

        activityRepo.delete(activity);
    }

    // ─── Messages ─────────────────────────────────────────────────────────────

    public List<GroupTripMessageResponse> getMessages(UUID tripId) {
        return messageRepo.findByGroupTripIdOrderBySentAtAsc(tripId)
                .stream().map(messageMapper::toResponse).toList();
    }

    public GroupTripMessageResponse sendMessage(UUID tripId, String userEmail, GroupTripMessageRequest request) {
        if (!memberRepo.existsByGroupTripIdAndUserEmail(tripId, userEmail)) {
            throw new RuntimeException("You are not a member of this group trip");
        }

        GroupTripMessage message = GroupTripMessage.builder()
                .id(UUID.randomUUID())
                .groupTripId(tripId)
                .senderEmail(userEmail)
                .senderName(request.getSenderName() != null ? request.getSenderName() : userEmail.split("@")[0])
                .text(request.getText())
                .sentAt(Instant.now())
                .build();

        return messageMapper.toResponse(messageRepo.save(message));
    }

    // ─── Expenses ─────────────────────────────────────────────────────────────

    public List<GroupTripExpenseResponse> getExpenses(UUID tripId) {
        return expenseRepo.findByGroupTripId(tripId)
                .stream().map(expenseMapper::toResponse).toList();
    }

    public GroupTripExpenseResponse addExpense(UUID tripId, String userEmail, GroupTripExpenseRequest request) {
        if (!memberRepo.existsByGroupTripIdAndUserEmail(tripId, userEmail)) {
            throw new RuntimeException("You are not a member of this group trip");
        }

        List<String> splitAmong = request.getSplitAmongEmails();
        if (splitAmong == null || splitAmong.isEmpty()) {
            // Default: split among all members
            splitAmong = memberRepo.findByGroupTripId(tripId)
                    .stream().map(GroupTripMember::getUserEmail).toList();
        }

        GroupTripExpense expense = GroupTripExpense.builder()
                .id(UUID.randomUUID())
                .groupTripId(tripId)
                .title(request.getTitle())
                .amount(request.getAmount())
                .paidByEmail(userEmail)
                .splitAmongEmails(splitAmong)
                .createdAt(Instant.now())
                .build();

        return expenseMapper.toResponse(expenseRepo.save(expense));
    }

    public GroupTripDetailResponse.BudgetSummary getBudgetSummary(UUID tripId) {
        List<GroupTripExpense> expenses = expenseRepo.findByGroupTripId(tripId);
        double totalCost = expenses.stream().mapToDouble(GroupTripExpense::getAmount).sum();
        int memberCount = (int) memberRepo.countByGroupTripId(tripId);
        double perPerson = memberCount > 0 ? totalCost / memberCount : 0;

        return GroupTripDetailResponse.BudgetSummary.builder()
                .totalCost(totalCost)
                .memberCount(memberCount)
                .perPersonCost(Math.round(perPerson * 100.0) / 100.0)
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
