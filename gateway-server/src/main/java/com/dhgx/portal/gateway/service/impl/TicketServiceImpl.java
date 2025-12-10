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

/**
 * Ticket 领域服务实现，负责生成、校验以及回收一次性票据。
 *
 * 采用 Redis 存储短时凭证，通过 TTL 控制有效期，校验通过后立即删除实现一次性使用。
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final TicketProperties ticketProperties;
    private final GatewaySystemProperties systemProperties;

    @Override
    public Mono<TicketCreateResponse> createTicket(TicketCreateRequest request) {
        // 校验系统编码是否在网关已配置的路由中启用
        GatewaySystemProperties.SystemRoute systemRoute = systemProperties.findByCode(request.getSystemCode())
                .orElseThrow(() -> new GatewayException(30001, "目标系统不存在或已停用"));

        // 生成一次性 ticket，并封装基础信息
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
                // 保存到 Redis，使用 TTL 控制过期时间
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
                // 反序列化后按规则检查有效性，并在校验通过时删除 Redis 记录
                .flatMap(value -> deserialize(value)
                        .flatMap(ticketInfo -> validateAndConsumeTicket(request, key, ticketInfo)));
    }

    /**
     * 校验 ticket 的有效性并删除缓存，实现一次性消费。
     */
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

    /**
     * Ticket 对象序列化为 JSON，方便以字符串形式写入 Redis。
     */
    private Mono<String> serialize(TicketInfoDTO ticketInfo) {
        try {
            return Mono.just(objectMapper.writeValueAsString(ticketInfo));
        } catch (JsonProcessingException e) {
            return Mono.error(new GatewayException(50000, "序列化 ticket 失败"));
        }
    }

    /**
     * 将 Redis 中的 JSON 字符串转换回 Ticket 对象。
     */
    private Mono<TicketInfoDTO> deserialize(String value) {
        try {
            return Mono.just(objectMapper.readValue(value, TicketInfoDTO.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new GatewayException(50000, "解析 ticket 失败"));
        }
    }

    /**
     * 根据路由配置或全局默认值选择网关访问地址，便于门户拼接跳转链接。
     */
    private String determineGatewayBaseUrl(GatewaySystemProperties.SystemRoute route) {
        if (route != null && StringUtils.hasText(route.getGatewayBaseUrl())) {
            return route.getGatewayBaseUrl();
        }
        return ticketProperties.getDefaultGatewayBaseUrl();
    }
}
