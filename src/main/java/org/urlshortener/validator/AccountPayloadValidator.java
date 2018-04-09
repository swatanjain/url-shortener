package org.urlshortener.validator;

import static org.urlshortener.ApplicationConstants.ACCOUNT_ID;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.urlshortener.controller.AccountController;
import org.urlshortener.dataaccess.AccountRepository;
import org.urlshortener.model.Account;

/**
 * Validates the HTTP request body for creation of new account. Auto-injected to {@link AccountController}
 * 	through Spring boot loader.
 * 
 * @since 1.0
 **/
@Component
public class AccountPayloadValidator implements Validator {

	@Autowired
	private AccountRepository repo;

	@Override
	public boolean supports(Class<?> clazz) {
		return Account.class.isAssignableFrom(clazz);
	}

	/**
	 * Receives {@link Account} instance from Spring's auto-deserialization mechanism.
	 * <p>Validates the field id (parameter "AccountId") that should not be empty or null as 
	 * 	provided in the HTTP request body. Rejects the request if parameter "AccountId"
	 * 	not set properly.</p>
	 * <p>Validates the value of parameter "AccountId" that must not exist already in the 
	 * 	repository. Rejects the	request if id already exists.</p> 
	 * 
	 * @param obj the object to be validated i.e. {@link Account} instance.
	 * @param errors Auto-injected Spring validation errors.
	 **/
	@Override
	public void validate(Object obj, Errors errors) {
		
		Account account = (Account) obj;
		
		if (account.getId() == null || account.getId().isEmpty()) {
			errors.rejectValue(ACCOUNT_ID, "accountId.empty", "cannot be empty");
			return;
		}
		
		Optional<Account> existingAccount = repo.read(account.getId());
		
		if (existingAccount.isPresent())
			errors.rejectValue(ACCOUNT_ID, "accountId.exists", "already exists");
		

	}

}