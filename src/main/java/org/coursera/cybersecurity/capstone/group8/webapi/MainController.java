package org.coursera.cybersecurity.capstone.group8.webapi;


import org.coursera.cybersecurity.capstone.group8.internal.InputSanitizer;
import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.coursera.cybersecurity.capstone.group8.internal.data.DecryptedMessage;
import org.coursera.cybersecurity.capstone.group8.internal.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private TemplateEngine templateEngine;
	
	@Autowired
	private InputSanitizer inputSanitizer;

	@ResponseBody
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(String userId, String password, String password2, 
			String secretQuestion, String secretAnswer, 
			HttpServletResponse httpServletResponse) {
		ensureSecureProtocol();
		log.info("Creating user " + userId);
		try {
			if (!password.equals(password2))
				throw new Exception("Passwords don't match");
			inputSanitizer.checkPassword(password);
			inputSanitizer.checkUsername(userId);
			inputSanitizer.checkSecretAnswer(secretAnswer);
			userManagement.createUser(userId, password, secretQuestion, secretAnswer);
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "ok";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(e);
		}
	}

	@ResponseBody
	@RequestMapping(path="/passwordReset", method=RequestMethod.POST)
	public String passwordReset(String username, String password, String password2, 
			String secretAnswer, HttpServletResponse httpServletResponse) {
		try {
			inputSanitizer.checkUsername(username);
			User user = (User) userManagement.loadUserByUsername(username);
			userManagement.checkSecretAnswerMatches(user, secretAnswer);
			inputSanitizer.checkPassword(password);
			if (!password.equals(password2))
				throw new Exception("New passwords don't match");
			userManagement.setNewPassword(user, password);
			userManagement.persist(user);
			log.info("Password reset successful for " + user);
			return "ok";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(e);
		}
	}

    @RequestMapping(path="/messageList", method=RequestMethod.GET)
    public String processMessages(@AuthenticationPrincipal User user, HttpServletRequest request,
                                HttpServletResponse response) {
        ensureSecureProtocol();
        log.info("messageList for " + user.getId());

        try {
            List<DecryptedMessage> allMsgs = userManagement.getMessagesForUser(user);
            //DUmmy data to test the table
            DecryptedMessage msg = new DecryptedMessage();
            msg.setFromUserId("12345");
            msg.setId(3443341212l);
            msg.setPlainTextMessage("Test adfg sdfg sdf gsdf h dfgj sg er var g srdg dr gst gv srv st vs v srv ae fa sdfasdfsdg ds hdf hsd fg");
            msg.setTimestamp(1234234232133l);
            msg.setToUserId("123");
            allMsgs.add(msg);

            WebContext ctx = new WebContext(request, response, request.getServletContext());
            ctx.setVariable("messages", allMsgs);
            ctx.setVariable("userid", user.getId());

            return templateEngine.process("message_list", ctx);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
	
	@RequestMapping(path="/sendMessage", method=RequestMethod.POST)
	public void sendMessage(@AuthenticationPrincipal User user, String recipientId, String message, 
			HttpServletResponse httpServletResponse) throws Exception {
		ensureSecureProtocol();
		if (!userManagement.userExists(recipientId))
			throw new Exception("Recipient not found");
		message = inputSanitizer.sanitizeMessage(message);
		log.info("Sending message from " + user + " to " + recipientId);
		DecryptedMessage decryptedMessage = new DecryptedMessage(user, recipientId, message);
		userManagement.saveMessage(decryptedMessage);
		httpServletResponse.setHeader("Location", "/webapi/messageList");
		httpServletResponse.sendRedirect("/webapi/messageList");
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
