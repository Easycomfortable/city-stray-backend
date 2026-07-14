package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citystray.common.Result;
import com.citystray.entity.AdoptionApply;
import com.citystray.entity.DonationRecord;
import com.citystray.entity.RescueOrder;
import com.citystray.entity.StrayReport;
import com.citystray.mapper.AdoptionApplyMapper;
import com.citystray.mapper.DonationRecordMapper;
import com.citystray.mapper.RescueOrderMapper;
import com.citystray.mapper.StrayReportMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private StrayReportMapper strayReportMapper;

    @Autowired
    private DonationRecordMapper donationRecordMapper;

    /**
     * 获取概览统计数据
     * newReports = 今日新建救援工单数
     * activeOrders = 状态不是 adopted/closed 的工单数
     * pendingAdoptions = 阶段为 submitted/reviewing/visiting/trial 的领养申请数
     * todayDonation = 今日SUCCESS状态捐赠金额合计
     * *Change 字段暂置 0
     */
    @ApiOperation("获取概览统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);

        // 今日新增救援工单数
        Long newReports = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .ge("create_time", todayStart)
                        .le("create_time", todayEnd)
        );
        data.put("newReports", newReports != null ? newReports.intValue() : 0);

        // 活跃工单数（状态不是 adopted / closed）
        Long activeOrders = rescueOrderMapper.selectCount(
                new QueryWrapper<RescueOrder>()
                        .notIn("status", Arrays.asList("adopted", "closed"))
        );
        data.put("activeOrders", activeOrders != null ? activeOrders.intValue() : 0);

        // 待处理领养申请数（阶段为 submitted / reviewing / visiting / trial）
        Long pendingAdoptions = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .in("stage", Arrays.asList("submitted", "reviewing", "visiting", "trial"))
        );
        data.put("pendingAdoptions", pendingAdoptions != null ? pendingAdoptions.intValue() : 0);

        // 今日捐赠金额（状态为SUCCESS的捐赠记录）
        QueryWrapper<DonationRecord> donationWrapper = new QueryWrapper<>();
        donationWrapper.eq("status", "SUCCESS")
                .ge("create_time", todayStart)
                .le("create_time", todayEnd);
        List<DonationRecord> todayDonations = donationRecordMapper.selectList(donationWrapper);
        BigDecimal todayDonationTotal = todayDonations.stream()
                .map(DonationRecord::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("todayDonation", todayDonationTotal);

        // 同比变化百分比（暂置 0）
        data.put("newReportsChange", 0);
        data.put("activeOrdersChange", 0);
        data.put("pendingAdoptionsChange", 0);
        data.put("todayDonationChange", 0);

        return Result.success(data);
    }

    /**
     * 获取救助趋势（近 N 天）
     * 返回：dates[], newReports[], responded[], completed[]
     * newReports = 当天创建的工单数
     * responded = 当天响应时间(respond_time)不为空的工单数（或当天创建且状态非 pending 的）
     * completed = 当天关闭时间(close_time)不为空的工单数
     */
    @ApiOperation("获取救助趋势")
    @GetMapping("/rescue-trend")
    public Result<Map<String, Object>> rescueTrend(
            @ApiParam("天数(默认7)") @RequestParam(defaultValue = "7") Integer days) {

        List<String> dates = new ArrayList<>();
        List<Long> newReports = new ArrayList<>();
        List<Long> responded = new ArrayList<>();
        List<Long> completed = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            // 日期标签：如 "1日", "2日"
            dates.add(date.getDayOfMonth() + "日");

            // 当天创建的工单总数
            Long dayNew = rescueOrderMapper.selectCount(
                    new QueryWrapper<RescueOrder>()
                            .ge("create_time", dayStart)
                            .le("create_time", dayEnd)
            );
            newReports.add(dayNew != null ? dayNew : 0L);

            // 当天响应的工单数（respond_time 在当天范围内）
            Long dayResponded = rescueOrderMapper.selectCount(
                    new QueryWrapper<RescueOrder>()
                            .ge("respond_time", dayStart)
                            .le("respond_time", dayEnd)
            );
            responded.add(dayResponded != null ? dayResponded : 0L);

            // 当天完成的工单数（close_time 在当天范围内）
            Long dayCompleted = rescueOrderMapper.selectCount(
                    new QueryWrapper<RescueOrder>()
                            .ge("close_time", dayStart)
                            .le("close_time", dayEnd)
            );
            completed.add(dayCompleted != null ? dayCompleted : 0L);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("newReports", newReports);
        result.put("responded", responded);
        result.put("completed", completed);

        return Result.success(result);
    }

    /**
     * 获取区域热力图数据 — 按区域统计流浪动物上报数量
     */
    @ApiOperation("获取区域热力图数据")
    @GetMapping("/region-heatmap")
    public Result<List<Map<String, Object>>> regionHeatmap() {
        // 查询所有未删除的上报记录，按区域聚合
        List<StrayReport> allReports = strayReportMapper.selectList(
                new QueryWrapper<StrayReport>()
                        .eq("deleted", 0)
                        .isNotNull("district")
        );

        // 按 district 分组统计
        Map<String, List<StrayReport>> grouped = new HashMap<>();
        for (StrayReport r : allReports) {
            String district = r.getDistrict();
            if (district != null && !district.isEmpty()) {
                grouped.computeIfAbsent(district, k -> new ArrayList<>()).add(r);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<StrayReport>> entry : grouped.entrySet()) {
            List<StrayReport> reports = entry.getValue();
            Map<String, Object> item = new HashMap<>();
            item.put("district", entry.getKey());
            item.put("count", reports.size());

            // 计算平均经纬度（用于地图定位）
            double avgLng = reports.stream()
                    .filter(r -> r.getLongitude() != null)
                    .mapToDouble(r -> r.getLongitude().doubleValue())
                    .average().orElse(0.0);
            double avgLat = reports.stream()
                    .filter(r -> r.getLatitude() != null)
                    .mapToDouble(r -> r.getLatitude().doubleValue())
                    .average().orElse(0.0);
            item.put("longitude", avgLng);
            item.put("latitude", avgLat);

            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 获取领养率统计
     * success = stage 为 adopted 的数量
     * pending = stage 为 submitted / reviewing / visiting 的数量
     * rejected = stage 为 rejected 的数量
     * trial = stage 为 trial 的数量
     */
    @ApiOperation("获取领养率统计")
    @GetMapping("/adoption-rate")
    public Result<Map<String, Object>> adoptionRate() {
        Map<String, Object> data = new HashMap<>();

        // success: adopted
        Long success = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .eq("stage", "adopted")
        );
        data.put("success", success != null ? success.intValue() : 0);

        // pending: submitted / reviewing / visiting
        Long pending = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .in("stage", Arrays.asList("submitted", "reviewing", "visiting"))
        );
        data.put("pending", pending != null ? pending.intValue() : 0);

        // rejected
        Long rejected = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .eq("stage", "rejected")
        );
        data.put("rejected", rejected != null ? rejected.intValue() : 0);

        // trial
        Long trial = adoptionApplyMapper.selectCount(
                new QueryWrapper<AdoptionApply>()
                        .eq("stage", "trial")
        );
        data.put("trial", trial != null ? trial.intValue() : 0);

        return Result.success(data);
    }
}
