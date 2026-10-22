package com.nexora.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexora.dto.ForgotPasswordRequest;
import com.nexora.dto.LoginRequest;
import com.nexora.dto.RegisterRequest;
import com.nexora.dto.ResetPasswordRequest;
import com.nexora.dto.VerifyOtpRequest;

import com.nexora.entities.User;
import com.nexora.service.AuthServiceImpl;
import com.nexora.service.OtpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthServiceImpl authService;
    private final OtpService otpService;

    public AuthController(
            AuthServiceImpl authService,
            OtpService otpService) {

        this.authService = authService;
        this.otpService = otpService;
    }

    // REGISTER API
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest request) {

        Map<String, Object> response = authService.Login(request);

        return ResponseEntity.ok(response);
    }

    // FORGOT PASSWORD - SEND OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        otpService.sendOtp(request.getEmail());

        return ResponseEntity.ok(
                Map.of(
                    "message",
                    "If an account exists for this email, an OTP will be sent."
                )
        );
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        String resetToken = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                Map.of(
                    "message", "OTP verified successfully",
                    "resetToken", resetToken
                )
        );
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        otpService.resetPassword(
                request.getResetToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                    "message", "Password reset successfully"
                )
        );
    }
}