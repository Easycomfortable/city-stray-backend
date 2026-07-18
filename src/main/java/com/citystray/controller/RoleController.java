package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.SysRole;
import com.citystray.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "角色管理")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @ApiOperation("分页查询角色列表")
    public Result<?> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<Map<String, Object>> pageResult = roleService.list(page, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping("/save")
    @ApiOperation("保存/更新角色")
    public Result<?> save(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除角色")
    public Result<?> delete(@ApiParam("角色ID") @PathVariable Long id) {
        roleService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/permissions")
    @ApiOperation("更新角色权限")
    public Result<?> updatePermissions(
            @ApiParam("角色ID") @PathVariable Long id,
            @RequestBody Map<String, List<Long>> body) {

        List<Long> permissions = body.get("permissions");
        roleService.updatePermissions(id, permissions);
        return Result.success();
    }
}
