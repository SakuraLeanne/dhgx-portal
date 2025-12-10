package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic info about a business system shown in the portal.
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
