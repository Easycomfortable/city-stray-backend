package com.citystray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citystray.entity.AdoptionApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptionApplyMapper extends BaseMapper<AdoptionApply> {

    /** 查询领养申请列表（关联申请人和动物信息） */
    List<Map<String, Object>> selectApplyList(@Param("params") Map<String, Object> params);
}
