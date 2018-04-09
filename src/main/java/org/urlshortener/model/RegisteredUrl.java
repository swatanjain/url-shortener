package org.urlshortener.model;

import static org.urlshortener.ApplicationConstants.ID;
import static org.urlshortener.ApplicationConstants.REDIRECT_TYPE;
import static org.urlshortener.ApplicationConstants.URL;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.urlshortener.app.UrlShortenerApp;
import org.urlshortener.controller.RedirectController;
import org.urlshortener.controller.UrlRegistrationController;
import org.urlshortener.dataaccess.UrlRepository;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents the URL entity which maps the application generated short URL id 
 * 	to respective full length URL registered with the application.
 * 
 * @since 1.0
 * @see UrlRegistrationController
 * @see UrlRepository
 * @see UrlShortenerApp
 **/
@Component
@JsonPropertyOrder({ID, URL, REDIRECT_TYPE})
public class RegisteredUrl extends BaseModel {

	private int redirectType = HttpStatus.FOUND.value();
	private String id;
	@NotNull private String url;

	/**
	 * @return the configured {@link HttpStatus} to redirect clients
	 * 	when redirect request will be made for respective short URL.
	 * @see RedirectController
	 **/
	public int getRedirectType() {
		return redirectType;
	}

	/**
	 * @return the registered full length URL
	 **/
	public String getUrl() {
		return url;
	}

	/**
	 * @return an unique identifier to access this registered URL
	 **/
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param the unique identifier to access this registered URL
	 **/
	public void setId(String id) {
		this.id = id;
	}
	
}
