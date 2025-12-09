package com.dhgx.portal.gateway.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TicketInfoDTO {
    private String ticket;
    private Long userId;
    private String username;
    private Long tenantId;
    private List<String> roles;
    private String systemCode;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private Boolean used;
    private Map<String, Object> extInfo;
}
