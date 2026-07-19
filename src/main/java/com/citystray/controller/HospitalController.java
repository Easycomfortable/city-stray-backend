package com.citystray.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.annotation.OperationLog;
import com.citystray.entity.Hospital;
import com.citystray.entity.RescueOrder;
import com.citystray.mapper.HospitalMapper;
import com.citystray.mapper.RescueOrderMapper;
import com.citystray.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合作医院管理控制器
 */
@Api(tags = "合作医院管理")
@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private RescueOrderMapper rescueOrderMapper;

    /**
     * 分页查询合作医院列表
     * 前端字段映射：serviceHours="", certified=status==1, monthlyPatients=monthlyVisits, specialties=discountInfo
     */
    @ApiOperation("分页查询合作医院列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("搜索关键词(名称或地址)") @RequestParam(required = false) String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        QueryWrapper<Hospital> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("address", keyword));
        }
        wrapper.orderByDesc("create_time");

        IPage<Hospital> pageResult = hospitalService.page(new Page<>(page, pageSize), wrapper);

        List<Map<String, Object>> records = pageResult.getRecords().stream()
                .map(this::toListMap)
                .collect(Collectors.toList());

        return Result.success(new PageResult<>(pageResult.getTotal(), records));
    }

    /**
     * 获取医院详情
     * 前端字段映射：增加 monthlyCost=0
     */
    @ApiOperation("获取医院详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("医院ID") @PathVariable Long id) {
        Hospital hospital = hospitalService.getById(id);
        if (hospital == null) {
            return Result.success(null);
        }
        return Result.success(toDetailMap(hospital));
    }

    /**
     * 新增医院
     */
    @OperationLog(module = "合作医院", type = "CREATE", content = "新增合作医院")
    @ApiOperation("新增医院")
    @PostMapping("/save")
    public Result<?> save(@RequestBody Hospital hospital) {
        hospitalService.save(hospital);
        return Result.success(null);
    }

    /**
     * 更新医院信息
     */
    @OperationLog(module = "合作医院", type = "UPDATE", content = "更新医院信息")
    @ApiOperation("更新医院信息")
    @PutMapping("/{id}")
    public Result<?> update(
            @ApiParam("医院ID") @PathVariable Long id,
            @RequestBody Hospital hospital) {
        hospital.setId(id);
        hospitalService.updateById(hospital);
        return Result.success(null);
    }

    /**
     * 删除医院（逻辑删除）
     */
    @OperationLog(module = "合作医院", type = "DELETE", content = "删除合作医院")
    @ApiOperation("删除医院")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @ApiParam("医院ID") @PathVariable Long id) {
        hospitalService.removeById(id);
        return Result.success(null);
    }

    /**
     * 获取医院收费项目列表
     * 从hospital表的price_standard字段解析JSON数组
     */
    @ApiOperation("获取医院收费项目列表")
    @GetMapping("/{id}/items")
    public Result<List<Map<String, Object>>> items(
            @ApiParam("医院ID") @PathVariable Long id) {
        Hospital hospital = hospitalService.getById(id);
        if (hospital == null) {
            return Result.success(new ArrayList<>());
        }

        String priceStandard = hospital.getPriceStandard();
        if (priceStandard == null || priceStandard.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 解析priceStandard JSON数组: [{"name":"绝育手术","price":200,"unit":"次"},...]
        List<Map<String, Object>> items = new ArrayList<>();
        try {
            JSONArray arr = JSONUtil.parseArray(priceStandard);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Map<String, Object> item = new HashMap<>();
                item.put("id", (long) (i + 1));
                item.put("hospitalId", id);
                item.put("name", obj.getStr("name", ""));
                item.put("price", obj.getBigDecimal("price"));
                item.put("unit", obj.getStr("unit", "次"));
                item.put("remark", obj.getStr("remark", ""));
                items.add(item);
            }
        } catch (Exception e) {
            // priceStandard不是合法JSON，返回空列表
        }

        return Result.success(items);
    }

    /**
     * 新增/保存医院收费项目
     * 将新项目追加到hospital的price_standard JSON数组中
     */
    @OperationLog(module = "合作医院", type = "CREATE", content = "新增收费项目")
    @ApiOperation("新增医院收费项目")
    @PostMapping("/item/save")
    public Result<?> saveItem(@RequestBody Map<String, Object> body) {
        Long hospitalId = Long.valueOf(body.get("hospitalId").toString());
        Hospital hospital = hospitalService.getById(hospitalId);
        if (hospital == null) {
            return Result.error("医院不存在");
        }

        // 解析现有的priceStandard
        JSONArray arr;
        String priceStandard = hospital.getPriceStandard();
        if (priceStandard != null && !priceStandard.isEmpty()) {
            try {
                arr = JSONUtil.parseArray(priceStandard);
            } catch (Exception e) {
                arr = new JSONArray();
            }
        } else {
            arr = new JSONArray();
        }

        // 构建新项目
        JSONObject newItem = new JSONObject();
        newItem.set("name", body.getOrDefault("name", ""));
        newItem.set("price", body.getOrDefault("price", 0));
        newItem.set("unit", body.getOrDefault("unit", "次"));
        newItem.set("remark", body.getOrDefault("remark", ""));
        arr.add(newItem);

        // 写回数据库
        hospital.setPriceStandard(arr.toString());
        hospitalService.updateById(hospital);

        return Result.success(null);
    }

    /**
     * 获取医院统计数据
     * 查询rescue_order表统计该医院的就诊数据
     */
    @ApiOperation("获取医院统计数据")
    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> stats(
            @ApiParam("医院ID") @PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();

        // 本月就诊量：本月关联该医院的工单数
        LocalDate now = LocalDate.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDateTime.of(now, LocalTime.MAX);

        Long monthlyPatients = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .eq("hospital_id", id)
                        .ge("create_time", monthStart)
                        .le("create_time", monthEnd)
        );
        data.put("monthlyPatients", monthlyPatients != null ? monthlyPatients.intValue() : 0);

        // 累计就诊量：关联该医院的所有工单数
        Long totalOrders = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .eq("hospital_id", id)
        );
        data.put("totalOrders", totalOrders != null ? totalOrders.intValue() : 0);

        // 治疗中工单数
        Long treatingCount = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .eq("hospital_id", id)
                        .in("status", java.util.Arrays.asList("treating", "recovering"))
        );
        data.put("treatingCount", treatingCount != null ? treatingCount.intValue() : 0);

        // 月度费用（暂取monthlyVisits * 150估算，无实际费用字段）
        Hospital hospital = hospitalService.getById(id);
        int monthlyVisits = hospital != null && hospital.getMonthlyVisits() != null ? hospital.getMonthlyVisits() : 0;
        data.put("monthlyCost", monthlyVisits * 150);

        return Result.success(data);
    }

    // ========== 私有映射方法 ==========

    /**
     * 实体 → 列表前端字段映射
     */
    private Map<String, Object> toListMap(Hospital h) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", h.getId());
        map.put("name", h.getName());
        map.put("address", h.getAddress());
        map.put("phone", h.getPhone());
        map.put("serviceHours", "");
        map.put("certified", h.getStatus() != null && h.getStatus() == 1);
        map.put("monthlyPatients", h.getMonthlyVisits() != null ? h.getMonthlyVisits() : 0);
        map.put("specialties", h.getDiscountInfo());
        map.put("longitude", h.getLongitude());
        map.put("latitude", h.getLatitude());
        map.put("district", h.getDistrict());
        return map;
    }

    /**
     * 实体 → 详情前端字段映射（含 monthlyCost）
     */
    private Map<String, Object> toDetailMap(Hospital h) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", h.getId());
        map.put("name", h.getName());
        map.put("address", h.getAddress());
        map.put("phone", h.getPhone());
        map.put("serviceHours", "");
        map.put("certified", h.getStatus() != null && h.getStatus() == 1);
        map.put("monthlyPatients", h.getMonthlyVisits() != null ? h.getMonthlyVisits() : 0);
        map.put("monthlyCost", 0);
        map.put("specialties", h.getDiscountInfo());
        map.put("longitude", h.getLongitude());
        map.put("latitude", h.getLatitude());
        return map;
    }
}
