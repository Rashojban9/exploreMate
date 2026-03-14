package com.exploreMate.group_trip_service.mapper;

import com.exploreMate.group_trip_service.dto.GroupTripExpenseResponse;
import com.exploreMate.group_trip_service.model.GroupTripExpense;
import org.springframework.stereotype.Component;

@Component
public class GroupTripExpenseMapper {

    public GroupTripExpenseResponse toResponse(GroupTripExpense expense) {
        return GroupTripExpenseResponse.builder()
                .id(expense.getId())
                .groupTripId(expense.getGroupTripId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .paidByEmail(expense.getPaidByEmail())
                .splitAmongEmails(expense.getSplitAmongEmails())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
