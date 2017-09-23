package org.coursera.cybersecurity.capstone.group8.webapi;

import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webapi")
public class MainController {
	@RequestMapping("/register")
	public void register(String userId, String password, String realName) {
		ensureSecureProtocol();
	}
	
	@RequestMapping("/login")
	public void login(String userId, String password) {
		ensureSecureProtocol();
	}
	
	@RequestMapping("/getMessages")
	public void getMessages(User user) {
		ensureSecureProtocol();
	}
	
	@RequestMapping("/sendMessage")
	public void sendMessage(User user, String recipientId, String message) {
		ensureSecureProtocol();
	}
	
	private void ensureSecureProtocol() {
		
	}
}
