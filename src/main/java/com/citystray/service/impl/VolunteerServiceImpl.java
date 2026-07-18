package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.TaskLog;
import com.citystray.entity.Volunteer;
import com.citystray.mapper.TaskLogMapper;
import com.citystray.mapper.VolunteerMapper;
import com.citystray.service.VolunteerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 志愿者服务实现类
 */
@Slf4j
@Service
public class VolunteerServiceImpl extends ServiceImpl<VolunteerMapper, Volunteer>
        implements VolunteerService {

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * 分页查询志愿者列表
     */
    @Override
    public PageResult<Volunteer> getVolunteerList(Integer page, Integer size, String authStatus, String keyword, String skillTag) {
        LambdaQueryWrapper<Volunteer> wrapper = new LambdaQueryWrapper<>();

        if (authStatus != null && !authStatus.isEmpty()) {
            wrapper.eq(Volunteer::getAuthStatus, Integer.parseInt(authStatus));
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Volunteer::getRealName, keyword)
                    .or().like(Volunteer::getPhone, keyword));
        }
        if (skillTag != null && !skillTag.isEmpty()) {
            wrapper.like(Volunteer::getSkillTags, skillTag);
        }

        wrapper.orderByDesc(Volunteer::getCreateTime);

        IPage<Volunteer> pageResult = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
    }

    /**
     * 获取志愿者详情
     */
    @Override
    public Volunteer getVolunteerById(Long id) {
        Volunteer volunteer = this.getById(id);
        if (volunteer == null) {
            throw new RuntimeException("志愿者不存在");
        }
        return volunteer;
    }

    /**
     * 审核志愿者认证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewVolunteer(Long id, String authStatus, String rejectReason) {
        Volunteer volunteer = this.getById(id);
        if (volunteer == null) {
            throw new RuntimeException("志愿者不存在");
        }

        volunteer.setAuthStatus(Integer.parseInt(authStatus));

        // 如果审核拒绝，设置拒绝原因
        if ("2".equals(authStatus) && rejectReason != null && !rejectReason.isEmpty()) {
            volunteer.setRejectReason(rejectReason);
        }

        this.updateById(volunteer);
        log.info("志愿者[{}]认证审核结果：{}，拒绝原因：{}", id, authStatus, rejectReason);
    }

    /**
     * 获取积分记录（查询task_log表，按志愿者筛选积分相关日志）
     */
    @Override
    public PageResult<Map<String, Object>> getPointsRecords(Integer page, Integer size, Long volunteerId) {
        LambdaQueryWrapper<TaskLog> wrapper = new LambdaQueryWrapper<>();
        if (volunteerId != null) {
            wrapper.eq(TaskLog::getVolunteerId, volunteerId);
        }
        wrapper.eq(TaskLog::getAction, "points")
                .orderByDesc(TaskLog::getCreateTime);

        IPage<TaskLog> pageResult = new Page<>(page, size);
        IPage<TaskLog> result = taskLogMapper.selectPage(pageResult, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (TaskLog taskLog : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", taskLog.getId());
            map.put("action", taskLog.getAction());
            map.put("content", taskLog.getContent());
            map.put("createTime", taskLog.getCreateTime());
            map.put("volunteerId", taskLog.getVolunteerId());
            records.add(map);
        }

        return new PageResult<>((long) result.getTotal(), records);
    }

    /**
     * 添加积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long volunteerId, Integer points, String reason) {
        Volunteer volunteer = this.getById(volunteerId);
        if (volunteer == null) {
            throw new RuntimeException("志愿者不存在");
        }

        // 更新志愿者积分
        Integer currentPoints = volunteer.getPoints() != null ? volunteer.getPoints() : 0;
        volunteer.setPoints(currentPoints + points);
        this.updateById(volunteer);

        // 创建积分日志（TaskLog没有points字段，积分信息记录在content中）
        TaskLog taskLog = new TaskLog();
        taskLog.setVolunteerId(volunteerId);
        taskLog.setAction("points");
        taskLog.setContent("积分变动：" + points + "分，原因：" + reason);
        taskLogMapper.insert(taskLog);

        log.info("志愿者[{}]增加积分：{}，原因：{}，当前总积分：{}", volunteerId, points, reason, volunteer.getPoints());
    }

    /**
     * 更新志愿者信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVolunteer(Volunteer volunteer) {
        Volunteer existing = this.getById(volunteer.getId());
        if (existing == null) {
            throw new RuntimeException("志愿者不存在");
        }
        this.updateById(volunteer);
        log.info("更新志愿者信息：{}", volunteer.getId());
    }
}
