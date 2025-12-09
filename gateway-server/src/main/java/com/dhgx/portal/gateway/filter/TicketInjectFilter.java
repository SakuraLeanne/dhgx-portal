package com.dhgx.portal.gateway.filter;

import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.gateway.config.FilterResponseWriter;
import com.dhgx.portal.gateway.config.GatewaySystemProperties;
import com.dhgx.portal.gateway.exception.GatewayException;
import com.dhgx.portal.gateway.model.TicketValidateRequest;
import com.dhgx.portal.gateway.model.TicketValidationResponse;
import com.dhgx.portal.gateway.service.TicketService;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TicketInjectFilter implements GlobalFilter, Ordered {

    private final TicketService ticketService;
    private final GatewaySystemProperties systemProperties;
    private final FilterResponseWriter responseWriter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        String ticket = exchange.getRequest().getQueryParams().getFirst("ticket");
        if (!StringUtils.hasText(ticket)) {
            return chain.filter(exchange);
        }

        Optional<GatewaySystemProperties.SystemRoute> route = matchRoute(uri.getPath());
        if (route.isEmpty()) {
            return chain.filter(exchange);
        }

        TicketValidateRequest validateRequest = new TicketValidateRequest();
        validateRequest.setTicket(ticket);
        validateRequest.setSystemCode(route.get().getSystemCode());

        return ticketService.validateTicket(validateRequest)
                .flatMap(user -> forwardWithHeaders(exchange, chain, user))
                .onErrorResume(GatewayException.class, ex ->
                        responseWriter.writeJson(exchange.getResponse(), ApiResponse.failure(ex.getCode(), ex.getMessage()), HttpStatus.UNAUTHORIZED));
    }

    private Optional<GatewaySystemProperties.SystemRoute> matchRoute(String path) {
        return systemProperties.getRoutes().stream()
                .filter(route -> route.isTicketEnabled() && StringUtils.hasText(route.getRoutePrefix()) && path.startsWith(route.getRoutePrefix()))
                .findFirst();
    }

    private Mono<Void> forwardWithHeaders(ServerWebExchange exchange, GatewayFilterChain chain, TicketValidationResponse user) {
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id", user.getUserId() == null ? "" : String.valueOf(user.getUserId()))
                .header("X-User-Name", user.getUsername() == null ? "" : user.getUsername())
                .header("X-Tenant-Id", user.getTenantId() == null ? "" : String.valueOf(user.getTenantId()))
                .header("X-User-Roles", user.getRoles() == null ? "" : String.join(",", user.getRoles()))
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
