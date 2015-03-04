/**
 * 
 */
package com.appirio.tech.core.service.identity;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;

/**
 * Identity Service Application
 * Created for quick WebFlow authentication
 * 
 * @author sudo
 *
 */
public class IdentityApplication extends APIApplication {

	@Override
	public void initialize(Bootstrap<APIBaseConfiguration> bootstrap) {
		super.initialize(bootstrap);
		bootstrap.addBundle(new AssetsBundle("/pub", "/pub"));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new IdentityApplication().run(args);
	}

}
