package com.citystray.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("points_rule")
public class PointsRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleKey;

    private String ruleName;

    private BigDecimal pointsValue;

    private String unit;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
