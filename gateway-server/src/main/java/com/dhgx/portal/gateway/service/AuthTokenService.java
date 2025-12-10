package com.dhgx.portal.gateway.service;

import com.dhgx.portal.gateway.model.AuthUserInfoDTO;
import reactor.core.publisher.Mono;

public interface AuthTokenService {

    Mono<AuthUserInfoDTO> checkToken(String accessToken);
}
