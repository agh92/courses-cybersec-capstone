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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
			HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		log.info("Creating user " + userId);
		try {
			inputSanitizer.checkUsername(userId);
			if (!password.equals(password2))
				throw new UserInputException("Passwords don't match");
			inputSanitizer.checkPassword(password);
			inputSanitizer.checkSecretAnswer(secretAnswer);
			userManagement.createUser(userId, password, secretQuestion, secretAnswer);
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "";
		} catch (UserInputException e) {
			log.error(e.getMessage());
			return handleError(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(request, httpServletResponse, e);
		}
	}

	@ResponseBody
	@RequestMapping(path="/passwordReset2", method=RequestMethod.POST)
	public String passwordReset2(String username, HttpServletRequest request, 
			HttpServletResponse response) {
		try {
			User user = (User) userManagement.loadUserByUsername(username);
            WebContext ctx = new WebContext(request, response, request.getServletContext());
            ctx.setVariable("userid", user.getId());
            ctx.setVariable("secretquestion", user.getSecretQuestion());
            return templateEngine.process("password_reset2", ctx);
		} catch (UsernameNotFoundException e) {
			log.error(e.getMessage());
			return handleError(request, response, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(request, response, e);
		}
	}

	@ResponseBody
	@RequestMapping(path="/passwordReset", method=RequestMethod.POST)
	public String passwordReset(String username, String password, String password2, 
			String secretAnswer, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
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
			httpServletResponse.setHeader("Location", "/login.html");
			httpServletResponse.sendRedirect("/login.html");
			return "";
		} catch (UsernameNotFoundException|UserInputException e) {
			log.error(e.getMessage());
			return handleError(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return handleError(request, httpServletResponse, e);
		}
	}

	@ResponseBody
    @RequestMapping(path="/loginPage", method=RequestMethod.GET)
    public String loginPage(HttpServletRequest request,
                                HttpServletResponse response) {

        try {
            WebContext ctx = new WebContext(request, response, request.getServletContext());
            ctx.setVariable("errormsg", request.getParameter("error") != null ? "Login failed" : "");

            return templateEngine.process("login", ctx);

        } catch (Exception e) {
			log.error(e.getMessage());
			return handleError(request, response, e);
        }
    }

	@ResponseBody
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
	
	@ResponseBody
	@RequestMapping(path="/sendMessage", method=RequestMethod.POST)
	public String sendMessage(@AuthenticationPrincipal User user, String recipientId, String message, 
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
			return "";
		} catch (UserInputException e) {
			log.error(e.getMessage());
			return handleError(request, httpServletResponse, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// We don't want to reveal the underlying exception
			return handleError(request, httpServletResponse, "Could not send message");
		}
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
