package org.urlshortener;

import static org.urlshortener.ApplicationConstants.LOCAL_SERVER_PORT_PROP;
import static org.urlshortener.ApplicationConstants.PROTOCOL_HTTP;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * General purpose application context.
 * @since 1.0
 **/
@Component
public class ApplicationContext {
	
	@Autowired
	private Environment environment;
	
	public URL getServerUrl() throws UnknownHostException, MalformedURLException {
		
		String protocol = PROTOCOL_HTTP;
		String host = InetAddress.getLocalHost().getHostAddress();
		int port = Integer.parseInt(environment.getProperty(LOCAL_SERVER_PORT_PROP));
		return new URL(protocol, host, port, "");
	}
	
}
