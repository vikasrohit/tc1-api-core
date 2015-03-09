/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.jetty.http.HttpStatus;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.response.ApiResponse;

/**
 * Provider to hook general runtime exception to Jersey's response mapping.
 * 
 * @author sudo
 *
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException exception) {
		ApiResponse apiResponse = new ApiResponse();
		String message;
		int status = HttpStatus.INTERNAL_SERVER_ERROR_500;
		// TODO: needs refactoring
		if(exception instanceof APIRuntimeException) {
			message = exception.getLocalizedMessage();
			status = ((APIRuntimeException)exception).getHttpStatus();
			apiResponse.setResult(true, status, message);
		}
		else if(exception.getCause()!=null &&
				exception.getCause() instanceof APIRuntimeException) {
			message = exception.getCause().getLocalizedMessage();
			status = ((APIRuntimeException)exception.getCause()).getHttpStatus();
			apiResponse.setResult(true, status, message);
		} else {
			message = exception.getLocalizedMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR_500;
			apiResponse.setResult(true, status, message);
		}
		
		return Response.serverError()
				.status(status)
				.entity(apiResponse)
				.build();
	}

}
