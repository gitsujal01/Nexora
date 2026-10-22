package com.nexora.controller;

import com.nexora.dto.ToggleTaskResponse;
import com.nexora.entities.Goal;
import com.nexora.service.GoalService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // GET /api/goals
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals(
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        return ResponseEntity.ok(
                goalService.getAllGoals(userId)
        );
    }

    // GET /api/goals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(
            @PathVariable String id,
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        return ResponseEntity.ok(
                goalService.getGoal(id, userId)
        );
    }

    // POST /api/goals
    @PostMapping
    public ResponseEntity<Goal> createGoal(
            @RequestBody Goal goal,
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        Goal createdGoal =
                goalService.createGoal(goal, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdGoal);
    }

    // PUT /api/goals/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(
            @PathVariable String id,
            @RequestBody Goal goal,
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        return ResponseEntity.ok(
                goalService.updateGoal(id, goal, userId)
        );
    }

    // DELETE /api/goals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable String id,
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        goalService.deleteGoal(id, userId);

        return ResponseEntity.noContent().build();
    }

    // PATCH /api/goals/{id}/tasks/{taskId}/toggle
    @PatchMapping("/{id}/tasks/{taskId}/toggle")
    public ResponseEntity<ToggleTaskResponse> toggleTask(
            @PathVariable String id,
            @PathVariable String taskId,
            Authentication authentication
    ) {

        String userId = getUserId(authentication);

        return ResponseEntity.ok(
                goalService.toggleTask(id, taskId, userId)
        );
    }

    private String getUserId(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new org.springframework.web.server
                    .ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first"
            );
        }

        /*
         * IMPORTANT:
         * This assumes authentication.getName() returns
         * the MongoDB user ID.
         *
         * If your JWT stores the user's email as the
         * principal name, resolve that email to the actual
         * user ID through your existing user repository.
         */
        return authentication.getName();
    }
}