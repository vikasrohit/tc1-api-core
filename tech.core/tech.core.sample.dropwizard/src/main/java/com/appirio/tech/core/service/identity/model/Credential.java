package com.appirio.tech.core.service.identity.model;

import com.appirio.tech.core.api.v3.model.AbstractIdResource;

public class Credential extends AbstractIdResource {

	private String password;
	
	private String activationCode;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
}
