package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("revisit_record")
@ApiModel(value = "RevisitRecord", description = "回访记录表")
public class RevisitRecord {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("回访记录ID")
    private Long id;

    @ApiModelProperty("关联领养申请ID")
    private Long applyId;

    @ApiModelProperty("回访日期")
    private LocalDate revisitDate;

    @ApiModelProperty("状况描述")
    private String conditionDesc;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

    @ApiModelProperty("健康状况")
    private String healthStatus;

    @ApiModelProperty("是否正常(0=异常,1=正常)")
    private Integer isNormal;

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
