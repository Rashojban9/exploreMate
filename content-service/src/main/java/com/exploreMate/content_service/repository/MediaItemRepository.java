package com.exploreMate.content_service.repository;

import com.exploreMate.content_service.model.MediaItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaItemRepository extends MongoRepository<MediaItem, String> {
}
