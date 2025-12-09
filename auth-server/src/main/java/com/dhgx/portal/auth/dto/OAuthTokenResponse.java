package com.dhgx.portal.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;
    @JsonProperty("id_token")
    private String idToken;
}
