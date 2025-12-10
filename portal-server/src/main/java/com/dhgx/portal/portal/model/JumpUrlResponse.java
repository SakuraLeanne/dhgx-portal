package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates the generated jump URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JumpUrlResponse {
    private String url;
    private String ticket;
}
