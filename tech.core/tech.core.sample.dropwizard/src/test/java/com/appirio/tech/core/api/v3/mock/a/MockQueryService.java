/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.service.RESTQueryService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
@Path("mock_a_models")
public class MockQueryService implements RESTQueryService<MockModelA> {

	private Map<TCID, MockModelA> mockStorage = new HashMap<TCID, MockModelA>();
	
	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return MockModelA.class;
	}
	
	public MockModelA handleGet(FieldSelector selector, TCID recordId) throws Exception {
		return null;
	}

	public List<MockModelA> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		List<MockModelA> result = new ArrayList<MockModelA>(mockStorage.values());
		return result;
	}

	public void insertModel(MockModelA model) {
		mockStorage.put(model.getId(), model);
	}
}
