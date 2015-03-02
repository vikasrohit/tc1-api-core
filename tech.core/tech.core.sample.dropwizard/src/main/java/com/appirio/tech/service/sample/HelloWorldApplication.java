/**
 * 
 */
package com.appirio.tech.service.sample;

import com.appirio.tech.service.sample.resources.HelloWorldResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author sudo
 * 
 */
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
		// TODO Auto-generated method stub
	}

	@Override
	public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
		final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(),
				configuration.getDefaultName());
		environment.jersey().register(resource);
	}

	public static void main(String[] args) throws Exception {
		new HelloWorldApplication().run(args);
	}
}
