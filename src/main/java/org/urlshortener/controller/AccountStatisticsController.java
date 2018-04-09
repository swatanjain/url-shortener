package org.urlshortener.controller;

import static org.urlshortener.ApplicationConstants.ACCOUNT_ID;
import static org.urlshortener.ApplicationConstants.STATISTIC_URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.urlshortener.Logger;
import org.urlshortener.model.Account;
import org.urlshortener.model.RegisteredUrl;

/**
 * Serves retrieval of statistics for the given account.
 * @since 1.0
 **/
@RestController
@RequestMapping(STATISTIC_URI + "/" + "{" + ACCOUNT_ID + "}")
public class AccountStatisticsController extends BaseController{


	/**
	 * Responds with a JSON object, a key:value map where the key is the registered URL 
	 * 	and the value is the number of redirects registered to that URL.  
	 **/
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getStats(@PathVariable(ACCOUNT_ID) String accountId) {

		Logger.debug("Stats requested for account with id: <" + accountId + ">");

		Optional<Account> account = accountRepo.read(accountId);
		if (account.isPresent()) {

			Account acc = account.get();
			List<RegisteredUrl> registeredUrls = new ArrayList<>();
			for(String shortUrlId: acc.getRedirects()) {
				Optional<RegisteredUrl> url = urlRepo.read(shortUrlId);
				url.ifPresent( urlObj -> registeredUrls.add(urlObj));
			}
			return new ResponseEntity<>( 
					renderRegisteredUrlsToRespectiveCountMap(registeredUrls), 
					getBasicResponseHeader(), HttpStatus.OK );

		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private Map<String, Long> renderRegisteredUrlsToRespectiveCountMap(Collection<RegisteredUrl> urls) {

		/* 
		 * Performs SQL like aggregate operation on all registered URL(s) under
		 * 	given account. Returns a Map instance specifying the count of redirects 
		 * 	registered for each full-length URL.
		 */
		return urls.stream().collect( 
				Collectors.groupingBy( 
						RegisteredUrl::getUrl, Collectors.counting() 
						));

	}

}
