package com.dhgx.portal.gateway.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * SSO 一次性 Ticket 的配置项。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.sso")
public class TicketProperties {

    /**
     * 门户调用创建 ticket 时使用的内部密钥。
     */
    private String innerSecret = "change-me";

    /**
     * 是否强制校验内部密钥。
     */
    private boolean enforceInnerSecret = true;

    /**
     * ticket 的生存时间。
     */
    private Duration ttl = Duration.ofSeconds(60);

    /**
     * 存储 ticket 的 Redis key 前缀。
     */
    private String redisKeyPrefix = "sso:ticket:";

    /**
     * 当具体系统未配置 gatewayBaseUrl 时使用的默认网关地址。
     */
    private String defaultGatewayBaseUrl = "https://gateway.example.com";
}
