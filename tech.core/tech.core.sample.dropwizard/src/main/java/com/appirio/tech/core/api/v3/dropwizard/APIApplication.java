/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import com.appirio.tech.core.api.v3.controller.V3APIResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author sudo
 * 
 */
public class APIApplication extends Application<APIBaseConfiguration> {

	@Override
	public String getName() {
		return "identity-sample";
	}

	@Override
	public void initialize(Bootstrap<APIBaseConfiguration> bootstrap) {
		// Not used for now
	}

	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		final V3APIResource resource = new V3APIResource();
		environment.jersey().register(resource);
	}

	public static void main(String[] args) throws Exception {
		new APIApplication().run(args);
	}
}
