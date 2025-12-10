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

/**
 * 门户对外 REST 接口，提供系统列表、菜单以及跳转链接能力。
 */
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Validated
public class PortalController {

    private final PortalService portalService;

    @GetMapping("/systems")
    public ApiResponse<List<PortalSystem>> systems() {
        // 模拟从后端数据源获取系统列表，当前使用内存数据
        return ApiResponse.success(portalService.listSystems());
    }

    @GetMapping("/menus")
    public ApiResponse<List<MenuNode>> menus() {
        // 返回门户菜单树结构
        return ApiResponse.success(portalService.listMenus());
    }

    @PostMapping("/jump-url")
    public ApiResponse<JumpUrlResponse> jumpUrl(@Valid @RequestBody JumpUrlRequest request) {
        // 根据请求组装携带 ticket 的网关跳转链接
        return ApiResponse.success(portalService.buildJumpUrl(request));
    }
}
