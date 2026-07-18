package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wx_subscribe_record")
@ApiModel(value = "WxSubscribeRecord", description = "微信订阅消息记录")
public class WxSubscribeRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("openid")
    private String openid;

    @ApiModelProperty("模板ID")
    private String templateId;

    @ApiModelProperty("模板数据JSON")
    private String dataJson;

    @ApiModelProperty("跳转页面")
    private String page;

    @ApiModelProperty("状态:PENDING/SENT/FAILED")
    private String status;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发送时间")
    private LocalDateTime sendTime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer deleted;
}
