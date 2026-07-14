package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adoption_apply")
@ApiModel(value = "AdoptionApply", description = "领养申请表")
public class AdoptionApply {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("领养申请ID")
    private Long id;

    @ApiModelProperty("申请编号")
    private String applyNo;

    @ApiModelProperty("申请用户ID")
    private Long userId;

    @ApiModelProperty("关联动物ID")
    private Long animalId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("职业")
    private String occupation;

    @ApiModelProperty("居住地址")
    private String address;

    @ApiModelProperty("居住环境描述")
    private String livingEnvironment;

    @ApiModelProperty("养宠经验")
    private String petExperience;

    @ApiModelProperty("家人是否同意(0=否,1=是)")
    private Integer familyConsent;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

    @ApiModelProperty("阶段(submitted/reviewing/approved/rejected/visiting/trial/adopted)")
    private String stage;

    @ApiModelProperty("审核人用户ID")
    private Long reviewUserId;

    @ApiModelProperty("审核备注")
    private String reviewRemark;

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
