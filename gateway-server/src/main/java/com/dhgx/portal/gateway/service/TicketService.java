package com.dhgx.portal.gateway.service;

import com.dhgx.portal.gateway.model.TicketCreateRequest;
import com.dhgx.portal.gateway.model.TicketCreateResponse;
import com.dhgx.portal.gateway.model.TicketValidateRequest;
import com.dhgx.portal.gateway.model.TicketValidationResponse;
import reactor.core.publisher.Mono;

/**
 * Ticket 服务接口，定义生成与校验票据的能力。
 */
public interface TicketService {

    Mono<TicketCreateResponse> createTicket(TicketCreateRequest request);

    Mono<TicketValidationResponse> validateTicket(TicketValidateRequest request);
}
