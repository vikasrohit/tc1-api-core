/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.RESTActionService;

/**
 * @author sudo
 *
 */
public class MockActionService implements RESTActionService {

	public String getRootResource() {
		return MockModelA.RESOURCE_PATH;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		return null;
	}

	public ApiResponse handleAction(TCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
