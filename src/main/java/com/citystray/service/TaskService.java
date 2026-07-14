package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.Task;

import java.util.Map;

/**
 * 任务服务接口
 */
public interface TaskService extends IService<Task> {

    /**
     * 获取可接任务列表
     */
    PageResult<Map<String, Object>> getAvailableTasks(Integer page, Integer size, Long volunteerId);

    /**
     * 接单
     */
    void acceptTask(Long taskId, Long volunteerId);

    /**
     * 完成任务
     */
    void completeTask(Long taskId, Long volunteerId, String content, String photos, Double serviceHours);

    /**
     * 获取我的任务列表
     */
    PageResult<Map<String, Object>> getMyTasks(Integer page, Integer size, Long volunteerId, String status);
}
