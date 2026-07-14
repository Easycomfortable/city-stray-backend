-- ============================================================
-- 城流浪（City Stray）城市流浪动物救助管理平台 - 数据库脚本
-- 数据库：city_stray
-- 版本：MySQL 8.0+
-- 字符集：utf8mb4
-- 创建日期：2026-01-01
-- ============================================================

-- 创建数据库（如尚未创建）
CREATE DATABASE IF NOT EXISTS `city_stray` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `city_stray`;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `openid`          VARCHAR(64)  DEFAULT NULL             COMMENT '微信openid',
  `username`        VARCHAR(50)  DEFAULT NULL             COMMENT '用户名',
  `password`        VARCHAR(100) DEFAULT NULL             COMMENT '密码（加密存储）',
  `phone`           VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
  `nickname`        VARCHAR(50)  DEFAULT NULL             COMMENT '昵称',
  `avatar`          VARCHAR(255) DEFAULT NULL             COMMENT '头像URL',
  `role`            VARCHAR(20)  NOT NULL DEFAULT 'user'  COMMENT '角色：user-普通用户 admin-管理员 rescue_admin-救助站管理员 hospital_admin-医院管理员',
  `status`          TINYINT      NOT NULL DEFAULT 1       COMMENT '账号状态：1-正常 0-禁用',
  `last_login_time` DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ============================================================
