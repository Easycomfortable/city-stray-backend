package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rescue_order")
@ApiModel(value = "RescueOrder", description = "救援工单表")
public class RescueOrder {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("工单ID")
    private Long id;

    @ApiModelProperty("工单编号")
    private String orderNo;

    @ApiModelProperty("关联上报记录ID")
    private Long reportId;

    @ApiModelProperty("关联动物ID")
    private Long animalId;

    @ApiModelProperty("关联志愿者ID")
    private Long volunteerId;

    @ApiModelProperty("关联医院ID")
    private Long hospitalId;

    @ApiModelProperty("状态(pending/responded/catching/treating/recovering/adoptable/adopted/closed)")
    private String status;

    @ApiModelProperty("所属区域")
    private String district;

    @ApiModelProperty("描述信息")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("分配时间")
    private LocalDateTime assignedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("响应时间")
    private LocalDateTime respondTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("关闭时间")
    private LocalDateTime closeTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @ApiModelProperty("逻辑删除(0=未删除,1=已删除)")
    private Integer deleted;
}
