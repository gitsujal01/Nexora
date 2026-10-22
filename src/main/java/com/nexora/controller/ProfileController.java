package com.nexora.controller;

import com.nexora.dto.ProfileCreateRequest;
import com.nexora.dto.ProfileUpdateRequest;
import com.nexora.dto.ProfileResponse;
import com.nexora.service.ProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createProfile(
            @RequestBody ProfileCreateRequest request,
            Authentication authentication) {

        String userId = getAuthenticatedUserId(authentication);

        return profileService.createProfile(userId, request);
    }

    @GetMapping("/me")
    public ProfileResponse getMyProfile(
            Authentication authentication) {

        String userId = getAuthenticatedUserId(authentication);

        return profileService.getProfile(userId);
    }

    @PutMapping("/me")
    public ProfileResponse updateMyProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        String userId = getAuthenticatedUserId(authentication);

        return profileService.updateProfile(userId, request);
    }

    private String getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {

            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first"
            );
        }

        return authentication.getName();
    }
}