-- 2. 志愿者扩展表
-- ============================================================
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`      BIGINT       NOT NULL                 COMMENT '关联用户ID',
  `real_name`    VARCHAR(50)  DEFAULT NULL             COMMENT '真实姓名',
  `id_card`      VARCHAR(18)  DEFAULT NULL             COMMENT '身份证号',
  `phone`        VARCHAR(20)  DEFAULT NULL             COMMENT '联系手机号',
  `skill_tags`   JSON         DEFAULT NULL             COMMENT '技能标签，如["捕捉","急救","驾驶"]',
  `total_hours`  DECIMAL(8,1) DEFAULT 0.0              COMMENT '累计服务时长（小时）',
  `points`       INT          DEFAULT 0                COMMENT '积分余额',
  `auth_status`  TINYINT      NOT NULL DEFAULT 0       COMMENT '认证状态：0-待审核 1-已认证 2-已拒绝 3-已禁用',
  `reject_reason` VARCHAR(200) DEFAULT NULL            COMMENT '拒绝原因',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_auth_status` (`auth_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='志愿者扩展表';

-- ============================================================
-- 3. 流浪动物上报表
-- ============================================================
DROP TABLE IF EXISTS `stray_report`;
CREATE TABLE `stray_report` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`      BIGINT       NOT NULL                 COMMENT '上报用户ID',
  `report_no`    VARCHAR(20)  NOT NULL                 COMMENT '上报编号（RS+日期+序号）',
  `longitude`    DECIMAL(10,7) DEFAULT NULL            COMMENT '经度',
  `latitude`     DECIMAL(10,7) DEFAULT NULL            COMMENT '纬度',
  `address`      VARCHAR(200) DEFAULT NULL             COMMENT '详细地址',
  `district`     VARCHAR(20)  DEFAULT NULL             COMMENT '所属区域',
  `animal_type`  VARCHAR(10)  DEFAULT NULL             COMMENT '动物类型：cat-猫 dog-狗 other-其他',
  `description`  TEXT         DEFAULT NULL             COMMENT '外观描述',
  `quantity`     INT          NOT NULL DEFAULT 1       COMMENT '数量',
  `is_injured`   TINYINT      DEFAULT 0                COMMENT '是否受伤：0-否 1-是',
  `is_friendly`  TINYINT      DEFAULT 0                COMMENT '是否亲人：0-否 1-是',
  `photos`       JSON         DEFAULT NULL             COMMENT '照片URL列表',
  `status`       TINYINT      NOT NULL DEFAULT 0       COMMENT '处理状态：0-待处理 1-已处理',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_no` (`report_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_district` (`district`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='流浪动物上报表';

-- ============================================================
-- 4. 救助工单表
-- ============================================================
DROP TABLE IF EXISTS `rescue_order`;
CREATE TABLE `rescue_order` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `order_no`      VARCHAR(20)  NOT NULL                 COMMENT '工单编号',
  `report_id`     BIGINT       DEFAULT NULL             COMMENT '关联上报记录ID',
  `animal_id`     BIGINT       DEFAULT NULL             COMMENT '关联动物档案ID',
  `volunteer_id`  BIGINT       DEFAULT NULL             COMMENT '指派志愿者ID',
  `hospital_id`   BIGINT       DEFAULT NULL             COMMENT '指派医院ID',
  `status`        VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '工单状态：pending-待处理 responded-已响应 catching-捕捉中 treating-治疗中 recovering-恢复中 adoptable-可领养 adopted-已领养 closed-已关闭',
  `district`      VARCHAR(20)  DEFAULT NULL             COMMENT '所属区域',
  `description`   TEXT         DEFAULT NULL             COMMENT '处理描述',
  `assigned_time` DATETIME     DEFAULT NULL             COMMENT '指派时间',
  `respond_time`  DATETIME     DEFAULT NULL             COMMENT '响应时间',
  `close_time`    DATETIME     DEFAULT NULL             COMMENT '关闭时间',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_report_id` (`report_id`),
  KEY `idx_animal_id` (`animal_id`),
  KEY `idx_volunteer_id` (`volunteer_id`),
  KEY `idx_hospital_id` (`hospital_id`),
  KEY `idx_status` (`status`),
  KEY `idx_district` (`district`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='救助工单表';

-- ============================================================
-- 5. 动物档案表
-- ============================================================
DROP TABLE IF EXISTS `animal`;
CREATE TABLE `animal` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `animal_no`      VARCHAR(20)  NOT NULL                 COMMENT '动物编号',
  `name`           VARCHAR(50)  DEFAULT NULL             COMMENT '名称',
  `breed`          VARCHAR(50)  DEFAULT NULL             COMMENT '品种',
  `gender`         TINYINT      DEFAULT NULL             COMMENT '性别：1-公 0-母',
  `age_estimate`   VARCHAR(20)  DEFAULT NULL             COMMENT '年龄估算',
  `weight`         DECIMAL(5,2) DEFAULT NULL             COMMENT '体重（公斤）',
  `color`          VARCHAR(30)  DEFAULT NULL             COMMENT '毛色',
  `is_neutered`    TINYINT      NOT NULL DEFAULT 0       COMMENT '是否已绝育：0-否 1-是',
  `chip_no`        VARCHAR(30)  DEFAULT NULL             COMMENT '芯片编号',
  `health_status`  VARCHAR(20)  DEFAULT NULL             COMMENT '健康状态：treating-治疗中 recovering-恢复中 adoptable-可领养 adopted-已领养 deceased-已死亡',
  `photos`         JSON         DEFAULT NULL             COMMENT '照片列表',
  `description`    TEXT         DEFAULT NULL             COMMENT '描述信息',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_animal_no` (`animal_no`),
  KEY `idx_health_status` (`health_status`),
  KEY `idx_breed` (`breed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='动物档案表';

-- ============================================================
-- 6. 医疗记录表
-- ============================================================
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `animal_id`     BIGINT        NOT NULL                 COMMENT '关联动物ID',
  `hospital_id`   BIGINT        DEFAULT NULL             COMMENT '关联医院ID',
  `record_type`   VARCHAR(20)   NOT NULL                 COMMENT '记录类型：vaccine-疫苗 diagnosis-诊断 deworming-驱虫 surgery-手术',
  `diagnosis`     VARCHAR(200)  DEFAULT NULL             COMMENT '诊断结果',
  `treatment`     TEXT          DEFAULT NULL             COMMENT '治疗方案',
  `medication`    VARCHAR(200)  DEFAULT NULL             COMMENT '用药信息',
  `doctor_name`   VARCHAR(50)   DEFAULT NULL             COMMENT '主治医生',
  `cost`          DECIMAL(10,2) DEFAULT NULL             COMMENT '费用（元）',
  `record_date`   DATE          DEFAULT NULL             COMMENT '记录日期',
  `notes`         TEXT          DEFAULT NULL             COMMENT '备注',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_animal_id` (`animal_id`),
  KEY `idx_hospital_id` (`hospital_id`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='医疗记录表';

-- ============================================================
-- 7. 领养申请表
-- ============================================================
DROP TABLE IF EXISTS `adoption_apply`;
CREATE TABLE `adoption_apply` (
  `id`                BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `apply_no`          VARCHAR(20)  NOT NULL                 COMMENT '申请编号',
  `user_id`           BIGINT       NOT NULL                 COMMENT '申请用户ID',
  `animal_id`         BIGINT       NOT NULL                 COMMENT '申请领养动物ID',
  `real_name`         VARCHAR(50)  DEFAULT NULL             COMMENT '真实姓名',
  `phone`             VARCHAR(20)  DEFAULT NULL             COMMENT '联系电话',
  `age`               INT          DEFAULT NULL             COMMENT '年龄',
  `occupation`        VARCHAR(50)  DEFAULT NULL             COMMENT '职业',
  `address`           VARCHAR(200) DEFAULT NULL             COMMENT '居住地址',
  `living_environment` TEXT        DEFAULT NULL             COMMENT '居住环境描述',
  `pet_experience`    TEXT         DEFAULT NULL             COMMENT '养宠经验',
  `family_consent`    TINYINT      DEFAULT 0                COMMENT '家人是否同意：0-否 1-是',
  `photos`            JSON         DEFAULT NULL             COMMENT '居住环境照片',
  `stage`             VARCHAR(20)  NOT NULL DEFAULT 'submitted' COMMENT '申请阶段：submitted-已提交 reviewing-审核中 approved-已通过 rejected-已拒绝 visiting-家访中 trial-试养中 adopted-已领养',
  `review_user_id`    BIGINT       DEFAULT NULL             COMMENT '审核人ID',
  `review_remark`     TEXT         DEFAULT NULL             COMMENT '审核备注',
  `reject_reason`     TEXT         DEFAULT NULL             COMMENT '拒绝原因',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`           TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_animal_id` (`animal_id`),
  KEY `idx_stage` (`stage`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='领养申请表';

-- ============================================================
-- 8. 家访记录表
-- ============================================================
DROP TABLE IF EXISTS `adoption_visit`;
CREATE TABLE `adoption_visit` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `apply_id`      BIGINT       NOT NULL                 COMMENT '关联领养申请ID',
  `visitor_name`  VARCHAR(50)  DEFAULT NULL             COMMENT '家访人员姓名',
  `visit_date`    DATE         DEFAULT NULL             COMMENT '家访日期',
  `result`        VARCHAR(20)  DEFAULT NULL             COMMENT '评估结果：good-良好 fair-一般 poor-较差',
  `evaluation`    TEXT         DEFAULT NULL             COMMENT '评估详情',
  `photos`        JSON         DEFAULT NULL             COMMENT '家访照片',
  `notes`         TEXT         DEFAULT NULL             COMMENT '备注',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_visit_date` (`visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家访记录表';

-- ============================================================
-- 9. 领养协议表
-- ============================================================
DROP TABLE IF EXISTS `adoption_agreement`;
CREATE TABLE `adoption_agreement` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `apply_id`        BIGINT       NOT NULL                 COMMENT '关联领养申请ID',
  `animal_id`       BIGINT       NOT NULL                 COMMENT '关联动物ID',
  `adopter_name`    VARCHAR(50)  DEFAULT NULL             COMMENT '领养人姓名',
  `adopter_phone`   VARCHAR(20)  DEFAULT NULL             COMMENT '领养人电话',
  `adopter_id_card` VARCHAR(18)  DEFAULT NULL             COMMENT '领养人身份证号',
  `sign_date`       DATE         DEFAULT NULL             COMMENT '签署日期',
  `agreement_no`    VARCHAR(30)  DEFAULT NULL             COMMENT '协议编号',
  `content`         TEXT         DEFAULT NULL             COMMENT '协议内容',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_animal_id` (`animal_id`),
  UNIQUE KEY `uk_agreement_no` (`agreement_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='领养协议表';

-- ============================================================
-- 10. 回访记录表
-- ============================================================
DROP TABLE IF EXISTS `revisit_record`;
CREATE TABLE `revisit_record` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `apply_id`        BIGINT       NOT NULL                 COMMENT '关联领养申请ID',
  `revisit_date`    DATE         DEFAULT NULL             COMMENT '回访日期',
  `condition_desc`  TEXT         DEFAULT NULL             COMMENT '近况描述',
  `photos`          JSON         DEFAULT NULL             COMMENT '回访照片',
  `health_status`   VARCHAR(20)  DEFAULT NULL             COMMENT '健康状态',
  `is_normal`       TINYINT      DEFAULT 1                COMMENT '是否正常：0-异常 1-正常',
  `notes`           TEXT         DEFAULT NULL             COMMENT '备注',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_revisit_date` (`revisit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='回访记录表';

-- ============================================================
-- 11. 志愿任务表
-- ============================================================
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `order_id`       BIGINT        NOT NULL                 COMMENT '关联救助工单ID',
  `volunteer_id`   BIGINT        DEFAULT NULL             COMMENT '指派志愿者ID',
  `task_type`      VARCHAR(20)   NOT NULL                 COMMENT '任务类型：respond-响应 catch-捕捉 transport-转运 foster-寄养',
  `status`         VARCHAR(20)   NOT NULL DEFAULT 'available' COMMENT '任务状态：available-可领取 accepted-已接受 in_progress-进行中 completed-已完成 cancelled-已取消',
  `points_reward`  INT           NOT NULL DEFAULT 10      COMMENT '积分奖励',
  `description`    TEXT          DEFAULT NULL             COMMENT '任务描述',
  `location`       VARCHAR(200)  DEFAULT NULL             COMMENT '任务地点',
  `longitude`      DECIMAL(10,7) DEFAULT NULL             COMMENT '经度',
  `latitude`       DECIMAL(10,7) DEFAULT NULL             COMMENT '纬度',
  `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_volunteer_id` (`volunteer_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='志愿任务表';

-- ============================================================
-- 12. 任务日志表
-- ============================================================
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `task_id`        BIGINT        NOT NULL                 COMMENT '关联任务ID',
  `volunteer_id`   BIGINT        NOT NULL                 COMMENT '操作志愿者ID',
  `action`         VARCHAR(50)   DEFAULT NULL             COMMENT '操作描述',
  `content`        TEXT          DEFAULT NULL             COMMENT '日志内容',
  `photos`         JSON          DEFAULT NULL             COMMENT '相关照片',
  `service_hours`  DECIMAL(4,1)  DEFAULT NULL             COMMENT '本次服务时长（小时）',
  `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`        TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_volunteer_id` (`volunteer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务日志表';

-- ============================================================
-- 13. 合作医院表
-- ============================================================
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `name`            VARCHAR(100) NOT NULL                 COMMENT '医院名称',
  `address`         VARCHAR(200) DEFAULT NULL             COMMENT '医院地址',
  `phone`           VARCHAR(20)  DEFAULT NULL             COMMENT '联系电话',
  `contact_person`  VARCHAR(50)  DEFAULT NULL             COMMENT '负责人',
  `license_no`      VARCHAR(50)  DEFAULT NULL             COMMENT '资质编号',
  `district`        VARCHAR(20)  DEFAULT NULL             COMMENT '所属区域',
  `longitude`       DECIMAL(10,7) DEFAULT NULL            COMMENT '经度',
  `latitude`        DECIMAL(10,7) DEFAULT NULL            COMMENT '纬度',
  `status`          TINYINT      NOT NULL DEFAULT 1       COMMENT '合作状态：1-合作中 0-已暂停 2-已终止',
  `discount_info`   TEXT         DEFAULT NULL             COMMENT '优惠项目说明',
  `price_standard`  TEXT         DEFAULT NULL             COMMENT '结算价格标准',
  `monthly_visits`  INT          NOT NULL DEFAULT 0       COMMENT '本月接诊数',
  `total_visits`    INT          NOT NULL DEFAULT 0       COMMENT '累计接诊数',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_district` (`district`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合作医院表';


-- ============================================================
-- 示例数据
-- ============================================================

-- 用户数据
INSERT INTO `user` (`id`, `openid`, `username`, `password`, `phone`, `nickname`, `avatar`, `role`, `status`) VALUES
(1, 'ox_test_admin_001',  'admin',    '$2a$10$ZsGvdNtcpnN8Vdn1zQVG0u/69VdN16wOD/G8dFQ.VA9pjJ3kJ4FzW', '13800000001', '系统管理员',  NULL, 'admin',          1),
(2, 'ox_test_user_001',   'zhangsan', '$2a$10$ZsGvdNtcpnN8Vdn1zQVG0u/69VdN16wOD/G8dFQ.VA9pjJ3kJ4FzW', '13800000002', '张三',       NULL, 'user',           1),
(3, 'ox_test_user_002',   'lisi',     '$2a$10$ZsGvdNtcpnN8Vdn1zQVG0u/69VdN16wOD/G8dFQ.VA9pjJ3kJ4FzW', '13800000003', '李四',       NULL, 'user',           1),
(4, 'ox_test_rescue_001', 'wangwei',  '$2a$10$ZsGvdNtcpnN8Vdn1zQVG0u/69VdN16wOD/G8dFQ.VA9pjJ3kJ4FzW', '13800000004', '王队长',     NULL, 'rescue_admin',   1),
(5, 'ox_test_hosp_001',   'doctorliu','$2a$10$ZsGvdNtcpnN8Vdn1zQVG0u/69VdN16wOD/G8dFQ.VA9pjJ3kJ4FzW', '13800000005', '刘医生',     NULL, 'hospital_admin', 1);

-- 志愿者数据
INSERT INTO `volunteer` (`id`, `user_id`, `real_name`, `id_card`, `phone`, `skill_tags`, `total_hours`, `points`, `auth_status`) VALUES
(1, 2, '张三', '110101199001011234', '13800000002', '["捕捉","驾驶"]',     48.5, 120, 1),
(2, 3, '李四', '110101199205052345', '13800000003', '["急救","护理"]',     32.0,  85, 1),
(3, 4, '王伟', '110101198803033456', '13800000004', '["捕捉","急救","驾驶"]', 96.0, 260, 1);

-- 合作医院数据
INSERT INTO `hospital` (`id`, `name`, `address`, `phone`, `contact_person`, `license_no`, `district`, `longitude`, `latitude`, `status`, `discount_info`, `price_standard`) VALUES
(1, '爱心宠物医院',     '朝阳区建国路88号',     '010-88886666', '张院长', 'BJ-VET-2024-001', '朝阳区', 116.4552000, 39.9091000, 1, '绝育手术8折优惠',     '绝育300元/例，疫苗50元/针'),
(2, '仁心动物诊所',     '海淀区中关村大街66号',  '010-66668888', '李主任', 'BJ-VET-2024-002', '海淀区', 116.3162000, 39.9818000, 1, '流浪动物免费体检',    '基础检查100元/次，手术500元起'),
(3, '康贝宠物医疗中心', '丰台区南三环西路12号',  '010-55557777', '王院长', 'BJ-VET-2024-003', '丰台区', 116.3345000, 39.8468000, 1, '救助组织7折结算',     '综合诊疗200元起');

-- 流浪动物上报数据
INSERT INTO `stray_report` (`id`, `user_id`, `report_no`, `longitude`, `latitude`, `address`, `district`, `animal_type`, `description`, `quantity`, `is_injured`, `is_friendly`, `photos`, `status`) VALUES
(1, 2, 'RS20260101001', 116.4075260, 39.9040300, '朝阳区建国路万达广场北侧',   '朝阳区', 'cat',   '橘色成年猫，约3岁，右前腿有伤口', 1, 1, 1, '["https://example.com/cat1.jpg"]', 1),
(2, 3, 'RS20260101002', 116.3100000, 39.9560000, '海淀区中关村软件园二期东门',  '海淀区', 'dog',   '黑白花小型犬，性格温顺，戴项圈',  1, 0, 1, '["https://example.com/dog1.jpg"]', 1),
(3, 2, 'RS20260102001', 116.3500000, 39.8800000, '丰台区南三环草桥地铁站附近',  '丰台区', 'cat',   '两只小奶猫，一白一灰，约2个月大', 2, 0, 1, '["https://example.com/kitten1.jpg"]', 0),
(4, 3, 'RS20260102002', 116.4500000, 39.9200000, '朝阳区望京SOHO地下停车场',    '朝阳区', 'dog',   '黄色中华田园犬，体型中等，怕人',  1, 0, 0, NULL, 0);

-- 动物档案数据
INSERT INTO `animal` (`id`, `animal_no`, `name`, `breed`, `gender`, `age_estimate`, `weight`, `color`, `is_neutered`, `chip_no`, `health_status`, `photos`, `description`) VALUES
(1, 'AN20260101001', '小橘',   '中华田园猫', 1, '约3岁',   4.50, '橘色',    1, 'CHIP-2026-0001', 'recovering', '["https://example.com/cat1.jpg"]', '右前腿有旧伤，已治疗恢复中'),
(2, 'AN20260101002', '花花',   '中华田园犬', 0, '约2岁',   6.80, '黑白花色', 0, 'CHIP-2026-0002', 'adoptable',  '["https://example.com/dog1.jpg"]', '性格温顺，已完成体检和疫苗'),
(3, 'AN20260102001', '雪球',   '中华田园猫', 0, '约2个月', 0.80, '白色',    0, NULL,                 'treating',   '["https://example.com/kitten1.jpg"]', '奶猫，体质偏弱，正在观察治疗');

-- 救助工单数据
INSERT INTO `rescue_order` (`id`, `order_no`, `report_id`, `animal_id`, `volunteer_id`, `hospital_id`, `status`, `district`, `description`, `assigned_time`, `respond_time`) VALUES
(1, 'RO20260101001', 1, 1, 1, 1, 'recovering', '朝阳区', '志愿者张三已捕捉，送往爱心宠物医院治疗', '2026-01-01 10:30:00', '2026-01-01 09:15:00'),
(2, 'RO20260101002', 2, 2, 3, 2, 'adoptable',  '海淀区', '志愿者王伟已捕捉，送至仁心动物诊所体检完毕', '2026-01-01 14:00:00', '2026-01-01 12:30:00'),
(3, 'RO20260102001', 3, 3, 1, 1, 'treating',   '丰台区', '两只奶猫已捕捉，其中白色奶猫正在治疗',     '2026-01-02 09:00:00', '2026-01-02 08:20:00');

-- 医疗记录数据
INSERT INTO `medical_record` (`id`, `animal_id`, `hospital_id`, `record_type`, `diagnosis`, `treatment`, `medication`, `doctor_name`, `cost`, `record_date`, `notes`) VALUES
(1, 1, 1, 'diagnosis', '右前腿骨折',     '清创、固定、抗感染治疗',    '阿莫西林 每日两次',   '刘医生', 580.00, '2026-01-01', '骨折较严重，需观察两周'),
(2, 1, 1, 'vaccine',   '已完成首针疫苗', '注射猫三联疫苗第一针',       NULL,                   '刘医生',  80.00, '2026-01-05', '状态良好，可以接种'),
(3, 2, 2, 'diagnosis', '体检正常',       '基础体检各项指标正常',       '驱虫药一次',          '李主任', 200.00, '2026-01-01', '健康状况良好，适合领养'),
(4, 2, 2, 'deworming', '常规驱虫',       '体内驱虫+体外驱虫',         '拜耳驱虫药',          '李主任', 120.00, '2026-01-02', '下次驱虫三个月后'),
(5, 3, 1, 'diagnosis', '体质偏弱',       '补充营养液，保温观察',       '营养膏 每日一次',     '刘医生', 150.00, '2026-01-02', '奶猫需特别护理');

-- 领养申请数据
INSERT INTO `adoption_apply` (`id`, `apply_no`, `user_id`, `animal_id`, `real_name`, `phone`, `age`, `occupation`, `address`, `living_environment`, `pet_experience`, `family_consent`, `stage`) VALUES
(1, 'AD20260105001', 2, 2, '张三', '13800000002', 34, '软件工程师', '朝阳区望京花园小区3号楼1201', '三居室，有封闭阳台，小区允许养宠', '养过两只猫，有丰富养宠经验', 1, 'approved'),
(2, 'AD20260106001', 3, 2, '李四', '13800000003', 28, '设计师',     '海淀区万柳书院5号楼803',   '两居室，客厅宽敞，附近有公园',     '目前养了一只狗',              1, 'reviewing');

-- 家访记录数据
INSERT INTO `adoption_visit` (`id`, `apply_id`, `visitor_name`, `visit_date`, `result`, `evaluation`, `notes`) VALUES
(1, 1, '王伟', '2026-01-07', 'good', '居住环境整洁，有封闭阳台和纱窗，小区环境适合养宠，领养人态度认真负责', '建议通过');

-- 领养协议数据
INSERT INTO `adoption_agreement` (`id`, `apply_id`, `animal_id`, `adopter_name`, `adopter_phone`, `adopter_id_card`, `sign_date`, `agreement_no`, `content`) VALUES
(1, 1, 2, '张三', '13800000002', '110101199001011234', '2026-01-08', 'AG20260108001', '领养人承诺善待动物，定期体检，不遗弃，接受定期回访。如无法继续饲养需联系救助站。');

-- 回访记录数据
INSERT INTO `revisit_record` (`id`, `apply_id`, `revisit_date`, `condition_desc`, `health_status`, `is_normal`, `notes`) VALUES
(1, 1, '2026-01-15', '花花适应良好，活泼好动，饮食正常，已熟悉新环境', '健康', 1, '领养人反馈非常积极'),
(2, 1, '2026-02-15', '花花状态良好，已完成第二次疫苗接种', '健康', 1, '一切正常');

-- 志愿任务数据
INSERT INTO `task` (`id`, `order_id`, `volunteer_id`, `task_type`, `status`, `points_reward`, `description`, `location`, `longitude`, `latitude`) VALUES
(1, 1, 1, 'catch',     'completed',  20, '前往万达广场北侧捕捉受伤橘猫',     '朝阳区建国路万达广场北侧',  116.4075260, 39.9040300),
(2, 1, 1, 'transport', 'completed',  10, '将受伤橘猫转运至爱心宠物医院',     '朝阳区建国路',             116.4075260, 39.9040300),
(3, 2, 3, 'catch',     'completed',  20, '前往中关村软件园捕捉流浪犬',       '海淀区中关村软件园二期',  116.3100000, 39.9560000),
(4, 3, 1, 'catch',     'completed',  20, '前往草桥地铁站附近捕捉两只奶猫',  '丰台区南三环草桥地铁站',  116.3500000, 39.8800000),
(5, 3, 1, 'transport', 'completed',  10, '将奶猫转运至爱心宠物医院',         '丰台区南三环',             116.3500000, 39.8800000);

-- 任务日志数据
INSERT INTO `task_log` (`id`, `task_id`, `volunteer_id`, `action`, `content`, `service_hours`) VALUES
(1, 1, 1, '接受任务', '已确认前往现场',                       0.0),
(2, 1, 1, '到达现场', '已到达万达广场北侧，发现目标橘猫',       0.0),
(3, 1, 1, '完成捕捉', '使用诱捕笼成功捕捉，猫咪右前腿有伤',   2.5),
(4, 2, 1, '完成转运', '已将猫咪安全送达爱心宠物医院',         1.0),
(5, 3, 3, '完成捕捉', '花花很温顺，直接用牵引绳带走',         1.5);
