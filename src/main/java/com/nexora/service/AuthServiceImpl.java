package com.nexora.service;

import java.util.Optional;
import java.util.Map;

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

    @Override
    public User register(RegisterRequest request) {

        Optional<User> existingUser =
                userrepo.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());

        // Store encoded password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return userrepo.save(user);
    }

    @Override
    public Map<String, Object> Login(LoginRequest request) {

        Optional<User> optionalUser =
                userrepo.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optionalUser.get();

        // Compare entered password with encoded password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        return Map.of(
                "token", token,
                "email", user.getEmail(),
                "fullname", user.getFullname(),
                "message", "Login successful"
        );
    }
}