package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Animal;
import com.citystray.service.AnimalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 动物管理控制器
 */
@Api(tags = "动物管理")
@RestController
@RequestMapping("/api/animal")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    /**
     * 分页查询动物列表
     */
    @ApiOperation("分页查询动物列表")
    @GetMapping("/list")
    public Result<PageResult<Animal>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("动物名称") @RequestParam(required = false) String name,
            @ApiParam("品种") @RequestParam(required = false) String breed,
            @ApiParam("健康状态") @RequestParam(required = false) String healthStatus,
            @ApiParam("是否绝育") @RequestParam(required = false) Boolean isNeutered) {
        return Result.success(animalService.getAnimalList(page, size, name, breed, healthStatus, isNeutered));
    }

    /**
     * 获取动物详情
     */
    @ApiOperation("获取动物详情")
    @GetMapping("/{id}")
    public Result<Animal> detail(
            @ApiParam("动物ID") @PathVariable Long id) {
        return Result.success(animalService.getAnimalById(id));
    }

    /**
     * 新增动物
     */
    @ApiOperation("新增动物")
    @PostMapping
    public Result<Long> add(@RequestBody Animal animal) {
        return Result.success(animalService.addAnimal(animal));
    }

    /**
     * 更新动物信息
     */
    @ApiOperation("更新动物信息")
    @PutMapping
    public Result<?> update(@RequestBody Animal animal) {
        animalService.updateAnimal(animal);
        return Result.success();
    }

    /**
     * 删除动物（逻辑删除）
     */
    @ApiOperation("删除动物")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @ApiParam("动物ID") @PathVariable Long id) {
        animalService.deleteAnimal(id);
        return Result.success();
    }

    /**
     * 更新动物健康状态
     */
    @ApiOperation("更新动物健康状态")
    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String healthStatus = body.get("healthStatus").toString();
        animalService.updateHealthStatus(id, healthStatus);
        return Result.success();
    }

    /**
     * 获取动物医疗记录
     */
    @ApiOperation("获取动物医疗记录")
    @GetMapping("/{id}/medical")
    public Result<PageResult<Map<String, Object>>> medicalRecords(
            @ApiParam("动物ID") @PathVariable Long id,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(animalService.getMedicalRecords(id, page, size));
    }
}
