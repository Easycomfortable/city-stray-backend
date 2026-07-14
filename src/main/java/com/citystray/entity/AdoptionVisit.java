package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("adoption_visit")
@ApiModel(value = "AdoptionVisit", description = "领养回访表")
public class AdoptionVisit {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("回访记录ID")
    private Long id;

    @ApiModelProperty("关联领养申请ID")
    private Long applyId;

    @ApiModelProperty("回访人姓名")
    private String visitorName;

    @ApiModelProperty("回访日期")
    private LocalDate visitDate;

    @ApiModelProperty("回访结果(good/fair/poor)")
    private String result;

    @ApiModelProperty("评估说明")
    private String evaluation;

    @ApiModelProperty("照片(JSON字符串)")
    private String photos;

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
