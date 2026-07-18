package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "字典管理")
public class DictController {

    private final DictService dictService;

    @GetMapping("/list")
    @ApiOperation("分页查询字典列表")
    public Result<?> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<Map<String, Object>> pageResult = dictService.list(page, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping("/save")
    @ApiOperation("保存/更新字典")
    public Result<?> save(@RequestBody Map<String, Object> data) {
        dictService.save(data);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除字典")
    public Result<?> delete(@ApiParam("字典ID") @PathVariable Long id) {
        dictService.deleteById(id);
        return Result.success();
    }
}
