package com.appirio.tech.core.identity.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.service.AbstractMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.appirio.tech.core.identity.dao.UserDAO;
import com.appirio.tech.core.identity.model.User;
import com.appirio.tech.core.identity.util.idgen.SequenceDAO;
import com.appirio.tech.core.identity.util.ldap.LDAPService;
import com.appirio.tech.core.identity.util.ldap.MemberStatus;
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
		return users;
	}

	@Override
	public TCID handlePost(HttpServletRequest request, final User user) throws Exception {

		SequenceDAO seqDao = (SequenceDAO)ResourceFactory.instance().getObject(SequenceDAO.class);
		Long userId = seqDao.nextVal("sequence_user_seq");

		TCID id = new TCID(userId);
		user.setId(id);
		user.setActive(false);
		UserDAO userDao = (UserDAO)ResourceFactory.instance().getObject(UserDAO.class);
		userDao.create(user);
		
		LDAPService ldapService = new LDAPService();
		ldapService.registerMember(userId, user.getHandle(), "password", MemberStatus.ACTIVATED); // TODO: -> "U"
		
		return id;
		/*
		return userDao.inTransaction(
			new Transaction<TCID, UserDAO>() {
				@Override
				public TCID inTransaction(UserDAO tran, TransactionStatus status) throws Exception {
					try {
						tran.createUser(user);
						return user.getId();
					} finally {
						try { tran.rollback(); } catch(Exception e) {}
					}
				}
			}
		);
		*/
	}

	@Override
	public TCID handlePut(HttpServletRequest request, User object) throws Exception {
		return new TCID(123456);
	}

	@Override
	public void handleDelete(HttpServletRequest request, TCID id) throws Exception {
	}

	@Override
	public DaoBase<User> getResourceDao() {
		return null;
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
