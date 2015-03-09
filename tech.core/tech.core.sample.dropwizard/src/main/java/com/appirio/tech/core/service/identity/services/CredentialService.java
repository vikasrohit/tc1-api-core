package com.appirio.tech.core.service.identity.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.service.AbstractMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.appirio.tech.core.service.identity.model.Credential;
import com.appirio.tech.core.service.identity.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Path("credentials")
public class CredentialService extends AbstractMetadataService implements RESTPersistentService<Credential> {

	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return Credential.class;
	}

	@Override
	public Credential handleGet(FieldSelector selector, TCID recordId) throws Exception {
		return null;
	}

	@Override
	public List<Credential> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		return null;
	}

	@Override
	public TCID handlePost(HttpServletRequest request, Credential credential) throws Exception {
		// get activation code from header
		String activationCode = request.getHeader("Authorization");
		// decode activationCode and retrieve user by ID
		User user = null;
		// update user and insert credential 
		// send email after activation (?)
		return new TCID(12345);
	}

	@Override
	public TCID handlePut(HttpServletRequest request, Credential credential) throws Exception {
		return null;
	}

	@Override
	public void handleDelete(HttpServletRequest request, TCID id) throws Exception {
		
	}

	@Override
	public DaoBase<Credential> getResourceDao() {
		return null;
	}

}
