/**
 * 
 */
package com.appirio.tech.core.api.v3;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;

/**
 * Tests initializing properties after DropWizard config (yml) loads.
 * 
 * @author sudo
 *
 */
public class InitializerTest {

	@ClassRule
	public static final DropwizardAppRule<APIBaseConfiguration> RULE = new DropwizardAppRule<APIBaseConfiguration>(
			APIApplication.class, "src/test/resources/initializer_test.yml");

	@Test
	public void testConfiguration() throws Exception {
		APIBaseConfiguration config = RULE.getConfiguration();
		Assert.assertNotNull(config.getV3services());
		Assert.assertEquals(3, config.getV3services().size());
		
		ResourceFactory factory = ResourceFactory.build(config);
		
		Assert.assertNotNull(factory.getQueryService("mock_a_models"));
		Assert.assertNotNull(factory.getQueryService("mock_b_models"));
		
		Assert.assertNotNull(factory.getMetadataService("mock_b_models"));
		
		Assert.assertNotNull(factory.getPersistentService("mock_b_models"));
		
		Assert.assertNotNull(factory.getActionService("mock_a_models"));
		Assert.assertNotNull(factory.getActionService("mock_b_models"));
		
		Assert.assertNotNull(factory.getResourceModel("mock_a_models"));
		Assert.assertNotNull(factory.getResourceModel("mock_b_models"));
	}
	
	@Test (expected=ResourceNotMappedException.class)
	public void testConfigurationException() throws Exception {
		APIBaseConfiguration config = RULE.getConfiguration();
		ResourceFactory factory = ResourceFactory.build(config);
		
		Assert.assertNull(factory.getMetadataService("mock_a_models"));
		Assert.assertNull(factory.getPersistentService("mock_a_models"));
		
		Assert.assertNull(factory.getMetadataService("mock_a_models"));
		Assert.assertNull(factory.getMetadataService("mock_b_models"));
	}
}
