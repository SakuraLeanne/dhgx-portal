package com.dhgx.portal.gateway.model;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

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
