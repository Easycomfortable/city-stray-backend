package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.common.Result;
import com.citystray.annotation.OperationLog;
import com.citystray.entity.SysMenu;
import com.citystray.entity.SysRole;
import com.citystray.mapper.SysMenuMapper;
import com.citystray.mapper.SysRoleMapper;
import com.citystray.mapper.SysRoleMenuMapper;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "菜单管理")
public class MenuController {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

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

    @GetMapping("/user-menus")
    @ApiOperation("获取当前用户菜单树（按角色过滤）")
    public Result<?> userMenus() {
        // 查询所有可见菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);

        // 尝试按角色过滤
        String roleCode = UserContext.getRole();
        if (roleCode != null) {
            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(SysRole::getCode, roleCode);
            SysRole role = sysRoleMapper.selectOne(roleWrapper);

            if (role != null) {
                List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(role.getId());
                if (menuIds != null && !menuIds.isEmpty()) {
                    Set<Long> menuIdSet = new HashSet<>(menuIds);
                    // 保留有权限的菜单 + 有子菜单的父级目录
                    Set<Long> parentIds = allMenus.stream()
                            .filter(m -> menuIdSet.contains(m.getId()))
                            .map(m -> m.getParentId() != null ? m.getParentId() : 0L)
                            .filter(pid -> pid != 0L)
                            .collect(Collectors.toSet());
                    menuIdSet.addAll(parentIds);

                    allMenus = allMenus.stream()
                            .filter(m -> menuIdSet.contains(m.getId()))
                            .collect(Collectors.toList());
                }
            }
        }

        List<Map<String, Object>> tree = buildTree(allMenus, 0L);
        return Result.success(tree);
    }

    @PostMapping("/save")
    @OperationLog(module = "菜单管理", type = "CREATE", content = "保存菜单")
    @ApiOperation("保存/更新菜单")
    public Result<?> save(@RequestBody SysMenu menu) {
        if (menu.getId() == null) {
            menu.setCreateTime(LocalDateTime.now());
            sysMenuMapper.insert(menu);
            // 新菜单自动分配给超级管理员(role_id=1)
            try {
                sysRoleMenuMapper.batchInsert(1L, Collections.singletonList(menu.getId()));
            } catch (Exception e) {
                log.warn("自动分配管理员菜单失败: {}", e.getMessage());
            }
        } else {
            menu.setUpdateTime(LocalDateTime.now());
            sysMenuMapper.updateById(menu);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "菜单管理", type = "DELETE", content = "删除菜单")
    @ApiOperation("删除菜单")
    public Result<?> delete(@ApiParam("菜单ID") @PathVariable Long id) {
        sysMenuMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 递归构建菜单树（含循环引用检测）
     */
    private List<Map<String, Object>> buildTree(List<SysMenu> allMenus, Long parentId) {
        return buildTree(allMenus, parentId, new HashSet<>());
    }

    private List<Map<String, Object>> buildTree(List<SysMenu> allMenus, Long parentId, Set<Long> visited) {
        List<Map<String, Object>> tree = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            Long menuParentId = menu.getParentId() != null ? menu.getParentId() : 0L;
            if (menuParentId.equals(parentId) && !visited.contains(menu.getId())) {
                visited.add(menu.getId());
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
                List<Map<String, Object>> children = buildTree(allMenus, menu.getId(), visited);
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }

        return tree;
    }
}
