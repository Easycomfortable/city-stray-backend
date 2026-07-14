package com.citystray.service;

import com.citystray.common.PageResult;
import com.citystray.entity.DonationProject;

import java.util.Map;

public interface FinanceService {
    // 捐赠记录
    PageResult<Map<String, Object>> donationList(Integer page, Integer pageSize, String keyword, String dateRange);

    // 捐赠项目
    PageResult<Map<String, Object>> projectList(Integer page, Integer pageSize);
    void saveProject(DonationProject project);
    void deleteProject(Long id);

    // 财务报告
    Map<String, Object> financeReport(String month);

    // 支出记录
    PageResult<Map<String, Object>> expenseList(Integer page, Integer pageSize, String category);

    // 对账
    Map<String, Object> reconcile();
}
