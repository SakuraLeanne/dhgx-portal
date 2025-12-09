package com.dhgx.portal.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String clientId;
    private List<String> scopes;
    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;
}
