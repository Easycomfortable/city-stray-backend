package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.Task;
import com.citystray.entity.TaskLog;
import com.citystray.mapper.TaskLogMapper;
import com.citystray.mapper.TaskMapper;
import com.citystray.service.TaskService;
import com.citystray.service.VolunteerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 任务服务实现类
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 获取可接任务列表
     * 查询状态为available的任务，按创建时间倒序排列
     */
    @Override
    public PageResult<Map<String, Object>> getAvailableTasks(Integer page, Integer size, Long volunteerId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, "available")
                .orderByDesc(Task::getCreateTime);

        IPage<Task> pageResult = this.page(new Page<>(page, size), wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Task task : pageResult.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("orderId", task.getOrderId());
            map.put("taskType", task.getTaskType());
            map.put("status", task.getStatus());
            map.put("pointsReward", task.getPointsReward());
            map.put("description", task.getDescription());
            map.put("location", task.getLocation());
            map.put("longitude", task.getLongitude());
            map.put("latitude", task.getLatitude());
            map.put("createTime", task.getCreateTime());
            records.add(map);
        }

        return new PageResult<>(pageResult.getTotal(), records);
    }

    /**
     * 接单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptTask(Long taskId, Long volunteerId) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!"available".equals(task.getStatus())) {
            throw new RuntimeException("该任务当前不可接单，状态：" + task.getStatus());
        }

        // 更新任务状态为已接单
        task.setVolunteerId(volunteerId);
        task.setStatus("accepted");
        this.updateById(task);

        // 创建接单日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setVolunteerId(volunteerId);
        taskLog.setAction("accept");
        taskLog.setContent("志愿者已接单");
        taskLogMapper.insert(taskLog);

        log.info("志愿者[{}]已接单任务[{}]", volunteerId, taskId);
    }

    /**
     * 完成任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, Long volunteerId, String content, String photos, Double serviceHours) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!"accepted".equals(task.getStatus()) && !"in_progress".equals(task.getStatus())) {
            throw new RuntimeException("该任务当前不可完成，状态：" + task.getStatus());
        }

        // 更新任务状态为已完成
        task.setStatus("completed");
        this.updateById(task);

        // 创建完成日志
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setVolunteerId(volunteerId);
        taskLog.setAction("complete");
        taskLog.setContent(content != null && !content.isEmpty() ? content : "任务已完成");
        taskLog.setPhotos(photos);
        // 记录服务时长
        if (serviceHours != null && serviceHours > 0) {
            taskLog.setServiceHours(BigDecimal.valueOf(serviceHours));
        }
        taskLogMapper.insert(taskLog);

        // 为志愿者添加积分（默认使用任务的积分奖励）
        Integer points = task.getPointsReward() != null ? task.getPointsReward() : 10;
        volunteerService.addPoints(volunteerId, points, "完成任务获得积分");

        log.info("志愿者[{}]完成任务[{}]，获得积分：{}", volunteerId, taskId, points);
    }

    /**
     * 获取我的任务列表
     */
    @Override
    public PageResult<Map<String, Object>> getMyTasks(Integer page, Integer size, Long volunteerId, String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getVolunteerId, volunteerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Task::getStatus, status);
        }
        wrapper.orderByDesc(Task::getCreateTime);

        IPage<Task> pageResult = this.page(new Page<>(page, size), wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Task task : pageResult.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("orderId", task.getOrderId());
            map.put("taskType", task.getTaskType());
            map.put("status", task.getStatus());
            map.put("pointsReward", task.getPointsReward());
            map.put("description", task.getDescription());
            map.put("location", task.getLocation());
            map.put("createTime", task.getCreateTime());
            records.add(map);
        }

        return new PageResult<>(pageResult.getTotal(), records);
    }
}
