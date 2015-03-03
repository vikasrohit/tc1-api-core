/**
 * 
 */
package com.appirio.tech.core.sample.model;

import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;

/**
 * @author sudo
 *
 */
public class User extends AbstractIdResource {

	public static final String RESOURCE_PATH = "users";
	
	private String handle;
	private String email;
	private String firstName;
	private String lastName;
	
	@Override
	@ApiMapping(visible = false)
	public String getRootResource() {
		return RESOURCE_PATH;
	}
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
}
