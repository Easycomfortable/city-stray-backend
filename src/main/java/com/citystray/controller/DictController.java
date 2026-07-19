package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.annotation.OperationLog;
import com.citystray.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/data/{code}")
    @ApiOperation("按字典编码获取字典项")
    public Result<?> getDataByCode(
            @ApiParam("字典编码") @PathVariable String code) {
        List<Map<String, Object>> items = dictService.getItemsByCode(code);
        return Result.success(items);
    }

    @PostMapping("/save")
    @OperationLog(module = "字典管理", type = "CREATE", content = "保存字典")
    @ApiOperation("保存/更新字典")
    public Result<?> save(@RequestBody Map<String, Object> data) {
        dictService.save(data);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "字典管理", type = "DELETE", content = "删除字典")
    @ApiOperation("删除字典")
    public Result<?> delete(@ApiParam("字典ID") @PathVariable Long id) {
        dictService.deleteById(id);
        return Result.success();
    }
}
