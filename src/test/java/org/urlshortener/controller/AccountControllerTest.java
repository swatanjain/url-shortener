package org.urlshortener.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.urlshortener.ApplicationConstants.ACCOUNT_URI;
import static org.urlshortener.ApplicationConstants.ACCOUNT_REPO_PATH;
import static org.urlshortener.ApplicationConstants.DOT;
import static org.urlshortener.ApplicationConstants.JSON_FILE_SUFFIX;

import java.io.File;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *	This class tests all use-cases of the {@link AccountController}'s createAccount() 
 *	method by simulating HTTP request body with "AccountId" as a body parameter
 */
public class AccountControllerTest extends BaseTest{

	@Test
	public void testCreateAccount() throws Exception {

		/*
		 * Performing POST request on "/account" API
		 *
		 * Verifying the returned HTTP status 201 i.e. CREATED
		 * Verifying the returned content-type "application/json"
		 */
		String requestBody = ACCOUNT_CREATION_REQUEST_BODY.format( new String[]{TEST_ACCOUNT_ID} );
		MockHttpServletResponse response = mockMvc.perform(post(ACCOUNT_URI)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestBody))
				.andExpect(
						status().isCreated())
				.andExpect(
						content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		String responseBody = response.getContentAsString();

		/* 
		 * Asserting the response body in case of successful response.
		 * Note that the password will be arbitrarily generated. Hence,
		 * 	we are matching the response body with a regular expression 
		 * 	specifying password as any alphanumeric sequence of 8 characters
		 * 	whereas matching rest of the response body as is.
		 */
		String expectedResponse = "[{]"
				+ "\"success\":true,"
				+ "\"description\":\"Your account is opened\","
				+ "\"password\":\"" + PASSWORD_REGEX + "\""
				+ "[}]";
		assertTrue(	responseBody.matches(expectedResponse) );

	}

	@Test
	public void testCreateAccountRepoPersistence() throws Exception {

		createAccount(TEST_ACCOUNT_ID);

		/*
		 * Asserting whether the given account was persisted into the file system or not.
		 */
		File persistedAccFile = new File( ACCOUNT_REPO_PATH
				+ File.separator
				+ TEST_ACCOUNT_ID 
				+ DOT
				+ JSON_FILE_SUFFIX);
		assertTrue(persistedAccFile.exists());

	}


	@Test
	public void testCreateAccountWhenAccountAlreadyExists() throws Exception {

		createAccount(TEST_ACCOUNT_ID);

		/* 
		 * Performing POST request on "/account" API with duplicate AccoundId
		 * 	which we just now created in createAccount() call above.
		 * 
		 * Verifying the returned HTTP status 400 i.e. BAD_REQUEST
		 * Verifying the returned content-type "application/json"
		 */
		String requestBody = ACCOUNT_CREATION_REQUEST_BODY.format( new String[]{TEST_ACCOUNT_ID} );
		MockHttpServletResponse response = mockMvc.perform(post(ACCOUNT_URI)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestBody))
				.andExpect(
						status().isBadRequest())
				.andExpect(
						content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		/*
		 *	Verifying the response body with expected rejection message 
		 */
		String expectedResponse = "{"
				+ "\"error\":\"AccountId already exists\""
				+ "}";
		assertEquals(expectedResponse, response.getContentAsString());

	}


	@Test
	public void testCreateAccountWhenAccountIdEmpty() throws Exception {

		/* 
		 * Performing POST request on "/account" API with empty request body
		 * 
		 * Verifying the returned HTTP status 400 i.e. BAD_REQUEST
		 */
		String requestBody = "{}"; //Empty JSON body
		MockHttpServletResponse response = mockMvc.perform(post(ACCOUNT_URI)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestBody))
				.andExpect(
						status().isBadRequest())
				.andExpect(
						content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		/*
		 *	Verifying the response body with expected rejection message 
		 */
		String expectedResponse = "{"
				+ "\"error\":\"AccountId cannot be empty\""
				+ "}";
		assertEquals(expectedResponse, response.getContentAsString());

	}

}
