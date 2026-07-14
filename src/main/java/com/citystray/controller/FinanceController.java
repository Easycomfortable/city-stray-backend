package com.citystray.controller;

import com.citystray.annotation.RequireRole;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.DonationProject;
import com.citystray.entity.DonationRecord;
import com.citystray.mapper.DonationRecordMapper;
import com.citystray.service.FinanceService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 财务管理控制器
 */
@Slf4j
@Api(tags = "财务管理")
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final DonationRecordMapper donationRecordMapper;

    @ApiOperation("捐赠记录列表")
    @GetMapping("/donation/list")
    @RequireRole({"admin"})
    public Result<PageResult<Map<String, Object>>> donationList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("日期范围") @RequestParam(required = false) String dateRange) {
        return Result.success(financeService.donationList(page, pageSize, keyword, dateRange));
    }

    @ApiOperation("捐赠项目列表")
    @GetMapping("/project/list")
    @RequireRole({"admin"})
    public Result<PageResult<Map<String, Object>>> projectList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(financeService.projectList(page, pageSize));
    }

    @ApiOperation("保存捐赠项目")
    @PostMapping("/project/save")
    @RequireRole({"admin"})
    public Result<?> saveProject(@RequestBody DonationProject project) {
        financeService.saveProject(project);
        return Result.success();
    }

    @ApiOperation("删除捐赠项目")
    @DeleteMapping("/project/{id}")
    @RequireRole({"admin"})
    public Result<?> deleteProject(@ApiParam("项目ID") @PathVariable Long id) {
        financeService.deleteProject(id);
        return Result.success();
    }

    @ApiOperation("财务报告")
    @GetMapping("/report")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> report(
            @ApiParam("月份(yyyy-MM)") @RequestParam(required = false) String month) {
        return Result.success(financeService.financeReport(month));
    }

    @ApiOperation("支出记录列表")
    @GetMapping("/expense/list")
    @RequireRole({"admin"})
    public Result<PageResult<Map<String, Object>>> expenseList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("类别") @RequestParam(required = false) String category) {
        return Result.success(financeService.expenseList(page, pageSize, category));
    }

    @ApiOperation("对账")
    @PostMapping("/reconcile")
    @RequireRole({"admin"})
    public Result<Map<String, Object>> reconcile() {
        return Result.success(financeService.reconcile());
    }

    @ApiOperation("用户捐款（小程序端）")
    @PostMapping("/donate")
    public Result<?> donate(@RequestBody Map<String, Object> body) {
        DonationRecord record = new DonationRecord();
        record.setProjectId(body.get("projectId") != null
                ? Long.valueOf(body.get("projectId").toString()) : null);
        record.setAmount(body.get("amount") != null
                ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO);
        record.setDonorName((String) body.getOrDefault("donorName", "匿名"));
        record.setDonorPhone((String) body.getOrDefault("donorPhone", ""));
        record.setUserId(UserContext.getUserId());
        record.setPaymentMethod("WECHAT");
        record.setStatus("SUCCESS");
        donationRecordMapper.insert(record);
        return Result.success(record.getId());
    }

    @ApiOperation("导出财务报表")
    @GetMapping("/report/export")
    @RequireRole({"admin"})
    public void exportReport(@RequestParam(required = false) String month, HttpServletResponse response) {
        Map<String, Object> reportData = financeService.financeReport(month);
        List<Map<String, Object>> rows = new ArrayList<>();

        // 汇总行
        Map<String, Object> summaryRow = new LinkedHashMap<>();
        summaryRow.put("类别", "汇总");
        summaryRow.put("类型", "收入");
        summaryRow.put("金额", reportData.getOrDefault("income", BigDecimal.ZERO));
        summaryRow.put("笔数", "-");
        rows.add(summaryRow);

        Map<String, Object> expenseRow = new LinkedHashMap<>();
        expenseRow.put("类别", "汇总");
        expenseRow.put("类型", "支出");
        expenseRow.put("金额", reportData.getOrDefault("expense", BigDecimal.ZERO));
        expenseRow.put("笔数", "-");
        rows.add(expenseRow);

        // 收入明细
        List<Map<String, Object>> incomeDetails = (List<Map<String, Object>>) reportData.getOrDefault("incomeDetails", Collections.emptyList());
        for (Map<String, Object> item : incomeDetails) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("类别", item.getOrDefault("category", ""));
            row.put("类型", "收入");
            row.put("金额", item.getOrDefault("amount", BigDecimal.ZERO));
            row.put("笔数", item.getOrDefault("count", 0));
            rows.add(row);
        }

        // 支出明细
        List<Map<String, Object>> expenseDetails = (List<Map<String, Object>>) reportData.getOrDefault("expenseDetails", Collections.emptyList());
        for (Map<String, Object> item : expenseDetails) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("类别", item.getOrDefault("category", ""));
            row.put("类型", "支出");
            row.put("金额", item.getOrDefault("amount", BigDecimal.ZERO));
            row.put("笔数", item.getOrDefault("count", 0));
            rows.add(row);
        }

        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.write(rows);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=finance_report.xlsx");
        try {
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            writer.close();
        } catch (Exception e) {
            log.error("导出财务报表失败", e);
        }
    }
}
