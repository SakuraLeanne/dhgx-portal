package com.dhgx.portal.gateway.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 业务系统主动校验 ticket 的请求体。
 */
@Data
public class TicketValidateRequest {

    @NotBlank
    private String ticket;

    @NotBlank
    private String systemCode;
}
