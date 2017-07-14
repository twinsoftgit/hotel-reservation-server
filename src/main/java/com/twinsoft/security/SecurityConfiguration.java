package com.twinsoft.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * The adapter provides some configuration methods that can be customized through overriding., we will use
 * our WebSecurityConfigurerAdapter to turn off the default security options and to configure users and an
 * authentication process. It is also important to pay attention to the adapter filtering order. On Spring, the filter
 * with the greatest value takes precedence over the lowest ones. On Spring Boot 1.5.+, the default order of the adapter
 * is 100, but you can change it using the @Order annotation.
 * 
 * @author Miodrag Pavkovic
 */
@Configuration
@EnableWebSecurity(debug = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().disable() // disable form authentication
				.anonymous().disable() // disable anonymous user
				.httpBasic().and()
				// restricting access to authenticated users
				.authorizeRequests().anyRequest().authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin")
				.password("password").authorities("ROLE_ADMIN");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// provides the default AuthenticationManager as a Bean
		return super.authenticationManagerBean();
	}
}
