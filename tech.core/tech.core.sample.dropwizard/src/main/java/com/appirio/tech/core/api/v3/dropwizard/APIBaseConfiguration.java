/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
	List<String> v3services;
	
	public List<String> getV3services() {
		return v3services;
	}

	public void setV3services(List<String> v3services) {
		this.v3services = v3services;
	}
	
	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
}
