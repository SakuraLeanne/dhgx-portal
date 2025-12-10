package com.dhgx.portal.gateway.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

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
         * Business system code, e.g. BIZ-APP.
         */
        private String systemCode;

        /**
         * Route prefix that matches gateway routes, e.g. /biz-app/**.
         */
        private String routePrefix;

        /**
         * Whether ticket validation should apply for this route.
         */
        private boolean ticketEnabled = true;

        /**
         * Base URL returned to portal when issuing jump URLs.
         */
        private String gatewayBaseUrl;
    }
}
