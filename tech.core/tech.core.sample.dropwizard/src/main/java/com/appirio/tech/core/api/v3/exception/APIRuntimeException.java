/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;

/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class APIRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	public APIRuntimeException() {
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APIRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
