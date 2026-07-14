-- ============================================================
-- 城流浪（City Stray）系统管理模块 - 数据库表脚本
-- 由 D 角色（后端·支撑业务）创建
-- ============================================================

USE `city_stray`;

-- 14. 系统角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `name`        VARCHAR(50)  NOT NULL                 COMMENT '角色名称',
  `code`        VARCHAR(50)  NOT NULL                 COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL             COMMENT '描述',
  `sort`        INT          DEFAULT 0                COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1-启用 0-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统角色表';

-- 15. 系统菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `parent_id`   BIGINT       DEFAULT 0                COMMENT '父菜单ID，0为顶级',
  `name`        VARCHAR(50)  NOT NULL                 COMMENT '菜单名称',
  `icon`        VARCHAR(50)  DEFAULT NULL             COMMENT '图标',
  `path`        VARCHAR(200) DEFAULT NULL             COMMENT '路由路径',
  `component`   VARCHAR(200) DEFAULT NULL             COMMENT '组件路径',
  `permission`  VARCHAR(100) DEFAULT NULL             COMMENT '权限标识',
  `type`        TINYINT      NOT NULL DEFAULT 0       COMMENT '类型：0-目录 1-菜单 2-按钮',
  `sort`        INT          DEFAULT 0                COMMENT '排序',
  `visible`     TINYINT      NOT NULL DEFAULT 1       COMMENT '是否可见：1-是 0-否',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单表';

-- 16. 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL                COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL                COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联表';

