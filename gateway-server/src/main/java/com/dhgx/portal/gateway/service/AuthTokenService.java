package com.dhgx.portal.gateway.service;

import com.dhgx.portal.gateway.model.AuthUserInfoDTO;
import reactor.core.publisher.Mono;

/**
 * access_token 校验服务接口。
 */
public interface AuthTokenService {

    Mono<AuthUserInfoDTO> checkToken(String accessToken);
}
