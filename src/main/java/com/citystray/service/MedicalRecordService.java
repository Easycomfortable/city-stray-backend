package com.citystray.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.entity.MedicalRecord;

/**
 * 医疗记录服务接口
 */
public interface MedicalRecordService extends IService<MedicalRecord> {

    /**
     * 根据动物ID分页查询医疗记录
     *
     * @param animalId 动物ID
     * @param page     当前页码
     * @param size     每页数量
     * @return 分页医疗记录
     */
    IPage<MedicalRecord> getByAnimalId(Long animalId, Integer page, Integer size);
}
