package com.nexora.service;

import java.util.Map;

import com.nexora.dto.LoginRequest;
import com.nexora.dto.RegisterRequest;
import com.nexora.entities.User;

public interface AuthService {

    User register(RegisterRequest request);

    Map<String, Object> Login(LoginRequest request);
}