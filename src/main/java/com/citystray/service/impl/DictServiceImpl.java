package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.entity.SysDictData;
import com.citystray.entity.SysDictType;
import com.citystray.mapper.SysDictDataMapper;
import com.citystray.mapper.SysDictTypeMapper;
import com.citystray.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictServiceImpl implements DictService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;

    @Override
    public PageResult<Map<String, Object>> list(Integer page, Integer pageSize) {
        // 分页查询字典类型（@TableLogic 自动过滤已删除记录）
        Page<SysDictType> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDictType::getId);
        Page<SysDictType> typePage = sysDictTypeMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (SysDictType type : typePage.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", type.getId());
            map.put("name", type.getName());
            map.put("code", type.getCode());
            map.put("description", type.getDescription());

            // 查询该类型下的字典数据项
            LambdaQueryWrapper<SysDictData> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(SysDictData::getDictTypeId, type.getId());
            dataWrapper.orderByAsc(SysDictData::getSort);
            List<SysDictData> dataList = sysDictDataMapper.selectList(dataWrapper);

            List<Map<String, Object>> items = new ArrayList<>();
            for (SysDictData data : dataList) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("label", data.getLabel());
                item.put("value", data.getValue());
                item.put("sort", data.getSort());
                item.put("isDefault", data.getIsDefault());
                items.add(item);
            }
            map.put("items", items);

            records.add(map);
        }

        return new PageResult<>(typePage.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public void save(Map<String, Object> data) {
        String name = (String) data.get("name");
        String code = (String) data.get("code");
        String description = (String) data.get("description");
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

        // 判断是新增还是更新（根据 id 是否存在）
        Long typeId = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;

        SysDictType type;
        if (typeId == null) {
            // 新增
            type = new SysDictType();
            type.setName(name);
            type.setCode(code);
            type.setDescription(description);
            type.setCreateTime(LocalDateTime.now());
            sysDictTypeMapper.insert(type);
        } else {
            // 更新
            type = sysDictTypeMapper.selectById(typeId);
            if (type == null) {
                throw new RuntimeException("字典类型不存在");
            }
            type.setName(name);
            type.setCode(code);
            type.setDescription(description);
            type.setUpdateTime(LocalDateTime.now());
            sysDictTypeMapper.updateById(type);

            // 删除原有字典数据项
            LambdaQueryWrapper<SysDictData> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(SysDictData::getDictTypeId, typeId);
            sysDictDataMapper.delete(delWrapper);
        }

        // 保存字典数据项
        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> item : items) {
                SysDictData dictData = new SysDictData();
                dictData.setDictTypeId(type.getId());
                dictData.setLabel((String) item.get("label"));
                dictData.setValue(item.get("value") != null ? item.get("value").toString() : "");
                dictData.setSort(item.get("sort") != null ? Integer.valueOf(item.get("sort").toString()) : 0);
                dictData.setIsDefault(item.get("isDefault") != null ? Integer.valueOf(item.get("isDefault").toString()) : 0);
                dictData.setCreateTime(LocalDateTime.now());
                sysDictDataMapper.insert(dictData);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 删除字典类型
        sysDictTypeMapper.deleteById(id);

        // 删除该类型下的所有字典数据项
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictTypeId, id);
        sysDictDataMapper.delete(wrapper);
    }
}
