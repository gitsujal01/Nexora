package com.nexora.service;

import com.nexora.dto.DashBoardResponse;
import com.nexora.entities.UserProgress;

import com.nexora.repository.MissionRepository;
import com.nexora.repository.ChallengeRepository;
import com.nexora.repository.StudyScheduleRepository;
import com.nexora.repository.UserProgressRepository;

import org.springframework.stereotype.Service;

@Service
public class DashBoardService {

    private final MissionRepository missionRepository;
    private final ChallengeRepository challengeRepository;
    private final StudyScheduleRepository scheduleRepository;
    private final UserProgressRepository progressRepository;

    public DashBoardService(
            MissionRepository missionRepository,
            ChallengeRepository challengeRepository,
            StudyScheduleRepository scheduleRepository,
            UserProgressRepository progressRepository
    ) {
        this.missionRepository = missionRepository;
        this.challengeRepository = challengeRepository;
        this.scheduleRepository = scheduleRepository;
        this.progressRepository = progressRepository;
    }

    public DashBoardResponse getDashboard(String userId) {

        UserProgress progress = progressRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    UserProgress newProgress = new UserProgress();

                    newProgress.setUserId(userId);
                    newProgress.setTotalXP(0);
                    newProgress.setStreak(0);
                    newProgress.setBestStreak(0);

                    return progressRepository.save(newProgress);
                });

        return new DashBoardResponse(
                missionRepository.findByUserId(userId),
                challengeRepository.findByUserId(userId),
                scheduleRepository.findByUserId(userId),
                progress
        );
    }
}