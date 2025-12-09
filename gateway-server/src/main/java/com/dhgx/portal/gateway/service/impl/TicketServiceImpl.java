package com.dhgx.portal.gateway.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import com.dhgx.portal.gateway.config.GatewaySystemProperties;
import com.dhgx.portal.gateway.config.TicketProperties;
import com.dhgx.portal.gateway.exception.GatewayException;
import com.dhgx.portal.gateway.model.TicketCreateRequest;
import com.dhgx.portal.gateway.model.TicketCreateResponse;
import com.dhgx.portal.gateway.model.TicketInfoDTO;
import com.dhgx.portal.gateway.model.TicketValidateRequest;
import com.dhgx.portal.gateway.model.TicketValidationResponse;
import com.dhgx.portal.gateway.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final TicketProperties ticketProperties;
    private final GatewaySystemProperties systemProperties;

    @Override
    public Mono<TicketCreateResponse> createTicket(TicketCreateRequest request) {
        GatewaySystemProperties.SystemRoute systemRoute = systemProperties.findByCode(request.getSystemCode())
                .orElseThrow(() -> new GatewayException(30001, "目标系统不存在或已停用"));

        String ticket = "TICKET-" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TicketInfoDTO ticketInfo = new TicketInfoDTO();
        ticketInfo.setTicket(ticket);
        ticketInfo.setUserId(request.getUserId());
        ticketInfo.setUsername(request.getUsername());
        ticketInfo.setTenantId(request.getTenantId());
        ticketInfo.setRoles(request.getRoles());
        ticketInfo.setSystemCode(request.getSystemCode());
        ticketInfo.setCreateTime(now);
        ticketInfo.setExpireTime(now.plus(ticketProperties.getTtl()));
        ticketInfo.setUsed(false);
        ticketInfo.setExtInfo(request.getExtInfo());

        String key = ticketProperties.getRedisKeyPrefix() + ticket;
        return serialize(ticketInfo)
                .flatMap(json -> redisTemplate.opsForValue()
                        .set(key, json, ticketProperties.getTtl())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new GatewayException(50000, "保存 ticket 失败")))
                        .thenReturn(new TicketCreateResponse(ticket, determineGatewayBaseUrl(systemRoute))));
    }

    @Override
    public Mono<TicketValidationResponse> validateTicket(TicketValidateRequest request) {
        String key = ticketProperties.getRedisKeyPrefix() + request.getTicket();
        return redisTemplate.opsForValue()
                .get(key)
                .switchIfEmpty(Mono.error(new GatewayException(20001, "ticket 无效或已过期")))
                .flatMap(value -> deserialize(value)
                        .flatMap(ticketInfo -> validateAndConsumeTicket(request, key, ticketInfo)));
    }

    private Mono<TicketValidationResponse> validateAndConsumeTicket(TicketValidateRequest request, String key, TicketInfoDTO ticketInfo) {
        if (ticketInfo.getExpireTime() != null && ticketInfo.getExpireTime().isBefore(LocalDateTime.now())) {
            return Mono.error(new GatewayException(20001, "ticket 无效或已过期"));
        }
        if (Boolean.TRUE.equals(ticketInfo.getUsed())) {
            return Mono.error(new GatewayException(20001, "ticket 无效或已过期"));
        }
        if (!request.getSystemCode().equalsIgnoreCase(ticketInfo.getSystemCode())) {
            return Mono.error(new GatewayException(20002, "ticket 不属于当前系统"));
        }
        return redisTemplate.delete(key)
                .thenReturn(TicketValidationResponse.builder()
                        .userId(ticketInfo.getUserId())
                        .username(ticketInfo.getUsername())
                        .tenantId(ticketInfo.getTenantId())
                        .roles(ticketInfo.getRoles())
                        .extInfo(ticketInfo.getExtInfo())
                        .build());
    }

    private Mono<String> serialize(TicketInfoDTO ticketInfo) {
        try {
            return Mono.just(objectMapper.writeValueAsString(ticketInfo));
        } catch (JsonProcessingException e) {
            return Mono.error(new GatewayException(50000, "序列化 ticket 失败"));
        }
    }

    private Mono<TicketInfoDTO> deserialize(String value) {
        try {
            return Mono.just(objectMapper.readValue(value, TicketInfoDTO.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new GatewayException(50000, "解析 ticket 失败"));
        }
    }

    private String determineGatewayBaseUrl(GatewaySystemProperties.SystemRoute route) {
        if (route != null && StringUtils.hasText(route.getGatewayBaseUrl())) {
            return route.getGatewayBaseUrl();
        }
        return ticketProperties.getDefaultGatewayBaseUrl();
    }
}
