package com.appirio.tech.core.api.v3.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.model.AbstractResource;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

@Path("/v3/users")
@Produces(MediaType.APPLICATION_JSON)
public class V3APIResource {
	private final AtomicLong counter;

	public V3APIResource() {
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public ApiResponse getObjects(@QueryParam("name") Optional<String> name) {
		ApiResponse response = new ApiResponse();
		response.setId(String.valueOf(counter.incrementAndGet()));
		return response;
	}

	public ApiResponse getObjects(
			@QueryParam(value="fields") Optional<String> fields,
			@QueryParam(value="filter") Optional<String> filter,
			@QueryParam(value="limit") Optional<String> limit,
			@QueryParam(value="offset") Optional<String> offset,
			@QueryParam(value="offsetId") Optional<String> offsetId,
			@QueryParam(value="orderBy") Optional<String> orderBy,
			@QueryParam(value="include") Optional<String> include,
			HttpServletRequest request) throws Exception {
/*
		FieldSelector selector;
//		if(fields==null || fields.trim().length()==0) {
//			selector = ResourceHelper.getDefaultFieldSelector(resourceFactory.getResourceModel(resource));
//		} else {
			selector = FieldSelector.instanceFromV2String(fields.get());
//		}
		QueryParameter query = new QueryParameter(selector,
													FilterParameter.fromEncodedString(filter.get()),
													LimitQuery.instanceFromRaw(limit.get(), offset.get(), offsetId.get()),
													OrderByQuery.instanceFromRaw(orderBy.get()));

		RESTQueryService<?> service = resourceFactory.getQueryService(resource);
		List<? extends AbstractResource> models = service.handleGet(request, query);
		
		//attach metadata if requested and MetadataService exists for this resource
		Object metadataObject = null;
		if(include!=null && include.startsWith("metadata")) {
			try {
				RESTMetadataService metaService = resourceFactory.getMetadataService(resource);
				metadataObject = metaService.getMetadata(request, query);
			} catch (ResourceNotMappedException e) {
				metadataObject = "metadata not supported";
			}
		}
		
		return createFieldSelectorResponse(models, metadataObject, query.getSelector());
*/
		return null;
	}
}
