package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Hospital;
import com.citystray.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 合作医院管理控制器
 */
@Api(tags = "合作医院管理")
@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 分页查询合作医院列表
     */
    @ApiOperation("分页查询合作医院列表")
    @GetMapping("/list")
    public Result<PageResult<Hospital>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("医院名称") @RequestParam(required = false) String name,
            @ApiParam("所在区域") @RequestParam(required = false) String district,
            @ApiParam("合作状态") @RequestParam(required = false) String status) {
        return Result.success(hospitalService.getHospitalList(page, size, name, district, status));
    }

    /**
     * 获取医院详情
     */
    @ApiOperation("获取医院详情")
    @GetMapping("/{id}")
    public Result<Hospital> detail(
            @ApiParam("医院ID") @PathVariable Long id) {
        return Result.success(hospitalService.getHospitalById(id));
    }

    /**
     * 新增医院
     */
    @ApiOperation("新增医院")
    @PostMapping
    public Result<Long> add(@RequestBody Hospital hospital) {
        return Result.success(hospitalService.addHospital(hospital));
    }

    /**
     * 更新医院信息
     */
    @ApiOperation("更新医院信息")
    @PutMapping
    public Result<?> update(@RequestBody Hospital hospital) {
        hospitalService.updateHospital(hospital);
        return Result.success();
    }

    /**
     * 删除医院（逻辑删除）
     */
    @ApiOperation("删除医院")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @ApiParam("医院ID") @PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return Result.success();
    }

    /**
     * 更新合作状态
     */
    @ApiOperation("更新合作状态")
    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String status = body.get("status").toString();
        hospitalService.updateStatus(id, status);
        return Result.success();
    }
}
