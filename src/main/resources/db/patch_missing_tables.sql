-- ============================================================
-- 补充 D 模块缺失的 15 张表  +  修复管理员密码
-- 使用方式：在 Navicat 中打开此文件，选择 city_stray 数据库，全部执行
-- ============================================================

-- ============================================================
-- 0. 修复管理员密码（使 admin / admin123 能登录）
-- ============================================================
UPDATE `user` SET `password` = '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK' WHERE `username` = 'admin';
UPDATE `user` SET `password` = '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK' WHERE `username` = 'zhangsan';
UPDATE `user` SET `password` = '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK' WHERE `username` = 'lisi';
UPDATE `user` SET `password` = '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK' WHERE `username` = 'wangwei';
UPDATE `user` SET `password` = '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK' WHERE `username` = 'doctorliu';

-- ============================================================
-- 1. 系统日志表
-- ============================================================
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
  `user_id`     BIGINT       DEFAULT NULL             COMMENT '用户ID',
  `username`    VARCHAR(50)  DEFAULT NULL             COMMENT '用户名',
  `module`      VARCHAR(50)  DEFAULT NULL             COMMENT '模块',
  `type`        VARCHAR(20)  DEFAULT NULL             COMMENT '操作类型',
  `content`     VARCHAR(500) DEFAULT NULL             COMMENT '操作内容',
  `method`      VARCHAR(200) DEFAULT NULL             COMMENT '请求方法',
  `url`         VARCHAR(500) DEFAULT NULL             COMMENT '请求URL',
  `ip`          VARCHAR(50)  DEFAULT NULL             COMMENT '请求IP',
  `duration`    INT          DEFAULT NULL             COMMENT '执行时长(ms)',
  `success`     TINYINT      DEFAULT NULL             COMMENT '是否成功(0=失败,1=成功)',
  `error_msg`   TEXT         DEFAULT NULL             COMMENT '错误信息',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统日志表';

-- ============================================================
-- 2. 站内通知表
-- ============================================================
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`      BIGINT       DEFAULT NULL             COMMENT '接收用户ID',
  `title`        VARCHAR(100) DEFAULT NULL             COMMENT '通知标题',
  `content`      VARCHAR(500) DEFAULT NULL             COMMENT '通知内容',
  `type`         VARCHAR(20)  DEFAULT NULL             COMMENT '类型:SYSTEM/ADOPTION/RESCUE/STORY/DONATION/VOLUNTEER',
  `related_type` VARCHAR(50)  DEFAULT NULL             COMMENT '关联业务类型',
  `related_id`   BIGINT       DEFAULT NULL             COMMENT '关联业务ID',
  `is_read`      TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '是否已读',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time`    DATETIME     DEFAULT NULL             COMMENT '阅读时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站内通知表';

-- ============================================================
-- 3. 菜单权限表
-- ============================================================
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '菜单ID',
  `parent_id`   BIGINT       DEFAULT 0                COMMENT '父菜单ID',
  `name`        VARCHAR(50)  NOT NULL                 COMMENT '菜单名称',
  `icon`        VARCHAR(100) DEFAULT NULL             COMMENT '菜单图标',
  `path`        VARCHAR(200) DEFAULT NULL             COMMENT '路由路径',
  `component`   VARCHAR(200) DEFAULT NULL             COMMENT '组件路径',
  `permission`  VARCHAR(100) DEFAULT NULL             COMMENT '权限标识',
  `type`        TINYINT      NOT NULL DEFAULT 1       COMMENT '菜单类型(0=目录,1=菜单,2=按钮)',
  `sort`        INT          NOT NULL DEFAULT 0       COMMENT '排序',
  `visible`     TINYINT      NOT NULL DEFAULT 1       COMMENT '是否可见(0=隐藏,1=显示)',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ============================================================
-- 4. 角色表
-- ============================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '角色ID',
  `name`        VARCHAR(50)  NOT NULL                 COMMENT '角色名称',
  `code`        VARCHAR(50)  NOT NULL                 COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL             COMMENT '角色描述',
  `sort`        INT          NOT NULL DEFAULT 0       COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态(0=禁用,1=正常)',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ============================================================
-- 5. 字典类型表
-- ============================================================
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '字典类型ID',
  `name`        VARCHAR(100) NOT NULL                 COMMENT '字典名称',
  `code`        VARCHAR(100) NOT NULL                 COMMENT '字典编码',
  `description` VARCHAR(200) DEFAULT NULL             COMMENT '字典描述',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态(0=禁用,1=正常)',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- ============================================================
