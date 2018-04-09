package org.urlshortener.configurator;

import static org.urlshortener.ApplicationConstants.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.urlshortener.controller.RedirectController;


/**
 * Configures Spring {@link ResourceHandlerRegistry} and {@link ViewControllerRegistry}
 * 	to serve application's static resources. And since {@link RedirectController}'s 
 * 	request mapping pattern also matches these static URI's hence this class also ensures
 * 	that both of these registries obtain higher precedence over other controller components
 * 	while resolving the rendered view.
 * 
 * @since 1.0
 **/
@Configuration
@EnableWebMvc
public class StaticResourcesConfigurator implements WebMvcConfigurer {
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
    	registry
          .addResourceHandler(HELP_LOCATION)
          .addResourceLocations("classpath:/META-INF/resources/"); 
    	registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
    }
    
    @Override
	public void addViewControllers(ViewControllerRegistry registry) {

		registry.addRedirectViewController(HELP_URI, HELP_LOCATION);
		registry.addRedirectViewController(HELP_URI + "/", HELP_LOCATION);
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

	}
    
}