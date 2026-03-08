package com.exploreMate.auth_service.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exploreMate.auth_service.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByEmail(String email);
    void deleteByEmail(String email);
}
