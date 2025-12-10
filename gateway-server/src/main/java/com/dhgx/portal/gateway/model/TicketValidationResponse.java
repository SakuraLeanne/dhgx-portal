package com.dhgx.portal.gateway.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * ticket 校验成功后回传给业务系统的用户信息。
 */
@Data
@Builder
public class TicketValidationResponse {
    private Long userId;
    private String username;
    private Long tenantId;
    private List<String> roles;
    private Map<String, Object> extInfo;
}
