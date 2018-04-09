/* 
 * MIT License
 *
 * Copyright (c) 2018 Swatantra Jain
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.urlshortener.app;

import static org.urlshortener.ApplicationConstants.APPLICATION_BASE_PACKAGE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * <p>URL shortener is a HTTP service that supports generation of short URL(s) and
 * 	redirection to registered full length URL(s).</p>
 * 
 * <p>This class serves as the application entry point. Does the configuration 
 * 	and loading of all Spring components and also starts the embedded http server
 * 	that supports following URIs:
 * 	<ol>
 * 		<li>/account - Supports creation of user accounts.</li>
 * 		<li>/register - Supports registration of URL(s) under given user account.</li>
 * 		<li>/statistic/{AccountId} -  Supports retrieval of URL(s) registered under 
 * 					given account and registration count of each URL.</li>
 * 	</ol></p>
 * 
 * @version 1.0
 **/
@SpringBootApplication(scanBasePackages={APPLICATION_BASE_PACKAGE})
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class UrlShortenerApp {

	public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApp.class, args);
    }

}
