package com.infor.cloudsuite.platform.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ServiceUserAuthToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6160170757795590175L;
	
	private final Object principal;
	private Object credentials;

	public ServiceUserAuthToken(Object principal, Object credentials) {
		
		super(null);
		this.principal=principal;
		this.credentials=credentials;
		super.setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
	
}
