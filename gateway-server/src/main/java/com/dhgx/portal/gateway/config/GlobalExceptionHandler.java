package com.dhgx.portal.gateway.config;

import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.gateway.exception.GatewayException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ApiResponse<Void> handleGatewayException(GatewayException ex) {
        return ApiResponse.failure(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("validation failed");
        return ApiResponse.failure(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknown(Exception ex) {
        return ApiResponse.failure(50000, "网关内部错误");
    }
}
