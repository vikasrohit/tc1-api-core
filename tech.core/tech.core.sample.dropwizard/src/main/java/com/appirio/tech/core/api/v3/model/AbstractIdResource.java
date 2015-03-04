/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.service.RESTResource;


/**
 * Base class for domain object that has ID, which means that the object is
 * stored as a record in persistent storage.
 * 
 * @author sudo
 *
 */
public abstract class AbstractIdResource implements RESTResource {
	private TCID id;
	
	public void setId(TCID id) {
		this.id = id;
	}
	
	public TCID getId() {
		return id;
	}
}
