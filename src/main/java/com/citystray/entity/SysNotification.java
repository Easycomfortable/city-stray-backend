package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_notification")
@ApiModel(value = "SysNotification", description = "站内通知实体")
public class SysNotification {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("接收用户ID")
    private Long userId;

    @ApiModelProperty("通知标题")
    private String title;

    @ApiModelProperty("通知内容")
    private String content;

    @ApiModelProperty("类型:SYSTEM/ADOPTION/RESCUE/STORY/DONATION/VOLUNTEER")
    private String type;

    @ApiModelProperty("关联业务类型")
    private String relatedType;

    @ApiModelProperty("关联业务ID")
    private Long relatedId;

    @ApiModelProperty("是否已读")
    private Boolean isRead;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("阅读时间")
    private LocalDateTime readTime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer deleted;
}
