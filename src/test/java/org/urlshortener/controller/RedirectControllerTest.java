package org.urlshortener.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.urlshortener.ApplicationConstants.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 *	This class tests all use-cases of {@link RedirectController}'s redirect() 
 *	method by simulating HTTP GET request with shortUrlId as an URL path parameter 
 */
public class RedirectControllerTest extends BaseTest{

	@Test
	public void testRedirect() throws Exception {

		/*
		 * To test redirect we need to have an account created and URL registered first
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);
		String shortUrlId = registerUrl(auth, TEST_URL);

		/* 
		 * Performing GET request "/redirect" API 
		 * Note that if redirectType not specified in registration request
		 * 	then by default redirect will be made with HTTP status 302 i.e. FOUND
		 * 
		 * Verifying the returned HTTP status 302 i.e. FOUND
		 * Verifying the redirect URL "http://google.com", the URL we registered under account "myAcccountId"
		 */
		String uri = REDIRECT_URI_FORMAT.format(new String[]{shortUrlId});
		mockMvc.perform(
				get(uri))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl(TEST_URL));

	}
	
	@Test
	public void testRedirectWithRedirectType() throws Exception {

		/*
		 * To test redirect we need to have an account created and URL registered first
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);
		String shortUrlId = registerUrl(auth, HttpStatus.MOVED_PERMANENTLY, TEST_URL);

		/* 
		 * Performing GET request "/redirect" API 
		 * 
		 * Verifying the returned HTTP status 301 i.e. MOVED_PERMANENTLY
		 * Verifying the redirect URL "http://google.com", 
		 * 	the URL we registered under account "myAcccountId"
		 */
		String uri = REDIRECT_URI_FORMAT.format(new String[]{shortUrlId});
		mockMvc.perform(
				get(uri))
		.andExpect(status().isMovedPermanently())
		.andExpect(redirectedUrl(TEST_URL));

	}
	
	@Test
	public void testRedirectWhenShortIdNotRegistered() throws Exception {

		/*
		 * Performing GET request "/redirect" API with 
		 * 	any arbitrary URL which is not registered. 
		 * 
		 * Verifying the returned HTTP status 404 i.e. NOT_FOUND
		 */
		String uri = REDIRECT_URI_FORMAT.format(new String[]{"xYz123ab"});
		mockMvc.perform(
				get(uri))
		.andExpect(status().isNotFound());

	}
	
	
	@Test
	public void testRedirectWithHelpUri() throws Exception {

		/*
		 * Performing GET request "/redirect" API with 
		 * 	any /help URI which is not registered. 
		 * 
		 * Verifying the returned HTTP status 302 i.e. FOUND
		 * Verifying the redirect location to another URI
		 *  defined by HELP_LOCATION
		 */
		mockMvc.perform(
				get(HELP_URI))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl( HELP_LOCATION ));

	}

}
