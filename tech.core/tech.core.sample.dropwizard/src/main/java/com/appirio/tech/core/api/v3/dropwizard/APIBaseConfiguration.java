/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Configuration;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
	String v3models;
	String v3services;
	String v3exceptions;
	
	public void setV3models(String v3models) {
		this.v3models = v3models;
	}
	
	public String getV3models() {
		return v3models;
	}

	public String getV3services() {
		return v3services;
	}

	public void setV3services(String v3services) {
		this.v3services = v3services;
	}

	public String getV3exceptions() {
		return v3exceptions;
	}

	public void setV3exceptions(String v3exceptions) {
		this.v3exceptions = v3exceptions;
	}
}
