	package com.nexora.service;

	import java.util.Date;

	import org.springframework.stereotype.Service;

	import com.nexora.entities.LoggedOutToken;
	import com.nexora.repository.LoggedOutTokenRepository;
	import com.nexora.security.JwtService;

	import io.jsonwebtoken.Claims;

	@Service
	public class LogoutService {

	    private final LoggedOutTokenRepository tokenRepository;
	    private final JwtService jwtService;

	    public LogoutService(
	            LoggedOutTokenRepository tokenRepository,
	            JwtService jwtService) {

	        this.tokenRepository = tokenRepository;
	        this.jwtService = jwtService;
	    }

	    public void logout(String token) {

	        Claims claims = jwtService.extractClaims(token);

	        Date expiration = claims.getExpiration();

	        if (expiration == null || !expiration.after(new Date())) {
	            throw new IllegalArgumentException(
	                    "Token has expired"
	            );
	        }

	        if (!tokenRepository.existsByToken(token)) {
	            tokenRepository.save(
	                    new LoggedOutToken(token, expiration)
	            );
	        }
	    }

	    public boolean isTokenLoggedOut(String token) {
	        return tokenRepository.existsByToken(token);
	    }
}
