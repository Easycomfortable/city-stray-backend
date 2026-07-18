package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.entity.DonationProject;
import com.citystray.entity.DonationRecord;
import com.citystray.entity.ExpenseRecord;
import com.citystray.mapper.DonationProjectMapper;
import com.citystray.mapper.DonationRecordMapper;
import com.citystray.mapper.ExpenseRecordMapper;
import com.citystray.service.FinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    private final DonationProjectMapper projectMapper;
    private final DonationRecordMapper donationRecordMapper;
    private final ExpenseRecordMapper expenseRecordMapper;

    /**
     * 捐赠记录列表 - 联查项目名称
     * 前端期望字段: id, donorName, anonymous, projectName, amount, paymentMethod, status, paymentNo, createTime
     */
    @Override
    public PageResult<Map<String, Object>> donationList(Integer page, Integer pageSize, String keyword, String dateRange) {
        Page<DonationRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(DonationRecord::getDonorName, keyword)
                .or()
                .like(DonationRecord::getPaymentNo, keyword)
            );
        }

        // dateRange format: "2026-07-01,2026-07-31" or array
        if (StringUtils.hasText(dateRange)) {
            String[] dates = dateRange.split(",");
            if (dates.length == 2) {
                wrapper.ge(DonationRecord::getCreateTime, dates[0].trim() + " 00:00:00");
                wrapper.le(DonationRecord::getCreateTime, dates[1].trim() + " 23:59:59");
            }
        }

        wrapper.orderByDesc(DonationRecord::getCreateTime);
        Page<DonationRecord> recordPage = donationRecordMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (DonationRecord r : recordPage.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getId());
            map.put("donorName", r.getDonorName());
            map.put("anonymous", r.getAnonymous() == 1);
            map.put("amount", r.getAmount());
            map.put("paymentMethod", r.getPaymentMethod());
            map.put("status", r.getStatus());
            map.put("paymentNo", r.getPaymentNo());
            map.put("createTime", r.getCreateTime());

            // 查项目名称
            if (r.getProjectId() != null) {
                DonationProject project = projectMapper.selectById(r.getProjectId());
                map.put("projectName", project != null ? project.getName() : "未知项目");
            } else {
                map.put("projectName", "自由捐赠");
            }
            records.add(map);
        }

        return new PageResult<>(recordPage.getTotal(), records);
    }

    /**
     * 捐赠项目列表
     * 前端期望字段: id, name, targetAmount, raisedAmount, status, donorCount, description, coverImage
     */
    @Override
    public PageResult<Map<String, Object>> projectList(Integer page, Integer pageSize) {
        Page<DonationProject> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<DonationProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DonationProject::getCreateTime);
        Page<DonationProject> projectPage = projectMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (DonationProject p : projectPage.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("coverImage", p.getCoverImage());
            map.put("targetAmount", p.getTargetAmount());
            map.put("raisedAmount", p.getRaisedAmount());
            map.put("status", p.getStatus());
            map.put("donorCount", p.getDonorCount());
            records.add(map);
        }

        return new PageResult<>(projectPage.getTotal(), records);
    }

    @Override
    public void saveProject(DonationProject project) {
        if (project.getId() == null) {
            project.setRaisedAmount(BigDecimal.ZERO);
            project.setDonorCount(0);
            projectMapper.insert(project);
        } else {
            projectMapper.updateById(project);
        }
    }

    @Override
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }

    /**
     * 财务报告 - 按月份统计
     * 前端期望字段: income, expense, totalBalance, incomeDetails[{category,amount,count,ratio}], expenseDetails[...]
     */
    @Override
    public Map<String, Object> financeReport(String month) {
        Map<String, Object> data = new LinkedHashMap<>();

        // 解析月份，默认当前月
        YearMonth ym;
        try {
            ym = StringUtils.hasText(month) ? YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM")) : YearMonth.now();
        } catch (Exception e) {
            ym = YearMonth.now();
        }

        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        // 计算本月捐赠收入（SUCCESS状态的捐赠）
        LambdaQueryWrapper<DonationRecord> donWrapper = new LambdaQueryWrapper<>();
        donWrapper.eq(DonationRecord::getStatus, "SUCCESS");
        donWrapper.ge(DonationRecord::getTransactionTime, monthStart);
        donWrapper.le(DonationRecord::getTransactionTime, monthEnd.plusDays(1));
        List<DonationRecord> donations = donationRecordMapper.selectList(donWrapper);

        BigDecimal income = BigDecimal.ZERO;
        Map<String, BigDecimal[]> incomeByCategory = new LinkedHashMap<>();
        for (DonationRecord d : donations) {
            income = income.add(d.getAmount());
            String cat = "捐赠收入";
            incomeByCategory.computeIfAbsent(cat, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            incomeByCategory.get(cat)[0] = incomeByCategory.get(cat)[0].add(d.getAmount());
            incomeByCategory.get(cat)[1] = incomeByCategory.get(cat)[1].add(BigDecimal.ONE);
        }

        // 计算本月支出（APPROVED状态的支出）
        LambdaQueryWrapper<ExpenseRecord> expWrapper = new LambdaQueryWrapper<>();
        expWrapper.eq(ExpenseRecord::getApprovalStatus, "APPROVED");
        expWrapper.ge(ExpenseRecord::getExpenseDate, monthStart);
        expWrapper.le(ExpenseRecord::getExpenseDate, monthEnd);
        List<ExpenseRecord> expenses = expenseRecordMapper.selectList(expWrapper);

        BigDecimal expense = BigDecimal.ZERO;
        Map<String, BigDecimal[]> expenseByCategory = new LinkedHashMap<>();
        Map<String, String> categoryNames = new HashMap<>();
        categoryNames.put("MEDICAL", "医疗费用");
        categoryNames.put("FOOD", "饲料费用");
        categoryNames.put("OPERATION", "运营费用");
        categoryNames.put("OTHER", "其他费用");

        for (ExpenseRecord e : expenses) {
            expense = expense.add(e.getAmount());
            String cat = categoryNames.getOrDefault(e.getCategory(), "其他费用");
            expenseByCategory.computeIfAbsent(cat, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            expenseByCategory.get(cat)[0] = expenseByCategory.get(cat)[0].add(e.getAmount());
            expenseByCategory.get(cat)[1] = expenseByCategory.get(cat)[1].add(BigDecimal.ONE);
        }

        // 累计结余 = 全部收入 - 全部支出
        LambdaQueryWrapper<DonationRecord> allDonWrapper = new LambdaQueryWrapper<>();
        allDonWrapper.eq(DonationRecord::getStatus, "SUCCESS");
        List<DonationRecord> allDonations = donationRecordMapper.selectList(allDonWrapper);
        BigDecimal totalIncome = allDonations.stream().map(DonationRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<ExpenseRecord> allExpWrapper = new LambdaQueryWrapper<>();
        allExpWrapper.eq(ExpenseRecord::getApprovalStatus, "APPROVED");
        List<ExpenseRecord> allExpenses = expenseRecordMapper.selectList(allExpWrapper);
        BigDecimal totalExpense = allExpenses.stream().map(ExpenseRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("income", income);
        data.put("expense", expense);
        data.put("totalBalance", totalIncome.subtract(totalExpense));

        // 收入明细
        List<Map<String, Object>> incomeDetails = new ArrayList<>();
        for (Map.Entry<String, BigDecimal[]> entry : incomeByCategory.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", entry.getKey());
            item.put("amount", entry.getValue()[0]);
            item.put("count", entry.getValue()[1].intValue());
            double ratio = income.compareTo(BigDecimal.ZERO) > 0
                ? entry.getValue()[0].multiply(BigDecimal.valueOf(100)).divide(income, 1, RoundingMode.HALF_UP).doubleValue()
                : 0;
            item.put("ratio", ratio);
            incomeDetails.add(item);
        }
        data.put("incomeDetails", incomeDetails);

        // 支出明细
        List<Map<String, Object>> expenseDetails = new ArrayList<>();
        for (Map.Entry<String, BigDecimal[]> entry : expenseByCategory.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", entry.getKey());
            item.put("amount", entry.getValue()[0]);
            item.put("count", entry.getValue()[1].intValue());
            double ratio = expense.compareTo(BigDecimal.ZERO) > 0
                ? entry.getValue()[0].multiply(BigDecimal.valueOf(100)).divide(expense, 1, RoundingMode.HALF_UP).doubleValue()
                : 0;
            item.put("ratio", ratio);
            expenseDetails.add(item);
        }
        data.put("expenseDetails", expenseDetails);

        return data;
    }

    /**
     * 支出记录列表
     * 前端期望字段: id, category, amount, description, applicant, approvalStatus, expenseDate, createTime
     */
    @Override
    public PageResult<Map<String, Object>> expenseList(Integer page, Integer pageSize, String category) {
        Page<ExpenseRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ExpenseRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            wrapper.eq(ExpenseRecord::getCategory, category);
        }
        wrapper.orderByDesc(ExpenseRecord::getCreateTime);
        Page<ExpenseRecord> expPage = expenseRecordMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (ExpenseRecord e : expPage.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("category", e.getCategory());
            map.put("amount", e.getAmount());
            map.put("description", e.getDescription());
            map.put("applicant", e.getApplicant());
            map.put("approvalStatus", e.getApprovalStatus());
            map.put("expenseDate", e.getExpenseDate());
            map.put("createTime", e.getCreateTime());
            records.add(map);
        }

        return new PageResult<>(expPage.getTotal(), records);
    }

    /**
     * 对账 - 校验捐赠金额和项目已筹金额是否一致
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reconcile() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> issues = new ArrayList<>();

        // 查询所有捐赠项目
        List<DonationProject> projects = projectMapper.selectList(new LambdaQueryWrapper<>());
        for (DonationProject p : projects) {
            // 统计该项目实际成功捐赠金额
            LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DonationRecord::getProjectId, p.getId());
            wrapper.eq(DonationRecord::getStatus, "SUCCESS");
            List<DonationRecord> records = donationRecordMapper.selectList(wrapper);

            BigDecimal actualRaised = records.stream().map(DonationRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int actualCount = records.size();

            // 比对
            if (actualRaised.compareTo(p.getRaisedAmount()) != 0 || actualCount != p.getDonorCount()) {
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("projectId", p.getId());
                issue.put("projectName", p.getName());
                issue.put("recordedRaised", p.getRaisedAmount());
                issue.put("actualRaised", actualRaised);
                issue.put("recordedCount", p.getDonorCount());
                issue.put("actualCount", actualCount);
                issues.add(issue);

                // 自动修正
                p.setRaisedAmount(actualRaised);
                p.setDonorCount(actualCount);
                projectMapper.updateById(p);
            }
        }

        result.put("checked", projects.size());
        result.put("issues", issues.size());
        result.put("details", issues);
        result.put("reconcileTime", LocalDateTime.now());

        return result;
    }
}
