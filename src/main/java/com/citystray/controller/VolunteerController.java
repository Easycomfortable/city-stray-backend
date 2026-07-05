package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Volunteer;
import com.citystray.service.VolunteerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 志愿者管理控制器
 */
@Api(tags = "志愿者管理")
@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 分页查询志愿者列表
     */
    @ApiOperation("分页查询志愿者列表")
    @GetMapping("/list")
    public Result<PageResult<Volunteer>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("认证状态") @RequestParam(required = false) String authStatus,
            @ApiParam("关键词搜索") @RequestParam(required = false) String keyword,
            @ApiParam("技能标签") @RequestParam(required = false) String skillTag) {
        return Result.success(volunteerService.getVolunteerList(page, size, authStatus, keyword, skillTag));
    }

    /**
     * 审核志愿者认证
     */
    @ApiOperation("审核志愿者认证")
    @PutMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String authStatus = body.get("authStatus").toString();
        String rejectReason = body.getOrDefault("rejectReason", "").toString();
        volunteerService.reviewVolunteer(id, authStatus, rejectReason);
        return Result.success();
    }

    /**
     * 获取志愿者详情
     */
    @ApiOperation("获取志愿者详情")
    @GetMapping("/{id}")
    public Result<Volunteer> detail(
            @ApiParam("志愿者ID") @PathVariable Long id) {
        return Result.success(volunteerService.getVolunteerById(id));
    }

    /**
     * 获取积分记录
     */
    @ApiOperation("获取积分记录")
    @GetMapping("/points")
    public Result<PageResult<Map<String, Object>>> pointsList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("志愿者ID") @RequestParam(required = false) Long volunteerId) {
        return Result.success(volunteerService.getPointsRecords(page, size, volunteerId));
    }

    /**
     * 添加积分
     */
    @ApiOperation("添加积分")
    @PostMapping("/points")
    public Result<?> addPoints(@RequestBody Map<String, Object> body) {
        Long volunteerId = Long.valueOf(body.get("volunteerId").toString());
        Integer points = Integer.valueOf(body.get("points").toString());
        String reason = body.getOrDefault("reason", "").toString();
        volunteerService.addPoints(volunteerId, points, reason);
        return Result.success();
    }

    /**
     * 更新志愿者信息
     */
    @ApiOperation("更新志愿者信息")
    @PutMapping
    public Result<?> update(@RequestBody Volunteer volunteer) {
        volunteerService.updateVolunteer(volunteer);
        return Result.success();
    }
}
