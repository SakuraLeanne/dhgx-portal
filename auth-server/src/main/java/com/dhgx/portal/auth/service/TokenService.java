package com.dhgx.portal.auth.service;

import com.dhgx.portal.auth.model.AuthorizationCode;
import com.dhgx.portal.auth.model.OAuthClient;
import com.dhgx.portal.auth.model.TokenPair;
import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final Map<String, AuthorizationCode> codeStore = new ConcurrentHashMap<>();
    private final Map<String, TokenPair> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, TokenPair> refreshTokenStore = new ConcurrentHashMap<>();

    public AuthorizationCode createAuthorizationCode(Long userId, OAuthClient client) {
        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .code(UUID.randomUUID().toString().replace("-", ""))
                .clientId(client.getClientId())
                .userId(userId)
                .scopes(client.getScopes())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        codeStore.put(authorizationCode.getCode(), authorizationCode);
        return authorizationCode;
    }

    public TokenPair exchangeAuthorizationCode(String code, OAuthClient client) {
        AuthorizationCode authorizationCode = codeStore.remove(code);
        if (authorizationCode == null || authorizationCode.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_CODE_INVALID);
        }
        if (!authorizationCode.getClientId().equals(client.getClientId())) {
            throw new BusinessException(ErrorCode.CLIENT_ID_INVALID);
        }
        return createTokenPair(authorizationCode.getUserId(), client, authorizationCode.getScopes());
    }

    public TokenPair refreshToken(String refreshToken, OAuthClient client) {
        TokenPair tokenPair = refreshTokenStore.get(refreshToken);
        if (tokenPair == null || tokenPair.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        if (!tokenPair.getClientId().equals(client.getClientId())) {
            throw new BusinessException(ErrorCode.CLIENT_ID_INVALID);
        }
        return createTokenPair(tokenPair.getUserId(), client, tokenPair.getScopes());
    }

    public TokenPair validateToken(String accessToken) {
        TokenPair tokenPair = tokenStore.get(accessToken);
        if (tokenPair == null || tokenPair.getAccessTokenExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        return tokenPair;
    }

    public void logout(String accessToken) {
        TokenPair tokenPair = tokenStore.remove(accessToken);
        if (tokenPair != null) {
            refreshTokenStore.remove(tokenPair.getRefreshToken());
        }
    }

    private TokenPair createTokenPair(Long userId, OAuthClient client, java.util.List<String> scopes) {
        TokenPair tokenPair = TokenPair.builder()
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .userId(userId)
                .clientId(client.getClientId())
                .scopes(scopes)
                .accessTokenExpiresAt(Instant.now().plusSeconds(client.getAccessTokenValiditySeconds()))
                .refreshTokenExpiresAt(Instant.now().plusSeconds(client.getRefreshTokenValiditySeconds()))
                .build();
        tokenStore.put(tokenPair.getAccessToken(), tokenPair);
        refreshTokenStore.put(tokenPair.getRefreshToken(), tokenPair);
        return tokenPair;
    }
}