-- 6. 字典数据表
-- ============================================================
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '字典数据ID',
  `dict_type_id` BIGINT       NOT NULL                 COMMENT '字典类型ID',
  `label`        VARCHAR(100) NOT NULL                 COMMENT '字典标签',
  `value`        VARCHAR(100) NOT NULL                 COMMENT '字典键值',
  `sort`         INT          NOT NULL DEFAULT 0       COMMENT '排序',
  `status`       TINYINT      NOT NULL DEFAULT 1       COMMENT '状态(0=禁用,1=正常)',
  `is_default`   TINYINT      NOT NULL DEFAULT 0       COMMENT '是否默认(0=否,1=是)',
  `remark`       VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_id` (`dict_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- ============================================================
-- 7. 知识科普文章表
-- ============================================================
DROP TABLE IF EXISTS `content_article`;
CREATE TABLE `content_article` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `title`        VARCHAR(200) NOT NULL                 COMMENT '标题',
  `category`     VARCHAR(20)  DEFAULT NULL             COMMENT '分类:GUIDE/RESCUE/TNR/MEDICAL',
  `summary`      VARCHAR(500) DEFAULT NULL             COMMENT '摘要',
  `content`      TEXT         DEFAULT NULL             COMMENT '正文内容',
  `cover_image`  VARCHAR(500) DEFAULT NULL             COMMENT '封面图',
  `tags`         VARCHAR(500) DEFAULT NULL             COMMENT '标签(JSON数组)',
  `author`       VARCHAR(50)  DEFAULT NULL             COMMENT '作者',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
  `view_count`   INT          NOT NULL DEFAULT 0       COMMENT '浏览量',
  `publish_time` DATETIME     DEFAULT NULL             COMMENT '发布时间',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识科普文章表';

-- ============================================================
-- 8. 轮播图表
-- ============================================================
DROP TABLE IF EXISTS `content_banner`;
CREATE TABLE `content_banner` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `title`       VARCHAR(100) DEFAULT NULL             COMMENT '标题',
  `image_url`   VARCHAR(500) DEFAULT NULL             COMMENT '图片URL',
  `link_url`    VARCHAR(500) DEFAULT NULL             COMMENT '跳转链接',
  `sort`        INT          NOT NULL DEFAULT 0       COMMENT '排序值',
  `enabled`     TINYINT(1)   NOT NULL DEFAULT 1       COMMENT '是否启用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='轮播图表';

-- ============================================================
-- 9. 公告表
-- ============================================================
DROP TABLE IF EXISTS `content_notice`;
CREATE TABLE `content_notice` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `title`        VARCHAR(200) NOT NULL                 COMMENT '标题',
  `content`      TEXT         DEFAULT NULL             COMMENT '内容',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
  `publish_time` DATETIME     DEFAULT NULL             COMMENT '发布时间',
  `view_count`   INT          NOT NULL DEFAULT 0       COMMENT '浏览量',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- ============================================================
