package com.dhgx.portal.auth.config;

import com.dhgx.portal.common.model.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Wraps controller responses in {@link ApiResponse} to provide a consistent response format.
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            return body;
        }
        if (body instanceof ResponseEntity<?>) {
            ResponseEntity<?> entity = (ResponseEntity<?>) body;
            Object entityBody = entity.getBody();
            if (entityBody instanceof ApiResponse) {
                return body;
            }
            ApiResponse<Object> wrapped = ApiResponse.success(entityBody);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(entity.getHeaders());
            return ResponseEntity.status(entity.getStatusCode()).headers(headers).body(wrapped);
        }
        return ApiResponse.success(body);
    }
}
