package com.nexora.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nexora.entities.PasswordResetOtp;
import com.nexora.entities.User;
import com.nexora.repository.PasswordResetOtpRepository;
import com.nexora.repository.userRepository;

@Service
public class OtpService {

    private final PasswordResetOtpRepository otpRepository;
    private final userRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
            PasswordResetOtpRepository otpRepository,
            userRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Generate a six-digit OTP
    private String generateOtp() {
        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000));
    }

    // Generate a SHA-256 hash for reset tokens
    private String hashValue(String value) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash reset token", ex);
        }
    }

    // STEP 1: Send OTP to registered email
    public void sendOtp(String email) {

        String normalizedEmail =
                email.trim().toLowerCase();

        // Avoid revealing whether an email is registered
        if (!userRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        otpRepository.deleteByEmail(normalizedEmail);

        String otp = generateOtp();

        PasswordResetOtp record = new PasswordResetOtp();

        record.setEmail(normalizedEmail);
        record.setOtpHash(passwordEncoder.encode(otp));
        record.setExpiresAt(Instant.now().plusSeconds(300));
        record.setAttempts(0);
        record.setVerified(false);

        otpRepository.save(record);

        try {
            emailService.sendOtp(normalizedEmail, otp);
        } catch (RuntimeException ex) {
            otpRepository.deleteByEmail(normalizedEmail);

            throw new IllegalStateException(
                    "Failed to send OTP email. Check Gmail configuration.",
                    ex);
        }
    }

    // STEP 2: Verify OTP and issue a reset token
    public String verifyOtp(String email, String otp) {

        String normalizedEmail =
                email.trim().toLowerCase();

        PasswordResetOtp record = otpRepository
                .findTopByEmailOrderByExpiresAtDesc(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid or expired OTP"));

        if (record.isVerified()
                || record.getExpiresAt() == null
                || !record.getExpiresAt().isAfter(Instant.now())
                || record.getAttempts() >= 5) {

            throw new IllegalArgumentException(
                    "Invalid or expired OTP");
        }

        record.setAttempts(record.getAttempts() + 1);

        if (!passwordEncoder.matches(
                otp, record.getOtpHash())) {

            otpRepository.save(record);

            throw new IllegalArgumentException("Invalid OTP");
        }

        // Generate a secure reset token
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        String resetToken = java.util.Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        record.setVerified(true);
        record.setResetTokenHash(hashValue(resetToken));
        record.setResetTokenExpiresAt(
                Instant.now().plusSeconds(600));

        otpRepository.save(record);

        return resetToken;
    }

    // STEP 3: Reset the password
    public void resetPassword(
            String resetToken,
            String newPassword) {

        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Reset token is required");
        }

        String tokenHash = hashValue(resetToken);

        PasswordResetOtp record = otpRepository.findAll()
                .stream()
                .filter(item ->
                        tokenHash.equals(item.getResetTokenHash()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid or expired reset token"));

        if (!record.isVerified()
                || record.getResetTokenExpiresAt() == null
                || !record.getResetTokenExpiresAt()
                        .isAfter(Instant.now())) {

            throw new IllegalArgumentException(
                    "Invalid or expired reset token");
        }

        User user = userRepository
                .findByEmail(record.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found"));

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Delete the reset record to prevent token reuse
        otpRepository.deleteByEmail(record.getEmail());
    }
}