package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.RescueOrder;

import java.util.List;
import java.util.Map;

/**
 * 救助工单服务接口
 */
public interface RescueOrderService extends IService<RescueOrder> {

    /**
     * 分页查询工单列表
     *
     * @param page   当前页码
     * @param size   每页数量
     * @param params 查询参数（状态、区域、工单号、时间范围等）
     * @return 分页结果
     */
    PageResult<Map<String, Object>> getOrderList(Integer page, Integer size, Map<String, Object> params);

    /**
     * 获取工单详情（含关联信息）
     *
     * @param id 工单ID
     * @return 工单详情
     */
    Map<String, Object> getOrderDetail(Long id);

    /**
     * 获取工单时间线
     *
     * @param orderId  工单ID
     * @param reportId 上报ID
     * @return 时间线记录列表
     */
    List<Map<String, Object>> getOrderTimeline(Long orderId, Long reportId);

    /**
     * 更新工单状态
     *
     * @param id          工单ID
     * @param status      目标状态
     * @param description 状态变更说明
     */
    void updateStatus(Long id, String status, String description);

    /**
     * 分配志愿者
     *
     * @param orderId      工单ID
     * @param volunteerId  志愿者ID
     */
    void assignVolunteer(Long orderId, Long volunteerId);

    /**
     * 创建救助工单（由上报触发）
     *
     * @param reportId 上报记录ID
     * @return 新建工单ID
     */
    Long createFromReport(Long reportId);
}
