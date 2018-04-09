package org.urlshortener.configurator;

import static org.urlshortener.ApplicationConstants.ACCOUNT_URI;
import static org.urlshortener.ApplicationConstants.REGISTER_URI;
import static org.urlshortener.ApplicationConstants.ROLE_REGISTER;
import static org.urlshortener.ApplicationConstants.ROLE_STAT_RETRIEVAL;
import static org.urlshortener.ApplicationConstants.STATISTIC_URI;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.urlshortener.model.Account;


/**
 * <p>Prepares application security configuration and loads in-memory {@link UserDetailsManager} service.</p>
 * <p>Exposes API(s) for creating and removing user session as and when required.</p>
 * 
 * @since 1.0
 **/
@Configuration
@EnableWebSecurity
public class SecurityManagerAndConfigurator extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsManager userDetailsManager;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * <p>Configures authorization parameters for all HTTP end-points of the application.</p>
	 * <p>Method is invoked internally by Spring boot loader while initializing.</p>
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.httpBasic().and().authorizeRequests()
		.antMatchers("/", "/help" ,ACCOUNT_URI).permitAll()
		.antMatchers(REGISTER_URI).hasAuthority(ROLE_REGISTER)
		.antMatchers(STATISTIC_URI, STATISTIC_URI + "/*").hasAuthority(ROLE_STAT_RETRIEVAL)
		.and().csrf().disable();

	}

	/**
	 * <p>Configures authentication parameters with an {@link InMemoryUserDetailsManager}
	 * service. Also, configures a {@link PasswordEncoder} to the service with a {@link BCryptPasswordEncoder} instance.</p>
	 * <p>Method is invoked internally by Spring boot loader while initializing.</p>
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsManager)
		.passwordEncoder(passwordEncoder);

	}

	/**
	 * <p>Returns an {@link InMemoryUserDetailsManager} instance.
	 * </code>@Bean</code> annotation allows Spring to load the service before loading the {@link SecurityManagerAndConfigurator}
	 * 	component itself.</p>
	 **/
	@Bean
	UserDetailsManager getInMemoryUserDetailsManager() {
		return new InMemoryUserDetailsManager(new Properties());
	}

	/**
	 * Registers user authentication information with the application's security manager.
	 * Also, by default provides user the authority to invoke "/register" & "/statistic" URIs.
	 * @param account {@link Account} instance
	 **/
	public void createUserSession(Account account) {
		
		//Register the user credentials only if not present already.
		if ( !userDetailsManager.userExists(account.getId()) ) {
			userDetailsManager.createUser(
					new User(
							account.getId(),  
							passwordEncoder.encode(account.getPassword()), 
							Arrays.asList(
									new SimpleGrantedAuthority(ROLE_REGISTER),
									new SimpleGrantedAuthority(ROLE_STAT_RETRIEVAL))));
		}

	}

	/**
	 * Removes user authentication information from the application's security manager.
	 * @param account {@link Account} instance
	 **/
	public void removeUserSession(Account account) {
		userDetailsManager.deleteUser(account.getId());
	}


}