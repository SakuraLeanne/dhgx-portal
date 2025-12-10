package com.dhgx.portal.gateway.model;

import java.util.List;

import lombok.Data;

@Data
public class AuthUserInfoDTO {
    private boolean active;
    private Long userId;
    private String username;
    private Long tenantId;
    private List<String> roles;
    private Long exp;
}
