package com.citystray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citystray.entity.RescueOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RescueOrderMapper extends BaseMapper<RescueOrder> {

    /** 查询工单列表（关联上报人、志愿者、动物信息） */
    List<Map<String, Object>> selectOrderList(@Param("params") Map<String, Object> params);

    /** 获取工单时间线（所有操作记录） */
    List<Map<String, Object>> selectOrderTimeline(@Param("orderId") Long orderId,
                                                  @Param("reportId") Long reportId);
}
