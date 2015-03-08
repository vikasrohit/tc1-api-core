/**
 * 
 */
package com.appirio.tech.core.sample.resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.metadata.CountableMetadata;
import com.appirio.tech.core.api.v3.metadata.Metadata;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.SortOrder;
import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.appirio.tech.core.api.v3.resource.DDLResource;
import com.appirio.tech.core.api.v3.resource.GetResource;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.sample.exception.StorageException;
import com.appirio.tech.core.sample.representation.User;
import com.appirio.tech.core.sample.storage.InMemoryUserStorage;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

/**
 * @author sudo
 *
 */
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UserCRUDResource implements GetResource, DDLResource {

	private InMemoryUserStorage storage = InMemoryUserStorage.instance();
	
	@Override
	@GET
	@Path("/{resourceId}")
	@Timed
	public ApiResponse getObject(@PathParam("resourceId") TCID recordId,
			@APIFieldParam(repClass = User.class) FieldSelector selector, @Context HttpServletRequest request)
			throws Exception {
		for(User user : storage.getUserList()) {
			if(user.getId().equals(recordId)) {
				return ApiResponseFactory.createFieldSelectorResponse(user, selector);
			}
		}
		return null;
	}

	@Override
	@GET
	@Timed
	public ApiResponse getObjects(@APIQueryParam(repClass = User.class) QueryParameter query,
			@QueryParam("include") Optional<String> includeIn, @Context HttpServletRequest request) throws Exception {
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
		
		return ApiResponseFactory.createFieldSelectorResponse(resultList, getMetadata(request, query), query.getSelector());
	}

	@Override
	@POST
	@Timed
	public ApiResponse createObject(@Valid PostPutRequest postRequest, @Context HttpServletRequest request)
			throws Exception {
		User object = (User)postRequest.getParamObject(User.class);
		object.setCreatedAt(new DateTime());
		object.setModifiedAt(new DateTime());
		return ApiResponseFactory.createResponse(storage.insertUser(object).getId());
	}

	@Override
	@PUT
	@Path("/{resourceId}")
	@Timed
	public ApiResponse updateObject(@PathParam("resourceId") String resourceId, @Valid PostPutRequest putRequest,
			@Context HttpServletRequest request) throws Exception {
		User object = (User)putRequest.getParamObject(User.class);
		object.setModifiedAt(new DateTime());
		storage.updateUser(object);
		return ApiResponseFactory.createResponse(object.getId());
	}

	@Override
	@DELETE
	@Path("/{resourceId}")
	@Timed
	public ApiResponse deleteObject(@PathParam("resourceId") String resourceId, @Context HttpServletRequest request)
			throws Exception {
		storage.deleteUser(new TCID(resourceId));
		return ApiResponseFactory.createResponse(new TCID(resourceId));
	}

	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		CountableMetadata metadata = new CountableMetadata();
		metadata.setTotalCount(storage.getFilteredUserList(query.getFilter()).size());
		//populateFieldInfo(metadata);
		return metadata;
	}
}
