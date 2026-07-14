package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stray_report")
@ApiModel(value = "StrayReport", description = "流浪动物上报表")
public class StrayReport {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("上报记录ID")
    private Long id;

    @ApiModelProperty("上报用户ID")
    private Long userId;

    @ApiModelProperty("上报编号")
    private String reportNo;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("所属区域")
    private String district;

    @ApiModelProperty("动物类型(cat/dog/other)")
    private String animalType;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("是否受伤(0=否,1=是)")
    private Integer isInjured;

    @ApiModelProperty("是否亲人(0=否,1=是)")
    private Integer isFriendly;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

    @ApiModelProperty("状态(0=待处理,1=已处理)")
    private Integer status;

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
