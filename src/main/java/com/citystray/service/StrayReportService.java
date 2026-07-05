package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.StrayReport;

/**
 * 流浪动物上报服务
 */
public interface StrayReportService extends IService<StrayReport> {

    /** 提交上报并自动创建救助工单 */
    Long submitReport(StrayReport report);

    /** 分页查询上报记录 */
    PageResult<StrayReport> getReportList(Integer page, Integer size, Long userId, String status, String district);

    /** 获取上报详情 */
    StrayReport getReportById(Long id);
}
