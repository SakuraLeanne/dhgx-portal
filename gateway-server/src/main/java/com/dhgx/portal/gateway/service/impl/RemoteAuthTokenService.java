package com.dhgx.portal.gateway.service.impl;

import com.dhgx.portal.gateway.config.AuthTokenProperties;
import com.dhgx.portal.gateway.exception.GatewayException;
import com.dhgx.portal.gateway.model.AuthUserInfoDTO;
import com.dhgx.portal.gateway.service.AuthTokenService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RemoteAuthTokenService implements AuthTokenService {

    private final WebClient.Builder webClientBuilder;
    private final AuthTokenProperties authTokenProperties;

    @Override
    public Mono<AuthUserInfoDTO> checkToken(String accessToken) {
        return webClientBuilder.build()
                .post()
                .uri(authTokenProperties.getCheckTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", basicAuthHeader())
                .body(BodyInserters.fromFormData("token", accessToken))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .map(this::mapToAuthUser)
                .onErrorMap(ex -> new GatewayException(40101, "access_token 校验失败"));
    }

    private AuthUserInfoDTO mapToAuthUser(Map<String, Object> response) {
        boolean active = Boolean.TRUE.equals(response.get("active"));
        if (!active) {
            throw new GatewayException(40101, "access_token 无效");
        }
        AuthUserInfoDTO userInfo = new AuthUserInfoDTO();
        userInfo.setActive(true);
        userInfo.setUsername((String) response.getOrDefault("user_name", ""));
        userInfo.setUserId(parseLong(response.get("user_id")));
        userInfo.setTenantId(parseLong(response.get("tenant_id")));
        userInfo.setRoles((List<String>) response.getOrDefault("authorities", List.of()));
        Object exp = response.get("exp");
        if (exp instanceof Number) {
            userInfo.setExp(((Number) exp).longValue());
        }
        return userInfo;
    }

    private Long parseLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String basicAuthHeader() {
        String token = authTokenProperties.getClientId() + ":" + authTokenProperties.getClientSecret();
        return "Basic " + Base64Utils.encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
}
