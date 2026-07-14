package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.AdoptionApply;

import java.util.List;
import java.util.Map;

/**
 * 领养申请服务接口
 */
public interface AdoptionApplyService extends IService<AdoptionApply> {

    /**
     * 分页查询领养申请列表
     *
     * @param page     当前页码
     * @param size     每页数量
     * @param stage    申请阶段（可选）
     * @param realName 申请人姓名（可选，模糊查询）
     * @return 分页结果
     */
    PageResult<Map<String, Object>> getAdoptionList(Integer page, Integer size, String stage, String realName);

    /**
     * 获取领养申请详情（含家访记录、协议、动物信息）
     *
     * @param id 申请ID
     * @return 申请详情
     */
    Map<String, Object> getAdoptionDetail(Long id);

    /**
     * 审核领养申请
     *
     * @param id           申请ID
     * @param stage        目标阶段
     * @param remark       审核备注
     * @param rejectReason 拒绝原因（通过时可为空）
     */
    void reviewAdoption(Long id, String stage, String remark, String rejectReason);

    /**
     * 安排家访
     *
     * @param applyId    申请ID
     * @param visitorName 回访人姓名
     * @param visitDate  回访日期（yyyy-MM-dd格式字符串）
     * @param notes      备注
     */
    void arrangeVisit(Long applyId, String visitorName, String visitDate, String notes);

    /**
     * 确认正式领养
     *
     * @param applyId 申请ID
     */
    void confirmAdoption(Long applyId);

    /**
     * 获取家访记录
     *
     * @param applyId 申请ID
     * @return 家访记录列表
     */
    List<Map<String, Object>> getVisitRecords(Long applyId);

    /**
     * 获取领养协议
     *
     * @param applyId 申请ID
     * @return 协议信息
     */
    Map<String, Object> getAgreement(Long applyId);
}
