package com.exploreMate.content_service.repository;

import com.exploreMate.content_service.model.PageContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageContentRepository extends MongoRepository<PageContent, String> {
    Optional<PageContent> findBySlug(String slug);
}
