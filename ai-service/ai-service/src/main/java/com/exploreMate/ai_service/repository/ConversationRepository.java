package com.exploreMate.ai_service.repository;

import com.exploreMate.ai_service.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for conversation storage
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    
    /**
     * Find all conversations for a specific user, sorted by updated time (most recent first)
     */
    List<Conversation> findByUserEmailOrderByUpdatedAtDesc(String userEmail);
    
    /**
     * Find a specific conversation by user email and session ID
     */
    Optional<Conversation> findByUserEmailAndSessionId(String userEmail, String sessionId);
    
    /**
     * Delete a conversation by user email and session ID
     */
    void deleteByUserEmailAndSessionId(String userEmail, String sessionId);
}
