package com.dhgx.portal.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * access_token 鉴权相关的配置参数。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthTokenProperties {

    /**
     * 免鉴权路径列表，匹配后直接跳过 GlobalAuthFilter。
     */
    private List<String> whitelist = new ArrayList<>();

    /**
     * 远程模式下调用的 /oauth2/check_token 地址。
     */
    private String checkTokenUrl = "http://auth-server/oauth2/check_token";

    /**
     * 校验 token 使用的 OAuth2 客户端 ID。
     */
    private String clientId = "gateway";

    /**
     * 校验 token 使用的 OAuth2 客户端密钥。
     */
    private String clientSecret = "gateway-secret";
}
