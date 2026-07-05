package com.citystray.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citystray.common.PageResult;
import com.citystray.entity.Animal;

import java.util.Map;

/**
 * 动物信息服务接口
 */
public interface AnimalService extends IService<Animal> {

    /**
     * 分页查询动物列表
     */
    PageResult<Animal> getAnimalList(Integer page, Integer size, String name, String breed, String healthStatus, Boolean isNeutered);

    /**
     * 获取动物详情
     */
    Animal getAnimalById(Long id);

    /**
     * 新增动物
     */
    Long addAnimal(Animal animal);

    /**
     * 更新动物信息
     */
    void updateAnimal(Animal animal);

    /**
     * 删除动物（逻辑删除）
     */
    void deleteAnimal(Long id);

    /**
     * 更新动物健康状态
     */
    void updateHealthStatus(Long id, String status);

    /**
     * 获取动物医疗记录
     */
    PageResult<Map<String, Object>> getMedicalRecords(Long animalId, Integer page, Integer size);
}
