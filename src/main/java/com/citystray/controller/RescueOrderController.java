package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.RescueOrder;
import com.citystray.service.RescueOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 救助工单管理控制器
 */
@Api(tags = "救助工单管理")
@RestController
@RequestMapping("/api/rescue")
public class RescueOrderController {

    @Autowired
    private RescueOrderService rescueOrderService;

    /**
     * 分页查询工单列表
     */
    @ApiOperation("分页查询工单列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("工单状态") @RequestParam(required = false) String status,
            @ApiParam("所在区域") @RequestParam(required = false) String district,
            @ApiParam("工单编号") @RequestParam(required = false) String orderNo,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        Map<String, Object> params = Map.of(
                "status", status != null ? status : "",
                "district", district != null ? district : "",
                "orderNo", orderNo != null ? orderNo : "",
                "startTime", startTime != null ? startTime : "",
                "endTime", endTime != null ? endTime : ""
        );
        return Result.success(rescueOrderService.getOrderList(page, size, params));
    }

    /**
     * 获取工单详情
     */
    @ApiOperation("获取工单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("工单ID") @PathVariable Long id) {
        return Result.success(rescueOrderService.getOrderDetail(id));
    }

    /**
     * 获取工单时间线
     */
    @ApiOperation("获取工单时间线")
    @GetMapping("/{id}/timeline")
    public Result<List<Map<String, Object>>> timeline(
            @ApiParam("工单ID") @PathVariable Long id,
            @ApiParam("上报记录ID") @RequestParam Long reportId) {
        return Result.success(rescueOrderService.getOrderTimeline(id, reportId));
    }

    /**
     * 更新工单状态
     */
    @ApiOperation("更新工单状态")
    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String status = body.get("status").toString();
        String description = body.getOrDefault("description", "").toString();
        rescueOrderService.updateStatus(id, status, description);
        return Result.success();
    }

    /**
     * 分配志愿者
     */
    @ApiOperation("分配志愿者")
    @PutMapping("/assign")
    public Result<?> assignVolunteer(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Long volunteerId = Long.valueOf(body.get("volunteerId").toString());
        rescueOrderService.assignVolunteer(orderId, volunteerId);
        return Result.success();
    }

    /**
     * 由上报创建工单
     */
    @ApiOperation("由上报创建工单")
    @PostMapping("/create")
    public Result<Long> createFromReport(
            @ApiParam("上报记录ID") @RequestParam Long reportId) {
        return Result.success(rescueOrderService.createFromReport(reportId));
    }
}
