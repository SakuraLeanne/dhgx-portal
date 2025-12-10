package com.dhgx.portal.gateway.config;

import com.dhgx.portal.common.model.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 统一的响应输出工具类，供 Gateway 过滤器在链路中直接返回 JSON 结构。
 */
@Component
@RequiredArgsConstructor
public class FilterResponseWriter {

    private final ObjectMapper objectMapper;

    /**
     * 将 ApiResponse 写入响应体，设置状态码与 JSON Content-Type。
     */
    public Mono<Void> writeJson(ServerHttpResponse response, ApiResponse<?> body, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory bufferFactory = response.bufferFactory();
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = bufferFactory.wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }
}
