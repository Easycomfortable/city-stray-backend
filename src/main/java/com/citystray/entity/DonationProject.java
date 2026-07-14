package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("donation_project")
@ApiModel(value = "DonationProject", description = "捐赠项目表")
public class DonationProject {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目描述")
    private String description;

    @ApiModelProperty("封面图片URL")
    private String coverImage;

    @ApiModelProperty("目标金额")
    private BigDecimal targetAmount;

    @ApiModelProperty("已筹金额")
    private BigDecimal raisedAmount;

    @ApiModelProperty("捐赠人数")
    private Integer donorCount;

    @ApiModelProperty("状态：ACTIVE-进行中 ENDED-已结束")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer deleted;
}
