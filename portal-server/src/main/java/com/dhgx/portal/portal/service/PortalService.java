package com.dhgx.portal.portal.service;

import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ErrorCode;
import com.dhgx.portal.portal.model.JumpUrlRequest;
import com.dhgx.portal.portal.model.JumpUrlResponse;
import com.dhgx.portal.portal.model.MenuNode;
import com.dhgx.portal.portal.model.PortalSystem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PortalService {

    public List<PortalSystem> listSystems() {
        // TODO replace mock data with database query
        return Arrays.asList(
                PortalSystem.builder()
                        .id(1L)
                        .code("oa")
                        .name("协同办公")
                        .baseUrl("https://oa.example.com")
                        .description("办公自动化平台")
                        .build(),
                PortalSystem.builder()
                        .id(2L)
                        .code("bi")
                        .name("BI 分析")
                        .baseUrl("https://bi.example.com")
                        .description("数据可视化与分析")
                        .build()
        );
    }

    public List<MenuNode> listMenus() {
        // TODO replace mock data with database query
        MenuNode home = MenuNode.builder()
                .id(1L)
                .name("首页")
                .path("/home")
                .children(Collections.emptyList())
                .build();

        MenuNode apps = MenuNode.builder()
                .id(2L)
                .name("应用中心")
                .path("/apps")
                .children(Collections.emptyList())
                .build();

        MenuNode admin = MenuNode.builder()
                .id(3L)
                .name("管理")
                .path("/admin")
                .children(Arrays.asList(
                        MenuNode.builder()
                                .id(4L)
                                .parentId(3L)
                                .name("用户管理")
                                .path("/admin/users")
                                .children(Collections.emptyList())
                                .build(),
                        MenuNode.builder()
                                .id(5L)
                                .parentId(3L)
                                .name("角色管理")
                                .path("/admin/roles")
                                .children(Collections.emptyList())
                                .build()
                ))
                .build();

        List<MenuNode> roots = new ArrayList<>();
        roots.add(home);
        roots.add(apps);
        roots.add(admin);
        return roots;
    }

    public JumpUrlResponse buildJumpUrl(JumpUrlRequest request) {
        PortalSystem system = listSystems().stream()
                .filter(it -> it.getCode().equalsIgnoreCase(request.getSystemCode()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "未找到对应的业务系统"));

        // TODO call gateway to create a one-time ticket and sign the jump URL
        String ticket = "ticket-" + UUID.randomUUID();
        String normalizedPath = request.getTargetPath().startsWith("/") ? request.getTargetPath() : "/" + request.getTargetPath();
        String baseUrl = system.getBaseUrl().endsWith("/")
                ? system.getBaseUrl().substring(0, system.getBaseUrl().length() - 1)
                : system.getBaseUrl();
        String url = baseUrl + normalizedPath + "?ticket=" + ticket;

        return JumpUrlResponse.builder()
                .url(url)
                .ticket(ticket)
                .build();
    }
}
