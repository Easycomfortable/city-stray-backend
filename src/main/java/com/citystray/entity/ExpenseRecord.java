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
@TableName("expense_record")
@ApiModel(value = "ExpenseRecord", description = "支出记录表")
public class ExpenseRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("支出类别：MEDICAL/FOOD/OPERATION/OTHER")
    private String category;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("用途说明")
    private String description;

    @ApiModelProperty("关联类型：RESCUE/MEDICAL/ADOPTION/OTHER")
    private String relatedType;

    @ApiModelProperty("关联业务ID")
    private Long relatedId;

    @ApiModelProperty("申请人")
    private String applicant;

    @ApiModelProperty("审批状态：PENDING/APPROVED/REJECTED")
    private String approvalStatus;

    @ApiModelProperty("审批人")
    private String approvalUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("审批时间")
    private LocalDateTime approvalTime;

    @ApiModelProperty("凭证图片")
    private String voucherImages;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("支出日期")
    private LocalDate expenseDate;

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
