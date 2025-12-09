package com.dhgx.portal.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String username;
    private String nickname;
    private List<String> roles;
    private String satoken;
    private LocalDateTime loginTime;
}
