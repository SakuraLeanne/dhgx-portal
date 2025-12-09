package com.dhgx.portal.auth.service;

import com.dhgx.portal.auth.model.UserAccount;
import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<String, UserAccount> users = new HashMap<>();

    public UserService() {
        users.put("admin", UserAccount.builder()
                .userId(1L)
                .username("admin")
                .nickname("系统管理员")
                .password("123456")
                .disabled(false)
                .roles(Arrays.asList("ADMIN", "USER"))
                .email("admin@example.com")
                .phoneNumber("13800000000")
                .tenantId(1001L)
                .build());
        users.put("disabled", UserAccount.builder()
                .userId(2L)
                .username("disabled")
                .nickname("禁用账号")
                .password("123456")
                .disabled(true)
                .roles(Arrays.asList("USER"))
                .tenantId(1001L)
                .build());
    }

    public UserAccount authenticate(String username, String password) {
        UserAccount account = Optional.ofNullable(users.get(username))
                .orElseThrow(() -> new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR));
        if (account.isDisabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!account.getPassword().equals(password)) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        account.setLoginTime(LocalDateTime.now());
        return account;
    }

    public UserAccount findById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
