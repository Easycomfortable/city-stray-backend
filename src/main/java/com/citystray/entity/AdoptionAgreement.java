package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("adoption_agreement")
@ApiModel(value = "AdoptionAgreement", description = "领养协议表")
public class AdoptionAgreement {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("领养协议ID")
    private Long id;

    @ApiModelProperty("关联领养申请ID")
    private Long applyId;

    @ApiModelProperty("关联动物ID")
    private Long animalId;

    @ApiModelProperty("领养人姓名")
    private String adopterName;

    @ApiModelProperty("领养人电话")
    private String adopterPhone;

    @ApiModelProperty("领养人身份证号")
    private String adopterIdCard;

    @ApiModelProperty("签署日期")
    private LocalDate signDate;

    @ApiModelProperty("协议编号")
    private String agreementNo;

    @ApiModelProperty("协议内容")
    private String content;

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
