package com.nexora.repository;

import com.nexora.entities.StudySchedule;
import com.nexora.repository.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudyScheduleRepository
        extends MongoRepository<StudySchedule, String> {

    List<StudySchedule> findByUserId(String userId);

    Optional<StudySchedule> findByIdAndUserId(
            String id,
            String userId
    );
}