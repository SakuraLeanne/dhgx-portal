package com.dhgx.portal.auth.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public final class BearerTokenExtractor {
    private static final String PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static String extract(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(PREFIX)) {
            return header.substring(PREFIX.length());
        }
        return null;
    }
}
