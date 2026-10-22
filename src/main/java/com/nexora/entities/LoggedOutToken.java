package com.nexora.entities;


	import java.util.Date;

	import org.springframework.data.annotation.Id;
	import org.springframework.data.mongodb.core.mapping.Document;

	@Document(collection = "logged_out_tokens")
	public class LoggedOutToken {

	    @Id
	    private String id;

	    private String token;
	    private Date expiration;

	    public LoggedOutToken() {
	    }

	    public LoggedOutToken(String token, Date expiration) {
	        this.token = token;
	        this.expiration = expiration;
	    }

	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

	    public Date getExpiration() {
	        return expiration;
	    }

	    public void setExpiration(Date expiration) {
	        this.expiration = expiration;
	    }
}
