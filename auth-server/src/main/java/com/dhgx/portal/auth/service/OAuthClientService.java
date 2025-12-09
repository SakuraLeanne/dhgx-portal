package com.dhgx.portal.auth.service;

import com.dhgx.portal.auth.model.OAuthClient;
import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuthClientService {
    private final Map<String, OAuthClient> clients = new HashMap<>();

    public OAuthClientService() {
        clients.put("portal", OAuthClient.builder()
                .clientId("portal")
                .clientSecret("portal-secret")
                .redirectUri("https://portal.example.com/login/callback")
                .scopes(Arrays.asList("openid", "profile"))
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(7200)
                .enabled(true)
                .build());
    }

    public OAuthClient validateClient(String clientId, String redirectUri) {
        OAuthClient client = Optional.ofNullable(clients.get(clientId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_ID_INVALID));
        if (!client.isEnabled()) {
            throw new BusinessException(ErrorCode.CLIENT_ID_INVALID, "客户端未启用");
        }
        if (redirectUri != null && !client.getRedirectUri().equals(redirectUri)) {
            throw new BusinessException(ErrorCode.REDIRECT_URI_MISMATCH);
        }
        return client;
    }

    public OAuthClient authenticateClient(String clientId, String clientSecret) {
        OAuthClient client = Optional.ofNullable(clients.get(clientId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_ID_INVALID));
        if (!client.getClientSecret().equals(clientSecret)) {
            throw new BusinessException(ErrorCode.CLIENT_SECRET_INVALID);
        }
        if (!client.isEnabled()) {
            throw new BusinessException(ErrorCode.CLIENT_ID_INVALID, "客户端未启用");
        }
        return client;
    }
}
