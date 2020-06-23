package com.bookmyride.demo.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
/**
 * 
 * @author seeya.wamane
 *
 */
public class TripInfoNotFoundException extends RuntimeException implements GraphQLError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Object> extensions = new HashMap<>();
	
	public TripInfoNotFoundException(String message) {
        super(message);
        extensions.put("invalidParams", message);
        extensions.put("status code", 400);
    }


	 @Override
	    public Map<String, Object> getExtensions() {
	        return extensions;
	    }
	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorType getErrorType() {
		return ErrorType.DataFetchingException;
	}

}
