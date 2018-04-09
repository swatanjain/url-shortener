package org.urlshortener.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *	This class tests all use-cases of {@link AccountStatisticsController}'s getStats() 
 *	method by simulating HTTP GET request with "AccountId" as an URL path parameter 
 */
public class AccountStatisticsControllerTest extends BaseTest{

	@Test
	public void testStatistics() throws Exception {

		/*
		 * To retrieve statistics we need to have an account created first 
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);

		/*
		 * To retrieve statistics we also need to have few URLs registered.
		 * Here we're registering URL "http://google.com" 10 times under account "myAccountId"
		 * And since every redirect will be registered separately, we'll later verify that
		 * the returned redirect count for this URL should be 10.
		 */
		for (int i = 0; i < 10; i++) {
			registerUrl(auth, TEST_URL);
		}

		/*
		 * Performing GET request on "/statistic" API
		 * 
		 * Verifying the returned content-type "application/json"
		 * Verifying the returned HTTP status 200 i.e. OK
		 */
		String uri = STATISTIC_URI_FORMAT.format( new String[] {TEST_ACCOUNT_ID} );
		MockHttpServletResponse response = mockMvc.perform(
				get(uri)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		/* 	Verifying the response body that should be a key:value map where the key
		 *  should be the registered URL and the value should be the number of redirects to that URL.
		 */
		String expectedResponse = "{"
				+ "\"" + TEST_URL + "\":10"
				+ "}";
		assertEquals(expectedResponse, response.getContentAsString());

	}

	@Test
	public void testStatisticsWithoutAuthHeader() throws Exception {

		/*
		 * Since statistics retrieval is an authenticated operation.
		 * Below request should fail as it is missing the authorization header.
		 * 
		 * Performing GET request on "/statistic" API
		 * 
		 * Verifying the returned HTTP status 401 i.e. UNAUTHORIZED
		 */
		String uri = STATISTIC_URI_FORMAT.format( new String[] {TEST_ACCOUNT_ID} );
		mockMvc.perform(
				get(uri))
		.andExpect(
				status().isUnauthorized());

	}

	@Test
	public void testStatisticsWhenAccountNotExists() throws Exception {

		/*
		 * To test statistics we need to have an account created
		 */
		String auth = createAccount(TEST_ACCOUNT_ID);

		/*
		 * Performing GET request on "/statistic" API with 
		 * 	any arbitrary AccountId which doesn't exists
		 * 
		 * Verifying the returned HTTP status 404 i.e. NOT_FOUND
		 */
		String uri = STATISTIC_URI_FORMAT.format( new String[] {"xyzabc12"} );
		mockMvc.perform(
				get(uri)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth))
		.andExpect(status().isNotFound());


	}

}
