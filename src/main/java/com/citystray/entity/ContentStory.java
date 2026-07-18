package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("content_story")
@ApiModel(value = "ContentStory", description = "救助故事实体")
public class ContentStory {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("发布用户ID")
    private Long userId;

    @ApiModelProperty("作者昵称")
    private String authorName;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("故事内容")
    private String content;

    @ApiModelProperty("封面图")
    private String coverImage;

    @ApiModelProperty("状态:PENDING/APPROVED/REJECTED")
    private String status;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

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
