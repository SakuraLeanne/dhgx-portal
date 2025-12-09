package com.dhgx.portal.gateway.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.sso")
public class TicketProperties {

    /**
     * Internal secret used to allow portal-server to create tickets.
     */
    private String innerSecret = "change-me";

    /**
     * Whether to enforce the inner secret check on ticket creation requests.
     */
    private boolean enforceInnerSecret = true;

    /**
     * Ticket time to live.
     */
    private Duration ttl = Duration.ofSeconds(60);

    /**
     * Redis key prefix for ticket entries.
     */
    private String redisKeyPrefix = "sso:ticket:";

    /**
     * Default gateway base URL for ticket redirects when a system specific one is not configured.
     */
    private String defaultGatewayBaseUrl = "https://gateway.example.com";
}
