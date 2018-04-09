package org.urlshortener.controller;

import static org.urlshortener.ApplicationConstants.ERROR;
import static org.urlshortener.ApplicationConstants.ERRORS;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.urlshortener.ApplicationContext;
import org.urlshortener.dataaccess.AccountRepository;
import org.urlshortener.dataaccess.UrlRepository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Base parent for all controller components. Provides reusable methods to concrete implementations.
 * @since 1.0
 **/
public class BaseController {
	
	@Autowired
	protected AccountRepository accountRepo;
	
	@Autowired
	protected UrlRepository urlRepo;
	
	@Autowired
	protected ApplicationContext appContext;
	
	private RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
			.withinRange('0', 'z')
			.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
			.build();
	
	/**
	 * Prepares basic HTTP response header for returning content with type "application/json"
	 **/
	protected HttpHeaders getBasicResponseHeader() {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) ;
		return responseHeaders;
		
	}
	
	/**
	 * Returns a random string as a alphanumeric sequence of 8 characters
	 **/
	protected String getNextRandomString() {
		return randomStringGenerator.generate(8);
	}
	
	/**
	 * Prepares response body for failed HTTP requests.
	 **/
	protected String renderFailureMessage(Errors errors) {

		JsonObject response = new JsonObject();
		if(errors.hasFieldErrors() && errors.getFieldErrorCount() == 1) {
			FieldError error = errors.getFieldError();
			String description = error.getField() + " " + error.getDefaultMessage();
			response.add(ERROR, new JsonPrimitive(description));
		} else {
			JsonArray errorArray = new JsonArray();
			response.add(ERRORS, errorArray);
			for(ObjectError error: errors.getAllErrors()) {
				JsonObject errorObj = new JsonObject();
				errorObj.add(error.getObjectName(), new JsonPrimitive(error.getDefaultMessage()));
				errorArray.add(errorObj);
			}
		}
		return response.toString();

	}

}
