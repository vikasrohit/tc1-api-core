/**
 * 
 */
package com.appirio.tech.service.sample;

import com.appirio.tech.core.api.v3.controller.V3APIResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author sudo
 * 
 */
public class IdentitySampleApplication extends Application<IdentitySampleConfiguration> {

	@Override
	public String getName() {
		return "identity-sample";
	}

	@Override
	public void initialize(Bootstrap<IdentitySampleConfiguration> bootstrap) {
		// Not used for now
	}

	@Override
	public void run(IdentitySampleConfiguration configuration, Environment environment) throws Exception {
		final V3APIResource resource = new V3APIResource();
		environment.jersey().register(resource);
	}

	public static void main(String[] args) throws Exception {
		new IdentitySampleApplication().run(args);
	}
}
