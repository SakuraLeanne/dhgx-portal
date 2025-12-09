package com.dhgx.portal.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "username 不能为空")
    private String username;

    @NotBlank(message = "password 不能为空")
    private String password;

    private String captchaId;

    private String captcha;
}
