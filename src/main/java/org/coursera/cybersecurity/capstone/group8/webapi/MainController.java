package org.coursera.cybersecurity.capstone.group8.webapi;

import javax.servlet.http.HttpServletResponse;

import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webapi")
public class MainController {
	private Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private UserManagement userManagement;
	
	@ResponseBody
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(String userId, String password, String realName, 
			HttpServletResponse httpServletResponse) {
		ensureSecureProtocol();
		log.info("Creating user " + userId + " " + password + " " + realName);
		try {
			userManagement.createUser(userId, password, realName);
			httpServletResponse.setHeader("Location", "/login.html");
			// TODO redirect to login
			return "ok";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(e);
		}
	}
	
	@RequestMapping(path="/login", method=RequestMethod.POST)
	public void login(String userId, String password) {
		ensureSecureProtocol();
		// TODO change sessionID securely after successful login - previous could have been over plain http

	}
	
	@RequestMapping(path="/getMessages", method=RequestMethod.GET)
	public void getMessages(User user) {
		ensureSecureProtocol();
	}
	
	@RequestMapping(path="/sendMessage", method=RequestMethod.POST)
	public void sendMessage(User user, String recipientId, String message) {
		ensureSecureProtocol();
	}
	
	private void ensureSecureProtocol() {
		// TODO implement me
	}
	
	private String handleError(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Error</title></head><body>");
		sb.append("An error has happened: ").append(e.getMessage()).append("<br/>");
		sb.append("You can <a href=\"#\" onClick=\"history.back()\">return back</a> or go to the <a href=\"/index.html\">homepage</a>.");
		sb.append("</body></html>");
		return sb.toString();
	}
}
