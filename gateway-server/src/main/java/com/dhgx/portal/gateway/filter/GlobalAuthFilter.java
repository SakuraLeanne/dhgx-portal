package com.dhgx.portal.gateway.filter;

import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.gateway.config.AuthTokenProperties;
import com.dhgx.portal.gateway.config.FilterResponseWriter;
import com.dhgx.portal.gateway.model.AuthUserInfoDTO;
import com.dhgx.portal.gateway.service.AuthTokenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private final AuthTokenService authTokenService;
    private final AuthTokenProperties authTokenProperties;
    private final FilterResponseWriter responseWriter;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(exchange.getRequest());
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, "未携带 access_token 或 token 无效");
        }

        return authTokenService.checkToken(token)
                .flatMap(user -> {
                    if (!user.isActive()) {
                        return unauthorized(exchange, "access_token 无效");
                    }
                    ServerHttpRequest mutated = enrichHeaders(exchange.getRequest(), user);
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(ex -> unauthorized(exchange, "access_token 无效"));
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = authTokenProperties.getWhitelist();
        if (CollectionUtils.isEmpty(whitelist)) {
            return false;
        }
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String resolveToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return request.getCookies().getFirst("access_token") != null
                ? request.getCookies().getFirst("access_token").getValue()
                : null;
    }

    private ServerHttpRequest enrichHeaders(ServerHttpRequest request, AuthUserInfoDTO user) {
        return request.mutate()
                .header("X-User-Id", user.getUserId() == null ? "" : String.valueOf(user.getUserId()))
                .header("X-User-Name", user.getUsername() == null ? "" : user.getUsername())
                .header("X-Tenant-Id", user.getTenantId() == null ? "" : String.valueOf(user.getTenantId()))
                .header("X-User-Roles", user.getRoles() == null ? "" : String.join(",", user.getRoles()))
                .build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return responseWriter.writeJson(exchange.getResponse(), ApiResponse.failure(40101, message), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
