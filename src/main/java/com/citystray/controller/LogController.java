package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "日志管理")
public class LogController {

    private final LogService logService;

    @GetMapping("/list")
    @ApiOperation("分页查询日志列表")
    public Result<?> list(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("日志类型") @RequestParam(required = false) String type,
            @ApiParam("日期范围") @RequestParam(required = false) String dateRange,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<Map<String, Object>> pageResult = logService.list(keyword, type, dateRange, page, pageSize);
        return Result.success(pageResult);
    }
}
