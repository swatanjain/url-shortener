package org.urlshortener.validator;

import static org.urlshortener.ApplicationConstants.REDIRECT_TYPE;
import static org.urlshortener.ApplicationConstants.URL;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.urlshortener.controller.UrlRegistrationController;
import org.urlshortener.model.RegisteredUrl;

/**
 * Validates the HTTP request body for registration of an URL. Auto-injected to {@link UrlRegistrationController}
 * 	through Spring boot loader.
 * 
 * @since 1.0
 **/
@Component
public class UrlPayloadValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUrl.class.isAssignableFrom(clazz);
	}

	/**
	 * Receives {@link RegisteredUrl} instance from Spring's auto-deserialization mechanism.
	 * <p>Validates the parameter "url" that should not be empty or null as provided in the HTTP 
	 * 	request body. Rejects the request if parameter "url" not set properly.</p>
	 * <p>Validates the parameter "redirectType" that must either be 301 or 302 as provided in the HTTP 
	 * 	request body. Rejects the request if parameter "redirectType" not set properly.</p>
	 * 
	 * @param obj the object to be validated i.e. {@link RegisteredUrl} instance.
	 * @param errors Auto-injected Spring validation errors.
	 **/
	@Override
	public void validate(Object target, Errors errors) {
		
		RegisteredUrl redirectInfo = (RegisteredUrl) target;
		
		if (redirectInfo.getUrl() == null || redirectInfo.getUrl().isEmpty()) {
			errors.rejectValue(URL, "url.empty", "cannot be empty");
			return;
		}
		
		int redirectType = redirectInfo.getRedirectType();
		if (redirectType != HttpStatus.MOVED_PERMANENTLY.value()
				&& redirectType != HttpStatus.FOUND.value()) {
			errors.rejectValue(REDIRECT_TYPE, "redirect.type.invalid", "can only be 301|302");
			return;
		}
		
	}

}