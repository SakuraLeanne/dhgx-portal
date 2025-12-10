package com.dhgx.portal.portal.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Request body for building a one-time jump URL via the gateway.
 */
@Data
public class JumpUrlRequest {
    @NotBlank(message = "systemCode 不能为空")
    private String systemCode;

    /**
     * Path inside the target system, e.g. /dashboard.
     */
    @NotBlank(message = "targetPath 不能为空")
    private String targetPath;
}
