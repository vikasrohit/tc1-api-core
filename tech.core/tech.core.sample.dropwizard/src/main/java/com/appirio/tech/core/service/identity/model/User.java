/**
 * 
 */
package com.appirio.tech.core.service.identity.model;

import com.appirio.tech.core.api.v3.model.AbstractIdResource;

/**
 * @author sudo
 *
 */
public class User extends AbstractIdResource {

	private String handle;
	private String email;
	private String firstName;
	private String lastName;
	private Credential credential;
	private Boolean active;
	
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Boolean isActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Credential getCredential() {
		return credential;
	}
	public void setCredential(Credential credential) {
		this.credential = credential;
	}
}
