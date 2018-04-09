package org.urlshortener;

/**
 * Provides various constants for different parameter names and also for 
 *	formatting and preparing response messages.
 *
 * @since 1.0
 **/
public final class ApplicationConstants {
	
	private ApplicationConstants() {}
	
	// Application interfacing specific
	public static final String APPLICATION_BASE_PACKAGE = "org.urlshortener";
	public static final String ACCOUNT_URI = "/account";
	public static final String REGISTER_URI = "/register";
	public static final String STATISTIC_URI = "/statistic";

	public static final String ACCOUNT_REPO_PATH = System.getProperty("user.home") + "/.urlshortener/data/accounts";
	public static final String REGISTERED_URLS_REPO_PATH = System.getProperty("user.home") + "/.urlshortener/data/registeredUrls";
	
	public static final String ID = "id";
	public static final String SHORT_URL_ID = "shortUrlId";
	
	public static final String HELP_URI = "/help";
	public static final String HELP_LOCATION = "user_guide.html";
	
	
	// General purpose
	public static final String JSON_FILE_SUFFIX = "json";
	
	
	// Security/configuration related
	public static final String ROLE_REGISTER = "ROLE_REGISTER";
	public static final String ROLE_STAT_RETRIEVAL = "ROLE_STAT_RETRIEVAL";
	
	public static final String PROTOCOL_HTTP = "http";
	public static final String LOCAL_SERVER_PORT_PROP = "local.server.port";


	// Formatting related
	public static final String UNDERSCORE = "_";
	public static final String DOT = ".";

	// Request parameters
	public static final String ACCOUNT_ID = "AccountId";
	public static final String REDIRECTS = "redirects";
	public static final String URL = "url";
	public static final String REDIRECT_TYPE = "redirectType";
	
	// Response parameters
	public static final String SUCCESS = "success";
	public static final String DESCRIPTION = "description";
	public static final String PASSWORD = "password";
	public static final String SHORT_URL = "shortUrl";
	public static final String ERROR = "error";
	public static final String ERRORS = "errors";
	
}
