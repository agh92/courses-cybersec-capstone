package org.coursera.cybersecurity.capstone.group8;

import org.coursera.cybersecurity.capstone.group8.internal.CryptoEngine;
import org.coursera.cybersecurity.capstone.group8.internal.InputSanitizer;
import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.MessageRepository;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

public class TestConfig {
	@Bean
	public UserManagement getUserManagement() {
		return new UserManagement();
	}
	
	@Bean
	public CryptoEngine getCryptoEngine() {
		return new CryptoEngine();
	}

	@Bean
	public UserRepository getUserRepository() {
		return mock(UserRepository.class);
	}

	@Bean
	public MessageRepository getMessageRepository() {
		return mock(MessageRepository.class);
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public InputSanitizer getInputSanitizer() {
		return new InputSanitizer();
	}
}
