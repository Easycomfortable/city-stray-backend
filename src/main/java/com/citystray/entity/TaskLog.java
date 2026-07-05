package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("task_log")
@ApiModel(value = "TaskLog", description = "任务日志表")
public class TaskLog {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("任务日志ID")
    private Long id;

    @ApiModelProperty("关联任务ID")
    private Long taskId;

    @ApiModelProperty("关联志愿者ID")
    private Long volunteerId;

    @ApiModelProperty("操作类型")
    private String action;

    @ApiModelProperty("日志内容")
    private String content;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

    @ApiModelProperty("服务时长(小时)")
    private BigDecimal serviceHours;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableLogic
    @ApiModelProperty("逻辑删除(0=未删除,1=已删除)")
    private Integer deleted;
}
