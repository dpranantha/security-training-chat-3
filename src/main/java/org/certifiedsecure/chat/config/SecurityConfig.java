package org.certifiedsecure.chat.config;

import org.certifiedsecure.chat.security.ChatAuthenticationTokenFilter;
import org.certifiedsecure.chat.security.ChatUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Custom SecurityConfig
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("org.certifiedsecure.chat")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ChatUserDetailsService chatUserDetailsService;

	/**
	 * Ignore all static resources
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**");
	}

	/**
	 * Configure security
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		final ChatAuthenticationTokenFilter chatAuthenticationTokenFilter = new ChatAuthenticationTokenFilter(
				chatUserDetailsService);
		http.addFilterBefore(chatAuthenticationTokenFilter, BasicAuthenticationFilter.class);
		http.headers().cacheControl().disable();
		http.csrf().disable();
	}
}