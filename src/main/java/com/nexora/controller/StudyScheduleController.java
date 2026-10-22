package com.nexora.controller;

import com.nexora.entities.StudySchedule;
import com.nexora.repository.StudyScheduleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/study-schedules")
public class StudyScheduleController {

    private final StudyScheduleRepository repository;

    public StudyScheduleController(
            StudyScheduleRepository repository
    ) {
        this.repository = repository;
    }

    @PostMapping
    public StudySchedule createSchedule(
            @RequestBody StudySchedule schedule,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        validateSchedule(schedule);

        schedule.setId(null);
        schedule.setUserId(userId);

        return repository.save(schedule);
    }

    @GetMapping
    public List<StudySchedule> getSchedules(
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        return repository.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public StudySchedule updateSchedule(
            @PathVariable String id,
            @RequestBody StudySchedule request,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        validateSchedule(request);

        StudySchedule existing = repository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Study schedule not found"
                ));

        existing.setTopic(request.getTopic());
        existing.setDate(request.getDate());
        existing.setTime(request.getTime());
        existing.setDuration(request.getDuration());
        existing.setRepeatDaily(request.isRepeatDaily());

        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(
            @PathVariable String id,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        StudySchedule existing = repository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Study schedule not found"
                ));

        repository.delete(existing);
    }

    private String getAuthenticatedUserId(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return authentication.getName();
    }

    private void validateSchedule(StudySchedule schedule) {
        if (schedule.getTopic() == null
                || schedule.getTopic().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Topic is required"
            );
        }

        if (schedule.getDate() == null
                || schedule.getTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date and time are required"
            );
        }

        if (schedule.getDuration() <= 0
                || schedule.getDuration() > 480) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duration must be between 1 and 480 minutes"
            );
        }
    }
}