package org.urlshortener.controller;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.urlshortener.ApplicationConstants.ACCOUNT_URI;
import static org.urlshortener.ApplicationConstants.LOCAL_SERVER_PORT_PROP;
import static org.urlshortener.ApplicationConstants.REGISTER_URI;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.urlshortener.ApplicationContext;
import org.urlshortener.app.UrlShortenerApp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Base parent for all *ControllerTest classes.
 * <p>Prepares spring boot context, MVC mocks and provides some reusable methods 
 * 	to extended classes for efficient unit testing.</p>
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = UrlShortenerApp.class)
public abstract class BaseTest {

	protected final static String TEST_ACCOUNT_ID = "myAccountId";
	protected final static String TEST_URL = "http://google.com";
	protected final static String PASSWORD_REGEX = "[a-zA-Z0-9]{8}";
	protected final static String SHORT_URL_ID_REGEX = "[a-zA-Z0-9]{8}";
	
	protected static final MessageFormat STATISTIC_URI_FORMAT = new MessageFormat("/statistic/{0}");
	protected static final MessageFormat REDIRECT_URI_FORMAT = new MessageFormat("/{0}");

	protected final static MessageFormat ACCOUNT_CREATION_REQUEST_BODY = new MessageFormat("'{' \"AccountId\": \"{0}\" '}'");
	protected final static MessageFormat URL_REGISTRATION_REQUEST_BODY = new MessageFormat("'{' \"url\": \"{0}\" '}'");
	protected final static MessageFormat URL_REGISTRATION_REQUEST_BODY_WITH_REDIRECT_TYPE = new MessageFormat("'{' \"url\": \"{0}\", \"redirectType\": \"{1}\" '}'");

	protected MockMvc mockMvc;
	private JsonParser jsonParser = new JsonParser();

	static {
		
		if(System.getProperty("project.build.directory") == null)
			System.setProperty("project.build.directory", "target");
		
		System.setProperty("user.home", System.getProperty("project.build.directory"));
		
		if(System.getProperty("asciidoc.snippets") == null)
			System.setProperty("asciidoc.snippets", System.getProperty("project.build.directory") + "/generated-snippets");
		
	}
	
	@Autowired
	protected WebApplicationContext springContext;

	@Autowired
	protected ObjectMapper mapper;

	@Autowired
	protected ApplicationContext appContext;

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(System.getProperty("asciidoc.snippets"));

	@Before
	public void setup() throws JsonParseException, JsonMappingException, IOException {

		Properties properties = new Properties();
		properties.put(LOCAL_SERVER_PORT_PROP, "8080");
		ConfigurableEnvironment env = (ConfigurableEnvironment) springContext.getEnvironment();
		env.getPropertySources().addLast(new PropertiesPropertySource("test-property-source", properties));

		mockMvc = MockMvcBuilders.webAppContextSetup(springContext)
				.apply(documentationConfiguration(restDocumentation))
				.apply(springSecurity())
				.alwaysDo(document("{method-name}", 
						preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
				.build();

		AccountController accController =  springContext.getBean(AccountController.class);
		accController.deleteAccount(TEST_ACCOUNT_ID, mock(BindingResult.class));

	}

	/**
	 * Creates a new account.
	 * @param accountId an unique account identifier
	 **/
	protected String createAccount(String accountId) throws Exception {

		/*
		 * Performing POST request on "/account" controller.
		 * 
		 * Verifying the returned HTTP status 201 i.e. CREATED
		 * Verifying the returned content-type "application/json"
		 */
		MockHttpServletResponse response = mockMvc.perform(post(ACCOUNT_URI)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{ \"AccountId\": \"" + accountId + "\" }"))
				.andExpect(
						status().isCreated())
				.andExpect(
						content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		JsonObject responseJson = jsonParser.parse(
				response.getContentAsString())
				.getAsJsonObject();

		/*
		 * From the account creation response body we're extracting the
		 * 	password and performing appropriate encoding to prepare authorization
		 * 	header for subsequent invocation to "/register" or "/statistic/*" APIs
		 */
		String password = responseJson.get("password").getAsString();
		String auth = Base64.getEncoder()
				.encodeToString(
						(accountId + ":" + password)
						.getBytes());

		return auth;

	}

	/**
	 * Registers a new URL under given user account.
	 * 
	 * @param auth the Basic Authorization header to identify the user account.
	 * @param redirectType the redirectType status to be returned
	 * 	when redirection request will be made on given URL.
	 * @param url the full-length URL to be registered.
	 **/
	protected String registerUrl(String auth, HttpStatus redirectType, String url) throws Exception {

		/*
		 * Performing POST request on "/register" API
		 * 
		 * Verifying the returned HTTP status 201 i.e. CREATED
		 * Verifying the returned content-type "application/json"
		 */
		String requestBody;
		if(redirectType != null)
			requestBody = URL_REGISTRATION_REQUEST_BODY_WITH_REDIRECT_TYPE.format(new String[]{url, redirectType.toString()});
		else
			requestBody = URL_REGISTRATION_REQUEST_BODY.format(new String[]{url});

		MockHttpServletResponse response = mockMvc.perform(
				post(REGISTER_URI)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn()
				.getResponse();

		JsonObject responseJson = new JsonParser().parse(
				response.getContentAsString())
				.getAsJsonObject();

		String shortUrl = responseJson.get("shortUrl").getAsString();
		return new URL(shortUrl).getPath().substring(1);

	}

	/**
	 * Registers a new URL under given user account with default redirectType status
	 * 	configured as 302 i.e. {@link HttpStatus}'s FOUND constant.
	 * 
	 * @param auth the Basic Authorization header to identify the user account.
	 * @param url the full-length URL to be registered.
	 **/
	protected String registerUrl(String auth, String url) throws Exception {

		return registerUrl(auth, null, url);

	}




}
