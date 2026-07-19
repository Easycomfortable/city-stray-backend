package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.annotation.OperationLog;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.RescueOrder;
import com.citystray.entity.Task;
import com.citystray.entity.TaskLog;
import com.citystray.mapper.TaskLogMapper;
import com.citystray.mapper.TaskMapper;
import com.citystray.service.RescueOrderService;
import com.citystray.service.NotificationService;
import com.citystray.entity.StrayReport;
import com.citystray.mapper.StrayReportMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 救助工单管理控制器
 */
@Api(tags = "救助工单管理")
@RestController
@RequestMapping("/api/rescue-order")
public class RescueOrderController {

    @Autowired
    private RescueOrderService rescueOrderService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StrayReportMapper strayReportMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * 分页查询工单列表
     */
    @ApiOperation("分页查询工单列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("工单状态") @RequestParam(required = false) String status,
            @ApiParam("动物类型") @RequestParam(required = false) String animalType,
            @ApiParam("日期范围(逗号分隔)") @RequestParam(required = false) String dateRange,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", keyword != null ? keyword : "");
        params.put("status", status != null ? status : "");
        params.put("district", "");

        // 解析日期范围
        String startTime = "";
        String endTime = "";
        if (dateRange != null && !dateRange.isEmpty()) {
            String[] parts = dateRange.split(",");
            if (parts.length >= 1) startTime = parts[0].trim();
            if (parts.length >= 2) endTime = parts[1].trim();
        }
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        PageResult<Map<String, Object>> serviceResult = rescueOrderService.getOrderList(page, pageSize, params);

        // 转换字段名以匹配前端期望的格式
        List<Map<String, Object>> transformedRecords = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Map<String, Object> row : serviceResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", row.get("id"));
                item.put("reporterName", getStr(row, "reporter_name", "未知"));
                item.put("animalType", getStr(row, "report_animal_type", "DOG"));
                item.put("animalCount", 1);
                item.put("location", getStr(row, "report_address", getStr(row, "district", "")));
                item.put("status", row.get("status"));
                item.put("volunteerName", getStr(row, "volunteer_name", ""));
                item.put("createTime", row.get("create_time"));
                item.put("overdue", false);
                item.put("injured", false);
                item.put("friendly", false);
                item.put("description", getStr(row, "report_description", getStr(row, "description", "")));
                item.put("photos", getStr(row, "report_photos", ""));
                item.put("longitude", row.get("longitude"));
                item.put("latitude", row.get("latitude"));
                transformedRecords.add(item);
            }
        }

        return Result.success(new PageResult<>(serviceResult.getTotal(), transformedRecords));
    }

    /**
     * 获取工单详情
     */
    @ApiOperation("获取工单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("工单ID") @PathVariable Long id) {
        Map<String, Object> raw = rescueOrderService.getOrderDetail(id);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", raw.getOrDefault("id", id));
        item.put("reporterName", getStr(raw, "reporter_name", "未知"));
        item.put("animalType", getStr(raw, "report_animal_type", "DOG"));
        item.put("animalCount", 1);
        item.put("location", getStr(raw, "report_address", getStr(raw, "district", "")));
        item.put("status", raw.get("status"));
        item.put("volunteerName", getStr(raw, "volunteer_name", ""));
        item.put("volunteerPhone", getStr(raw, "volunteer_phone", ""));
        item.put("createTime", raw.get("create_time"));
        item.put("overdue", false);
        item.put("injured", false);
        item.put("friendly", false);
        item.put("description", getStr(raw, "report_description", getStr(raw, "description", "")));
        item.put("photos", getStr(raw, "report_photos", ""));
        item.put("animalId", raw.get("animal_id"));
        item.put("longitude", raw.get("longitude"));
        item.put("latitude", raw.get("latitude"));

        return Result.success(item);
    }

    /**
     * 更新工单状态
     */
    @OperationLog(module = "救助工单", type = "UPDATE", content = "更新工单状态")
    @ApiOperation("更新工单状态")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @ApiParam("工单ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        rescueOrderService.updateStatus(id, status, "");

        // 通知举报人工单状态变更
        try {
            RescueOrder order = rescueOrderService.getById(id);
            if (order != null && order.getReportId() != null) {
                StrayReport report = strayReportMapper.selectById(order.getReportId());
                if (report != null && report.getUserId() != null) {
                    String statusText = getRescueStatusText(status);
                    notificationService.sendNotification(
                        report.getUserId(),
                        "救助工单更新",
                        "您的救助工单状态已更新为：" + statusText,
                        "RESCUE", "RESCUE", id
                    );
                }
            }
        } catch (Exception e) {
            // 通知失败不影响主流程
        }

        return Result.success();
    }

    private String getRescueStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "pending":    return "待处理";
            case "responded":  return "已响应";
            case "catching":   return "捕捉中";
            case "treating":   return "治疗中";
            case "recovering": return "恢复中";
            case "adoptable":  return "可领养";
            case "adopted":    return "已领养";
            case "closed":     return "已关闭";
            default:           return status;
        }
    }

    /**
     * 分配志愿者
     */
    @OperationLog(module = "救助工单", type = "UPDATE", content = "分配志愿者")
    @ApiOperation("分配志愿者")
    @PostMapping("/{id}/assign")
    public Result<?> assignVolunteer(
            @ApiParam("工单ID") @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long volunteerId = Long.valueOf(body.get("volunteerId").toString());
        rescueOrderService.assignVolunteer(id, volunteerId);
        return Result.success();
    }

    /**
     * 关联医院
     */
    @OperationLog(module = "救助工单", type = "UPDATE", content = "关联医院")
    @ApiOperation("关联医院")
    @PostMapping("/{id}/hospital")
    public Result<?> assignHospital(
            @ApiParam("工单ID") @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long hospitalId = Long.valueOf(body.get("hospitalId").toString());
        RescueOrder order = rescueOrderService.getById(id);
        if (order == null) {
            return Result.error("工单不存在");
        }
        order.setHospitalId(hospitalId);
        rescueOrderService.updateById(order);
        return Result.success();
    }

    /**
     * 获取工单时间线
     */
    @ApiOperation("获取工单时间线")
    @GetMapping("/{id}/timeline")
    public Result<List<Map<String, Object>>> timeline(
            @ApiParam("工单ID") @PathVariable Long id) {
        // 获取工单信息以取得reportId
        RescueOrder order = rescueOrderService.getById(id);
        Long reportId = (order != null) ? order.getReportId() : null;

        List<Map<String, Object>> rawList = rescueOrderService.getOrderTimeline(id, reportId != null ? reportId : 0L);

        // 转换字段名: action_time -> time
        List<Map<String, Object>> result = new ArrayList<>();
        if (rawList != null) {
            for (Map<String, Object> row : rawList) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("time", row.get("action_time"));
                item.put("action", row.get("action"));
                result.add(item);
            }
        }

        return Result.success(result);
    }

    /**
     * 添加工单备注
     */
    @OperationLog(module = "救助工单", type = "CREATE", content = "添加工单备注")
    @ApiOperation("添加工单备注")
    @PostMapping("/{id}/note")
    public Result<?> addNote(
            @ApiParam("工单ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");

        // 查找关联的任务
        LambdaQueryWrapper<Task> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(Task::getOrderId, id);
        Task task = taskMapper.selectOne(taskWrapper);

        TaskLog taskLog = new TaskLog();
        if (task != null) {
            taskLog.setTaskId(task.getId());
            taskLog.setVolunteerId(task.getVolunteerId() != null ? task.getVolunteerId() : 0L);
        } else {
            // 没有关联任务时，用默认值避免NOT NULL约束报错
            taskLog.setTaskId(0L);
            taskLog.setVolunteerId(0L);
        }
        taskLog.setAction("note");
        taskLog.setContent(content);
        taskLogMapper.insert(taskLog);

        return Result.success();
    }

    /**
     * 导出工单Excel
     * 查询所有工单数据并导出为xlsx文件
     */
    @OperationLog(module = "救助工单", type = "EXPORT", content = "导出工单数据")
    @ApiOperation("导出工单")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        // 查询所有工单（取前5000条）
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", "");
        params.put("status", "");
        params.put("district", "");
        params.put("startTime", "");
        params.put("endTime", "");
        PageResult<Map<String, Object>> serviceResult = rescueOrderService.getOrderList(1, 5000, params);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Map<String, Object> row : serviceResult.getRecords()) {
                Map<String, Object> exportRow = new LinkedHashMap<>();
                exportRow.put("工单编号", row.get("order_no"));
                exportRow.put("举报人", getStr(row, "reporter_name", "未知"));
                exportRow.put("动物类型", getStr(row, "report_animal_type", ""));
                exportRow.put("所在区域", getStr(row, "district", ""));
                exportRow.put("详细地址", getStr(row, "report_address", ""));
                exportRow.put("工单状态", getRescueStatusText(
                        row.get("status") != null ? row.get("status").toString() : ""));
                exportRow.put("志愿者", getStr(row, "volunteer_name", ""));
                exportRow.put("创建时间", row.get("create_time"));
                rows.add(exportRow);
            }
        }

        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.write(rows);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=rescue_orders.xlsx");
        try {
            javax.servlet.ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            writer.close();
        } catch (Exception e) {
            // 导出失败时不抛出异常
        }
    }

    // ========== 辅助方法 ==========

    private String getStr(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        if (val == null) return defaultValue;
        String s = val.toString();
        return s.isEmpty() ? defaultValue : s;
    }
}
