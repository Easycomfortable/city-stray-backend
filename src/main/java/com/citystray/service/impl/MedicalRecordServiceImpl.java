package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.entity.MedicalRecord;
import com.citystray.mapper.MedicalRecordMapper;
import com.citystray.service.MedicalRecordService;
import org.springframework.stereotype.Service;

/**
 * 医疗记录服务实现类
 */
@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord>
        implements MedicalRecordService {

    /**
     * 根据动物ID分页查询医疗记录
     */
    @Override
    public IPage<MedicalRecord> getByAnimalId(Long animalId, Integer page, Integer size) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getAnimalId, animalId)
                .orderByDesc(MedicalRecord::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
