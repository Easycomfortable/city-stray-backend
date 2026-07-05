package com.citystray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citystray.entity.Animal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {

    /** 查询动物列表（含医疗记录和领养申请计数） */
    List<Map<String, Object>> selectAnimalList(@Param("params") Map<String, Object> params);
}
