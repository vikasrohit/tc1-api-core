package com.appirio.tech.core.service.identity.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.service.AbstractMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.appirio.tech.core.service.identity.dao.UserDAO;
import com.appirio.tech.core.service.identity.model.User;
import com.appirio.tech.core.service.identity.util.Utils;
import com.appirio.tech.core.service.identity.util.ldap.LDAPService;
import com.appirio.tech.core.service.identity.util.ldap.MemberStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Path("users")
public class UserService extends AbstractMetadataService implements RESTPersistentService<User> {

	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return User.class;
	}

	@Override
	public User handleGet(FieldSelector selector, TCID recordId) throws Exception {
		UserDAO dao = (UserDAO)ResourceFactory.instance().getObject(UserDAO.class);
		User user = dao.findUserById(Long.valueOf(recordId.getId()));
		return user;
	}

	@Override
	public List<User> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		// TODO: DUMMY
		// test
		// seq 
		//SequenceDAO seqDao = (SequenceDAO)ResourceFactory.instance().getObject(SequenceDAO.class);
		//Long userId = seqDao.nextVal("USER_SEQ");
		//Long userId = seqDao.nextVal("sequence_user_seq");
		
		UserDAO userDao = (UserDAO)ResourceFactory.instance().getObject(UserDAO.class);
		//boolean exists = userDao.handleExists("kohatatest08a");
		User usr = userDao.findUserByEmail("ykohata+test08@appirio.com");
		
		Long userId = 40135335L;
		List<User> users = new ArrayList<User>();
		User u = new User();
		u.setId(new TCID(userId));
		u.setEmail("dummy@atopcoder.com");
		u.setHandle("dummy");
		u.setActive(true);
		//u.setCredencial(new Credential());
		u.setFirstName("John");
		u.setLastName("Doe");
		users.add(u);
		users.add(usr);
		return users;
	}

	@Override
	public TCID handlePost(HttpServletRequest request, final User user) throws Exception {

		UserDAO userDao = (UserDAO)ResourceFactory.instance().getObject(UserDAO.class);

		List<String> messages = new LinkedList<String>();
        validateHandle(user.getHandle(), messages, userDao);
        validateEmail(user.getEmail(), messages, userDao);
        validatePassword(user.getCredential()!=null ? user.getCredential().getPassword() : "", messages);
		/*
		// TODO: validate user
            validateFirstName();
            validateLastName();
            validateCountry();
            validateVerificationCode();
		 */
        if(messages.size()>0) {
        	throw new APIRuntimeException(HttpServletResponse.SC_BAD_REQUEST, messages.get(0));
        }
        
		//TODO:
		user.setActive(true);
		TCID userId = userDao.create(user);
		
		LDAPService ldapService = new LDAPService();
		ldapService.registerMember(Long.parseLong(userId.toString()), user.getHandle(),
					user.getCredential().getPassword(), MemberStatus.ACTIVATED); // TODO: -> "U"
		
		return userId;
	}

	@Override
	public TCID handlePut(HttpServletRequest request, User object) throws Exception {
		throw new APIRuntimeException(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void handleDelete(HttpServletRequest request, TCID id) throws Exception {
		throw new APIRuntimeException(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	@Override
	public DaoBase<User> getResourceDao() {
		return null;
	}
	
    private void validateHandle(String handle, List<String> messages, UserDAO userDao) {
    	if(handle == null || handle.length()==0) {
            messages.add("Handle is requried");
            return;
    	}
        // Check if the handle is invalid.
        String result = Utils.validateHandle(handle);
        if (null != result) {
            messages.add(result);
        } else {
            if (userDao.handleExists(handle)) {
                messages.add("Handle '" + handle + "' has already been taken");
            }
        }
    }	

    private void validateEmail(String email, List<String> messages, UserDAO userDao) {
        // validate email.
        if (email==null || email.length()==0) {
            messages.add("Email is required");
            return;
        }
        String result = Utils.validateEmail(email);
        if (null != result) {
            messages.add(result);
            return;
        }
        User user = userDao.findUserByEmail(email);
        if (user != null) {
            messages.add("The email - '" + email + "' is already registered, please use another one.");
        }
    }
    
    private void validatePassword(String password, List<String> messages){
    	if (password==null || password.length()==0) {
            messages.add("Password is required");
            return;
        }
    	String result = Utils.validatePassword(password);
        if (null != result) {
            messages.add(result);
            return;
        }
        return;
    }
/*
	public void activate(HttpServletRequest request) {
		String activationCode = request.getHeader("Authrozation");
		activate(activationCode);
	}

	public void activate(String activationCode) {
		//TODO
		// retrieve User
		// check activationCode
		// clear activationCode
		// set active = true
		// save user
		return;
	}
	
	@Override
	public ApiResponse handleAction(TCID recordId, String action, HttpServletRequest request) throws Exception {
		try {
			Object result = MethodUtils.invokeMethod(this, action, new Object[]{recordId, request});
			if(result instanceof ApiResponse)
				return (ApiResponse) result;
			
			ApiResponse resp = new ApiResponse();
			resp.setResult(true, HttpServletResponse.SC_OK, result);
			return resp;
		} catch(Exception e) {
			ApiResponse resp = new ApiResponse();
			resp.setResult(true, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return resp;
		}
	}

	@Override
	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		try {
			Object result = MethodUtils.invokeMethod(this, action, request);
			if(result instanceof ApiResponse)
				return (ApiResponse) result;
			
			ApiResponse resp = new ApiResponse();
			resp.setResult(true, HttpServletResponse.SC_OK, result);
			return resp;
		} catch(Exception e) {
			ApiResponse resp = new ApiResponse();
			resp.setResult(true, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return resp;
		}
	}
*/

}
