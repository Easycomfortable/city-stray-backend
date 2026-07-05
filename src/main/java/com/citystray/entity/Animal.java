package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("animal")
@ApiModel(value = "Animal", description = "动物档案表")
public class Animal {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("动物ID")
    private Long id;

    @ApiModelProperty("动物编号")
    private String animalNo;

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("品种")
    private String breed;

    @ApiModelProperty("性别(1=公,0=母)")
    private Integer gender;

    @ApiModelProperty("年龄估算")
    private String ageEstimate;

    @ApiModelProperty("体重(kg)")
    private BigDecimal weight;

    @ApiModelProperty("毛色")
    private String color;

    @ApiModelProperty("是否绝育(0=否,1=是)")
    private Integer isNeutered;

    @ApiModelProperty("芯片编号")
    private String chipNo;

    @ApiModelProperty("健康状态(treating/recovering/adoptable/adopted/deceased)")
    private String healthStatus;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

    @ApiModelProperty("描述信息")
    private String description;

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
