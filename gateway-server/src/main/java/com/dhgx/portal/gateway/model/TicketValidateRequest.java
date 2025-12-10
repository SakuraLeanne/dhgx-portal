package com.dhgx.portal.gateway.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TicketValidateRequest {

    @NotBlank
    private String ticket;

    @NotBlank
    private String systemCode;
}
