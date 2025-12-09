package com.dhgx.portal.portal.controller;

import com.dhgx.portal.common.model.ApiResponse;
import com.dhgx.portal.portal.model.JumpUrlRequest;
import com.dhgx.portal.portal.model.JumpUrlResponse;
import com.dhgx.portal.portal.model.MenuNode;
import com.dhgx.portal.portal.model.PortalSystem;
import com.dhgx.portal.portal.service.PortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Validated
public class PortalController {

    private final PortalService portalService;

    @GetMapping("/systems")
    public ApiResponse<List<PortalSystem>> systems() {
        return ApiResponse.success(portalService.listSystems());
    }

    @GetMapping("/menus")
    public ApiResponse<List<MenuNode>> menus() {
        return ApiResponse.success(portalService.listMenus());
    }

    @PostMapping("/jump-url")
    public ApiResponse<JumpUrlResponse> jumpUrl(@Valid @RequestBody JumpUrlRequest request) {
        return ApiResponse.success(portalService.buildJumpUrl(request));
    }
}
