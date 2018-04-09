package org.urlshortener.controller;

import static org.urlshortener.ApplicationConstants.ACCOUNT_URI;
import static org.urlshortener.ApplicationConstants.DESCRIPTION;
import static org.urlshortener.ApplicationConstants.PASSWORD;
import static org.urlshortener.ApplicationConstants.SUCCESS;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.urlshortener.Logger;
import org.urlshortener.model.Account;
import org.urlshortener.model.RegisteredUrl;
import org.urlshortener.validator.AccountPayloadValidator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Serves new account creation requests.
 * @since 1.0
 **/
@RestController
@RequestMapping(ACCOUNT_URI)
@ExposesResourceFor(Account.class)
public class AccountController extends BaseController {

	@Autowired
	private AccountPayloadValidator validator;

	/**
	 * Creates a new user account and returns the generated password in {@link HttpServletResponse} body.
	 * @param account Auto-injected {@link Account} instance with "AccountId" field deserialized from the HTTP request body.
	 * @param errors Auto-injected Spring validation errors
	 **/
	@RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE,  produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody Account account, Errors errors) {

		validator.validate(account, errors);

		if ( errors.hasErrors() ) 
			return new ResponseEntity<>(renderFailureMessage(errors), getBasicResponseHeader(), HttpStatus.BAD_REQUEST);

		account.setPassword(getNextRandomString());
		accountRepo.create(account);
		Logger.info("Account added: " + account.getId());
		
		return new ResponseEntity<>(renderSuccesMesssage(account.getPassword()), getBasicResponseHeader(), HttpStatus.CREATED);

	}

	public ResponseEntity<Object> deleteAccount(@PathVariable String accoundId, Errors errors) throws IOException {

		if ( errors.hasErrors() ) 
			return new ResponseEntity<>(renderFailureMessage(errors), getBasicResponseHeader(), HttpStatus.BAD_REQUEST);

		Optional<Account> accOpt = accountRepo.read(accoundId);
		if ( !accOpt.isPresent() )
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		/*
		 * Before deleting an Account all registered URL(s) under that account
		 * 	must also be deleted.
		 * To minimize dependencies between repository entities, the controller
		 * 	should take this responsibility.
		 */
		Account acc = accOpt.get();
		
		for(String redirectId: acc.getRedirects()) {
			
			Optional<RegisteredUrl> urlOpt = urlRepo.read(redirectId);
			if (urlOpt.isPresent())
				urlRepo.delete(urlOpt.get());
			
		}
		
		accountRepo.delete(acc);
		return new ResponseEntity<>(getBasicResponseHeader(), HttpStatus.OK);

	}

	private String renderSuccesMesssage(String password) {

		JsonObject response = new JsonObject();
		response.add(SUCCESS, new JsonPrimitive(true));
		response.add(DESCRIPTION, new JsonPrimitive("Your account is opened"));
		response.add(PASSWORD, new JsonPrimitive(password));
		return response.toString();

	}

}
