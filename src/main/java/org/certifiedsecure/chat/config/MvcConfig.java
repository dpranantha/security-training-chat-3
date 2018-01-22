package org.certifiedsecure.chat.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Custom configuration for WebMVC
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.certifiedsecure.chat")
public class MvcConfig extends WebMvcConfigurerAdapter {
	/**
	 * Configure static resources
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations(new String[] { "classpath:/static/", });
	}
}