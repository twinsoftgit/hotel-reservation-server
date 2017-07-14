package com.twinsoft.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * A custom converter for the access token
 * Used to enhance the access token with additional information
 *
 * @author Miodrag Pavkovic
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    private static final String TENANT_ID = "tenant_id";
    private static final int TENANT_SYSTEM_ID = 1;

    /**
     * Adds the hard coded tenant ID 1 to the JWT access token
     * @param accessToken
     * @param authentication
     * @return
     */
    @Override public OAuth2AccessToken enhance(final OAuth2AccessToken accessToken, final OAuth2Authentication authentication) {
        final Map<String, Object> additionalInformation = new HashMap<>(accessToken.getAdditionalInformation());
        additionalInformation.put(TENANT_ID, TENANT_SYSTEM_ID);
        final DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(additionalInformation);

        return super.enhance(customAccessToken, authentication);
    }
}
