package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("donation_record")
@ApiModel(value = "DonationRecord", description = "捐赠记录表")
public class DonationRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("关联捐赠项目ID")
    private Long projectId;

    @ApiModelProperty("捐赠用户ID")
    private Long userId;

    @ApiModelProperty("捐赠人姓名")
    private String donorName;

    @ApiModelProperty("捐赠人手机号")
    private String donorPhone;

    @ApiModelProperty("是否匿名：0-否 1-是")
    private Integer anonymous;

    @ApiModelProperty("捐赠金额")
    private BigDecimal amount;

    @ApiModelProperty("支付方式：WECHAT/ALIPAY/OFFLINE")
    private String paymentMethod;

    @ApiModelProperty("支付流水号")
    private String paymentNo;

    @ApiModelProperty("微信支付交易号")
    private String transactionId;

    @ApiModelProperty("状态：PENDING/SUCCESS/REFUNDED/FAILED")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("支付时间")
    private LocalDateTime transactionTime;

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
