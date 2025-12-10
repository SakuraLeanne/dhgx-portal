package com.dhgx.portal.gateway.model;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 创建 ticket 的请求参数模型，供门户传入用户与目标系统信息。
 */
@Data
public class TicketCreateRequest {

    @NotBlank
    private String systemCode;

    @NotNull
    private Long userId;

    @NotBlank
    private String username;

    private Long tenantId;

    private List<String> roles;

    private Map<String, Object> extInfo;
}
