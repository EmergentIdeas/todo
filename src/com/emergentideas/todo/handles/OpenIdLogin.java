package com.emergentideas.todo.handles;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;

import com.emergentideas.todo.services.TodoService;
import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.Constants;
import com.emergentideas.webhandle.assumptions.oak.ParmManipulator;
import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;
import com.emergentideas.webhandle.handlers.Handle;
import com.emergentideas.webhandle.output.Show;

public class OpenIdLogin {
	
	protected AuthenticationService authenticationService;
	protected TodoService todoService;
	protected OpenIdManager manager;
	protected String successShowPage = "/menu";
	protected String failureShowPage = "/login";
	
    static final long ONE_HOUR = 3600000L;
    static final long TWO_HOUR = ONE_HOUR * 2L;
    static final String ATTR_MAC = "openid_mac";
    static final String ATTR_ALIAS = "openid_alias";
    static final String ATTR_ENDPOINT = "openid_endpoint";


	@Handle("/openIdLogin")
	public Object login(String endpoint, ParmManipulator manip, HttpServletRequest request) {
		if(StringUtils.isBlank(endpoint)) {
			throw new IllegalArgumentException("The endpoint for an Open ID login must not be null.  Make sure it says Google, Yahoo, etc.");
		}
		
		Endpoint edp = getManager(manip).lookupEndpoint(endpoint);
		Association asso = getManager(manip).lookupAssociation(edp);
		
        request.getSession().setAttribute(ATTR_MAC, asso.getRawMacKey());
        request.getSession().setAttribute(ATTR_ALIAS, edp.getAlias());
        request.getSession().setAttribute(ATTR_ENDPOINT, endpoint);

		return new Show(manager.getAuthenticationUrl(edp, asso));
		
	}
	
	@Handle("/openIdLoginResult")
	public Object loginResult(HttpServletRequest request, ParmManipulator manip, String endpoint, Location location) {
        byte[] mac_key = (byte[]) request.getSession().getAttribute(ATTR_MAC);
        String alias = (String) request.getSession().getAttribute(ATTR_ALIAS);

		Authentication auth = manager.getAuthentication(request, mac_key, alias);
        checkNonce(request.getParameter("openid.response_nonce"));

		
		if(auth == null || StringUtils.isBlank(auth.getEmail())) {
			return new Show(failureShowPage);
		}
		
		User user = authenticationService.getUserByProfileName(auth.getEmail());
		if(user == null) {
			user = todoService.createNewUser(auth.getEmail(), auth.getFullname(), auth.getEmail(), (String) request.getSession().getAttribute(ATTR_ENDPOINT));
		}
		((Location)location.get(Constants.SESSION_LOCATION)).put(Constants.CURRENT_USER_OBJECT, user);
		
		// loading the object so it doesn't have a lazy init exception
		user.getGroupNames();

		
		return new Show(successShowPage);
	}
	
	protected User createUser(String profileName, String endpoint) {
		User user = authenticationService.createUser(profileName, profileName, null);
		authenticationService.setAuthenticationSystem(profileName, endpoint);
		return user;
	}
	
	protected OpenIdManager getManager(ParmManipulator manip) {
		if(manager == null) {
			manager = new OpenIdManager();
			manager.setReturnTo(manip.serverQualifyUrl("/openIdLoginResult"));
			manager.setRealm(manip.getCurrentRealm());
		}
		
		return manager;
	}
	
	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	@Wire
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public String getSuccessShowPage() {
		return successShowPage;
	}

	public void setSuccessShowPage(String successShowPage) {
		this.successShowPage = successShowPage;
	}

	public String getFailureShowPage() {
		return failureShowPage;
	}

	public void setFailureShowPage(String failureShowPage) {
		this.failureShowPage = failureShowPage;
	}

	void checkNonce(String nonce) {
		// check response_nonce to prevent replay-attack:
		if (nonce == null || nonce.length() < 20)
			throw new OpenIdException("Verify failed.");
		long nonceTime = getNonceTime(nonce);
		long diff = System.currentTimeMillis() - nonceTime;
		if (diff < 0)
			diff = (-diff);
		if (diff > ONE_HOUR)
			throw new OpenIdException("Bad nonce time.");
		if (isNonceExist(nonce))
			throw new OpenIdException("Verify nonce failed.");
		storeNonce(nonce, nonceTime + TWO_HOUR);
	}

	boolean isNonceExist(String nonce) {
		return false;
	}

	void storeNonce(String nonce, long expires) {
	}

	long getNonceTime(String nonce) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(
					nonce.substring(0, 19) + "+0000").getTime();
		} catch (ParseException e) {
			throw new OpenIdException("Bad nonce time.");
		}
	}

	public TodoService getTodoService() {
		return todoService;
	}

	@Wire
	public void setTodoService(TodoService todoService) {
		this.todoService = todoService;
	}

	
}
