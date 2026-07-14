package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
@ApiModel(value = "SysLog", description = "系统日志表")
public class SysLog {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("日志ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("操作类型")
    private String type;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("请求方法")
    private String method;

    @ApiModelProperty("请求URL")
    private String url;

    @ApiModelProperty("请求IP")
    private String ip;

    @ApiModelProperty("执行时长(ms)")
    private Integer duration;

    @ApiModelProperty("是否成功(0=失败,1=成功)")
    private Integer success;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
