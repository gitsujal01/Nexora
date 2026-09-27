package com.nexora.service;

import com.nexora.dto.LoginRequest;
import com.nexora.dto.RegisterRequest;
import com.nexora.entities.User;

public interface AuthService {
      public User register(RegisterRequest request);
      public User Login(LoginRequest request);
}
