package org.certifiedsecure.chat.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Security Filter to authenticate users based on their CHAT_AUTH cookie
 */
@Component
public class ChatAuthenticationTokenFilter implements Filter {
	private ChatUserDetailsService chatUserDetailsService;

	public ChatAuthenticationTokenFilter(ChatUserDetailsService chatUserDetailsService) {
		super();
		this.chatUserDetailsService = chatUserDetailsService;
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.getContext();

		Cookie cookie_auth = WebUtils.getCookie((HttpServletRequest) request, "CHAT_AUTH");
		if (cookie_auth != null) {
			UserDetails principal = chatUserDetailsService.loadUserByToken(cookie_auth.getValue());
			if (principal != null) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
						"password", principal.getAuthorities());
				context.setAuthentication(authentication);
			} else {
				context.setAuthentication(null);
			}
		} else {
			context.setAuthentication(null);
		}

		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
