/**
 * 
 */
package com.appirio.tech.core.api.v3;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.service.sample.IdentitySampleApplication;
import com.appirio.tech.service.sample.IdentitySampleConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * V3 API test point.
 * 
 * 
 * @author sudo
 * 
 */
public class V3EndpointTest {
	
	@ClassRule
	public static final DropwizardAppRule<IdentitySampleConfiguration> RULE = new DropwizardAppRule<IdentitySampleConfiguration>(
			IdentitySampleApplication.class, "src/main/resources/sample.yml");

	@Test
	public void testRoot() throws Exception {
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/users", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.OK_200);
	}
}
