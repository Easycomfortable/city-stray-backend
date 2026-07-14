-- ============================================================
-- 城流浪（City Stray）财务管理模块 - 数据库表脚本
-- 由 D 角色（后端·支撑业务）创建
-- ============================================================

USE `city_stray`;

-- 20. 捐赠项目表
DROP TABLE IF EXISTS `donation_project`;
CREATE TABLE `donation_project` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `name`        VARCHAR(100)  NOT NULL                 COMMENT '项目名称',
  `description` TEXT          DEFAULT NULL             COMMENT '项目描述',
  `cover_image` VARCHAR(255)  DEFAULT NULL             COMMENT '封面图片URL',
  `target_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00  COMMENT '目标金额',
  `raised_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00  COMMENT '已筹金额',
  `donor_count` INT           NOT NULL DEFAULT 0       COMMENT '捐赠人数',
  `status`      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中 ENDED-已结束',
  `start_date`  DATE          DEFAULT NULL             COMMENT '开始日期',
  `end_date`    DATE          DEFAULT NULL             COMMENT '结束日期',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='捐赠项目表';

-- 21. 捐赠记录表
DROP TABLE IF EXISTS `donation_record`;
CREATE TABLE `donation_record` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `project_id`      BIGINT        DEFAULT NULL             COMMENT '关联捐赠项目ID',
  `user_id`         BIGINT        DEFAULT NULL             COMMENT '捐赠用户ID',
  `donor_name`      VARCHAR(50)   DEFAULT NULL             COMMENT '捐赠人姓名',
  `donor_phone`     VARCHAR(20)   DEFAULT NULL             COMMENT '捐赠人手机号',
  `anonymous`       TINYINT       NOT NULL DEFAULT 0       COMMENT '是否匿名：0-否 1-是',
  `amount`          DECIMAL(12,2) NOT NULL                 COMMENT '捐赠金额',
  `payment_method`  VARCHAR(20)   NOT NULL DEFAULT 'WECHAT' COMMENT '支付方式：WECHAT-微信 ALIPAY-支付宝 OFFLINE-线下',
  `payment_no`      VARCHAR(50)   DEFAULT NULL             COMMENT '支付流水号',
  `transaction_id`  VARCHAR(64)   DEFAULT NULL             COMMENT '微信支付交易号',
  `status`          VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认 SUCCESS-成功 REFUNDED-已退款 FAILED-失败',
  `remark`          VARCHAR(200)  DEFAULT NULL             COMMENT '备注',
  `transaction_time` DATETIME     DEFAULT NULL             COMMENT '支付时间',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='捐赠记录表';

-- 22. 支出记录表
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `category`        VARCHAR(50)   NOT NULL                 COMMENT '支出类别：MEDICAL-医疗费用 FOOD-饲料费用 OPERATION-运营费用 OTHER-其他',
  `amount`          DECIMAL(12,2) NOT NULL                 COMMENT '金额',
  `description`     VARCHAR(200)  DEFAULT NULL             COMMENT '用途说明',
  `related_type`    VARCHAR(20)   DEFAULT NULL             COMMENT '关联类型：RESCUE-救助 MEDICAL-医疗 ADOPTION-领养 OTHER-其他',
  `related_id`      BIGINT        DEFAULT NULL             COMMENT '关联业务ID',
  `applicant`       VARCHAR(50)   DEFAULT NULL             COMMENT '申请人',
  `approval_status` VARCHAR(20)   NOT NULL DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批 APPROVED-已通过 REJECTED-已拒绝',
  `approval_user`   VARCHAR(50)   DEFAULT NULL             COMMENT '审批人',
  `approval_time`   DATETIME      DEFAULT NULL             COMMENT '审批时间',
  `voucher_images`  JSON          DEFAULT NULL             COMMENT '凭证图片',
  `expense_date`    DATE          DEFAULT NULL             COMMENT '支出日期',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_expense_date` (`expense_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支出记录表';

-- ============================================================
-- 示例数据
-- ============================================================

INSERT INTO `donation_project` (`id`, `name`, `description`, `target_amount`, `raised_amount`, `donor_count`, `status`, `start_date`) VALUES
(1, '流浪猫救助基金', '用于城市流浪猫的救助、治疗和安置', 50000.00, 12800.00, 86, 'ACTIVE', '2026-01-01'),
(2, 'TNR绝育计划', 'Trap-Neuter-Return，控制流浪动物数量', 30000.00, 8500.00, 42, 'ACTIVE', '2026-01-15'),
(3, '冬季保暖行动', '为流浪动物搭建临时庇护所和提供保暖物资', 20000.00, 18600.00, 120, 'ENDED', '2025-11-01');

INSERT INTO `donation_record` (`id`, `project_id`, `user_id`, `donor_name`, `anonymous`, `amount`, `payment_method`, `payment_no`, `status`, `transaction_time`) VALUES
(1,  1, 2, '爱心人士', 1,  500.00,  'WECHAT', 'WX20260701001', 'SUCCESS', '2026-07-01 10:30:00'),
(2,  2, 3, '张先生',   0,  1000.00, 'WECHAT', 'WX20260702002', 'SUCCESS', '2026-07-02 14:20:00'),
(3,  1, 2, '爱心人士', 1,  200.00,  'WECHAT', 'WX20260703003', 'SUCCESS', '2026-07-03 09:15:00'),
(4,  1, 4, '王伟',     0,  300.00,  'WECHAT', 'WX20260705004', 'SUCCESS', '2026-07-05 16:45:00'),
(5,  2, 5, '刘医生',   0,  2000.00, 'WECHAT', 'WX20260706005', 'SUCCESS', '2026-07-06 11:00:00'),
(6,  3, 2, '爱心人士', 1,  100.00,  'WECHAT', 'WX20260708006', 'SUCCESS', '2026-07-08 08:30:00'),
(7,  1, 3, '张先生',   0,  500.00,  'WECHAT', 'WX20260710007', 'PENDING', '2026-07-10 13:20:00'),
(8,  2, 4, '王伟',     0,  800.00,  'WECHAT', 'WX20260711008', 'SUCCESS', '2026-07-11 10:00:00');

INSERT INTO `expense_record` (`id`, `category`, `amount`, `description`, `related_type`, `related_id`, `applicant`, `approval_status`, `expense_date`) VALUES
(1, 'MEDICAL',   580.00,  '小橘右前腿骨折治疗',    'MEDICAL', 1, '王队长', 'APPROVED', '2026-01-01'),
(2, 'MEDICAL',   80.00,   '小橘首针疫苗',          'MEDICAL', 2, '王队长', 'APPROVED', '2026-01-05'),
(3, 'MEDICAL',   200.00,  '花花体检',              'MEDICAL', 3, '王队长', 'APPROVED', '2026-01-01'),
(4, 'MEDICAL',   120.00,  '花花驱虫',              'MEDICAL', 4, '王队长', 'APPROVED', '2026-01-02'),
(5, 'FOOD',      350.00,  '救助站猫粮狗粮采购',     'OTHER',   NULL, '王队长', 'APPROVED', '2026-07-01'),
(6, 'OPERATION', 1200.00, '宣传物料制作（海报、传单）','OTHER', NULL, '系统管理员', 'APPROVED', '2026-07-05'),
(7, 'MEDICAL',   150.00,  '雪球奶猫营养补充',       'MEDICAL', 5, '王队长', 'APPROVED', '2026-07-02'),
(8, 'FOOD',      280.00,  '流浪猫投喂点饲料补充',   'OTHER',   NULL, '张三', 'PENDING', '2026-07-10');
