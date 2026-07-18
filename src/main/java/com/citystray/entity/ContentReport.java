package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("content_report")
@ApiModel(value = "ContentReport", description = "内容举报实体")
public class ContentReport {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("举报人ID")
    private Long reporterId;

    @ApiModelProperty("举报人昵称")
    private String reporterName;

    @ApiModelProperty("举报对象类型:POST/COMMENT")
    private String targetType;

    @ApiModelProperty("举报对象ID")
    private Long targetId;

    @ApiModelProperty("被举报内容摘要")
    private String targetContent;

    @ApiModelProperty("举报原因")
    private String reason;

    @ApiModelProperty("状态:PENDING/RESOLVED/DISMISSED")
    private String status;

    @ApiModelProperty("处理人")
    private String handlerName;

    @ApiModelProperty("处理备注")
    private String handleRemark;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("处理时间")
    private LocalDateTime handleTime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer deleted;
}