-- 17. 字典类型表
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `name`        VARCHAR(100) NOT NULL                 COMMENT '字典名称',
  `code`        VARCHAR(100) NOT NULL                 COMMENT '字典编码',
  `description` VARCHAR(200) DEFAULT NULL             COMMENT '描述',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1-启用 0-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- 18. 字典数据表
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `dict_type_id` BIGINT      NOT NULL                 COMMENT '字典类型ID',
  `label`       VARCHAR(100) NOT NULL                 COMMENT '字典标签',
  `value`       VARCHAR(100) NOT NULL                 COMMENT '字典值',
  `sort`        INT          DEFAULT 0                COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1-启用 0-禁用',
  `is_default`  TINYINT      NOT NULL DEFAULT 0       COMMENT '是否默认：0-否 1-是',
  `remark`      VARCHAR(200) DEFAULT NULL             COMMENT '备注',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_id` (`dict_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- 19. 系统操作日志表
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`     BIGINT       DEFAULT NULL             COMMENT '操作用户ID',
  `username`    VARCHAR(50)  DEFAULT NULL             COMMENT '操作用户名',
  `module`      VARCHAR(50)  DEFAULT NULL             COMMENT '操作模块',
  `type`        VARCHAR(20)  DEFAULT NULL             COMMENT '操作类型：LOGIN/CREATE/UPDATE/DELETE/EXPORT',
  `content`     VARCHAR(500) DEFAULT NULL             COMMENT '操作内容',
  `method`      VARCHAR(200) DEFAULT NULL             COMMENT '请求方法',
  `url`         VARCHAR(200) DEFAULT NULL             COMMENT '请求URL',
  `ip`          VARCHAR(50)  DEFAULT NULL             COMMENT '操作IP',
  `duration`    INT          DEFAULT NULL             COMMENT '耗时(毫秒)',
  `success`     TINYINT      NOT NULL DEFAULT 1       COMMENT '是否成功：1-成功 0-失败',
  `error_msg`   TEXT         DEFAULT NULL             COMMENT '错误信息',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统操作日志表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 角色数据
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `sort`, `status`) VALUES
(1, '超级管理员', 'admin',          '拥有系统全部权限',     1, 1),
(2, '救助站管理员', 'rescue_admin', '管理救助工单和志愿者', 2, 1),
(3, '医院管理员',   'hospital_admin','管理医院和医疗记录',   3, 1),
(4, '普通用户',     'user',         '可浏览和提交领养申请', 4, 1);

-- 菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `icon`, `path`, `type`, `sort`, `visible`) VALUES
(1,  0, '仪表盘',     'dashboard',  '/dashboard',       1, 1,  1),
(2,  0, '救助管理',   'guide',      '/rescue-order',    0, 2,  1),
(3,  2, '救助工单',   'list',       '/rescue-order',    1, 1,  1),
(4,  0, '动物管理',   'animal',     '/animal',          1, 3,  1),
(5,  0, '领养管理',   'adopt',      '/adoption',        1, 4,  1),
(6,  0, '志愿者管理', 'peoples',    '/volunteer',       0, 5,  1),
(7,  6, '志愿者列表', 'user',       '/volunteer',       1, 1,  1),
(8,  6, '认证审核',   'form',       '/volunteer/certify',1, 2, 1),
(9,  0, '医院管理',   'hospital',   '/hospital',        1, 6,  1),
(10, 0, '财务管理',   'money',      '/finance',         0, 7,  1),
(11, 10,'捐赠管理',   'donation',   '/finance/donation',1, 1,  1),
(12, 10,'财务报表',   'chart',      '/finance/report',  1, 2,  1),
(13, 0, '内容管理',   'documentation','/content',       0, 8,  1),
(14, 13,'轮播图',     'banner',     '/content/banner',  1, 1,  1),
(15, 13,'公告管理',   'notice',     '/content/notice',  1, 2,  1),
(16, 0, '系统管理',   'setting',    '/system',          0, 9,  1),
(17, 16,'用户管理',   'user',       '/system/user',     1, 1,  1),
(18, 16,'角色管理',   'peoples',    '/system/role',     1, 2,  1),
(19, 16,'菜单管理',   'tree-table', '/system/menu',     1, 3,  1),
(20, 16,'字典管理',   'dict',       '/system/dict',     1, 4,  1),
(21, 16,'系统日志',   'log',        '/system/log',      1, 5,  1);

-- 超级管理员拥有所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- 救助站管理员权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1),(2, 2),(2, 3),(2, 4),(2, 5),(2, 6),(2, 7),(2, 8);

-- 医院管理员权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 1),(3, 4),(3, 9);

-- 普通用户权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 1),(4, 4),(4, 5);

-- 字典类型数据
INSERT INTO `sys_dict_type` (`id`, `name`, `code`, `description`, `status`) VALUES
(1, '动物健康状态',   'animal_health_status',  '动物的健康与收容状态',   1),
(2, '领养申请阶段',   'adoption_stage',        '领养申请的审批阶段',     1),
(3, '志愿者认证状态', 'volunteer_auth_status', '志愿者认证状态',         1),
(4, '救助工单状态',   'rescue_order_status',   '救助工单的处理状态',     1),
(5, '任务类型',       'task_type',             '志愿任务的类型',         1),
(6, '任务状态',       'task_status',           '志愿任务的状态',         1);

-- 字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_type_id`, `label`, `value`, `sort`, `is_default`) VALUES
(1,  1, '治疗中',   'treating',   1, 0),
(2,  1, '恢复中',   'recovering', 2, 0),
(3,  1, '可领养',   'adoptable',  3, 0),
(4,  1, '已领养',   'adopted',    4, 0),
(5,  1, '已死亡',   'deceased',   5, 0),
(6,  2, '已提交',   'submitted',  1, 1),
(7,  2, '审核中',   'reviewing',  2, 0),
(8,  2, '已通过',   'approved',   3, 0),
(9,  2, '已拒绝',   'rejected',   4, 0),
(10, 2, '家访中',   'visiting',   5, 0),
(11, 2, '试养中',   'trial',      6, 0),
(12, 2, '已领养',   'adopted',    7, 0),
(13, 3, '待审核',   '0',          1, 1),
(14, 3, '已认证',   '1',          2, 0),
(15, 3, '已拒绝',   '2',          3, 0),
(16, 3, '已禁用',   '3',          4, 0),
(17, 4, '待处理',   'pending',    1, 1),
(18, 4, '已响应',   'responded',  2, 0),
(19, 4, '捕捉中',   'catching',   3, 0),
(20, 4, '治疗中',   'treating',   4, 0),
(21, 4, '恢复中',   'recovering', 5, 0),
(22, 4, '可领养',   'adoptable',  6, 0),
(23, 4, '已领养',   'adopted',    7, 0),
(24, 4, '已关闭',   'closed',     8, 0),
(25, 5, '响应',     'respond',    1, 0),
(26, 5, '捕捉',     'catch',      2, 0),
(27, 5, '转运',     'transport',  3, 0),
(28, 5, '寄养',     'foster',     4, 0),
(29, 6, '可领取',   'available',  1, 1),
(30, 6, '已接受',   'accepted',   2, 0),
(31, 6, '进行中',   'in_progress',3, 0),
(32, 6, '已完成',   'completed',  4, 0),
(33, 6, '已取消',   'cancelled',  5, 0);
