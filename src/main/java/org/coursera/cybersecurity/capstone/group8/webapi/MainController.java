package org.coursera.cybersecurity.capstone.group8.webapi;


import org.coursera.cybersecurity.capstone.group8.internal.InputSanitizer;
import org.coursera.cybersecurity.capstone.group8.internal.UserInputException;
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

import java.io.IOException;
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
			HttpServletRequest request, HttpServletResponse httpServletResponse) {
		log.info("Creating user " + userId);
		try {
			if (!password.equals(password2))
				throw new UserInputException("Passwords don't match");
			inputSanitizer.checkPassword(password);
			inputSanitizer.checkUsername(userId);
			inputSanitizer.checkSecretAnswer(secretAnswer);
			userManagement.createUser(userId, password, secretQuestion, secretAnswer);
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "ok";
		} catch (UserInputException e) {
			log.error(e.getMessage());
			return handleError(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(request, httpServletResponse, e);
		}
	}

	@ResponseBody
	@RequestMapping(path="/passwordReset", method=RequestMethod.POST)
	public String passwordReset(String username, String password, String password2, 
			String secretAnswer, HttpServletRequest request, HttpServletResponse httpServletResponse) {
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
		} catch (UserInputException e) {
			log.error(e.getMessage());
			return handleError(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(request, httpServletResponse, e);
		}
	}

    @RequestMapping(path="/messageList", method=RequestMethod.GET)
    public String processMessages(@AuthenticationPrincipal User user, HttpServletRequest request,
                                HttpServletResponse response) {
        log.info("messageList for " + user.getId());

        try {
            List<DecryptedMessage> allMsgs = userManagement.getMessagesForUser(user);

            WebContext ctx = new WebContext(request, response, request.getServletContext());
            ctx.setVariable("messages", allMsgs);
            ctx.setVariable("userid", user.getId());

            return templateEngine.process("message_list", ctx);

        } catch (Exception e) {
			log.error(e.getMessage());
			return handleError(request, response, e);
        }
    }
	
	@RequestMapping(path="/sendMessage", method=RequestMethod.POST)
	public void sendMessage(@AuthenticationPrincipal User user, String recipientId, String message, 
			HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {
		try {
			if (!userManagement.userExists(recipientId))
				throw new UserInputException("Recipient not found");
			message = inputSanitizer.sanitizeMessage(message);
			log.info("Sending message from " + user + " to " + recipientId);
			DecryptedMessage decryptedMessage = new DecryptedMessage(user, recipientId, message);
			userManagement.saveMessage(decryptedMessage);
			httpServletResponse.setHeader("Location", "/webapi/messageList");
			httpServletResponse.sendRedirect("/webapi/messageList");
		} catch (UserInputException e) {
			log.error(e.getMessage());
			handleErrorNoReturn(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// We don't want to reveal the underlying exception
			handleErrorNoReturn(request, httpServletResponse, "Could not send message");
		}
	}
	
	private void handleErrorNoReturn(HttpServletRequest request, HttpServletResponse httpServletResponse, 
			Exception e) throws IOException {
		handleError(request, httpServletResponse, e.getMessage());
	}
	
	private void handleErrorNoReturn(HttpServletRequest request, HttpServletResponse httpServletResponse, 
			String errorMsg) throws IOException {
		String s = handleError(request, httpServletResponse, errorMsg);
		httpServletResponse.getWriter().write(s);
	}
	
	private String handleError(HttpServletRequest request, HttpServletResponse httpServletResponse, 
			Exception e) {
		return handleError(request, httpServletResponse, e.getMessage());
	}
	
	private String handleError(HttpServletRequest request, HttpServletResponse httpServletResponse, 
			String errorMsg) {
		httpServletResponse.reset();
        WebContext ctx = new WebContext(request, httpServletResponse, request.getServletContext());
        ctx.setVariable("errormsg", errorMsg);

        return templateEngine.process("error", ctx);
	}
}
