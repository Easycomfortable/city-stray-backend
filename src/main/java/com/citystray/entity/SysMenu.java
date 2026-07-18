package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
@ApiModel(value = "SysMenu", description = "菜单权限表")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("菜单ID")
    private Long id;

    @ApiModelProperty("父菜单ID")
    private Long parentId;

    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("路由路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("权限标识")
    private String permission;

    @ApiModelProperty("菜单类型(0=目录,1=菜单,2=按钮)")
    private Integer type;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否可见(0=隐藏,1=显示)")
    private Integer visible;

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
    @ApiModelProperty("子菜单列表")
    private List<SysMenu> children;
}
