package org.urlshortener.model;

import org.springframework.hateoas.Identifiable;
import org.urlshortener.ApplicationError;
import org.urlshortener.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base parent for all model types. Provides reusable method to
 * 	serialize the concrete type instance.
 * 
 * @since 1.0
 **/
public abstract class BaseModel implements Identifiable<String> {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * {@inheritDoc}
	 **/
	public String toString() {

		String result = "";
		try {
			result = objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			Logger.error("Failed to write as Json object the element with id: " + getId());
			throw new ApplicationError("Failed to write as Json object the element with id: " + getId(), e);
		}
		return result;
	}

}
