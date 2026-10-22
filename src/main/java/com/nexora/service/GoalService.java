package com.nexora.service;

import com.nexora.dto.ToggleTaskResponse;
import com.nexora.entities.Goal;
import com.nexora.entities.GoalTask;
import com.nexora.repository.GoalRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // Fetch all goals belonging to a user
    public List<Goal> getAllGoals(String userId) {
        return goalRepository.findByUserId(userId);
    }

    // Fetch a single goal belonging to a user
    public Goal getGoal(String goalId, String userId) {
        return findUserGoal(goalId, userId);
    }

    // Create a new goal
    public Goal createGoal(Goal goal, String userId) {

        validateGoal(goal);

        goal.setId(null);
        goal.setUserId(userId);
        goal.setCompletedDays(0);

        goal.setTasks(
                goal.getTasks() == null
                        ? new ArrayList<>()
                        : goal.getTasks()
        );

        goal.setDailyCompletions(new HashMap<>());
        goal.setCreatedAt(Instant.now());

        // Generate unique task IDs on the server
        Set<String> taskIds = new HashSet<>();

        for (GoalTask task : goal.getTasks()) {

            task.setId(java.util.UUID.randomUUID().toString());

            if (task.getTitle() == null ||
                    task.getTitle().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Task title cannot be empty"
                );
            }

            if (!taskIds.add(task.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate task ID"
                );
            }

            task.setTitle(task.getTitle().trim());
        }

        goal.setTitle(goal.getTitle().trim());

        if (goal.getDescription() == null ||
                goal.getDescription().isBlank()) {
            goal.setDescription(
                    "Make consistent progress towards your personal growth."
            );
        } else {
            goal.setDescription(goal.getDescription().trim());
        }

        if (goal.getIcon() == null ||
                goal.getIcon().isBlank()) {
            goal.setIcon("🎯");
        }

        return goalRepository.save(goal);
    }

    // Update editable goal details
    public Goal updateGoal(
            String goalId,
            Goal request,
            String userId
    ) {

        Goal existing = findUserGoal(goalId, userId);

        validateGoal(request);

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(
                request.getDescription() == null
                        ? ""
                        : request.getDescription().trim()
        );
        existing.setTargetDays(request.getTargetDays());
        existing.setXp(request.getXp());
        existing.setIcon(request.getIcon());

        /*
         * Do not accept userId, completedDays,
         * createdAt or dailyCompletions from the client.
         *
         * Preserve existing task IDs so historical
         * completions remain associated with those tasks.
         */

        return goalRepository.save(existing);
    }

    // Delete a goal
    public void deleteGoal(String goalId, String userId) {

        Goal goal = findUserGoal(goalId, userId);

        goalRepository.delete(goal);
    }

    // Toggle a task for the current local date
    public ToggleTaskResponse toggleTask(
            String goalId,
            String taskId,
            String userId
    ) {

        Goal goal = findUserGoal(goalId, userId);

        if (goal.getCompletedDays() >= goal.getTargetDays()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This goal is already completed"
            );
        }

        GoalTask task = goal.getTasks()
                .stream()
                .filter(t -> taskId.equals(t.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        )
                );

        // Use the server's configured timezone.
        // Configure it to match the app's intended timezone.
        String today = LocalDate.now().toString();

        Map<String, List<String>> completions =
                goal.getDailyCompletions();

        if (completions == null) {
            completions = new HashMap<>();
            goal.setDailyCompletions(completions);
        }

        List<String> todayTasks =
                new ArrayList<>(
                        completions.getOrDefault(
                                today,
                                new ArrayList<>()
                        )
                );

        boolean wasDayComplete =
                allTasksCompleted(goal, todayTasks);

        if (todayTasks.contains(task.getId())) {
            todayTasks.remove(task.getId());
        } else {
            todayTasks.add(task.getId());
        }

        boolean isDayComplete =
                allTasksCompleted(goal, todayTasks);

        completions.put(today, todayTasks);

        int completedDays = 0;

        // Count distinct dates on which all tasks were completed
        for (List<String> completedTaskIds :
                completions.values()) {

            if (allTasksCompleted(goal, completedTaskIds)) {
                completedDays++;
            }
        }

        goal.setCompletedDays(
                Math.min(completedDays, goal.getTargetDays())
        );

        /*
         * Reward only on the transition from incomplete
         * to complete for today's tasks.
         */
        boolean xpAwarded =
                !wasDayComplete && isDayComplete;

        int xpEarned =
                xpAwarded ? goal.getXp() : 0;

        Goal savedGoal = goalRepository.save(goal);

        return new ToggleTaskResponse(
                savedGoal,
                isDayComplete,
                xpAwarded,
                xpEarned
        );
    }

    private Goal findUserGoal(
            String goalId,
            String userId
    ) {

        return goalRepository.findByIdAndUserId(
                goalId,
                userId
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal not found"
                )
        );
    }

    private boolean allTasksCompleted(
            Goal goal,
            List<String> completedTaskIds
    ) {

        if (goal.getTasks() == null ||
                goal.getTasks().isEmpty()) {
            return false;
        }

        return goal.getTasks().stream()
                .allMatch(task ->
                        completedTaskIds.contains(task.getId())
                );
    }

    private void validateGoal(Goal goal) {

        if (goal == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Goal data is required"
            );
        }

        if (goal.getTitle() == null ||
                goal.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Goal title is required"
            );
        }

        if (goal.getTitle().trim().length() > 80) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Goal title cannot exceed 80 characters"
            );
        }

        if (goal.getDescription() != null &&
                goal.getDescription().length() > 250) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Description cannot exceed 250 characters"
            );
        }

        if (goal.getTargetDays() < 1 ||
                goal.getTargetDays() > 365) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Target days must be between 1 and 365"
            );
        }

        if (goal.getXp() < 1 ||
                goal.getXp() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "XP must be between 1 and 1000"
            );
        }

        if (goal.getTasks() == null ||
                goal.getTasks().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one daily task is required"
            );
        }

        if (goal.getTasks().size() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A goal cannot have more than 50 tasks"
            );
        }
    }
}