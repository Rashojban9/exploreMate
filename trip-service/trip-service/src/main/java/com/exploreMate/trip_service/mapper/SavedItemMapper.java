package com.exploreMate.trip_service.mapper;

import com.exploreMate.trip_service.dto.SavedItemRequest;
import com.exploreMate.trip_service.dto.SavedItemResponse;
import com.exploreMate.trip_service.model.SavedItem;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class SavedItemMapper {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SavedItem toEntity(SavedItemRequest request, String userEmail) {
        return SavedItem.builder()
                .userEmail(userEmail)
                .type(request.getType())
                .title(request.getTitle())
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .dateAdded(java.time.LocalDateTime.now())
                .build();
    }

    public SavedItemResponse toResponse(SavedItem savedItem) {
        return SavedItemResponse.builder()
                .id(Long.parseLong(savedItem.getId()))
                .type(savedItem.getType())
                .title(savedItem.getTitle())
                .location(savedItem.getLocation())
                .imageUrl(savedItem.getImageUrl())
                .description(savedItem.getDescription())
                .dateAdded(savedItem.getDateAdded() != null ? savedItem.getDateAdded().format(FORMATTER) : null)
                .build();
    }
}
