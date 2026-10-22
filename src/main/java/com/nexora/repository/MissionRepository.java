package com.nexora.repository;

import com.nexora.entities.Mission;
import com.nexora.repository.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MissionRepository
        extends MongoRepository<Mission, String> {

    List<Mission> findByUserId(String userId);

    Optional<Mission> findByIdAndUserId(
            String id,
            String userId
    );
}