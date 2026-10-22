package com.nexora.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "goals")
public class Goal {

    @Id
    private String id;

    private String userId;

    private String title;

    private String description;

    private int targetDays;

    private int completedDays;

    private int xp;

    private String icon;

    private List<GoalTask> tasks = new ArrayList<>();

    /*
     * Example:
     *
     * {
     *   "2026-10-21": ["task-101", "task-102"],
     *   "2026-10-22": ["task-101"]
     * }
     */
    private Map<String, List<String>> dailyCompletions =
            new HashMap<>();

    private Instant createdAt;

    public Goal() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTargetDays() {
        return targetDays;
    }

    public void setTargetDays(int targetDays) {
        this.targetDays = targetDays;
    }

    public int getCompletedDays() {
        return completedDays;
    }

    public void setCompletedDays(int completedDays) {
        this.completedDays = completedDays;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<GoalTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<GoalTask> tasks) {
        this.tasks = tasks;
    }

    public Map<String, List<String>> getDailyCompletions() {
        return dailyCompletions;
    }

    public void setDailyCompletions(
            Map<String, List<String>> dailyCompletions) {
        this.dailyCompletions = dailyCompletions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}