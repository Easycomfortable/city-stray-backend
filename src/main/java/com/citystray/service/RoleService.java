package com.citystray.service;

import com.citystray.common.PageResult;
import com.citystray.entity.SysRole;
import java.util.List;
import java.util.Map;

public interface RoleService {
    PageResult<Map<String, Object>> list(Integer page, Integer pageSize);
    void save(SysRole role);
    void deleteById(Long id);
    void updatePermissions(Long id, List<Long> menuIds);
}
