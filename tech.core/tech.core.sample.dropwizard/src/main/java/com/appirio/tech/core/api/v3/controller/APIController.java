package com.appirio.tech.core.api.v3.controller;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.eclipse.jetty.http.HttpStatus;

import com.appirio.tech.core.api.v3.ApiVersion;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.model.AbstractResource;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiFieldSelectorResponse;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

/**
 * An Entrypoint Controller class for all api /v3/* endpoints, that serves as Facade for all incoming requests.
 * 
 * For request/response protocol;
 * @see <a href="https://docs.google.com/a/appirio.com/presentation/d/15pucEI0MHj9y778EyaAWGh4MBH-I73i1-GS0ir7FhxE">API Specification</a>
 *
 * @author sudo
 */
@Path("/v3")
@Produces(MediaType.APPLICATION_JSON)
public class APIController {
	
	/**
	 * The jackson object mapper.
	 */
	private static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * The default return fields for the post/put/delete request.
	 */
	private static final String DEFAULT_DDL_RETURN_FIELDS = "id";

	/**
	 * Class that holds all API application provided services and models
	 */
	private ResourceFactory resourceFactory;

	public APIController(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	/**
	 * Get current {@link ResourceFactory} that is registered within this Controller
	 * 
	 * @return
	 */
	public ResourceFactory getResourceFactory() {
		return resourceFactory;
	}

	/**
	 * A Facade method to catch all requests to /{resource}/{id}.
	 * The method returns list of resources per V3 API spec.
	 * 
	 * @param resource
	 *            the resource name.
	 * @param recordId
	 * @param fieldsIn
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@GET
	@Path("/{resource}/{resourceId}")
	@Timed
	public ApiResponse getObject(
			@PathParam("resource") String resource,
			@PathParam("resourceId") TCID recordId,
			@QueryParam(value="fields") Optional<String> fieldsIn,
			@Context HttpServletRequest request) throws Exception {
		//add default fields if selector is empty.
		String fields = fieldsIn.isPresent()?fieldsIn.get():null;
		if(fields==null){
			fields = "";
			for(String field : ResourceHelper.getDefaultFields(resourceFactory.getResourceModel(resource))) {
				fields = field + ",";
			}
			//remove the last ","
			fields = fields.replaceAll(",$", "").trim();
		}

		QueryParameter query = new QueryParameter(FieldSelector.instanceFromV2String(fields));
		RESTQueryService<?> service = resourceFactory.getQueryService(resource);

		AbstractResource model = service.handleGet(query.getSelector(), recordId);
		return createFieldSelectorResponse(model, query.getSelector());
	}

	/**
	 * A Facade method to catch all requests to /{resource}.
	 * The method returns list of resources per V3 API spec.
	 * 
	 * @param resource
	 *            the resource name.
	 * @param fieldsIn
	 * @param filterIn
	 * @param limitIn
	 * @param offsetIn
	 * @param offsetIdIn
	 * @param orderByIn
	 * @param includeIn
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@GET
	@Path("/{resource}")
	@Timed
	public ApiResponse getObjects(
			@PathParam("resource") String resource,
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
			selector = ResourceHelper.getDefaultFieldSelector(resourceFactory.getResourceModel(resource));
		}

		QueryParameter query = new QueryParameter(selector,
				FilterParameter.fromEncodedString(filter),
				LimitQuery.instanceFromRaw(limit, offset, offsetId),
				OrderByQuery.instanceFromRaw(orderBy));
		
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
	}

	/**
	 * Handles the post request.
	 * 
	 * @param resource
	 *            the resource name.
	 * @param postRequest
	 *            the post request data.
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@POST
	@Path("/{resource}")
	@Timed
	public ApiResponse createObject(@PathParam("resource") String resource,
									@Valid PostPutRequest postRequest,
									@Context HttpServletRequest request) throws Exception {

		// get the RESTPersistentService according to the resource name
		@SuppressWarnings("rawtypes")
		RESTPersistentService resourceService = resourceFactory.getPersistentService(resource);

		@SuppressWarnings("unchecked")
		Class<? extends AbstractIdResource> resourceModel = (Class<? extends AbstractIdResource>) resourceFactory
				.getResourceModel(resource);

		AbstractIdResource resourceData;

		if (postRequest.getParam() == null) {
			// the resource model data is not specified, throw
			// ResourceInitializationException
			throw new ResourceInitializationException(String.format(
					"There is no data for [%s] resource in post request", resource));
		} else {
			try {
				// deserialize param data in post request (json data) to the
				// resource object.
				// Note: current version of ObjectMapper doesn't directly support #readValue(JsonNode, object) so putting into String
				resourceData = JACKSON_OBJECT_MAPPER.readValue(
						JACKSON_OBJECT_MAPPER.writeValueAsString(postRequest.getParam()), resourceModel);
			} catch (Exception ex) {
				// error when deserialize from json data
				throw new ResourceInitializationException(String.format(
						"Fail to initialize [%s] resource from post request", resource), ex);
			}
		}

		// call the RESTPersistentService.handlePost
		@SuppressWarnings("unchecked")
		TCID newID = resourceService.handlePost(request, resourceData);

		resourceData.setId(newID);

		String selector = (postRequest.getReturn()==null||postRequest.getReturn().isEmpty()) ? DEFAULT_DDL_RETURN_FIELDS : postRequest.getReturn();
		return createFieldSelectorResponse(resourceData, FieldSelector.instanceFromV2String(selector));
	}

	/**
	 * Handles the post request.
	 * 
	 * @param resource
	 *            the resource name.
	 * @param putRequest
	 *            the post request data.
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@PUT
	@Path("/{resource}/{resourceId}")
	@Timed
	public ApiResponse updateObject(@PathParam("resource") String resource,
									@PathParam("resourceId") String resourceId,
									@Valid PostPutRequest putRequest,
									@Context HttpServletRequest request) throws Exception {

		// get the RESTPersistentService according to the resource name
		@SuppressWarnings("rawtypes")
		RESTPersistentService resourceService = resourceFactory.getPersistentService(resource);

		@SuppressWarnings("unchecked")
		Class<? extends AbstractIdResource> resourceModel = (Class<? extends AbstractIdResource>) resourceFactory
				.getResourceModel(resource);

		AbstractIdResource resourceData = getParamObject(resource, putRequest, resourceModel);
		resourceData.setId(new TCID(resourceId));

		// call the RESTPersistentService.handlePost
		@SuppressWarnings("unchecked")
		TCID newID = resourceService.handlePut(request, resourceData);

		resourceData.setId(newID);

		String selector = (putRequest.getReturn()==null||putRequest.getReturn().isEmpty()) ? DEFAULT_DDL_RETURN_FIELDS : putRequest.getReturn();
		return createFieldSelectorResponse(resourceData, FieldSelector.instanceFromV2String(selector));
	}
	
	@DELETE
	@Path("/{resource}/{resourceId}")
	@Timed
	public ApiResponse deleteObject(@PathParam("resource") String resource,
									@PathParam("resourceId") String resourceId,
									@Context HttpServletRequest request) throws Exception {

		// get the RESTPersistentService according to the resource name
		@SuppressWarnings("rawtypes")
		RESTPersistentService resourceService = resourceFactory.getPersistentService(resource);

		// call the RESTPersistentService.handleDelete
		resourceService.handleDelete(request, new TCID(resourceId));

		return createResponse(new TCID(resourceId));
	}

	/**
	 * @param resource
	 * @param putRequest
	 * @param resourceModel
	 * @param resourceData
	 * @return
	 */
	private AbstractIdResource getParamObject(String resource, PostPutRequest putRequest,
			Class<? extends AbstractIdResource> resourceModel) {
		AbstractIdResource resourceData;
		if (putRequest.getParam() == null) {
			// the resource model data is not specified, throw
			// ResourceInitializationException
			throw new ResourceInitializationException(String.format(
					"There is no data for [%s] resource in post request", resource));
		} else {
			try {
				// deserialize param data in post request (json data) to the
				// resource object.
				// Note: current version of ObjectMapper doesn't directly support #readValue(JsonNode, object) so putting into String
				resourceData = JACKSON_OBJECT_MAPPER.readValue(
						JACKSON_OBJECT_MAPPER.writeValueAsString(putRequest.getParam()), resourceModel);
			} catch (Exception ex) {
				// error when deserialize from json data
				throw new ResourceInitializationException(String.format(
						"Fail to initialize [%s] resource from post request", resource), ex);
			}
		}
		return resourceData;
	}
	
	private ApiResponse createResponse(final Object object) {
		ApiResponse response = new ApiResponse();
		response.setId((new UID()).toString());
		response.setResult(true, HttpStatus.OK_200, object);
		response.setVersion(ApiVersion.v2);
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
