package com.nexora.dto;

import com.nexora.entities.Goal;

public class ToggleTaskResponse {

    private Goal goal;
    private boolean dayCompleted;
    private boolean xpAwarded;
    private int xpEarned;

    public ToggleTaskResponse() {
    }

    public ToggleTaskResponse(
            Goal goal,
            boolean dayCompleted,
            boolean xpAwarded,
            int xpEarned
    ) {
        this.goal = goal;
        this.dayCompleted = dayCompleted;
        this.xpAwarded = xpAwarded;
        this.xpEarned = xpEarned;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public boolean isDayCompleted() {
        return dayCompleted;
    }

    public void setDayCompleted(boolean dayCompleted) {
        this.dayCompleted = dayCompleted;
    }

    public boolean isXpAwarded() {
        return xpAwarded;
    }

    public void setXpAwarded(boolean xpAwarded) {
        this.xpAwarded = xpAwarded;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }
}