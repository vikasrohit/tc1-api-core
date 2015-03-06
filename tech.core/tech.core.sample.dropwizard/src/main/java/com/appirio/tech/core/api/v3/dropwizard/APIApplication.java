/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.appirio.tech.core.api.v3.controller.APIController;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import org.skife.jdbi.v2.DBI;

import com.appirio.tech.core.api.v3.controller.APIController;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.identity.dao.UserDAO;
import com.appirio.tech.core.identity.util.idgen.SequenceDAO;

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

		bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
	}

	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().setUrlPattern("/v3/*");
		final APIController resource = new APIController(ResourceFactory.build(configuration));
		
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "common_oltp");
		
		final UserDAO userDao = jdbi.onDemand(UserDAO.class);
		ResourceFactory.instance().registerObject(UserDAO.class, userDao);
		final SequenceDAO seqDao = jdbi.onDemand(SequenceDAO.class);
		ResourceFactory.instance().registerObject(SequenceDAO.class, seqDao);
		
		environment.jersey().register(resource);
	}

	public static void main(String[] args) throws Exception {
		new APIApplication().run(args);
	}
}