-- 10. 内容举报表
-- ============================================================
DROP TABLE IF EXISTS `content_report`;
CREATE TABLE `content_report` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `reporter_id`     BIGINT       DEFAULT NULL             COMMENT '举报人ID',
  `reporter_name`   VARCHAR(50)  DEFAULT NULL             COMMENT '举报人昵称',
  `target_type`     VARCHAR(20)  DEFAULT NULL             COMMENT '举报对象类型:POST/COMMENT',
  `target_id`       BIGINT       DEFAULT NULL             COMMENT '举报对象ID',
  `target_content`  VARCHAR(500) DEFAULT NULL             COMMENT '被举报内容摘要',
  `reason`          VARCHAR(500) DEFAULT NULL             COMMENT '举报原因',
  `status`          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/RESOLVED/DISMISSED',
  `handler_name`    VARCHAR(50)  DEFAULT NULL             COMMENT '处理人',
  `handle_remark`   VARCHAR(500) DEFAULT NULL             COMMENT '处理备注',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time`     DATETIME     DEFAULT NULL             COMMENT '处理时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_reporter_id` (`reporter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容举报表';

-- ============================================================
-- 11. 救助故事表
-- ============================================================
DROP TABLE IF EXISTS `content_story`;
CREATE TABLE `content_story` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`     BIGINT       DEFAULT NULL             COMMENT '发布用户ID',
  `author_name` VARCHAR(50)  DEFAULT NULL             COMMENT '作者昵称',
  `title`       VARCHAR(200) NOT NULL                 COMMENT '标题',
  `content`     TEXT         DEFAULT NULL             COMMENT '故事内容',
  `cover_image` VARCHAR(500) DEFAULT NULL             COMMENT '封面图',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/APPROVED/REJECTED',
  `view_count`  INT          NOT NULL DEFAULT 0       COMMENT '浏览量',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='救助故事表';

-- ============================================================
-- 12. 捐赠项目表
-- ============================================================
DROP TABLE IF EXISTS `donation_project`;
CREATE TABLE `donation_project` (
  `id`            BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `name`          VARCHAR(100)   NOT NULL                 COMMENT '项目名称',
  `description`   TEXT           DEFAULT NULL             COMMENT '项目描述',
  `cover_image`   VARCHAR(500)   DEFAULT NULL             COMMENT '封面图片URL',
  `target_amount` DECIMAL(12,2)  DEFAULT NULL             COMMENT '目标金额',
  `raised_amount` DECIMAL(12,2)  NOT NULL DEFAULT 0.00   COMMENT '已筹金额',
  `donor_count`   INT            NOT NULL DEFAULT 0       COMMENT '捐赠人数',
  `status`        VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中 ENDED-已结束',
  `start_date`    DATE           DEFAULT NULL             COMMENT '开始日期',
  `end_date`      DATE           DEFAULT NULL             COMMENT '结束日期',
  `create_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='捐赠项目表';

-- ============================================================
-- 13. 捐赠记录表
-- ============================================================
DROP TABLE IF EXISTS `donation_record`;
CREATE TABLE `donation_record` (
  `id`               BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `project_id`       BIGINT         DEFAULT NULL             COMMENT '关联捐赠项目ID',
  `user_id`          BIGINT         DEFAULT NULL             COMMENT '捐赠用户ID',
  `donor_name`       VARCHAR(50)    DEFAULT NULL             COMMENT '捐赠人姓名',
  `donor_phone`      VARCHAR(20)    DEFAULT NULL             COMMENT '捐赠人手机号',
  `anonymous`        TINYINT        NOT NULL DEFAULT 0       COMMENT '是否匿名：0-否 1-是',
  `amount`           DECIMAL(12,2)  NOT NULL                 COMMENT '捐赠金额',
  `payment_method`   VARCHAR(20)    DEFAULT NULL             COMMENT '支付方式：WECHAT/ALIPAY/OFFLINE',
  `payment_no`       VARCHAR(64)    DEFAULT NULL             COMMENT '支付流水号',
  `transaction_id`   VARCHAR(64)    DEFAULT NULL             COMMENT '微信支付交易号',
  `status`           VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/REFUNDED/FAILED',
  `remark`           VARCHAR(500)   DEFAULT NULL             COMMENT '备注',
  `transaction_time` DATETIME       DEFAULT NULL             COMMENT '支付时间',
  `create_time`      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`          TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='捐赠记录表';

-- ============================================================
-- 14. 支出记录表
-- ============================================================
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record` (
  `id`              BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `category`        VARCHAR(20)    DEFAULT NULL             COMMENT '支出类别：MEDICAL/FOOD/OPERATION/OTHER',
  `amount`          DECIMAL(12,2)  NOT NULL                 COMMENT '金额',
  `description`     VARCHAR(500)   DEFAULT NULL             COMMENT '用途说明',
  `related_type`    VARCHAR(20)    DEFAULT NULL             COMMENT '关联类型：RESCUE/MEDICAL/ADOPTION/OTHER',
  `related_id`      BIGINT         DEFAULT NULL             COMMENT '关联业务ID',
  `applicant`       VARCHAR(50)    DEFAULT NULL             COMMENT '申请人',
  `approval_status` VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '审批状态：PENDING/APPROVED/REJECTED',
  `approval_user`   VARCHAR(50)    DEFAULT NULL             COMMENT '审批人',
  `approval_time`   DATETIME       DEFAULT NULL             COMMENT '审批时间',
  `voucher_images`  VARCHAR(1000)  DEFAULT NULL             COMMENT '凭证图片',
  `expense_date`    DATE           DEFAULT NULL             COMMENT '支出日期',
  `create_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支出记录表';

-- ============================================================
-- 15. 微信订阅消息记录表
-- ============================================================
DROP TABLE IF EXISTS `wx_subscribe_record`;
CREATE TABLE `wx_subscribe_record` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`     BIGINT       DEFAULT NULL             COMMENT '用户ID',
  `openid`      VARCHAR(64)  DEFAULT NULL             COMMENT 'openid',
  `template_id` VARCHAR(64)  DEFAULT NULL             COMMENT '模板ID',
  `data_json`   TEXT         DEFAULT NULL             COMMENT '模板数据JSON',
  `page`        VARCHAR(200) DEFAULT NULL             COMMENT '跳转页面',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/SENT/FAILED',
  `error_msg`   VARCHAR(500) DEFAULT NULL             COMMENT '错误信息',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time`   DATETIME     DEFAULT NULL             COMMENT '发送时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信订阅消息记录表';
