package com.nexora.service;

import com.nexora.dto.ProfileCreateRequest;
import com.nexora.dto.ProfileUpdateRequest;
import com.nexora.dto.ProfileResponse;
import com.nexora.entities.Profile;
import com.nexora.repository.ProfileRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileResponse createProfile(
            String userId,
            ProfileCreateRequest request) {

        if (profileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Profile already exists"
            );
        }

        Profile profile = new Profile();

        profile.setUserId(userId);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setProfileImage(request.getProfileImage());

        Profile savedProfile = profileRepository.save(profile);

        return convertToResponse(savedProfile);
    }

    public ProfileResponse getProfile(String userId) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found"
                ));

        return convertToResponse(profile);
    }

    public ProfileResponse updateProfile(
            String userId,
            ProfileUpdateRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found"
                ));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setProfileImage(request.getProfileImage());

        Profile updatedProfile = profileRepository.save(profile);

        return convertToResponse(updatedProfile);
    }

    private ProfileResponse convertToResponse(Profile profile) {

        return new ProfileResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getBio(),
                profile.getLocation(),
                profile.getProfileImage()
        );
    }
}