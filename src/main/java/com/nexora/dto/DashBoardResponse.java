package com.nexora.dto;

import com.nexora.entities.Mission;
import com.nexora.entities.Challenge;
import com.nexora.entities.UserProgress;
import com.nexora.entities.StudySchedule;

import java.util.List;

public class DashBoardResponse {

    private List<Mission> missions;
    private List<Challenge> challenges;
    private List<StudySchedule> schedules;
    private UserProgress progress;

    public DashBoardResponse() {
    }

    public DashBoardResponse(
            List<Mission> missions,
            List<Challenge> challenges,
            List<StudySchedule> schedules,
            UserProgress progress
    ) {
        this.missions = missions;
        this.challenges = challenges;
        this.schedules = schedules;
        this.progress = progress;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public List<StudySchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<StudySchedule> schedules) {
        this.schedules = schedules;
    }

    public UserProgress getProgress() {
        return progress;
    }

    public void setProgress(UserProgress progress) {
        this.progress = progress;
    }
}