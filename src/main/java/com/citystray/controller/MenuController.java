package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.common.Result;
import com.citystray.entity.SysMenu;
import com.citystray.mapper.SysMenuMapper;
import com.citystray.annotation.RequireRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "菜单管理")
@RequireRole({"admin"})
public class MenuController {

    private final SysMenuMapper sysMenuMapper;

    @GetMapping("/tree")
    @ApiOperation("获取菜单树")
    public Result<?> tree() {
        // 查询所有菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);

        // 构建树形结构
        List<Map<String, Object>> tree = buildTree(allMenus, 0L);
        return Result.success(tree);
    }

    @PostMapping("/save")
    @ApiOperation("保存/更新菜单")
    public Result<?> save(@RequestBody SysMenu menu) {
        if (menu.getId() == null) {
            menu.setCreateTime(LocalDateTime.now());
            sysMenuMapper.insert(menu);
        } else {
            menu.setUpdateTime(LocalDateTime.now());
            sysMenuMapper.updateById(menu);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除菜单")
    public Result<?> delete(@ApiParam("菜单ID") @PathVariable Long id) {
        sysMenuMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 递归构建菜单树
     */
    private List<Map<String, Object>> buildTree(List<SysMenu> allMenus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            Long menuParentId = menu.getParentId() != null ? menu.getParentId() : 0L;
            if (menuParentId.equals(parentId)) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getName());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("icon", menu.getIcon());
                node.put("sort", menu.getSort());
                node.put("type", menu.getType());
                node.put("permission", menu.getPermission());
                node.put("parentId", menu.getParentId());

                // 递归构建子菜单
                List<Map<String, Object>> children = buildTree(allMenus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }

        return tree;
    }
}
