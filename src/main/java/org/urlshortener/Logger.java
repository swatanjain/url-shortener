package org.urlshortener;

import static org.urlshortener.ApplicationConstants.APPLICATION_BASE_PACKAGE;

/**
 * Abstracts underlying logging framework. Exposes simple static methods 
 * 	to log application messages.
 * 
 * @since 1.0
 **/
public final class Logger {
	
	private static final org.apache.log4j.Logger appLogger = org.apache.log4j.Logger.getLogger(APPLICATION_BASE_PACKAGE);
	
	private Logger() {}
	
	public static final void info(Object message) {
		appLogger.info(message);
	}
	
	public static final void debug(Object message) {
		appLogger.debug(message);
	}
	
	public static final void error(Object message) {
		appLogger.error(message);
	}
	
}
