package com.nexora.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_progress")
public class UserProgress {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private int totalXP;
    private int streak;
    private int bestStreak;
    private String lastCompletedDate;

    public UserProgress() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public String getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(String lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }
}