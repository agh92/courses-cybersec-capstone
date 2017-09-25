package org.coursera.cybersecurity.capstone.group8.webapi;


import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@RequestMapping("/webapi")
public class MainController {
	private Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private UserManagement userManagement;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
    private TemplateEngine templateEngine;

	@ResponseBody
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(String userId, String password, String password2, String realName, 
			String secretQuestion, String secretAnswer, 
			HttpServletResponse httpServletResponse) {
		ensureSecureProtocol();
		log.info("Creating user " + userId);
		try {
			if (!password.equals(password2))
				throw new Exception("Passwords don't match");
			checkPassword(password);
			checkUsername(userId);
			checkSecretAnswer(secretAnswer);
			if (realName == null || realName.trim().length() == 0)
				realName = userId;
			realName = sanitizeRealName(realName);
			userManagement.createUser(userId, password, realName, secretQuestion, secretAnswer);
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "ok";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(e);
		}
	}

	private void checkSecretAnswer(String secretAnswer) {
		// TODO ensure secret answer constraints
	}

	@ResponseBody
	@RequestMapping(path="/passwordReset", method=RequestMethod.POST)
	public String passwordReset(String username, String password, String password2, 
			String secretAnswer, HttpServletResponse httpServletResponse) {
		try {
			checkPassword(password);
			checkUsername(username);
			User user = (User) userManagement.loadUserByUsername(username);
			if (!passwordEncoder.matches(secretAnswer, user.getSecretHashedAnswer())) 
				throw new Exception("Secret answer doesn't match");
			if (!password.equals(password2))
				throw new Exception("New passwords don't match");
			user.setSaltedPasswordHash(passwordEncoder.encode(password));
			userManagement.persist(user);
			log.info("Password reset successful for " + user);
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
    public String processMessages(@AuthenticationPrincipal User user, Model model, HttpServletRequest request,
                                HttpServletResponse response) {
        ensureSecureProtocol();
        log.info("messageList for " + user.getId() + " - " + user.getRealName());

        try {
            List<DecryptedMessage> allMsgs = userManagement.getMessagesForUser(user);
            //model.addAttribute("messages", allMsgs);

            WebContext ctx = new WebContext(request, response, request.getServletContext());
            ctx.setVariable("messages", allMsgs);
            ctx.setVariable("userid", user.getId());
            ctx.setVariable("username", user.getRealName());

            //templateEngine.process("message_list", ctx, response.getWriter());
			return templateEngine.process("message_list", ctx);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
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
