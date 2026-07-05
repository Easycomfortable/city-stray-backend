package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务管理控制器
 */
@Api(tags = "任务管理")
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 获取可接任务列表
     */
    @ApiOperation("获取可接任务列表")
    @GetMapping("/available")
    public Result<PageResult<Map<String, Object>>> availableTasks(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("志愿者ID") @RequestParam Long volunteerId) {
        return Result.success(taskService.getAvailableTasks(page, size, volunteerId));
    }

    /**
     * 接单
     */
    @ApiOperation("接单")
    @PostMapping("/accept")
    public Result<?> acceptTask(@RequestBody Map<String, Object> body) {
        Long taskId = Long.valueOf(body.get("taskId").toString());
        Long volunteerId = Long.valueOf(body.get("volunteerId").toString());
        taskService.acceptTask(taskId, volunteerId);
        return Result.success();
    }

    /**
     * 完成任务
     */
    @ApiOperation("完成任务")
    @PostMapping("/complete")
    public Result<?> completeTask(@RequestBody Map<String, Object> body) {
        Long taskId = Long.valueOf(body.get("taskId").toString());
        Long volunteerId = Long.valueOf(body.get("volunteerId").toString());
        String content = body.getOrDefault("content", "").toString();
        String photos = body.getOrDefault("photos", "").toString();
        Double serviceHours = body.get("serviceHours") != null
                ? Double.valueOf(body.get("serviceHours").toString())
                : 0.0;
        taskService.completeTask(taskId, volunteerId, content, photos, serviceHours);
        return Result.success();
    }

    /**
     * 获取我的任务列表
     */
    @ApiOperation("获取我的任务列表")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> myTasks(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("志愿者ID") @RequestParam Long volunteerId,
            @ApiParam("任务状态") @RequestParam(required = false) String status) {
        return Result.success(taskService.getMyTasks(page, size, volunteerId, status));
    }
}
