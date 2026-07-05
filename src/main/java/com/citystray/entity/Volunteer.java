package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("volunteer")
@ApiModel(value = "Volunteer", description = "志愿者表")
public class Volunteer {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("志愿者ID")
    private Long id;

    @ApiModelProperty("关联用户ID")
    private Long userId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("技能标签(JSON字符串)")
    private String skillTags;

    @ApiModelProperty("累计服务时长(小时)")
    private BigDecimal totalHours;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("认证状态(0=待审核,1=已通过,2=已驳回,3=已禁用)")
    private Integer authStatus;

    @ApiModelProperty("驳回原因")
    private String rejectReason;

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
