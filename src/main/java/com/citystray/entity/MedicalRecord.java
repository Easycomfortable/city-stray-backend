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
@TableName("medical_record")
@ApiModel(value = "MedicalRecord", description = "医疗记录表")
public class MedicalRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("医疗记录ID")
    private Long id;

    @ApiModelProperty("关联动物ID")
    private Long animalId;

    @ApiModelProperty("关联医院ID")
    private Long hospitalId;

    @ApiModelProperty("记录类型(vaccine/diagnosis/deworming/surgery)")
    private String recordType;

    @ApiModelProperty("诊断结果")
    private String diagnosis;

    @ApiModelProperty("治疗方案")
    private String treatment;

    @ApiModelProperty("用药信息")
    private String medication;

    @ApiModelProperty("主治医生姓名")
    private String doctorName;

    @ApiModelProperty("费用")
    private BigDecimal cost;

    @ApiModelProperty("就诊日期")
    private LocalDate recordDate;

    @ApiModelProperty("备注")
    private String notes;

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
