/**
 * 
 */
package com.appirio.tech.core.service.identity;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;
import com.appirio.tech.core.service.identity.ctrl.CallbackWebflowCtrl;
import com.appirio.tech.core.service.identity.ctrl.LoginWebflowCtrl;

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
		bootstrap.addBundle(new ViewBundle());
	}
	
	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		super.run(configuration, environment);
		
		LoginWebflowCtrl ctrl = new LoginWebflowCtrl();
		environment.jersey().register(ctrl);
		
		CallbackWebflowCtrl callbackWebflowCtrl = new CallbackWebflowCtrl();
		environment.jersey().register(callbackWebflowCtrl);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new IdentityApplication().run(args);
	}

}
