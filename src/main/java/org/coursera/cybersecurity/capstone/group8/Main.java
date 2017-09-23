package org.coursera.cybersecurity.capstone.group8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
