package org.urlshortener.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.urlshortener.ApplicationConstants.DOT;
import static org.urlshortener.ApplicationConstants.JSON_FILE_SUFFIX;
import static org.urlshortener.ApplicationConstants.REGISTERED_URLS_REPO_PATH;
import static org.urlshortener.ApplicationConstants.REGISTER_URI;

import java.io.File;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *	This class tests all use-cases of the {@link UrlRegistrationController}'s registerUrl() 
 *	method by simulating HTTP request body with "url" as a body parameter
 */
public class UrlRegistrationControllerTest extends BaseTest{

	@Test
	public void testRegisterUrl() throws Exception {

		/*
		 * To register an URL we need to have an account created first 
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);

		/*
		 * Performing POST request on "/register" API with Basic Authorization header
		 * 
		 * Verifying the returned HTTP status 201 i.e. CREATED
		 * Verifying the returned content-type "application/json"
		 */
		MockHttpServletResponse response = mockMvc.perform(
				post(REGISTER_URI)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(URL_REGISTRATION_REQUEST_BODY.format(new String[]{TEST_URL})))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		String responseBody = response.getContentAsString();

		/* 
		 * Asserting the response body in case of successful response.
		 * Note that the shortUrlId will be arbitrarily generated. Hence,
		 * 	we are matching the response body with a regular expression 
		 * 	specifying shortUrlId as any alphanumeric sequence of 8 characters
		 * 	whereas matching rest of the response body as is.
		 */
		String expectedResponse = "[{]"
				+ "\"shortUrl\":\"" + appContext.getServerUrl() + "/" +  SHORT_URL_ID_REGEX + "\""
				+ "[}]";
		assertTrue(	responseBody.matches(expectedResponse) );

	}

	@Test
	public void testRegisterUrlWithoutAuthHeader() throws Exception {

		/*
		 * Since URL registration is an authenticated operation.
		 * Below request should fail as it is missing the authorization header.
		 * 
		 * Performing POST request on "/register" API
		 * 
		 * Verifying the returned HTTP status 401 i.e. UNAUTHORIZED
		 */
		mockMvc.perform(
				post(REGISTER_URI)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(URL_REGISTRATION_REQUEST_BODY.format(new String[]{TEST_URL})))
		.andExpect(status().isUnauthorized());

	}

	@Test
	public void testRegisterUrlRepoPersistence() throws Exception {

		/*
		 * To register an URL we need to have an account created first 
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);
		String generatedShortId = registerUrl(auth, TEST_URL);

		/*
		 * Asserting whether the registered URL was persisted into the file system or not.
		 */
		File persistedAccFile = new File( REGISTERED_URLS_REPO_PATH
				+ File.separator
				+ generatedShortId
				+ DOT
				+ JSON_FILE_SUFFIX);
		assertTrue(persistedAccFile.exists());

	}

	@Test
	public void testRegisterUrlWhenParameterUrlisEmpty() throws Exception {

		/*
		 * To register an URL we need to have an account created first 
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);

		/*
		 * Performing POST request on "/register" API with Basic Authorization header
		 * 
		 * Verifying the returned HTTP status 400 i.e. BAD_REQUEST
		 * Verifying the returned content-type "application/json"
		 */
		String requestBody = "{}"; //empty JSON body
		MockHttpServletResponse response = mockMvc.perform(
				post(REGISTER_URI)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();


		/*
		 *	Verifying the response body with expected rejection message 
		 */
		String expectedResponse = "{"
				+ "\"error\":\"url cannot be empty\""
				+ "}";
		assertEquals(expectedResponse, response.getContentAsString());

	}

}
