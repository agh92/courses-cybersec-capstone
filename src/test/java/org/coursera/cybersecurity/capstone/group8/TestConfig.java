package org.coursera.cybersecurity.capstone.group8;

import org.coursera.cybersecurity.capstone.group8.internal.CryptoEngine;
import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.UserRepository;
import org.springframework.context.annotation.Bean;
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
}