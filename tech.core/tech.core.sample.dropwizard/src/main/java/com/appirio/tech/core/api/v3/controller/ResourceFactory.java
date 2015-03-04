/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;
import com.appirio.tech.core.api.v3.exception.AppInitializationException;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.service.RESTActionService;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.appirio.tech.core.api.v3.service.RESTService;

/**
 * Singleton class to hold API service classes.
 * use {@link #build(APIBaseConfiguration)} to obtain the instance. 
 * 
 * @author sudo
 *
 */
public class ResourceFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	/**
	 * Singleton class that should always reside in Application
	 */
	private static final ResourceFactory instance = new ResourceFactory();
	private ResourceFactory() {}

	private Map<String, Object> serviceBeans = new HashMap<String, Object>();
	private Map<String, RESTQueryService<? extends RESTResource>> queryServiceMap = new HashMap<String, RESTQueryService<? extends RESTResource>>();
	private Map<String, RESTMetadataService> metadataServiceMap = new HashMap<String, RESTMetadataService>();
	private Map<String, RESTPersistentService<? extends RESTResource>> persistentServiceMap = new HashMap<String, RESTPersistentService<? extends RESTResource>>();
	private Map<String, RESTActionService> actionServiceMap = new HashMap<String, RESTActionService>();
	private Map<String, Class<? extends RESTResource>> modelMap = new HashMap<String, Class<? extends RESTResource>>();
	
	public RESTQueryService<? extends RESTResource> getQueryService(String resource) throws ResourceNotMappedException {
		if(queryServiceMap.containsKey(resource)) {
			return queryServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public RESTPersistentService<? extends AbstractIdResource> getPersistentService(String resource) throws ResourceNotMappedException {
		if(persistentServiceMap.containsKey(resource)) {
			return persistentServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public RESTActionService getActionService(String resource) {
		if(actionServiceMap.containsKey(resource)) {
			return actionServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public Class<? extends RESTResource> getResourceModel(String resource) throws Exception {
		if(modelMap.containsKey(resource)) {
			return modelMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource");
		}
	}

	public RESTMetadataService getMetadataService(String resource) {
		System.out.println(metadataServiceMap.keySet());
		if(metadataServiceMap.containsKey(resource)) {
			return metadataServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource");
		}
	}

	/**
	 * Initialize the Factory that holds all API application services/models from given configuration.
	 * 
	 * @param configuration
	 */
	protected void initialize(APIBaseConfiguration configuration) {
		// Parse v3services, instantiate and hold pointers in this instance
		for(String beanName : configuration.getV3services()) {
			if(serviceBeans.containsKey(beanName)) continue;
			try {
				RESTService object = (RESTService)Class.forName(beanName).newInstance();
				serviceBeans.put(beanName, object);
				if(object instanceof RESTQueryService) {
					queryServiceMap.put(getPathAnnotation(object), (RESTQueryService<?>)object);
				}
				if(object instanceof RESTPersistentService) {
					persistentServiceMap.put(getPathAnnotation(object), (RESTPersistentService<?>)object);
				}
				if(object instanceof RESTMetadataService) {
					metadataServiceMap.put(getPathAnnotation(object), (RESTMetadataService)object);
				}
				if(object instanceof RESTActionService) {
					actionServiceMap.put(getPathAnnotation(object), (RESTActionService)object);
				}
				modelMap.put(getPathAnnotation(object), object.getResourceClass());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
				throw new AppInitializationException("Can not intanciate specified API bean or the bean does not implement RESTService interface:" + beanName, e);
			}
		}

		if(logger.isDebugEnabled()) {
			logComplete();
		}
	}

	/**
	 * @param object
	 */
	private String getPathAnnotation(RESTService object) {
		for(Annotation annotation : object.getClass().getDeclaredAnnotations()) {
			if(annotation instanceof Path) {
				return ((Path) annotation).value();
			}
		}
		throw new ResourceInitializationException("Service class do not have @Path annotated");
	}
	
	/**
	 * Logs the current resource configuration .
	 * 
	 * @param handlerList
	 * @param queryServiceMap
	 * @param actionServiceMap
	 * @param modelMap
	 */
	private void logComplete() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApiController Initialization complete:").append("\n");
		int i=0;

		i=0;
		builder.append("\tRESTQueryService...").append("\n");
		for(RESTQueryService<?> queryService : queryServiceMap.values()) {
			builder.append("\t\t:" + i++ + ":" + queryService.getClass().getCanonicalName()).append("\n");
		}
		
		i=0;
		builder.append("\tRESTMetadataService...").append("\n");
		for(RESTMetadataService metadataService : metadataServiceMap.values()) {
			builder.append("\t\t:" + i++ + ":" + metadataService.getClass().getCanonicalName()).append("\n");
		}

		i=0;
		builder.append("\tRESTPersistentService...").append("\n");
		for(RESTPersistentService<?> persistentService : persistentServiceMap.values()) {
			builder.append("\t\t:" + i++ + ":" + persistentService.getClass().getCanonicalName()).append("\n");
		}
		
		i=0;
		builder.append("\tRESTActionService...").append("\n");
		for(RESTActionService actionService : actionServiceMap.values()) {
			builder.append("\t\t:" + i++ + ":" + actionService.getClass().getCanonicalName()).append("\n");
		}
		
		i=0;
		builder.append("\tModel Classes...").append("\n");
		for(Class<?> model : modelMap.values()) {
			builder.append("\t\t:" + i++ + ":" + model.getCanonicalName()).append("\n");
		}
		
		logger.debug(builder.toString());
	}
	
	public static ResourceFactory build(APIBaseConfiguration configuration) {
		instance.initialize(configuration);
		return instance;
	}
	
	public static ResourceFactory instance() {
		return instance;
	}
}
