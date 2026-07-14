package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("content_article")
@ApiModel(value = "ContentArticle", description = "知识科普文章实体")
public class ContentArticle {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("分类:GUIDE/RESCUE/TNR/MEDICAL")
    private String category;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("正文内容")
    private String content;

    @ApiModelProperty("封面图")
    private String coverImage;

    @ApiModelProperty("标签(JSON数组)")
    private String tags;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("状态:DRAFT/PUBLISHED")
    private String status;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发布时间")
    private LocalDateTime publishTime;

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
