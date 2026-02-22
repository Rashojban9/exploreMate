package com.exploreMate.trip_service.repo;

import com.exploreMate.trip_service.model.SavedItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedItemRepository extends MongoRepository<SavedItem, String> {
    List<SavedItem> findByUserEmail(String userEmail);
    void deleteByIdAndUserEmail(String id, String userEmail);
}
