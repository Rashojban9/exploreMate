package com.exploreMate.auth_service.repo;

import com.exploreMate.auth_service.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepo extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByEmail(String email);
}
