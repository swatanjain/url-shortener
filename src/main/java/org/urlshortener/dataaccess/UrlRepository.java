package org.urlshortener.dataaccess;

import static org.urlshortener.ApplicationConstants.REGISTERED_URLS_REPO_PATH;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.urlshortener.controller.UrlRegistrationController;
import org.urlshortener.model.RegisteredUrl;

/**
 * Handles repository persistence of the URLs registered by {@link UrlRegistrationController} component.
 * 
 * @since 1.0
 * @see BaseRepository
 **/
@Component
public class UrlRepository extends BaseRepository<RegisteredUrl> {
	
	public UrlRepository() throws IOException {
		super(REGISTERED_URLS_REPO_PATH);
	}

	@Override
	protected Class<RegisteredUrl> getType() {
		return RegisteredUrl.class;
	}

}
