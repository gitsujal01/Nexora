package com.nexora.entities;
	import java.time.Instant;

	import org.springframework.data.annotation.Id;
	import org.springframework.data.mongodb.core.index.Indexed;
	import org.springframework.data.mongodb.core.mapping.Document;

	@Document(collection = "password_reset_otps")
	public class PasswordResetOtp {

	    @Id
	    private String id;

	    @Indexed
	    private String email;

	    private String otpHash;

	    private Instant expiresAt;

	    private int attempts;

	    private boolean verified;

	    private String resetTokenHash;

	    private Instant resetTokenExpiresAt;

	    public PasswordResetOtp() {
	    }

	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getOtpHash() {
	        return otpHash;
	    }

	    public void setOtpHash(String otpHash) {
	        this.otpHash = otpHash;
	    }

	    public Instant getExpiresAt() {
	        return expiresAt;
	    }

	    public void setExpiresAt(Instant expiresAt) {
	        this.expiresAt = expiresAt;
	    }

	    public int getAttempts() {
	        return attempts;
	    }

	    public void setAttempts(int attempts) {
	        this.attempts = attempts;
	    }

	    public boolean isVerified() {
	        return verified;
	    }

	    public void setVerified(boolean verified) {
	        this.verified = verified;
	    }

	    public String getResetTokenHash() {
	        return resetTokenHash;
	    }

	    public void setResetTokenHash(String resetTokenHash) {
	        this.resetTokenHash = resetTokenHash;
	    }

	    public Instant getResetTokenExpiresAt() {
	        return resetTokenExpiresAt;
	    }

	    public void setResetTokenExpiresAt(Instant resetTokenExpiresAt) {
	        this.resetTokenExpiresAt = resetTokenExpiresAt;
	    }
}
