package com.citystray.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.StrayReport;
import com.citystray.mapper.StrayReportMapper;
import com.citystray.service.StrayReportService;
import com.citystray.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 流浪动物上报服务实现
 */
@Slf4j
@Service
public class StrayReportServiceImpl extends ServiceImpl<StrayReportMapper, StrayReport>
        implements StrayReportService {

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = DuplicateKeyException.class)
    public Long submitReport(StrayReport report) {
        // 生成上报编号: RS + yyyyMMdd + 3位序号（带碰撞重试）
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        String prefix = "RS" + dateStr;
        long count = this.count(new LambdaQueryWrapper<StrayReport>()
                .likeRight(StrayReport::getReportNo, prefix));
        int seq = (int) count + 1;

        // 设置基本信息
        report.setStatus(0); // 待处理
        Long userId = UserContext.getUserId();
        if (userId != null) {
            report.setUserId(userId);
        }
        // 修复: MySQL JSON字段不接受空字符串，转为空数组
        if (report.getPhotos() == null || report.getPhotos().trim().isEmpty()) {
            report.setPhotos("[]");
        }

        // 重试最多10次，处理编号碰撞
        for (int attempt = 0; attempt < 10; attempt++) {
            String reportNo = prefix + String.format("%03d", seq + attempt);
            report.setReportNo(reportNo);
            report.setId(null); // 清除可能的旧ID，确保执行INSERT而非UPDATE
            try {
                this.save(report);
                log.info("上报创建成功, 编号: {}", reportNo);
                return report.getId();
            } catch (DuplicateKeyException e) {
                log.warn("编号碰撞: {}，尝试下一个序号", reportNo);
            }
        }
        throw new RuntimeException("无法生成上报编号，请稍后重试");
    }

    @Override
    public PageResult<StrayReport> getReportList(Integer page, Integer size,
                                                  Long userId, String status, String district) {
        LambdaQueryWrapper<StrayReport> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(StrayReport::getUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(StrayReport::getStatus, Integer.parseInt(status));
        }
        if (district != null && !district.isEmpty()) {
            wrapper.eq(StrayReport::getDistrict, district);
        }
        wrapper.orderByDesc(StrayReport::getCreateTime);

        Page<StrayReport> pageResult = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
    }

    @Override
    public StrayReport getReportById(Long id) {
        return this.getById(id);
    }
}
