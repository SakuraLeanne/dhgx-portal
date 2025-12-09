package com.dhgx.portal.auth.controller;

import com.dhgx.portal.auth.dto.LoginRequest;
import com.dhgx.portal.auth.dto.LoginResponse;
import com.dhgx.portal.auth.model.UserAccount;
import com.dhgx.portal.auth.service.UserService;
import com.dhgx.portal.common.model.ApiResponse;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UserAccount account = userService.authenticate(request.getUsername(), request.getPassword());
        StpUtil.login(account.getUserId());
        LoginResponse response = LoginResponse.builder()
                .userId(account.getUserId())
                .username(account.getUsername())
                .nickname(account.getNickname())
                .roles(account.getRoles())
                .satoken(StpUtil.getTokenValue())
                .loginTime(account.getLoginTime())
                .build();
        return ApiResponse.success(response);
    }
}
