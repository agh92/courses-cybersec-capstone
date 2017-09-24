package org.coursera.cybersecurity.capstone.group8;

import org.coursera.cybersecurity.capstone.group8.internal.CryptoEngine;
import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.DbTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	@Bean
	public UserManagement getUserManagement() {
		return new UserManagement();
	}
	
	@Bean
	public CryptoEngine getCryptoEngine() {
		return new CryptoEngine();
	}
	
	@Bean
	public DbTools getDbTools() {
		return new DbTools();
	}
}
