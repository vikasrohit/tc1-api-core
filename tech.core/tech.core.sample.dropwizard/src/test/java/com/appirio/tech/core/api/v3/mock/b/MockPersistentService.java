/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.joda.time.DateTime;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.metadata.CountableMetadata;
import com.appirio.tech.core.api.v3.metadata.Metadata;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.AbstractMetadataService;
import com.appirio.tech.core.api.v3.service.RESTActionService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Mock Service class that handles all the REST calls.
 * 
 * @author sudo
 *
 */
@Path("mock_b_models")
public class MockPersistentService extends AbstractMetadataService implements RESTActionService, RESTPersistentService<MockModelB> {

	private AtomicInteger integer = new AtomicInteger(100);
	private Map<TCID, MockModelB> mockStorage = new HashMap<TCID, MockModelB>();

	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return MockModelB.class;
	}
	
	public MockModelB handleGet(FieldSelector selector, TCID recordId) throws Exception {
		return mockStorage.get(recordId);
	}

	public List<MockModelB> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		List<MockModelB> result = new ArrayList<MockModelB>(mockStorage.values());
		return result;
	}

	public TCID handlePost(HttpServletRequest request, MockModelB object) throws Exception {
		TCID id = new TCID(integer.getAndIncrement());
		object.setId(id);
		object.setModifiedAt(new DateTime());
		object.setCreatedAt(new DateTime());
		mockStorage.put(id, object);
		return object.getId();
	}

	public TCID handlePut(HttpServletRequest request, MockModelB object) throws Exception {
		MockModelB modelB = mockStorage.get(object.getId());
		modelB.setIntTest(object.getIntTest());
		modelB.setStrTest(object.getStrTest());
		object.setModifiedAt(new DateTime());
		return object.getId();
	}

	public void handleDelete(HttpServletRequest request, TCID id) throws Exception {
		mockStorage.remove(id);
	}

	public DaoBase<MockModelB> getResourceDao() {
		/* Not Implemented for mock yet */
		return null;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}

	public ApiResponse handleAction(TCID recordId, String action, HttpServletRequest request) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}
	
	@Override
	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		CountableMetadata metadata = new CountableMetadata();
		metadata.setTotalCount(mockStorage.size());
		populateFieldInfo(metadata);
		return metadata;
	}

	public void insertModel(MockModelB model) {
		mockStorage.put(model.getId(), model);
	}

	public Map<TCID, MockModelB> getStorage() {
		return mockStorage;
	}

	public void clearData() {
		mockStorage = new HashMap<TCID, MockModelB>();
	}
}
