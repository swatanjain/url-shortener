package org.urlshortener.model;

import static org.urlshortener.ApplicationConstants.ACCOUNT_ID;
import static org.urlshortener.ApplicationConstants.REDIRECTS;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;
import org.urlshortener.app.UrlShortenerApp;
import org.urlshortener.controller.AccountController;
import org.urlshortener.dataaccess.AccountRepository;
import org.urlshortener.dataaccess.UrlRepository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents the account entity which enables access to other authenticated
 * 	services provided by the application.
 * 
 * @since 1.0
 * @see AccountController
 * @see AccountRepository
 * @see UrlShortenerApp
 **/
@Component
@JsonPropertyOrder({ ACCOUNT_ID, REDIRECTS})
public class Account extends BaseModel {

	@JsonProperty(ACCOUNT_ID) @NotNull private String id;
	@NotNull private String password;
	@NotNull private Collection<String> redirects = new ArrayList<>();

	/**
	 * @return the application generated password to perform 
	 * 	authenticated operations under this account. 
	 **/
	public String getPassword() {
		return password;
	}

	/**
	 * @return Collection of short URL id(s) registered
	 *	under this account. 
	 **/
	public Collection<String> getRedirects() {
		return redirects;
	}

	/**
	 * @return the unique identifier to access this account. 
	 **/
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @deprecated in favor of {@link #getId}
	 **/
	@Deprecated
	@JsonIgnore
	public String getAccountId() {
		
		/* This method is required for jackson binding carried by auto-deserialization mechanism of Spring.
		 * No alternate solution as of now.
		 */
		return id;
	}
	
	/**
	 * Registers a new shortUrl id with this account. Respective full-length
	 * 	URL is stored separately in the URL repository while this account only
	 * 	stores the reference of that through given short URL id.
	 * 
	 * @param an unique short URL id
	 * @see UrlRepository
	 **/
	public void registerUrl(String shortUrlId) {
		redirects.add(shortUrlId);
	}

	/**
	 * Sets the application generated password to this user account.
	 * @param password
	 **/
	public void setPassword(String password) {
		this.password = password;
	}

}
