package org.coursera.cybersecurity.capstone.group8.webapi;


import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.Message;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/webapi")
public class MainController {
	private Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private UserManagement userManagement;



	@ResponseBody
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(String userId, String password, String password2, String realName, 
			HttpServletResponse httpServletResponse) {
		ensureSecureProtocol();
		log.info("Creating user " + userId);
		try {
			if (!password.equals(password2))
				throw new Exception("Passwords don't match");
			checkPassword(password);
			checkUsername(userId);
			if (realName == null || realName.trim().length() == 0)
				realName = userId;
			realName = sanitizeRealName(realName);
			userManagement.createUser(userId, password, realName);
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "ok";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(e);
		}
	}
	
	private String sanitizeRealName(String realName) {
		// TODO enforce max length, remove illegal characters
		return realName;
	}

	private void checkUsername(String userId) {
		// TODO enforce length and characters constraints
	}

	private void checkPassword(String password) {
		// TODO enforce strength requirements
	}

	@RequestMapping(path="/messageList", method=RequestMethod.GET)
    //TODO method to load messages into the template to display them to the user -- still need some work
    public void processMessages(@AuthenticationPrincipal User user, HttpServletRequest request, 
    		HttpServletResponse response, TemplateEngine templateEngine) {
		ensureSecureProtocol();
		log.info("messageList for " + user);

		try {
	        List<DecryptedMessage> allMsgs = userManagement.getMessagesForUser(user);
	
	        WebContext ctx = new WebContext(request, response, request.getServletContext());
	        ctx.setVariable("messages", allMsgs);
	        ctx.setVariable("userid", user.getId());
	        ctx.setVariable("username", user.getRealName());

            templateEngine.process("/message_list.html", ctx, response.getWriter());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }
	
	@RequestMapping(path="/sendMessage", method=RequestMethod.POST)
	public void sendMessage(@AuthenticationPrincipal User user, String recipientId, String message) throws Exception {
		ensureSecureProtocol();
		if (!userManagement.userExists(recipientId))
			throw new Exception("Recipient not found");
		message = sanitizeMessage(message);
		log.info("Sending message from " + user + " to " + recipientId);
	}
	
	private String sanitizeMessage(String message) {
		// TODO enforce max length and illegal characters
		return message;
	}

	private void ensureSecureProtocol() {
		// TODO throw an exception or do redirect to https if used over plain http
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
