package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.Animal;
import com.citystray.entity.MedicalRecord;
import com.citystray.mapper.AnimalMapper;
import com.citystray.mapper.MedicalRecordMapper;
import com.citystray.service.AnimalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 动物信息服务实现类
 */
@Slf4j
@Service
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal>
        implements AnimalService {

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    /**
     * 分页查询动物列表
     */
    @Override
    public PageResult<Animal> getAnimalList(Integer page, Integer size, String name, String breed, String healthStatus, Boolean isNeutered) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            wrapper.like(Animal::getName, name);
        }
        if (breed != null && !breed.isEmpty()) {
            wrapper.like(Animal::getBreed, breed);
        }
        if (healthStatus != null && !healthStatus.isEmpty()) {
            wrapper.eq(Animal::getHealthStatus, healthStatus);
        }
        if (isNeutered != null) {
            wrapper.eq(Animal::getIsNeutered, isNeutered ? 1 : 0);
        }

        wrapper.orderByDesc(Animal::getCreateTime);

        IPage<Animal> pageResult = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
    }

    /**
     * 获取动物详情
     */
    @Override
    public Animal getAnimalById(Long id) {
        Animal animal = this.getById(id);
        if (animal == null) {
            throw new RuntimeException("动物信息不存在");
        }
        return animal;
    }

    /**
     * 新增动物
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAnimal(Animal animal) {
        // 生成动物编号：AN + yyyyMMdd + 3位序列号
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AN" + dateStr;
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Animal::getAnimalNo, prefix);
        long count = this.count(wrapper);
        String seq = String.format("%03d", count + 1);
        animal.setAnimalNo(prefix + seq);

        this.save(animal);
        log.info("新增动物档案：{}，编号：{}", animal.getName(), animal.getAnimalNo());
        return animal.getId();
    }

    /**
     * 更新动物信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnimal(Animal animal) {
        Animal existing = this.getById(animal.getId());
        if (existing == null) {
            throw new RuntimeException("动物信息不存在");
        }
        this.updateById(animal);
        log.info("更新动物档案：{}", animal.getId());
    }

    /**
     * 删除动物（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnimal(Long id) {
        Animal animal = this.getById(id);
        if (animal == null) {
            throw new RuntimeException("动物信息不存在");
        }
        this.removeById(id);
        log.info("逻辑删除动物档案：{}", id);
    }

    /**
     * 更新动物健康状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHealthStatus(Long id, String status) {
        Animal animal = this.getById(id);
        if (animal == null) {
            throw new RuntimeException("动物信息不存在");
        }
        String oldStatus = animal.getHealthStatus();
        animal.setHealthStatus(status);
        this.updateById(animal);

        if ("adoptable".equals(status)) {
            log.info("动物[{}]（{}）健康状态变更为可领养，可触发领养通知推送", id, animal.getName());
        } else {
            log.info("动物[{}]健康状态变更：{} -> {}", id, oldStatus, status);
        }
    }

    /**
     * 获取动物医疗记录
     */
    @Override
    public PageResult<Map<String, Object>> getMedicalRecords(Long animalId, Integer page, Integer size) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getAnimalId, animalId)
                .orderByDesc(MedicalRecord::getCreateTime);

        IPage<MedicalRecord> pageResult = new Page<>(page, size);
        IPage<MedicalRecord> result = medicalRecordMapper.selectPage(pageResult, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (MedicalRecord record : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", record.getId());
            map.put("recordType", record.getRecordType());
            map.put("recordDate", record.getRecordDate());
            map.put("diagnosis", record.getDiagnosis());
            map.put("treatment", record.getTreatment());
            map.put("medication", record.getMedication());
            map.put("doctorName", record.getDoctorName());
            map.put("cost", record.getCost());
            map.put("notes", record.getNotes());
            map.put("hospitalId", record.getHospitalId());
            map.put("createTime", record.getCreateTime());
            records.add(map);
        }

        return new PageResult<>((long) result.getTotal(), records);
    }
}
