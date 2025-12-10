package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门户展示的业务系统基础信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalSystem {
    private Long id;
    private String code;
    private String name;
    private String baseUrl;
    private String description;
}
