package com.nexora.service;

	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.mail.SimpleMailMessage;
	import org.springframework.mail.javamail.JavaMailSender;
	import org.springframework.stereotype.Service;

	@Service
	public class EmailService {

	    private final JavaMailSender mailSender;

	    @Value("${spring.mail.username}")
	    private String fromEmail;

	    public EmailService(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    public void sendOtp(String recipientEmail, String otp) {

	        SimpleMailMessage message = new SimpleMailMessage();

	        message.setFrom(fromEmail);
	        message.setTo(recipientEmail);
	        message.setSubject("Nexora Password Reset OTP");

	        message.setText(
	                "Hello,\n\n"
	                + "Your Nexora password reset OTP is: " + otp + "\n\n"
	                + "This OTP is valid for 5 minutes.\n"
	                + "Do not share this OTP with anyone.\n\n"
	                + "If you did not request a password reset, "
	                + "you can ignore this email."
	        );

	        mailSender.send(message);
	    }
}
