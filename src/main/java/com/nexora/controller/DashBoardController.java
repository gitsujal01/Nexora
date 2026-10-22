package com.nexora.controller;

import com.nexora.dto.DashBoardResponse;
import com.nexora.service.DashBoardService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashboardService;

    public DashBoardController(
            DashBoardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashBoardResponse getDashboard(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return dashboardService.getDashboard(
                authentication.getName()
        );
    }
}