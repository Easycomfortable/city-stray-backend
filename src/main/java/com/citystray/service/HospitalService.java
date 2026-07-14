package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.Hospital;

/**
 * 合作医院服务接口
 */
public interface HospitalService extends IService<Hospital> {

    /**
     * 分页查询合作医院列表
     */
    PageResult<Hospital> getHospitalList(Integer page, Integer size, String name, String district, String status);

    /**
     * 获取医院详情
     */
    Hospital getHospitalById(Long id);

    /**
     * 新增医院
     */
    Long addHospital(Hospital hospital);

    /**
     * 更新医院信息
     */
    void updateHospital(Hospital hospital);

    /**
     * 删除医院（逻辑删除）
     */
    void deleteHospital(Long id);

    /**
     * 更新合作状态
     */
    void updateStatus(Long id, String status);
}
