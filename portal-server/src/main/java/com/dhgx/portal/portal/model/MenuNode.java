package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 门户导航菜单节点结构，可组成树形菜单。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuNode {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private List<MenuNode> children;
}
