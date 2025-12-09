package com.dhgx.portal.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClient {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scopes;
    private long accessTokenValiditySeconds;
    private long refreshTokenValiditySeconds;
    private boolean enabled;
}
