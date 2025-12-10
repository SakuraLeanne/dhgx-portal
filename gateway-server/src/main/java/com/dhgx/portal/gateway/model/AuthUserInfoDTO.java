package com.dhgx.portal.gateway.model;

import java.util.List;

import lombok.Data;

/**
 * 统一封装 access_token 校验后的用户信息。
 */
@Data
public class AuthUserInfoDTO {
    private boolean active;
    private Long userId;
    private String username;
    private Long tenantId;
    private List<String> roles;
    private Long exp;
}
