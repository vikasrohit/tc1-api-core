/**
 * 
 */
package com.appirio.tech.core.api.v3;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * V3 API test point.
 * 
 * 
 * @author sudo
 * 
 */
public class EndpointTest {
	
	@ClassRule
	public static final DropwizardAppRule<APIBaseConfiguration> RULE = new DropwizardAppRule<APIBaseConfiguration>(
			APIApplication.class, "src/main/resources/sample.yml");

	@Test
	public void testConfiguration() throws Exception {
		APIBaseConfiguration config = RULE.getConfiguration();
		Assert.assertNotNull(config.getV3models());
	}
	
	@Test
	public void testRoot() throws Exception {
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/users", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
	}
}
