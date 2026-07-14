package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.entity.SysLog;
import com.citystray.mapper.SysLogMapper;
import com.citystray.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final SysLogMapper sysLogMapper;

    @Override
    public PageResult<Map<String, Object>> list(String keyword, String type, Integer page, Integer pageSize) {
        Page<SysLog> pageParam = new Page<>(page, pageSize);

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        // 类型过滤
        if (StringUtils.hasText(type)) {
            wrapper.eq(SysLog::getType, type);
        }

        // 关键字搜索（匹配用户名或内容）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysLog::getUsername, keyword)
                    .or()
                    .like(SysLog::getContent, keyword)
            );
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysLog::getCreateTime);

        Page<SysLog> logPage = sysLogMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (SysLog sysLog : logPage.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", sysLog.getId());
            map.put("username", sysLog.getUsername());
            map.put("type", sysLog.getType());
            map.put("content", sysLog.getContent());
            map.put("ip", sysLog.getIp());
            map.put("createTime", sysLog.getCreateTime());
            records.add(map);
        }

        return new PageResult<>(logPage.getTotal(), records);
    }

    @Override
    public void save(SysLog sysLog) {
        if (sysLog.getCreateTime() == null) {
            sysLog.setCreateTime(LocalDateTime.now());
        }
        sysLogMapper.insert(sysLog);
    }
}
