package com.citystray.service;

import com.citystray.common.PageResult;
import com.citystray.entity.SysLog;
import java.util.Map;

public interface LogService {
    PageResult<Map<String, Object>> list(String keyword, String type, String dateRange, Integer page, Integer pageSize);
    void save(SysLog sysLog);
}
