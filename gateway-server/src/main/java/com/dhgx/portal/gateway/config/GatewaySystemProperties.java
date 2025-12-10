package com.dhgx.portal.gateway.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 网关侧关于业务系统路由的配置项。
 */
@Data
@ConfigurationProperties(prefix = "gateway.systems")
public class GatewaySystemProperties {

    private List<SystemRoute> routes = new ArrayList<>();

    public Optional<SystemRoute> findByCode(String systemCode) {
        return routes.stream()
                .filter(route -> route.getSystemCode().equalsIgnoreCase(systemCode))
                .findFirst();
    }

    @Data
    public static class SystemRoute {
        /**
         * 业务系统编码，例如 BIZ-APP。
         */
        private String systemCode;

        /**
         * 网关路由前缀，如 /biz-app/**。
         */
        private String routePrefix;

        /**
         * 是否对该路由启用 ticket 校验。
         */
        private boolean ticketEnabled = true;

        /**
         * 门户生成跳转链接时返回的网关前缀地址。
         */
        private String gatewayBaseUrl;
    }
}
