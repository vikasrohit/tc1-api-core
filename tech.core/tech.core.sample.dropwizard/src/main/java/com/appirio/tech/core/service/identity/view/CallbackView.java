/**
 * 
 */
package com.appirio.tech.core.service.identity.view;

import io.dropwizard.views.View;

/**
 * @author sudo
 *
 */
public class CallbackView extends View {
	private static final String templateName = "callback.ftl";

	private String auth0_id_token;
	private String auth0_access_token;
	private boolean debug;
	private String redirectUrl;
	
	public CallbackView(String auth0_id_token, String auth0_access_token, boolean debug, String redirectUrl){
		super(templateName);
		this.auth0_id_token = auth0_id_token;
		this.auth0_access_token = auth0_access_token;
		this.debug = debug;
		this.redirectUrl = redirectUrl;
	}

	public static String getTemplatename() {
		return templateName;
	}

	public String getAuth0_id_token() {
		return auth0_id_token;
	}

	public String getAuth0_access_token() {
		return auth0_access_token;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

}
