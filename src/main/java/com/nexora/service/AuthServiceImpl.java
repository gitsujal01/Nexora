package com.nexora.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nexora.dto.LoginRequest;
import com.nexora.dto.RegisterRequest;
import com.nexora.entities.User;
import com.nexora.repository.userRepository;

@Service
public class AuthServiceImpl implements AuthService
{
	
	private final userRepository userrepo;

	public AuthServiceImpl(userRepository userrepo) {
		super();
		this.userrepo = userrepo;
	}

	@Override
	public User register(RegisterRequest request) {
		// TODO Auto-generated method stub
		Optional<User> existingUser = userrepo.findByEmail(request.getEmail());
		if(existingUser.isPresent())
		{
			throw new RuntimeException("Email already registered");
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setFullname(request.getFullname());
		user.setPassword(request.getPassword());
		return userrepo.save(user);
	}

	@Override
	public User Login(LoginRequest request) {
        
		Optional<User> user = userrepo.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (!user.get().getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user.get();
	}
}
