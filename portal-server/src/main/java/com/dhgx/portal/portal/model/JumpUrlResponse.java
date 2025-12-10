package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装生成的跳转链接与对应的 ticket。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JumpUrlResponse {
    private String url;
    private String ticket;
}
