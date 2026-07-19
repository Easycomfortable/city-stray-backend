package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("content_comment")
@ApiModel(value = "ContentComment", description = "评论实体")
public class ContentComment {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("评论ID")
    private Long id;

    @ApiModelProperty("故事/帖子ID")
    private Long storyId;

    @ApiModelProperty("评论用户ID")
    private Long userId;

    @ApiModelProperty("评论用户昵称")
    private String nickname;

    @ApiModelProperty("评论用户头像")
    private String avatar;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("父评论ID(回复用)")
    private Long parentId;

    @ApiModelProperty("被回复用户昵称")
    private String replyTo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
