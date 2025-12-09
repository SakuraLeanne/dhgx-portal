package com.dhgx.portal.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    private Long userId;
    private String username;
    private String nickname;
    private String password;
    private boolean disabled;
    private List<String> roles;
    private LocalDateTime loginTime;
    private String email;
    private String phoneNumber;
    private Long tenantId;
}
