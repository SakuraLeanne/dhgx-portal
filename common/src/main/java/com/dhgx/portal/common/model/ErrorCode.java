package com.dhgx.portal.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error codes shared across services.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS(0, "success"),
    VALIDATION_FAILED(1001, "参数校验失败"),
    UNAUTHORIZED(1002, "未认证或令牌失效"),
    FORBIDDEN(1003, "无权限访问资源"),
    NOT_FOUND(1004, "资源不存在"),
    BUSINESS_ERROR(1100, "业务处理异常"),
    SERVER_ERROR(1500, "服务器内部错误");

    private final int code;
    private final String message;
}
