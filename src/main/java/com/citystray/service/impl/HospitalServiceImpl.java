package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.Hospital;
import com.citystray.mapper.HospitalMapper;
import com.citystray.service.HospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 合作医院服务实现类
 */
@Slf4j
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital>
        implements HospitalService {

    /**
     * 分页查询合作医院列表
     */
    @Override
    public PageResult<Hospital> getHospitalList(Integer page, Integer size, String name, String district, String status) {
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            wrapper.like(Hospital::getName, name);
        }
        if (district != null && !district.isEmpty()) {
            wrapper.eq(Hospital::getDistrict, district);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Hospital::getStatus, Integer.parseInt(status));
        }

        wrapper.orderByDesc(Hospital::getCreateTime);

        IPage<Hospital> pageResult = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
    }

    /**
     * 获取医院详情
     */
    @Override
    public Hospital getHospitalById(Long id) {
        Hospital hospital = this.getById(id);
        if (hospital == null) {
            throw new RuntimeException("医院信息不存在");
        }
        return hospital;
    }

    /**
     * 新增医院
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addHospital(Hospital hospital) {
        this.save(hospital);
        log.info("新增合作医院：{}", hospital.getName());
        return hospital.getId();
    }

    /**
     * 更新医院信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHospital(Hospital hospital) {
        Hospital existing = this.getById(hospital.getId());
        if (existing == null) {
            throw new RuntimeException("医院信息不存在");
        }
        this.updateById(hospital);
        log.info("更新合作医院：{}", hospital.getId());
    }

    /**
     * 删除医院（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHospital(Long id) {
        Hospital hospital = this.getById(id);
        if (hospital == null) {
            throw new RuntimeException("医院信息不存在");
        }
        this.removeById(id);
        log.info("逻辑删除合作医院：{}", id);
    }

    /**
     * 更新合作状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Hospital hospital = this.getById(id);
        if (hospital == null) {
            throw new RuntimeException("医院信息不存在");
        }
        hospital.setStatus(Integer.parseInt(status));
        this.updateById(hospital);
        log.info("合作医院[{}]状态更新为：{}", id, status);
    }
}
