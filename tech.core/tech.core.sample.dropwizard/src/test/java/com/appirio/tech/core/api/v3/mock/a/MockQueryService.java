/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.service.RESTQueryService;

/**
 * @author sudo
 *
 */
public class MockQueryService implements RESTQueryService<MockModelA> {

	private Map<TCID, MockModelA> mockStorage = new HashMap<TCID, MockModelA>();
	
	public String getRootResource() {
		return MockModelA.RESOURCE_PATH;
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
