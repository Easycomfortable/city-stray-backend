package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.common.PageResult;
import com.citystray.entity.SysRole;
import com.citystray.entity.User;
import com.citystray.mapper.SysRoleMapper;
import com.citystray.mapper.SysRoleMenuMapper;
import com.citystray.mapper.UserMapper;
import com.citystray.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Map<String, Object>> list(Integer page, Integer pageSize) {
        // 查询所有角色（@TableLogic 自动过滤已删除记录）
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getId);
        List<SysRole> roles = sysRoleMapper.selectList(wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (SysRole role : roles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", role.getId());
            map.put("name", role.getName());
            map.put("code", role.getCode());
            map.put("description", role.getDescription());

            // 查询拥有该角色的用户数量
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getRole, role.getCode());
            Long userCount = userMapper.selectCount(userWrapper);
            map.put("userCount", userCount);

            // 查询该角色的菜单权限 ID 列表
            List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(role.getId());
            map.put("permissions", menuIds);

            records.add(map);
        }

        // 手动分页
        int total = records.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Map<String, Object>> pageRecords = fromIndex < total
                ? records.subList(fromIndex, toIndex)
                : Collections.emptyList();

        return new PageResult<>((long) total, pageRecords);
    }

    @Override
    public void save(SysRole role) {
        if (role.getId() == null) {
            role.setCreateTime(LocalDateTime.now());
            sysRoleMapper.insert(role);
        } else {
            role.setUpdateTime(LocalDateTime.now());
            sysRoleMapper.updateById(role);
        }
    }

    @Override
    public void deleteById(Long id) {
        sysRoleMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermissions(Long id, List<Long> menuIds) {
        // 删除该角色原有的菜单关联
        sysRoleMenuMapper.deleteByRoleId(id);
        // 插入新的菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMenuMapper.batchInsert(id, menuIds);
        }
    }
}
