package org.coursera.cybersecurity.capstone.group8;

import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	@Bean
	public UserManagement getUserManagement() {
		return new UserManagement();
	}
}
