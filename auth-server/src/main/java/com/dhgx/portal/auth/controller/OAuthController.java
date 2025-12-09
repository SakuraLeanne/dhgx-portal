package com.dhgx.portal.auth.controller;

import com.dhgx.portal.auth.dto.OAuthTokenResponse;
import com.dhgx.portal.auth.model.AuthorizationCode;
import com.dhgx.portal.auth.model.OAuthClient;
import com.dhgx.portal.auth.model.TokenPair;
import com.dhgx.portal.auth.model.UserAccount;
import com.dhgx.portal.auth.service.OAuthClientService;
import com.dhgx.portal.auth.service.TokenService;
import com.dhgx.portal.auth.service.UserService;
import com.dhgx.portal.auth.util.BearerTokenExtractor;
import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.common.model.ErrorCode;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuthController {

    private final OAuthClientService clientService;
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/authorize")
    public ResponseEntity<ApiResponse<Void>> authorize(@RequestParam("client_id") String clientId,
                                                       @RequestParam("response_type") String responseType,
                                                       @RequestParam("redirect_uri") String redirectUri,
                                                       @RequestParam(value = "scope", required = false, defaultValue = "openid profile") String scope,
                                                       @RequestParam(value = "state", required = false) String state) {
        if (!StpUtil.isLogin()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.LOCATION, "/login");
            return ResponseEntity.status(302).headers(headers).build();
        }
        if (!"code".equalsIgnoreCase(responseType)) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_CODE_INVALID, "response_type 仅支持 code");
        }
        OAuthClient client = clientService.validateClient(clientId, redirectUri);
        AuthorizationCode code = tokenService.createAuthorizationCode(StpUtil.getLoginIdAsLong(), client);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(redirectUri)
                .queryParam("code", code.getCode())
                .queryParam("state", state);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, builder.build(true).toUriString());
        return ResponseEntity.status(302).headers(headers).body(ApiResponse.success(null));
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ApiResponse<OAuthTokenResponse> token(@RequestParam("grant_type") String grantType,
                                                 @RequestParam(value = "code", required = false) String code,
                                                 @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                                 @RequestParam("client_id") String clientId,
                                                 @RequestParam("client_secret") String clientSecret,
                                                 @RequestParam(value = "refresh_token", required = false) String refreshToken) {
        OAuthClient client = clientService.authenticateClient(clientId, clientSecret);
        TokenPair tokenPair;
        if ("authorization_code".equals(grantType)) {
            clientService.validateClient(clientId, redirectUri);
            tokenPair = tokenService.exchangeAuthorizationCode(code, client);
        } else if ("refresh_token".equals(grantType)) {
            tokenPair = tokenService.refreshToken(refreshToken, client);
        } else {
            throw new BusinessException(ErrorCode.AUTHORIZATION_CODE_INVALID, "不支持的 grant_type");
        }
        OAuthTokenResponse response = OAuthTokenResponse.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(Duration.between(Instant.now(), tokenPair.getAccessTokenExpiresAt()).getSeconds())
                .scope(tokenPair.getScopes().stream().collect(Collectors.joining(" ")))
                .build();
        return ApiResponse.success(response);
    }

    @PostMapping(value = "/check_token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ApiResponse<Map<String, Object>> checkToken(@RequestParam("token") @NotBlank String token) {
        TokenPair pair;
        try {
            pair = tokenService.validateToken(token);
        } catch (BusinessException e) {
            Map<String, Object> inactive = new HashMap<>();
            inactive.put("active", false);
            return ApiResponse.success(inactive);
        }
        UserAccount user = userService.findById(pair.getUserId());
        Map<String, Object> body = new HashMap<>();
        body.put("active", true);
        body.put("client_id", pair.getClientId());
        body.put("user_name", user != null ? user.getUsername() : null);
        body.put("user_id", pair.getUserId());
        body.put("scope", pair.getScopes());
        body.put("exp", pair.getAccessTokenExpiresAt().getEpochSecond());
        body.put("authorities", user != null ? user.getRoles() : null);
        body.put("tenant_id", user != null ? user.getTenantId() : null);
        return ApiResponse.success(body);
    }

    @GetMapping("/userinfo")
    public ApiResponse<Map<String, Object>> userInfo(HttpServletRequest request) {
        String token = BearerTokenExtractor.extract(request);
        TokenPair tokenPair = tokenService.validateToken(token);
        UserAccount user = userService.findById(tokenPair.getUserId());
        Map<String, Object> body = new HashMap<>();
        body.put("sub", String.valueOf(tokenPair.getUserId()));
        body.put("name", user != null ? user.getNickname() : null);
        body.put("preferred_username", user != null ? user.getUsername() : null);
        body.put("email", user != null ? user.getEmail() : null);
        body.put("phone_number", user != null ? user.getPhoneNumber() : null);
        body.put("tenant_id", user != null ? user.getTenantId() : null);
        body.put("roles", user != null ? user.getRoles() : null);
        return ApiResponse.success(body);
    }

    @GetMapping("/jwks")
    public ApiResponse<Map<String, Object>> jwks() {
        Map<String, Object> response = new HashMap<>();
        response.put("keys", java.util.Collections.emptyList());
        return ApiResponse.success(response);
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.POST, RequestMethod.GET})
    public ApiResponse<Map<String, String>> logout(HttpServletRequest request) {
        String token = BearerTokenExtractor.extract(request);
        if (token != null) {
            tokenService.logout(token);
        }
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        Map<String, String> result = new HashMap<>();
        result.put("msg", "logout success");
        return ApiResponse.success(result);
    }
}
