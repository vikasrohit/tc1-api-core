/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;


/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class APIParseException extends APIRuntimeException {

	/**
	 * 
	 */
	public APIParseException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public APIParseException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public APIParseException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APIParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}