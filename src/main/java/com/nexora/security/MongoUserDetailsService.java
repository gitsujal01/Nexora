package com.nexora.security;

	import com.nexora.entities.User;
	import com.nexora.repository.userRepository;

	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.core.userdetails.UsernameNotFoundException;
	import org.springframework.stereotype.Service;

	@Service
	public class MongoUserDetailsService implements UserDetailsService {

	    private final userRepository userRepository;

	    public MongoUserDetailsService(userRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @Override
	    public UserDetails loadUserByUsername(String email)
	            throws UsernameNotFoundException {

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                        new UsernameNotFoundException("User not found"));

	        return org.springframework.security.core.userdetails.User
	                .withUsername(user.getEmail())
	                .password(user.getPassword())
	                .authorities("USER")
	                .build();
	    }
}
