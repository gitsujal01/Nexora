package com.nexora.dto;

public class ProfileResponse {

    private String id;
    private String fullName;
    private String phone;
    private String bio;
    private String location;
    private String profileImage;

    public ProfileResponse() {
    }

    public ProfileResponse(
            String id,
            String fullName,
            String phone,
            String bio,
            String location,
            String profileImage) {

        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.bio = bio;
        this.location = location;
        this.profileImage = profileImage;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getProfileImage() {
        return profileImage;
    }
}