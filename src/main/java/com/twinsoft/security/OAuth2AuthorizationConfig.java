package com.twinsoft.security;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * Authorization server is the one responsible for verifying credentials and if credentials are OK, providing the
 * tokens[refresh-token as well as access-token]. It also contains information about registered clients and possible
 * access scopes and grant types. The token store is used to store the token. We will be using an in-memory token
 * store. @EnableAuthorizationServer enables an Authorization Server (i.e. an AuthorizationEndpoint and a TokenEndpoint)
 * in the current application context. Class AuthorizationServerConfigurerAdapter implements
 * AuthorizationServerConfigurer which provides all the necessary methods to configure an Authorization server.
 * 
 * @author Miodrag Pavkovic
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

	private int accessTokenValiditySeconds = 10000;
	private int refreshTokenValiditySeconds = 30000;

	@Value("${hotelserver.security.oauth2.resource.id}")
	private String resourceId;

	@Inject
	private AuthenticationManager authenticationManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#
	 * configure(org.springframework.security.oauth2.config.annotation.web.configurers.
	 * AuthorizationServerEndpointsConfigurer)
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// Configure the non-security features of the Authorization Server endpoints, like token store, token
		// customizations, user approvals and grant types.
		endpoints.authenticationManager(this.authenticationManager).tokenServices(tokenServices())
				.tokenStore(tokenStore()).accessTokenConverter(accessTokenConverter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#
	 * configure(org.springframework.security.oauth2.config.annotation.web.configurers.
	 * AuthorizationServerSecurityConfigurer)
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		// Configure the security of the Authorization Server, which means in practical terms the /oauth/token endpoint.
		oauthServer
				// we're allowing access to the token only for clients with
				// 'ROLE_TRUSTED_CLIENT' authority
				.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
				.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter#
	 * configure(org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer)
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Configure the ClientDetailsService, declaring individual clients and their properties.
		clients.inMemory().withClient("trusted-app")
				.authorizedGrantTypes("client_credentials", "password", "refresh_token")
				.authorities("ROLE_TRUSTED_CLIENT").scopes("read", "write").resourceIds(resourceId)
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds).secret("secret");
	}

	// To provide the JWT, we'll need to create a JwtTokenStore, a
	// JwtAccessTokenConverter and a DefaultTokenServices bean, and wire all
	// that to the AuthorizationServerEndpointsConfigurer.
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	/**
	 * 
	 * Using a symmetric key in the JwtAccessTokenConverter isn't the best approach for a production environment. We use
	 * an asymmetric key to sign the converter.
	 * 
	 * @return JwtAccessTokenConverter
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mykeys.jks"),
				"mypass".toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mykeys"));
		return converter;
	}

	/**
	 * @return DefaultTokenServices
	 */
	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
		return defaultTokenServices;
	}
}