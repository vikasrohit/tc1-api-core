/**
 * 
 */
package com.appirio.tech.core.api.v3;

import java.util.List;

import javax.ws.rs.core.MediaType;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;
import com.appirio.tech.core.api.v3.mock.a.MockModelA;
import com.appirio.tech.core.api.v3.mock.b.MockModelB;
import com.appirio.tech.core.api.v3.response.ApiResponse;
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
			APIApplication.class, "src/test/resources/initializer_test.yml");

	@Test
	public void testRoot() throws Exception {
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getType());
	}

	@Test
	public void testV3Protocol() throws Exception {
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		ApiResponse apiResponse = response.getEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		@SuppressWarnings("unchecked")
		List<MockModelA> content = (List<MockModelA>)apiResponse.getResult().getContent();
		Assert.assertTrue(content.size()==0);
	}

	@Test
	public void testPost() throws Exception {
		MockModelB modelB = new MockModelB();
		modelB.setIntTest(100);
		modelB.setStrTest("Test String");
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.accept("application/json").type("application/json").post(ClientResponse.class, modelB);
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getType());
		
		ApiResponse apiResponse = response.getEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		@SuppressWarnings("unchecked")
		List<MockModelB> content = (List<MockModelB>)apiResponse.getResult().getContent();
		Assert.assertTrue(content.size()==0);
	}
}
