package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_dict_type")
@ApiModel(value = "SysDictType", description = "字典类型表")
public class SysDictType {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("字典类型ID")
    private Long id;

    @ApiModelProperty("字典名称")
    private String name;

    @ApiModelProperty("字典编码")
    private String code;

    @ApiModelProperty("字典描述")
    private String description;

    @ApiModelProperty("状态(0=禁用,1=正常)")
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

    @TableField(exist = false)
    @ApiModelProperty("字典数据列表")
    private List<SysDictData> items;
}
