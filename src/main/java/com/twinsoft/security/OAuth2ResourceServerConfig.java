package com.twinsoft.security;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Resource Server hosts the resources [our REST API] the client is interested in. Resources are located on
 * /api/. @EnableResourceServer annotation, applied on OAuth2 Resource Servers, enables a Spring Security filter that
 * authenticates requests using an incoming OAuth2 token. Class ResourceServerConfigurerAdapter implements
 * ResourceServerConfigurer providing methods to adjust the access rules and paths that are protected by OAuth2
 * security.
 * 
 * @author Miodrag Pavkovic
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${hotelserver.security.oauth2.resource.id}")
	private String resourceId;

	// Inject the TokenStore and the DefaultTokenServices,
	// beans that were defined back on the AuthorizationServerConfigurerAdapter.
	// Both adapters must share the same logic on how to create and extract the
	// token in order for the authentication process to work.
	// The DefaultTokenServices bean provided at the AuthorizationConfig
	@Inject
	private DefaultTokenServices tokenServices;

	// The TokenStore bean provided at the AuthorizationConfig
	@Inject
	private TokenStore tokenStore;

	// To allow the ResourceServerConfigurerAdapter to understand the token,
	// it must share the same characteristics with AuthorizationServerConfigurerAdapter.
	// So, we must wire it up the beans in the ResourceServerSecurityConfigurer.
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(resourceId).tokenServices(tokenServices).tokenStore(tokenStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter#configure
	 * (org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// Configure the HttpSecurity to control the access to specific resources.
		http.requestMatcher(new OAuthRequestedMatcher()).csrf().disable().anonymous().disable().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// when restricting access to 'Roles' you must remove the "ROLE_" part role
				// for "ROLE_USER" use only "USER"
				.antMatchers("/api/hello").access("hasAnyRole('USER')").antMatchers("/api/me")
				.hasAnyRole("USER", "ADMIN").antMatchers("/api/admin").hasRole("ADMIN")
				// restricting all access to /api/** to authenticated users
				.antMatchers("/api/**").authenticated();
	}

	/**
	 * Restricts all options defined on this HttpSecurity only to requests directed at URIs with the path /api/,
	 * guaranteeing that the HttpSecurity defined at ResourceConfig will prevail over any other endpoint remaining.
	 * 
	 * @author Miodrag Pavkovic
	 */
	private static class OAuthRequestedMatcher implements RequestMatcher {

		private static final String API = "/api/";

		public boolean matches(HttpServletRequest request) {
			// Determine if the resource called is "/api/**"
			String path = request.getServletPath();
			if (path.length() >= 5) {
				path = path.substring(0, 5);
				boolean isApi = path.equals(API);
				return isApi;
			} else
				return false;
		}
	}

}
