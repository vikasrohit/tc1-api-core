package com.appirio.tech.core.api.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.http.HttpStatus;

import com.appirio.tech.core.api.v3.ApiVersion;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.model.AbstractResource;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiFieldSelectorResponse;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;
import com.appirio.tech.service.sample.model.User;
import com.appirio.tech.service.sample.services.UserCRUDService;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

@Path("/v3/users")
@Produces(MediaType.APPLICATION_JSON)
public class V3APIResource {
	private final AtomicLong counter;

	public V3APIResource() {
		this.counter = new AtomicLong();
	}

	public ApiResponse getObjects(@QueryParam("name") Optional<String> name) {
		ApiResponse response = new ApiResponse();
		response.setId(String.valueOf(counter.incrementAndGet()));
		return response;
	}

	@GET
	@Timed
	public ApiResponse getObjects(
			@QueryParam(value="fields") Optional<String> fieldsIn,
			@QueryParam(value="filter") Optional<String> filterIn,
			@QueryParam(value="limit") Optional<String> limitIn,
			@QueryParam(value="offset") Optional<String> offsetIn,
			@QueryParam(value="offsetId") Optional<String> offsetIdIn,
			@QueryParam(value="orderBy") Optional<String> orderByIn,
			@QueryParam(value="include") Optional<String> includeIn,
			@Context HttpServletRequest request) throws Exception {

		String fields = fieldsIn.isPresent()?fieldsIn.get():null;
		String filter = filterIn.isPresent()?filterIn.get():null;
		String limit = limitIn.isPresent()?limitIn.get():null;
		String offset = offsetIdIn.isPresent()?offsetIn.get():null;
		String offsetId = offsetIdIn.isPresent()?offsetIdIn.get():null;
		String orderBy = orderByIn.isPresent()?orderByIn.get():null;
		String include = includeIn.isPresent()?includeIn.get():null;
		
		FieldSelector selector;
		if(fields != null) {
			selector = FieldSelector.instanceFromV2String(fields);
		} else {
			selector = ResourceHelper.getDefaultFieldSelector(User.class);
		}

		QueryParameter query = new QueryParameter(selector,
				FilterParameter.fromEncodedString(filter),
				LimitQuery.instanceFromRaw(limit, offset, offsetId),
				OrderByQuery.instanceFromRaw(orderBy));
		
		RESTQueryService<?> service = new UserCRUDService();
		List<? extends AbstractResource> models = service.handleGet(request, query);

		//attach metadata if requested and MetadataService exists for this resource
		Object metadataObject = null;
		if(include!=null && include.startsWith("metadata")) {
			try {
				RESTMetadataService metaService = new UserCRUDService();
				metadataObject = metaService.getMetadata(request, query);
			} catch (ResourceNotMappedException e) {
				metadataObject = "metadata not supported";
			}
		}
		
		return createFieldSelectorResponse(models, metadataObject, query.getSelector());
	}

	private ApiResponse createResponse(final Object object) {
		ApiResponse response = new ApiResponse();
		response.setResult(true, HttpStatus.OK_200, object);
		response.setVersion(ApiVersion.v3);
		return response;
	}

	private ApiFieldSelectorResponse createFieldSelectorResponse(List<? extends AbstractResource> object, Object metadata, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
		for(AbstractResource resource : object) {
			ResourceHelper.setSerializeFields(resource, selector, fieldSelectionMap);
		}
		response.setResult(true, HttpStatus.OK_200, metadata, object);
		response.setVersion(ApiVersion.v3);
		response.setFieldSelectionMap(fieldSelectionMap);
		return response;
	}

	private ApiFieldSelectorResponse createFieldSelectorResponse(final AbstractResource object, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		response.setResult(true, HttpStatus.OK_200, object);
		response.setVersion(ApiVersion.v3);
		Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
		ResourceHelper.setSerializeFields(object, selector, fieldSelectionMap);
		response.setFieldSelectionMap(fieldSelectionMap);
		return response;
	}
}
