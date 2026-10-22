package com.nexora.repository;

	import org.springframework.data.mongodb.repository.MongoRepository;

	import com.nexora.entities.LoggedOutToken;

	public interface LoggedOutTokenRepository
	        extends MongoRepository<LoggedOutToken, String> {

	    boolean existsByToken(String token);
}
