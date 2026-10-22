package com.nexora.repository;

import com.nexora.entities.Challenge;
import com.nexora.repository.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository
        extends MongoRepository<Challenge, String> {

    List<Challenge> findByUserId(String userId);

    Optional<Challenge> findByIdAndUserId(
            String id,
            String userId
    );
}