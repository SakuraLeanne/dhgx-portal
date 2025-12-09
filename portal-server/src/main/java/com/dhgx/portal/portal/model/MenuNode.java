package com.dhgx.portal.portal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Menu node used for building the portal navigation tree.
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
