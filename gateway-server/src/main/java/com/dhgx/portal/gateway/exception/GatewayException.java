package com.dhgx.portal.gateway.exception;

import lombok.Getter;

/**
 * 网关自定义异常，携带业务错误码。
 */
@Getter
public class GatewayException extends RuntimeException {

    private final int code;

    public GatewayException(int code, String message) {
        super(message);
        this.code = code;
    }
}
