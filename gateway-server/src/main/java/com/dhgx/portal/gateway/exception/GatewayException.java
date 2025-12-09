package com.dhgx.portal.gateway.exception;

import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {

    private final int code;

    public GatewayException(int code, String message) {
        super(message);
        this.code = code;
    }
}
