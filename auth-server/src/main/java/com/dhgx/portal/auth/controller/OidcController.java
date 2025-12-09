package com.dhgx.portal.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OidcController {

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> openidConfiguration(HttpServletRequest request) {
        String issuer = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        Map<String, Object> body = new HashMap<>();
        body.put("issuer", issuer);
        body.put("authorization_endpoint", issuer + "/oauth2/authorize");
        body.put("token_endpoint", issuer + "/oauth2/token");
        body.put("userinfo_endpoint", issuer + "/oauth2/userinfo");
        body.put("jwks_uri", issuer + "/oauth2/jwks");
        body.put("response_types_supported", Collections.singletonList("code"));
        body.put("subject_types_supported", Collections.singletonList("public"));
        body.put("id_token_signing_alg_values_supported", Collections.singletonList("RS256"));
        body.put("scopes_supported", Arrays.asList("openid", "profile"));
        body.put("token_endpoint_auth_methods_supported", Collections.singletonList("client_secret_post"));
        return body;
    }
}
