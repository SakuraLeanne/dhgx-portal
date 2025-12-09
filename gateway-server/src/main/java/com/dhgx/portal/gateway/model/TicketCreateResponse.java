package com.dhgx.portal.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketCreateResponse {
    private String ticket;
    private String gatewayBaseUrl;
}
