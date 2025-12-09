package com.dhgx.portal.gateway.controller;

import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.gateway.config.TicketProperties;
import com.dhgx.portal.gateway.exception.GatewayException;
import com.dhgx.portal.gateway.model.TicketCreateRequest;
import com.dhgx.portal.gateway.model.TicketCreateResponse;
import com.dhgx.portal.gateway.model.TicketValidateRequest;
import com.dhgx.portal.gateway.model.TicketValidationResponse;
import com.dhgx.portal.gateway.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/sso/ticket")
@RequiredArgsConstructor
@Validated
public class SsoTicketController {

    private final TicketService ticketService;
    private final TicketProperties ticketProperties;

    @PostMapping("/create")
    public Mono<ApiResponse<TicketCreateResponse>> create(@RequestHeader(value = "X-Inner-Secret", required = false) String innerSecret,
                                                          @Valid @RequestBody TicketCreateRequest request) {
        if (ticketProperties.isEnforceInnerSecret()) {
            if (innerSecret == null || !ticketProperties.getInnerSecret().equals(innerSecret)) {
                return Mono.just(ApiResponse.failure(40001, "非法的内部调用"));
            }
        }
        return ticketService.createTicket(request)
                .map(ApiResponse::success);
    }

    @PostMapping("/validate")
    public Mono<ApiResponse<TicketValidationResponse>> validate(@Valid @RequestBody TicketValidateRequest request) {
        return ticketService.validateTicket(request)
                .map(ApiResponse::success)
                .onErrorResume(GatewayException.class, ex -> Mono.just(ApiResponse.failure(ex.getCode(), ex.getMessage())));
    }
}
