package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citystray.common.Result;
import com.citystray.entity.AdoptionApply;
import com.citystray.entity.Animal;
import com.citystray.entity.RescueOrder;
import com.citystray.entity.Volunteer;
import com.citystray.mapper.AdoptionApplyMapper;
import com.citystray.mapper.AnimalMapper;
import com.citystray.mapper.RescueOrderMapper;
import com.citystray.mapper.VolunteerMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据看板控制器
 */
@Api(tags = "数据看板")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private RescueOrderMapper rescueOrderMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    /**
     * 获取概览数据
     * 返回：本月救助数、待领养数、本月领养数、志愿者总数
     */
    @ApiOperation("获取概览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();

        // 本月起始时间
        LocalDate now = LocalDate.now();
        LocalDateTime monthStart = LocalDateTime.of(now.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime monthEnd = LocalDateTime.of(now, LocalTime.MAX);

        // 本月救助数
        Long monthlyRescueCount = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .ge("create_time", monthStart)
                        .le("create_time", monthEnd)
        );
        data.put("monthlyRescueCount", monthlyRescueCount);

        // 待领养数
        Long pendingAdoptionCount = animalMapper.selectCount(
                new QueryWrapper<Animal>()
                        .eq("adoption_status", "待领养")
        );
        data.put("pendingAdoptionCount", pendingAdoptionCount);

        // 本月领养数
        Long monthlyAdoptionCount = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .eq("stage", "已完成")
                        .ge("update_time", monthStart)
                        .le("update_time", monthEnd)
        );
        data.put("monthlyAdoptionCount", monthlyAdoptionCount);

        // 志愿者总数
        Long volunteerCount = volunteerMapper.selectCount(
                new QueryWrapper<Volunteer>()
                        .eq("auth_status", "已认证")
        );
        data.put("volunteerCount", volunteerCount);

        return Result.success(data);
    }

    /**
     * 获取近7天救助趋势
     */
    @ApiOperation("获取近7天救助趋势")
    @GetMapping("/rescue-trend")
    public Result<List<Map<String, Object>>> rescueTrend() {
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            Long count = rescueOrderMapper.selectCount(
                    new QueryWrapper<RescueOrder>()
                            .ge("create_time", dayStart)
                            .le("create_time", dayEnd)
            );

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 获取各区域救助统计
     */
    @ApiOperation("获取各区域救助统计")
    @GetMapping("/area-stats")
    public Result<List<Map<String, Object>>> areaStats() {
        // 查询所有工单，按区域分组统计
        List<RescueOrder> orders = rescueOrderMapper.selectList(
                new QueryWrapper<RescueOrder>().select("district")
        );

        Map<String, Long> grouped = orders.stream()
                .filter(o -> o.getDistrict() != null && !o.getDistrict().isEmpty())
                .collect(Collectors.groupingBy(RescueOrder::getDistrict, Collectors.counting()));

        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("district", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取领养状态分布
     */
    @ApiOperation("获取领养状态分布")
    @GetMapping("/adoption-stats")
    public Result<List<Map<String, Object>>> adoptionStats() {
        List<AdoptionApply> applications = adoptionApplyMapper.selectList(
                new QueryWrapper<AdoptionApply>().select("stage")
        );

        Map<String, Long> grouped = applications.stream()
                .filter(a -> a.getStage() != null && !a.getStage().isEmpty())
                .collect(Collectors.groupingBy(AdoptionApply::getStage, Collectors.counting()));

        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("stage", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }
}
