package com.citystray.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.StrayReport;
import com.citystray.mapper.StrayReportMapper;
import com.citystray.service.RescueOrderService;
import com.citystray.service.StrayReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RescueOrderService rescueOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitReport(StrayReport report) {
        // 生成上报编号: SR + yyyyMMdd + 3位序号
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        long count = this.count(new LambdaQueryWrapper<StrayReport>()
                .likeRight(StrayReport::getReportNo, "SR" + dateStr));
        String reportNo = "SR" + dateStr + String.format("%03d", count + 1);
        report.setReportNo(reportNo);
        report.setStatus(0); // 待处理
        this.save(report);

        // 自动创建救助工单
        rescueOrderService.createFromReport(report.getId());
        log.info("上报创建成功, 编号: {}, 自动生成救助工单", reportNo);

        return report.getId();
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
