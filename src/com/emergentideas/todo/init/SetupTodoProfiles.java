package com.emergentideas.todo.init;

import com.emergentideas.webhandle.Init;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;

public class SetupTodoProfiles {
	AuthenticationService authenticationService;

	@Init
	public void init() {
		if(authenticationService.getUserByProfileName("administrator") == null) {
			authenticationService.createUser("administrator", "administrator", "123");
			authenticationService.createGroup("administrators");
			authenticationService.addMember("administrators", "administrator");
		}
		if(authenticationService.doesGroupExist("users") == false) {
			authenticationService.createGroup("users");
		}
	}
	
	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	@Wire
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
}
