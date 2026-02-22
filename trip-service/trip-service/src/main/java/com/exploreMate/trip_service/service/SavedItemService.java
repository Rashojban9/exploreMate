package com.exploreMate.trip_service.service;

import com.exploreMate.trip_service.dto.SavedItemRequest;
import com.exploreMate.trip_service.dto.SavedItemResponse;
import com.exploreMate.trip_service.mapper.SavedItemMapper;
import com.exploreMate.trip_service.model.SavedItem;
import com.exploreMate.trip_service.repo.SavedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedItemService {
    
    private final SavedItemRepository savedItemRepository;
    private final SavedItemMapper savedItemMapper;

    public List<SavedItemResponse> getSavedItemsByUser(String userEmail) {
        return savedItemRepository.findByUserEmail(userEmail)
                .stream()
                .map(savedItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SavedItemResponse createSavedItem(String userEmail, SavedItemRequest request) {
        SavedItem savedItem = savedItemMapper.toEntity(request, userEmail);
        SavedItem saved = savedItemRepository.save(savedItem);
        return savedItemMapper.toResponse(saved);
    }

    public void deleteSavedItem(String id, String userEmail) {
        savedItemRepository.deleteByIdAndUserEmail(id, userEmail);
    }
}
