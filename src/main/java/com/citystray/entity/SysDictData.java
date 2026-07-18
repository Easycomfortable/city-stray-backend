package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dict_data")
@ApiModel(value = "SysDictData", description = "字典数据表")
public class SysDictData {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("字典数据ID")
    private Long id;

    @ApiModelProperty("字典类型ID")
    private Long dictTypeId;

    @ApiModelProperty("字典标签")
    private String label;

    @ApiModelProperty("字典键值")
    private String value;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态(0=禁用,1=正常)")
    private Integer status;

    @ApiModelProperty("是否默认(0=否,1=是)")
    private Integer isDefault;

    @ApiModelProperty("备注")
    private String remark;

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
