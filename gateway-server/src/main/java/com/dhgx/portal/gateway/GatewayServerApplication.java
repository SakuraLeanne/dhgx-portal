package com.dhgx.portal.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.dhgx.portal.gateway.config.AuthTokenProperties;
import com.dhgx.portal.gateway.config.GatewaySystemProperties;
import com.dhgx.portal.gateway.config.TicketProperties;

@SpringBootApplication
@EnableConfigurationProperties({TicketProperties.class, GatewaySystemProperties.class, AuthTokenProperties.class})
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
