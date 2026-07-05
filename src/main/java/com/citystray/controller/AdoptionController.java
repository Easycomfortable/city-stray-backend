package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.service.AdoptionApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 领养管理控制器
 */
@Api(tags = "领养管理")
@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionApplyService adoptionService;

    /**
     * 分页查询领养申请列表
     */
    @ApiOperation("分页查询领养申请列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("审核阶段") @RequestParam(required = false) String stage,
            @ApiParam("申请人姓名") @RequestParam(required = false) String realName) {
        return Result.success(adoptionService.getAdoptionList(page, size, stage, realName));
    }

    /**
     * 获取领养申请详情
     */
    @ApiOperation("获取领养申请详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("领养申请ID") @PathVariable Long id) {
        return Result.success(adoptionService.getAdoptionDetail(id));
    }

    /**
     * 审核领养申请
     */
    @ApiOperation("审核领养申请")
    @PutMapping("/review")
    public Result<?> review(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String stage = body.get("stage").toString();
        String remark = body.getOrDefault("remark", "").toString();
        String rejectReason = body.getOrDefault("rejectReason", "").toString();
        adoptionService.reviewAdoption(id, stage, remark, rejectReason);
        return Result.success();
    }

    /**
     * 安排家访
     */
    @ApiOperation("安排家访")
    @PostMapping("/visit")
    public Result<?> arrangeVisit(@RequestBody Map<String, Object> body) {
        Long applyId = Long.valueOf(body.get("applyId").toString());
        String visitorName = body.get("visitorName").toString();
        String visitDate = body.get("visitDate").toString();
        String notes = body.getOrDefault("notes", "").toString();
        adoptionService.arrangeVisit(applyId, visitorName, visitDate, notes);
        return Result.success();
    }

    /**
     * 确认正式领养
     */
    @ApiOperation("确认正式领养")
    @PutMapping("/confirm")
    public Result<?> confirmAdoption(@RequestBody Map<String, Object> body) {
        Long applyId = Long.valueOf(body.get("applyId").toString());
        adoptionService.confirmAdoption(applyId);
        return Result.success();
    }

    /**
     * 获取家访记录
     */
    @ApiOperation("获取家访记录")
    @GetMapping("/{id}/visits")
    public Result<List<Map<String, Object>>> getVisits(
            @ApiParam("领养申请ID") @PathVariable Long id) {
        return Result.success(adoptionService.getVisitRecords(id));
    }

    /**
     * 获取领养协议
     */
    @ApiOperation("获取领养协议")
    @GetMapping("/{id}/agreement")
    public Result<Map<String, Object>> getAgreement(
            @ApiParam("领养申请ID") @PathVariable Long id) {
        return Result.success(adoptionService.getAgreement(id));
    }
}
