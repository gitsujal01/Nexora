package com.nexora.service;

import java.util.Optional;
import java.util.Map;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nexora.dto.LoginRequest;
import com.nexora.dto.RegisterRequest;
import com.nexora.entities.User;
import com.nexora.repository.userRepository;
import com.nexora.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final userRepository userrepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            userRepository userrepo,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userrepo = userrepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // REGISTER
    @Override
    public User register(RegisterRequest request) {

        // Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check existing user
        Optional<User> existingUser =
                userrepo.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create user
        User user = new User();

        user.setEmail(email);
        user.setFullname(request.getFullname().trim());

        // Encrypt password using BCrypt
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return userrepo.save(user);
    }

    // LOGIN
    @Override
    public Map<String, Object> Login(LoginRequest request) {

        // Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Find user
        Optional<User> optionalUser =
                userrepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optionalUser.get();

        // Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT
        String token = jwtService.generateToken(user.getEmail());

        return Map.of(
                "token", token,
                "email", user.getEmail(),
                "fullname", user.getFullname(),
                "message", "Login successful"
        );
    }
}