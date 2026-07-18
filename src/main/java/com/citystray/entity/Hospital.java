package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hospital")
@ApiModel(value = "Hospital", description = "合作医院表")
public class Hospital {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("医院ID")
    private Long id;

    @ApiModelProperty("医院名称")
    private String name;

    @ApiModelProperty("医院地址")
    private String address;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("联系人")
    private String contactPerson;

    @ApiModelProperty("营业执照号")
    private String licenseNo;

    @ApiModelProperty("所属区域")
    private String district;

    @ApiModelProperty("经度")
    private java.math.BigDecimal longitude;

    @ApiModelProperty("纬度")
    private java.math.BigDecimal latitude;

    @ApiModelProperty("状态(1=正常,0=暂停,2=终止)")
    private Integer status;

    @ApiModelProperty("优惠信息")
    private String discountInfo;

    @ApiModelProperty("价格标准")
    private String priceStandard;

    @ApiModelProperty("当月就诊量")
    private Integer monthlyVisits;

    @ApiModelProperty("累计就诊量")
    private Integer totalVisits;

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
