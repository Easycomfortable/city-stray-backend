package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.Volunteer;

import java.util.Map;

/**
 * 志愿者服务接口
 */
public interface VolunteerService extends IService<Volunteer> {

    /**
     * 分页查询志愿者列表
     */
    PageResult<Volunteer> getVolunteerList(Integer page, Integer size, String authStatus, String keyword, String skillTag);

    /**
     * 获取志愿者详情
     */
    Volunteer getVolunteerById(Long id);

    /**
     * 审核志愿者认证
     */
    void reviewVolunteer(Long id, String authStatus, String rejectReason);

    /**
     * 获取积分记录
     */
    PageResult<Map<String, Object>> getPointsRecords(Integer page, Integer size, Long volunteerId);

    /**
     * 添加积分
     */
    void addPoints(Long volunteerId, Integer points, String reason);

    /**
     * 更新志愿者信息
     */
    void updateVolunteer(Volunteer volunteer);
}
