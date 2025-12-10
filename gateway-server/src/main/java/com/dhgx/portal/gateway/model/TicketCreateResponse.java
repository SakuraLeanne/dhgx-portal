package com.dhgx.portal.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 门户创建 ticket 成功后的返回数据。
 */
@Data
@AllArgsConstructor
public class TicketCreateResponse {
    private String ticket;
    private String gatewayBaseUrl;
}
