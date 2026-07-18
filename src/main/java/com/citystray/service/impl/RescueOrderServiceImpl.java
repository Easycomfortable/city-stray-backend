package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.*;
import com.citystray.mapper.*;
import com.citystray.service.RescueOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 救助工单服务实现类
 */
@Slf4j
@Service
public class RescueOrderServiceImpl extends ServiceImpl<RescueOrderMapper, RescueOrder>
        implements RescueOrderService {

    @Autowired
    private RescueOrderMapper rescueOrderMapper;

    @Autowired
    private StrayReportMapper strayReportMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * 分页查询工单列表（关联上报人、志愿者、动物信息）
     */
    @Override
    public PageResult<Map<String, Object>> getOrderList(Integer page, Integer size, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        // 查询全部符合条件的数据，再做分页截取
        List<Map<String, Object>> allList = rescueOrderMapper.selectOrderList(params);
        int total = allList.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> records = fromIndex < total
                ? allList.subList(fromIndex, toIndex) : Collections.emptyList();
        return new PageResult<>((long) total, records);
    }

    /**
     * 获取工单详情（含关联信息）
     */
    @Override
    public Map<String, Object> getOrderDetail(Long id) {
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> list = rescueOrderMapper.selectOrderList(params);
        return list.stream()
                .filter(m -> Objects.equals(m.get("id"), id) || String.valueOf(id).equals(String.valueOf(m.get("id"))))
                .findFirst()
                .orElseGet(() -> {
                    // 如果关联查询无结果，返回基本工单信息
                    RescueOrder order = this.getById(id);
                    Map<String, Object> map = new HashMap<>();
                    if (order != null) {
                        map.put("order", order);
                    }
                    return map;
                });
    }

    /**
     * 获取工单时间线（所有操作记录）
     */
    @Override
    public List<Map<String, Object>> getOrderTimeline(Long orderId, Long reportId) {
        return rescueOrderMapper.selectOrderTimeline(orderId, reportId);
    }

    /**
     * 更新工单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status, String description) {
        RescueOrder order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        // 校验状态流转
        List<String> validStatusList = Arrays.asList(
                "pending", "responded", "catching", "treating", "recovering", "adoptable", "adopted", "closed");
        if (!validStatusList.contains(status)) {
            throw new RuntimeException("非法的工单状态：" + status);
        }
        String oldStatus = order.getStatus();
        order.setStatus(status);
        this.updateById(order);

        // 记录状态变更日志
        log.info("工单[{}]状态变更：{} -> {}，说明：{}", order.getOrderNo(), oldStatus, status, description);

        // 写入task_log表记录时间线
        LambdaQueryWrapper<Task> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(Task::getOrderId, id);
        Task task = taskMapper.selectOne(taskWrapper);
        if (task != null) {
            TaskLog taskLog = new TaskLog();
            taskLog.setTaskId(task.getId());
            taskLog.setVolunteerId(task.getVolunteerId() != null ? task.getVolunteerId() : 0L);
            taskLog.setAction(status);
            taskLog.setContent(description);
            taskLogMapper.insert(taskLog);
        }
    }

    /**
     * 分配志愿者
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignVolunteer(Long orderId, Long volunteerId) {
        RescueOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        // 更新工单志愿者ID和状态
        order.setVolunteerId(volunteerId);
        order.setStatus("responded");
        order.setAssignedTime(LocalDateTime.now());
        this.updateById(order);

        // 创建任务记录
        Task task = new Task();
        task.setOrderId(orderId);
        task.setVolunteerId(volunteerId);
        task.setTaskType("respond");
        task.setStatus("accepted");
        taskMapper.insert(task);

        // 记录分配日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setVolunteerId(volunteerId);
        taskLog.setAction("respond");
        taskLog.setContent("志愿者已响应工单");
        taskLogMapper.insert(taskLog);

        log.info("工单[{}]已分配志愿者[{}]，任务ID[{}]", order.getOrderNo(), volunteerId, task.getId());
    }

    /**
     * 创建救助工单（由上报触发）
     * 工单编号格式：RO + yyyyMMdd + 3位序列号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFromReport(Long reportId) {
        StrayReport report = strayReportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("上报记录不存在");
        }

        RescueOrder order = new RescueOrder();
        // 生成工单编号：RO + yyyyMMdd + 3位序列号
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RO" + dateStr;
        LambdaQueryWrapper<RescueOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(RescueOrder::getOrderNo, prefix);
        long count = this.count(wrapper);
        String seq = String.format("%03d", count + 1);
        order.setOrderNo(prefix + seq);

        order.setReportId(reportId);
        order.setDistrict(report.getDistrict());
        order.setStatus("pending");
        this.save(order);

        log.info("由上报记录[{}]创建救助工单[{}]", reportId, order.getOrderNo());
        return order.getId();
    }
}
