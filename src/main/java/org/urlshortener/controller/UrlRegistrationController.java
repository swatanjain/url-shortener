package org.urlshortener.controller;

import static org.urlshortener.ApplicationConstants.REGISTER_URI;
import static org.urlshortener.ApplicationConstants.SHORT_URL;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.urlshortener.ApplicationError;
import org.urlshortener.Logger;
import org.urlshortener.model.Account;
import org.urlshortener.model.RegisteredUrl;
import org.urlshortener.validator.UrlPayloadValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


/**
 * Supports registration of full-length URLs under given user account.
 * @since 1.0
 **/
@RestController
@RequestMapping(REGISTER_URI)
@ExposesResourceFor(RegisteredUrl.class)
public class UrlRegistrationController extends BaseController {

	@Autowired
	private UrlPayloadValidator validator;

	@JsonIgnore
	private static final short MAX_RETRY_COUNT_TO_GENERATE_UNIQUE_KEY = 5;

	/**
	 * Registers the given full-length URL under given user account. User account information
	 * 	is extracted from Spring's security context.
	 **/
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> registerUrl(@RequestBody RegisteredUrl url, Errors errors) throws UnknownHostException, MalformedURLException {

		validator.validate(url, errors);

		if ( errors.hasErrors() ) 
			return new ResponseEntity<>(renderFailureMessage(errors), getBasicResponseHeader(), HttpStatus.BAD_REQUEST);

		/*
		 * Relying on Spring's authentication mechanism to extract and authenticate HTTP request
		 * 	based on the user account information retrieved from HTTP authorization header.
		 * 	Once successfully authenticated we're sure to get not null authenticated principal
		 * 	as the application's SecurityConfigurator class has already configured a security filter
		 * 	for requests with "/register" URI.
		 * 	In any other case, the filter chain itself will reject the request and call woudn't reach here.
		 */
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String accountId = auth.getName();
		Logger.info("Url registration requested by <" + accountId + "> for target url <"+ url.getUrl() + ">");

		Optional<Account> acc = accountRepo.read(accountId);
		if (acc.isPresent()) {

			url.setId(getNextUniqueId());
			urlRepo.create(url);
			acc.get().registerUrl(url.getId());
			accountRepo.update(acc.get());
			Logger.info("Registered url: " + url);
			return new ResponseEntity<>( renderSuccessfulRegistrationMsg(url), getBasicResponseHeader(), HttpStatus.CREATED);

		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	private String getNextUniqueId() {

		/* 
		 * Since, it is not guaranteed that RandomStringGenerator would always
		 * 	return a unique string and this random string will be treated as a
		 * 	primary key for storing URL with the URLRepository.
		 * Hence, we need to ensure some mechanism to retry in case of a rare
		 * 	event of duplicate id generation.
		 */
		short count=0;
		while(true) {

			String key = getNextRandomString();
			Optional<RegisteredUrl> existingRedirect = urlRepo.read(key);

			if (!existingRedirect.isPresent()) 
				return key;

			if(count++ > MAX_RETRY_COUNT_TO_GENERATE_UNIQUE_KEY)
				throw new ApplicationError("Could not generate unique key even after "+ MAX_RETRY_COUNT_TO_GENERATE_UNIQUE_KEY + " retries");	

		}

	}

	private String renderSuccessfulRegistrationMsg(RegisteredUrl url) throws UnknownHostException, MalformedURLException {

		JsonObject response = new JsonObject();
		response.add(SHORT_URL, new JsonPrimitive(getShortUrl(url)));
		return response.toString();

	}

	private String getShortUrl(RegisteredUrl url) throws UnknownHostException, MalformedURLException {
		return appContext.getServerUrl() + "/"+ url.getId();
	}

}
