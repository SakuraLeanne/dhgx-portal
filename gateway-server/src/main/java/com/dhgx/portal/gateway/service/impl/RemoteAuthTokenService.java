package com.dhgx.portal.gateway.service.impl;

import com.dhgx.portal.gateway.config.AuthTokenProperties;
import com.dhgx.portal.gateway.exception.GatewayException;
import com.dhgx.portal.gateway.model.AuthUserInfoDTO;
import com.dhgx.portal.gateway.service.AuthTokenService;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 通过调用 auth-server `/oauth2/check_token` 接口完成远程 token 校验的实现。
 */
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

    /**
     * 将 /check_token 返回的 Map 转换为内部统一的用户信息对象。
     */
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
        userInfo.setRoles((List<String>) response.getOrDefault("authorities", Collections.emptyList()));
        Object exp = response.get("exp");
        if (exp instanceof Number) {
            userInfo.setExp(((Number) exp).longValue());
        }
        return userInfo;
    }

    /**
     * 兼容字符串与数字类型的 ID 解析。
     */
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

    /**
     * 组装 Basic Auth 头，供客户端模式访问 `/check_token`。
     */
    private String basicAuthHeader() {
        String token = authTokenProperties.getClientId() + ":" + authTokenProperties.getClientSecret();
        return "Basic " + Base64Utils.encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
}
