package com.nexora.repository;

	import java.util.Optional;

	import org.springframework.data.mongodb.repository.MongoRepository;

	import com.nexora.entities.PasswordResetOtp;

	public interface PasswordResetOtpRepository
	        extends MongoRepository<PasswordResetOtp, String> {

	    Optional<PasswordResetOtp> findTopByEmailOrderByExpiresAtDesc(
	            String email);

	    void deleteByEmail(String email);
}
