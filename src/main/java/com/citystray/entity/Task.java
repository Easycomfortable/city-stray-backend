package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("task")
@ApiModel(value = "Task", description = "任务表")
public class Task {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("任务ID")
    private Long id;

    @ApiModelProperty("关联工单ID")
    private Long orderId;

    @ApiModelProperty("关联志愿者ID")
    private Long volunteerId;

    @ApiModelProperty("任务类型(respond/catch/transport/foster)")
    private String taskType;

    @ApiModelProperty("状态(available/accepted/in_progress/completed/cancelled)")
    private String status;

    @ApiModelProperty("积分奖励")
    private Integer pointsReward;

    @ApiModelProperty("任务描述")
    private String description;

    @ApiModelProperty("任务地点")
    private String location;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

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
