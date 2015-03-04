/**
 * 
 */
package com.appirio.tech.core.sample.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.joda.time.DateTime;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.metadata.CountableMetadata;
import com.appirio.tech.core.api.v3.metadata.Metadata;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.SortOrder;
import com.appirio.tech.core.api.v3.service.AbstractMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.appirio.tech.core.sample.exception.StorageException;
import com.appirio.tech.core.sample.model.User;
import com.appirio.tech.core.sample.storage.InMemoryUserStorage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
@Path("users")
public class UserCRUDService extends AbstractMetadataService implements RESTPersistentService<User> {

	private InMemoryUserStorage storage = InMemoryUserStorage.instance();
	
	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return User.class;
	}
	
	public User handleGet(FieldSelector selector, TCID recordId) throws Exception {
		for(User user : storage.getUserList()) {
			if(user.getId().equals(recordId)) {
				return user;
			}
		}
		return null;
	}

	public List<User> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		FilterParameter parameter = query.getFilter();
		List<User> resultList = storage.getFilteredUserList(parameter);
		
		//order_by
		if(query.getOrderByQuery().getOrderByField()!=null) {
			String orderField = query.getOrderByQuery().getOrderByField();
			final Boolean order = SortOrder.ASC_NULLS_FIRST==query.getOrderByQuery().getSortOrder()?true:false;
			if(orderField.equals("handle")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getHandle().compareTo(o2.getHandle());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("email")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getEmail().compareTo(o2.getEmail());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("firstName")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getFirstName().compareTo(o2.getFirstName());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("lastName")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getLastName().compareTo(o2.getLastName());
						return order?result:-1*result;
					}
				});
			}else {
				throw new StorageException("Unknown field on order by :" + orderField);
			}
		}
		
		//limit, off set
		if(query.getLimitQuery().getOffset() != null) {
			resultList = resultList.subList(query.getLimitQuery().getOffset(), resultList.size());
		}
		if(query.getLimitQuery().getLimit() != null && query.getLimitQuery().getLimit()<resultList.size()) {
			resultList = resultList.subList(0, query.getLimitQuery().getLimit());
		}
		
		return resultList;
	}

	public TCID handlePost(HttpServletRequest request, User object) throws Exception {
		object.setCreatedAt(new DateTime());
		object.setModifiedAt(new DateTime());
		return storage.insertUser(object).getId();
	}

	public TCID handlePut(HttpServletRequest request, User object) throws Exception {
		object.setModifiedAt(new DateTime());
		storage.updateUser(object);
		return object.getId();
	}

	public void handleDelete(HttpServletRequest request, TCID id) throws Exception {
		storage.deleteUser(id);
	}

	@Override
	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		CountableMetadata metadata = new CountableMetadata();
		metadata.setTotalCount(storage.getFilteredUserList(query.getFilter()).size());
		populateFieldInfo(metadata);
		return metadata;
	}
	
	/**
	 * We're not going to use this method.
	 */
	public DaoBase<User> getResourceDao() {
		return null;
	}

}
