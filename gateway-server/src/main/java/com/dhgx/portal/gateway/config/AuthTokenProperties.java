package com.dhgx.portal.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthTokenProperties {

    /**
     * Paths that bypass the GlobalAuthFilter.
     */
    private List<String> whitelist = new ArrayList<>();

    /**
     * Endpoint for /oauth2/check_token when using remote verification.
     */
    private String checkTokenUrl = "http://auth-server/oauth2/check_token";

    /**
     * OAuth2 client id for check_token.
     */
    private String clientId = "gateway";

    /**
     * OAuth2 client secret for check_token.
     */
    private String clientSecret = "gateway-secret";
}
