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
    USERNAME_OR_PASSWORD_ERROR(10001, "用户名或密码错误"),
    USER_DISABLED(10002, "账号已被禁用"),
    CAPTCHA_ERROR(10003, "验证码错误"),
    CLIENT_ID_INVALID(20001, "client_id 不合法"),
    CLIENT_SECRET_INVALID(20002, "client_secret 不正确"),
    REDIRECT_URI_MISMATCH(20003, "redirect_uri 不匹配"),
    AUTHORIZATION_CODE_INVALID(20004, "授权码无效或已过期"),
    TOKEN_INVALID(20005, "token 无效或已过期"),
    ACCESS_DENIED(30001, "无权限访问接口"),
    SERVER_ERROR(50000, "系统内部错误"),
    VALIDATION_FAILED(1001, "参数校验失败"),
    UNAUTHORIZED(1002, "未认证或令牌失效"),
    FORBIDDEN(1003, "无权限访问资源"),
    NOT_FOUND(1004, "资源不存在"),
    BUSINESS_ERROR(1100, "业务处理异常");

    private final int code;
    private final String message;
}
