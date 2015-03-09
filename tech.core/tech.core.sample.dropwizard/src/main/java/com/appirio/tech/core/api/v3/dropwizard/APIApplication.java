/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.appirio.tech.core.api.v3.controller.APIController;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.auth.JWTAuthProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * Application entry point for DropWizard framework.
 * This class is the standard class to run API V3 based RESTful apps. When running app from this class,
 * API V3 library will load model/service/exception that has extended/inherited defined classes/interfaces
 * and will communicate via V3 protocol automatically.
 * In case, all request will go through {@link APIController}
 * 
 * Application has freedom to create their own main() to run DropWizard apps, in which case, individual app
 * need to implement V3 protocol.
 * 
 * @author sudo
 * 
 */
public class APIApplication extends Application<APIBaseConfiguration> {

	@Override
	public String getName() {
		return "V3API-Application";
	}

	@Override
	public void initialize(Bootstrap<APIBaseConfiguration> bootstrap) {
		//V3 API communicates in ISO8601 format for DateTime
		bootstrap.getObjectMapper().setDateFormat(ISO8601DateFormat.getInstance());
		ApiResponse.JACKSON_OBJECT_MAPPER = bootstrap.getObjectMapper();
	}

	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		configureCors(environment);
		environment.jersey().setUrlPattern("/v3/*");
		final APIController resource = new APIController(ResourceFactory.build(configuration));
		environment.jersey().register(resource);
		//Register Authentication Provider to validate JWT with @Auth annotation
		environment.jersey().register(new JWTAuthProvider());
		//Catch all exception and wrap to V3 format
		environment.jersey().register(new RuntimeExceptionMapper());
	}
	
	// http://jitterted.com/tidbits/2014/09/12/cors-for-dropwizard-0-7-x/
	protected void configureCors(Environment environment) {
		Dynamic filter = environment.servlets().addFilter("CORS",
				CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}

	public static void main(String[] args) throws Exception {
		new APIApplication().run(args);
	}
}
