/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import java.util.List;

import io.dropwizard.Configuration;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
	List<String> v3models;
	List<String> v3services;
	List<String> v3exceptions;
	
	public void setV3models(List<String> v3models) {
		this.v3models = v3models;
	}
	
	public List<String> getV3models() {
		return v3models;
	}

	public List<String> getV3services() {
		return v3services;
	}

	public void setV3services(List<String> v3services) {
		this.v3services = v3services;
	}

	public List<String> getV3exceptions() {
		return v3exceptions;
	}

	public void setV3exceptions(List<String> v3exceptions) {
		this.v3exceptions = v3exceptions;
	}
}
