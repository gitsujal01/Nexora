package com.nexora.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nexora.entities.User;


public interface userRepository extends MongoRepository<User,String>{
	Optional<User> findByEmail(String email);
}
