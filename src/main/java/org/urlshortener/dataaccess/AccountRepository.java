package org.urlshortener.dataaccess;

import static org.urlshortener.ApplicationConstants.ACCOUNT_REPO_PATH;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.urlshortener.configurator.SecurityManagerAndConfigurator;
import org.urlshortener.controller.AccountController;
import org.urlshortener.model.Account;

/**
 * <p>Handles repository persistence of the accounts created by {@link AccountController} component.</p>
 * <p>Ensures that after every read/create/update operation the {@link SecurityManagerAndConfigurator} is notified
 * 	to update the account credentials and authorities. Also, ensures the authentication information
 * 	is removed from the {@link SecurityManagerAndConfigurator} when the account is deleted from the repository.</p>
 * 
 * @since 1.0
 * @see BaseRepository
 **/
@org.springframework.stereotype.Repository
public class AccountRepository extends BaseRepository<Account> {
	
	private SecurityManagerAndConfigurator securityManager;
	
	@Autowired
	public AccountRepository(SecurityManagerAndConfigurator securityManager) {
		
		super(ACCOUNT_REPO_PATH);
		this.securityManager = securityManager;
		Iterator<Account> iterator = getIterator();
		iterator.forEachRemaining( acc -> securityManager.createUserSession(acc) );

	}
	
	@Override 
	public Optional<Account> read(String id) {
		
		Optional<Account> acc = super.read(id);
		if(acc.isPresent())
			securityManager.createUserSession(acc.get());
		return acc;
		
	}
	
	@Override 
	public void create(Account acc) {
		
		super.create(acc);
		securityManager.createUserSession(acc);
	}
	
	@Override
	public boolean update(Account acc) {
		
		if (super.update(acc)) {
			securityManager.removeUserSession(acc);
			securityManager.createUserSession(acc);
			return true;
		}
		return false;
		
	}
	
	@Override
	public boolean delete(Account acc) throws IOException {
		
		if( super.delete(acc)) {
			securityManager.removeUserSession(acc);
			return true;
		}
		return false;
	}
	
	@Override
	protected Class<Account> getType() {
		return Account.class;
	}
	
}
