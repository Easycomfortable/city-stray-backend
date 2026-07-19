package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.StrayReport;
import com.citystray.service.RescueOrderService;
import com.citystray.service.StrayReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流浪动物上报控制器
 */
@Api(tags = "流浪动物上报管理")
@RestController
@RequestMapping("/api/report")
@Slf4j
public class StrayReportController {

    @Autowired
    private StrayReportService strayReportService;

    @Autowired
    private RescueOrderService rescueOrderService;

    /**
     * 上报流浪动物（同时创建救助工单）
     */
    @ApiOperation("上报流浪动物")
    @PostMapping
    public Result<Long> report(@RequestBody StrayReport strayReport) {
        Long reportId = strayReportService.submitReport(strayReport);
        // 救助工单创建放在独立事务中，失败不影响上报记录
        try {
            rescueOrderService.createFromReport(reportId);
            log.info("上报ID {} 自动生成救助工单", reportId);
        } catch (Exception e) {
            log.error("自动生成救助工单失败, 上报ID: {}, 错误: {}", reportId, e.getMessage());
        }
        return Result.success(reportId);
    }

    /**
     * 查询上报记录
     */
    @ApiOperation("查询上报记录")
    @GetMapping("/list")
    public Result<PageResult<StrayReport>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("上报人ID") @RequestParam(required = false) Long userId,
            @ApiParam("处理状态") @RequestParam(required = false) String status,
            @ApiParam("所在区域") @RequestParam(required = false) String district) {
        return Result.success(strayReportService.getReportList(page, size, userId, status, district));
    }

    /**
     * 获取上报详情
     */
    @ApiOperation("获取上报详情")
    @GetMapping("/{id}")
    public Result<StrayReport> detail(
            @ApiParam("上报记录ID") @PathVariable Long id) {
        return Result.success(strayReportService.getReportById(id));
    }
}
