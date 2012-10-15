package com.emergentideas.todo.handles;

import org.apache.commons.lang.StringUtils;

import com.emergentideas.todo.services.TodoService;
import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.Constants;
import com.emergentideas.webhandle.assumptions.oak.ParmManipulator;
import com.emergentideas.webhandle.assumptions.oak.RequestMessages;
import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;
import com.emergentideas.webhandle.handlers.Handle;
import com.emergentideas.webhandle.handlers.HttpMethod;
import com.emergentideas.webhandle.output.Show;
import com.emergentideas.webhandle.output.Template;
import com.emergentideas.webhandle.output.Wrap;

public class PublicHandle {
	
	protected AuthenticationService authenticationService;
	protected TodoService todoService;
	
	@Handle({"", "/", "/index.html"})
	@Template
	@Wrap("public_page")
	public String index() {
		return "index";
	}
	
	@Handle(value = "/signUp", method = HttpMethod.GET)
	@Template
	@Wrap("public_page")
	public Object signUpGet() {
		return "login/signUp";
	}


	@Handle(value = "/signUp", method = HttpMethod.POST)
	@Template
	@Wrap("public_page")
	public Object signUpPost(Location location, ParmManipulator manip, String userName, 
			String inputPassword, String confirmPassword, String fullName, RequestMessages messages) {
		if(StringUtils.isBlank(userName)) {
			return configureError("Your user name can not be blank.", location, manip, messages);
		}
		
		if(StringUtils.isBlank(fullName)) {
			return configureError("Your full name can not be blank.", location, manip, messages);
		}
		
		if(StringUtils.isBlank(inputPassword) || StringUtils.isBlank(confirmPassword)) {
			return configureError("Your password can not be blank.", location, manip, messages);
		}
		
		if(inputPassword.equals(confirmPassword) == false) {
			return configureError("Your passwords do not match.", location, manip, messages);
		}
		
		if(authenticationService.getUserByProfileName(userName) != null) {
			return configureError("The user name you entered has already been created.  Please choose another.", location, manip, messages);
		}
		
		User user = todoService.createNewUser(userName, fullName, inputPassword, AuthenticationService.LOCAL_AUTHENTICATION_SYSTEM);
		((Location)location.get(Constants.SESSION_LOCATION)).put(Constants.CURRENT_USER_OBJECT, user);
		
		return new Show("/menu");
	}
	
	protected String configureError(String error, Location location, ParmManipulator manip, RequestMessages messages) {
		messages.getErrorMessages().add(error);
		manip.addRequestParameters(location);
		return "login/signUp";
	}


	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	@Wire
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}


	public TodoService getTodoService() {
		return todoService;
	}

	@Wire
	public void setTodoService(TodoService todoService) {
		this.todoService = todoService;
	}
	
	
	
}
