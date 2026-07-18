package com.citystray.service;

import com.citystray.common.PageResult;
import java.util.Map;

public interface DictService {
    PageResult<Map<String, Object>> list(Integer page, Integer pageSize);
    void save(Map<String, Object> data);
    void deleteById(Long id);
}
