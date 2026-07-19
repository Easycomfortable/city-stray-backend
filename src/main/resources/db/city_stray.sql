/*
 Navicat Premium Dump SQL

 Source Server         : 1
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : city_stray

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 19/07/2026 16:33:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for adoption_agreement
-- ----------------------------
DROP TABLE IF EXISTS `adoption_agreement`;
CREATE TABLE `adoption_agreement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_id` bigint NOT NULL COMMENT '关联领养申请ID',
  `animal_id` bigint NOT NULL COMMENT '关联动物ID',
  `adopter_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '领养人姓名',
  `adopter_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '领养人电话',
  `adopter_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '领养人身份证号',
  `sign_date` date NULL DEFAULT NULL COMMENT '签署日期',
  `agreement_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '协议编号',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '协议内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_agreement_no`(`agreement_no` ASC) USING BTREE,
  INDEX `idx_apply_id`(`apply_id` ASC) USING BTREE,
  INDEX `idx_animal_id`(`animal_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '领养协议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of adoption_agreement
-- ----------------------------
INSERT INTO `adoption_agreement` VALUES (1, 1, 2, '张三', '13800000002', '110101199001011234', '2026-01-08', 'AG20260108001', '领养人承诺善待动物，定期体检，不遗弃，接受定期回访。如无法继续饲养需联系救助站。', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for adoption_apply
-- ----------------------------
DROP TABLE IF EXISTS `adoption_apply`;
CREATE TABLE `adoption_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请编号',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `animal_id` bigint NOT NULL COMMENT '申请领养动物ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职业',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '居住地址',
  `living_environment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '居住环境描述',
  `pet_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '养宠经验',
  `family_consent` tinyint NULL DEFAULT 0 COMMENT '家人是否同意：0-否 1-是',
  `photos` json NULL COMMENT '居住环境照片',
  `stage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'submitted' COMMENT '申请阶段：submitted-已提交 reviewing-审核中 approved-已通过 rejected-已拒绝 visiting-家访中 trial-试养中 adopted-已领养',
  `review_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `review_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '审核备注',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '拒绝原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_apply_no`(`apply_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_animal_id`(`animal_id` ASC) USING BTREE,
  INDEX `idx_stage`(`stage` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '领养申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of adoption_apply
-- ----------------------------
INSERT INTO `adoption_apply` VALUES (1, 'AD20260105001', 2, 2, '张三', '13800000002', 34, '软件工程师', '朝阳区望京花园小区3号楼1201', '三居室，有封闭阳台，小区允许养宠', '养过两只猫，有丰富养宠经验', 1, NULL, 'approved', NULL, NULL, NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `adoption_apply` VALUES (2, 'AD20260106001', 3, 2, '李四', '13800000003', 28, '设计师', '海淀区万柳书院5号楼803', '两居室，客厅宽敞，附近有公园', '目前养了一只狗', 1, NULL, 'reviewing', NULL, NULL, NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for adoption_visit
-- ----------------------------
DROP TABLE IF EXISTS `adoption_visit`;
CREATE TABLE `adoption_visit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_id` bigint NOT NULL COMMENT '关联领养申请ID',
  `visitor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家访人员姓名',
  `visit_date` date NULL DEFAULT NULL COMMENT '家访日期',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评估结果：good-良好 fair-一般 poor-较差',
  `evaluation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评估详情',
  `photos` json NULL COMMENT '家访照片',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_apply_id`(`apply_id` ASC) USING BTREE,
  INDEX `idx_visit_date`(`visit_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '家访记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of adoption_visit
-- ----------------------------
INSERT INTO `adoption_visit` VALUES (1, 1, '王伟', '2026-01-07', 'good', '居住环境整洁，有封闭阳台和纱窗，小区环境适合养宠，领养人态度认真负责', NULL, '建议通过', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for animal
-- ----------------------------
DROP TABLE IF EXISTS `animal`;
CREATE TABLE `animal`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动物编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品种',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1-公 0-母',
  `age_estimate` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年龄估算',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重（公斤）',
  `color` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '毛色',
  `is_neutered` tinyint NOT NULL DEFAULT 0 COMMENT '是否已绝育：0-否 1-是',
  `chip_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '芯片编号',
  `health_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '健康状态：treating-治疗中 recovering-恢复中 adoptable-可领养 adopted-已领养 deceased-已死亡',
  `photos` json NULL COMMENT '照片列表',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_animal_no`(`animal_no` ASC) USING BTREE,
  INDEX `idx_health_status`(`health_status` ASC) USING BTREE,
  INDEX `idx_breed`(`breed` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '动物档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of animal
-- ----------------------------
INSERT INTO `animal` VALUES (1, 'AN20260101001', '小橘', '中华田园猫', 1, '约3岁', 4.50, '橘色', 1, 'CHIP-2026-0001', 'recovering', '[\"https://example.com/cat1.jpg\"]', '右前腿有旧伤，已治疗恢复中', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `animal` VALUES (2, 'AN20260101002', '花花', '中华田园犬', 0, '约2岁', 6.80, '黑白花色', 0, 'CHIP-2026-0002', 'adoptable', '[\"https://example.com/dog1.jpg\"]', '性格温顺，已完成体检和疫苗', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `animal` VALUES (3, 'AN20260102001', '雪球', '中华田园猫', 0, '约2个月', 0.80, '白色', 0, NULL, 'treating', '[\"https://example.com/kitten1.jpg\"]', '奶猫，体质偏弱，正在观察治疗', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for content_article
-- ----------------------------
DROP TABLE IF EXISTS `content_article`;
CREATE TABLE `content_article`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类:GUIDE/RESCUE/TNR/MEDICAL',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摘要',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '正文内容',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签(JSON数组)',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识科普文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_article
-- ----------------------------
INSERT INTO `content_article` VALUES (1, '新手养猫指南：从接猫回家到日常照料', 'GUIDE', '本文详细介绍新手养猫的必备知识和注意事项', '一、接猫回家前的准备...', NULL, NULL, '管理员', 'PUBLISHED', 1256, '2026-06-15 10:00:00', '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_article` VALUES (2, '发现流浪猫受伤了该怎么办？', 'RESCUE', '发现受伤的流浪动物时的正确处理流程', '第一步：观察伤情...', NULL, NULL, '管理员', 'PUBLISHED', 892, '2026-06-10 14:30:00', '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_article` VALUES (3, 'TNR项目介绍：什么是捕捉-绝育-放归', 'TNR', 'TNR是目前国际公认的流浪动物人道管理方法', 'TNR是Trap-Neuter-Return的缩写...', NULL, NULL, '志愿者阿强', 'PUBLISHED', 567, '2026-06-05 09:00:00', '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_article` VALUES (4, '常见猫咪皮肤病的识别与处理', 'MEDICAL', '了解猫咪常见皮肤病的症状和初步处理方法', '一、猫癣 症状：圆形脱毛...', NULL, NULL, '管理员', 'PUBLISHED', 0, '2026-07-18 16:22:54', '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_article` VALUES (5, '如何正确喂养流浪猫', 'GUIDE', '科学喂养流浪猫的方法和建议', '选择合适的猫粮，避免喂食人类食物...', NULL, NULL, '管理员', 'PUBLISHED', 723, '2026-05-28 16:00:00', '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_article` VALUES (6, '这是一条测试用例', 'guide', '', '这是一条测试用例', NULL, '[]', '管理员', 'DRAFT', 1256, '2026-06-15 10:00:00', '2026-07-14 15:22:14', '2026-07-18 16:23:23', 1);
INSERT INTO `content_article` VALUES (7, '这是一条测试用例', 'guide', '', '这是一条测试用例', NULL, '[]', '管理员', 'PUBLISHED', 1256, '2026-06-15 10:00:00', '2026-07-14 15:22:14', '2026-07-18 16:28:32', 0);

-- ----------------------------
-- Table structure for content_banner
-- ----------------------------
DROP TABLE IF EXISTS `content_banner`;
CREATE TABLE `content_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片URL',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_banner
-- ----------------------------
INSERT INTO `content_banner` VALUES (1, '关爱流浪动物', '/uploads/banner/banner1.png', '/pages/about/index', 1, 1, '2026-07-14 15:22:14', '2026-07-18 14:07:50', 0);
INSERT INTO `content_banner` VALUES (2, '领养代替购买', '/uploads/banner/banner2.png', '/pages/adoption/index', 2, 1, '2026-07-14 15:22:14', '2026-07-18 14:07:50', 0);
INSERT INTO `content_banner` VALUES (3, '志愿者招募中', '/uploads/banner/banner3.png', '/pages/volunteer/index', 3, 1, '2026-07-14 15:22:14', '2026-07-18 14:07:50', 0);

-- ----------------------------
-- Table structure for content_comment
-- ----------------------------
DROP TABLE IF EXISTS `content_comment`;
CREATE TABLE `content_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `story_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `reply_to` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_story_id`(`story_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_comment
-- ----------------------------
INSERT INTO `content_comment` VALUES (1, 1, 1, 'admin', NULL, '这是一条测试数据', NULL, NULL, '2026-07-19 15:34:08', 0);

-- ----------------------------
-- Table structure for content_notice
-- ----------------------------
DROP TABLE IF EXISTS `content_notice`;
CREATE TABLE `content_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_notice
-- ----------------------------
INSERT INTO `content_notice` VALUES (1, '关于暑期领养活动的通知', '为鼓励更多爱心人士参与流浪动物领养，平台将在暑期推出领养优惠活动...', 'PUBLISHED', '2026-06-20 10:00:00', 512, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_notice` VALUES (2, '志愿者招募公告', '因救助站运营需要，现面向社会公开招募志愿者若干名...', 'DRAFT', NULL, 0, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_notice` VALUES (3, '平台系统升级公告', '为提升用户体验，平台将于本周六凌晨2点进行系统维护升级...', 'PUBLISHED', '2026-07-05 09:00:00', 324, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_notice` VALUES (4, '这是一条测试用例', '这是一条测试用例', 'published', '2026-06-20 10:00:00', 512, '2026-07-14 15:22:14', '2026-07-18 16:20:18', 1);
INSERT INTO `content_notice` VALUES (5, '这是一条测试用例', '这是一条测试用例', 'published', '2026-06-20 10:00:00', 512, '2026-07-14 15:22:14', '2026-07-19 10:37:09', 1);

-- ----------------------------
-- Table structure for content_report
-- ----------------------------
DROP TABLE IF EXISTS `content_report`;
CREATE TABLE `content_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reporter_id` bigint NULL DEFAULT NULL COMMENT '举报人ID',
  `reporter_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '举报人昵称',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '举报对象类型:POST/COMMENT',
  `target_id` bigint NULL DEFAULT NULL COMMENT '举报对象ID',
  `target_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被举报内容摘要',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '举报原因',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/RESOLVED/DISMISSED',
  `handler_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理人',
  `handle_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reporter_id`(`reporter_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内容举报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_report
-- ----------------------------
INSERT INTO `content_report` VALUES (1, 1, '用户A', 'POST', 10, '这是一条广告内容...', '涉嫌广告', 'resolved', 'admin', NULL, '2026-07-14 15:22:14', '2026-07-18 16:21:16', 0);
INSERT INTO `content_report` VALUES (2, 2, '用户B', 'COMMENT', 25, '不当评论内容...', '不当言论', 'RESOLVED', NULL, NULL, '2026-07-14 15:22:14', NULL, 0);
INSERT INTO `content_report` VALUES (3, 3, '用户C', 'POST', 15, '疑似虚假信息...', '虚假信息', 'dismissed', 'admin', NULL, '2026-07-14 15:22:14', '2026-07-18 16:21:18', 0);
INSERT INTO `content_report` VALUES (4, 1, 'admin', 'POST', 1, '小橘的救助故事', '其他违规', 'dismissed', 'admin', NULL, '2026-07-19 15:03:00', '2026-07-19 15:03:09', 0);

-- ----------------------------
-- Table structure for content_story
-- ----------------------------
DROP TABLE IF EXISTS `content_story`;
CREATE TABLE `content_story`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '发布用户ID',
  `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者昵称',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '故事内容',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/APPROVED/REJECTED',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '救助故事表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_story
-- ----------------------------
INSERT INTO `content_story` VALUES (1, 1, '志愿者小王', '小橘的救助故事', '那是一个下雨的傍晚，我在校园角落里发现了一只瑟瑟发抖的橘猫...', NULL, 'APPROVED', 256, 1, 1, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_story` VALUES (2, 2, '志愿者小李', '花花的新生', '花花被救助时只有三个月大，瘦得皮包骨头...', NULL, 'APPROVED', 0, 0, 0, '2026-07-14 15:22:14', '2026-07-18 14:08:09', 0);
INSERT INTO `content_story` VALUES (3, 3, '爱心人士小张', '从流浪到幸福', '在社区喂猫点认识了一只三花猫，决定带它去医院检查...', NULL, 'APPROVED', 189, 0, 0, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);
INSERT INTO `content_story` VALUES (4, NULL, '匿名用户', '雨中的相遇', '暴雨天在停车场发现一家五口的流浪猫家庭...', NULL, 'REJECTED', 0, 0, 0, '2026-07-14 15:22:14', '2026-07-14 15:22:14', 0);

-- ----------------------------
-- Table structure for donation_project
-- ----------------------------
DROP TABLE IF EXISTS `donation_project`;
CREATE TABLE `donation_project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '项目描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `target_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '目标金额',
  `raised_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '已筹金额',
  `donor_count` int NOT NULL DEFAULT 0 COMMENT '捐赠人数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中 ENDED-已结束',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of donation_project
-- ----------------------------
INSERT INTO `donation_project` VALUES (1, '流浪猫救助基金', '用于城市流浪猫的救助、治疗和安置', NULL, 50000.00, 12800.00, 86, 'ACTIVE', '2026-01-01', NULL, '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `donation_project` VALUES (2, 'TNR绝育计划', 'Trap-Neuter-Return，控制流浪动物数量', NULL, 30000.00, 8500.00, 42, 'ACTIVE', '2026-01-15', NULL, '2026-07-14 15:21:55', '2026-07-18 11:27:09', 0);
INSERT INTO `donation_project` VALUES (3, '冬季保暖行动', '为流浪动物搭建临时庇护所和提供保暖物资', '', 20000.00, 18600.00, 120, 'ENDED', '2025-11-01', NULL, '2026-07-14 15:21:55', '2026-07-18 13:39:55', 0);
INSERT INTO `donation_project` VALUES (4, '1', '1', '', 10000.00, 0.00, 0, 'ACTIVE', NULL, NULL, '2026-07-18 12:33:47', '2026-07-18 12:43:09', 1);
INSERT INTO `donation_project` VALUES (5, '2', '2', '', 10000.00, 0.00, 0, 'ACTIVE', NULL, NULL, '2026-07-18 12:36:55', '2026-07-18 12:43:09', 1);
INSERT INTO `donation_project` VALUES (6, '1', '1', '', 10000.00, 0.00, 0, 'ACTIVE', NULL, NULL, '2026-07-18 12:47:04', '2026-07-18 12:52:35', 1);
INSERT INTO `donation_project` VALUES (7, '毛巾', '1', '', 10000.00, 0.00, 0, 'ACTIVE', NULL, NULL, '2026-07-18 12:55:34', '2026-07-18 12:58:56', 1);
INSERT INTO `donation_project` VALUES (8, '毛巾', '1', '/uploads/2026/07/18/16204604fee34c499a8a4cf59e68510b.webp', 10000.00, 0.00, 0, 'ACTIVE', NULL, NULL, '2026-07-18 13:04:02', '2026-07-18 13:39:51', 1);
INSERT INTO `donation_project` VALUES (9, '这是一条测试数据', '', '/uploads/2026/07/19/d93f6ea77eef40579afa5b264a4084d1.webp', 10000.00, 10029.00, 4, 'ACTIVE', NULL, NULL, '2026-07-19 10:52:02', '2026-07-19 14:45:04', 1);

-- ----------------------------
-- Table structure for donation_record
-- ----------------------------
DROP TABLE IF EXISTS `donation_record`;
CREATE TABLE `donation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_id` bigint NULL DEFAULT NULL COMMENT '关联捐赠项目ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '捐赠用户ID',
  `donor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '捐赠人姓名',
  `donor_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '捐赠人手机号',
  `anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否 1-是',
  `amount` decimal(12, 2) NOT NULL COMMENT '捐赠金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'WECHAT' COMMENT '支付方式：WECHAT-微信 ALIPAY-支付宝 OFFLINE-线下',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信支付交易号',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认 SUCCESS-成功 REFUNDED-已退款 FAILED-失败',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `transaction_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_project_id`(`project_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of donation_record
-- ----------------------------
INSERT INTO `donation_record` VALUES (1, 1, 2, '爱心人士', NULL, 1, 500.00, 'WECHAT', 'WX20260701001', NULL, 'SUCCESS', NULL, '2026-07-01 09:15:00', '2026-07-01 09:15:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (2, 2, 3, '张先生', NULL, 0, 1000.00, 'WECHAT', 'WX20260702002', NULL, 'SUCCESS', NULL, '2026-07-02 14:30:00', '2026-07-02 14:30:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (3, 1, 2, '爱心人士', NULL, 1, 200.00, 'WECHAT', 'WX20260703003', NULL, 'SUCCESS', NULL, '2026-07-03 11:20:00', '2026-07-03 11:20:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (4, 1, 4, '王伟', NULL, 0, 300.00, 'WECHAT', 'WX20260705004', NULL, 'SUCCESS', NULL, '2026-07-05 16:45:00', '2026-07-05 16:45:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (5, 2, 5, '刘医生', NULL, 0, 2000.00, 'WECHAT', 'WX20260706005', NULL, 'SUCCESS', NULL, '2026-07-06 10:00:00', '2026-07-06 10:00:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (6, 3, 2, '爱心人士', NULL, 1, 100.00, 'WECHAT', 'WX20260708006', NULL, 'SUCCESS', NULL, '2026-07-08 08:30:00', '2026-07-08 08:30:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (7, 1, 3, '张先生', NULL, 0, 500.00, 'WECHAT', 'WX20260710007', NULL, 'PENDING', NULL, '2026-07-10 15:21:00', '2026-07-10 15:21:00', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (8, 2, 4, '王伟', NULL, 0, 800.00, 'WECHAT', 'WX20260711008', NULL, 'SUCCESS', NULL, '2026-07-14 15:21:55', '2026-07-14 15:21:55', '2026-07-18 11:24:02', 0);
INSERT INTO `donation_record` VALUES (9, 9, 1, '匿名', '', 0, 10.00, 'WECHAT', NULL, NULL, 'SUCCESS', NULL, NULL, '2026-07-19 14:37:50', '2026-07-19 14:37:50', 0);
INSERT INTO `donation_record` VALUES (10, 9, 1, '匿名', '', 0, 10.00, 'WECHAT', NULL, NULL, 'SUCCESS', NULL, NULL, '2026-07-19 14:38:04', '2026-07-19 14:38:04', 0);
INSERT INTO `donation_record` VALUES (11, 9, 1, '匿名', '', 0, 10.00, 'WECHAT', NULL, NULL, 'SUCCESS', NULL, NULL, '2026-07-19 14:39:17', '2026-07-19 14:39:17', 0);
INSERT INTO `donation_record` VALUES (12, 9, 1, '匿名', '', 0, 9999.00, 'WECHAT', NULL, NULL, 'SUCCESS', NULL, NULL, '2026-07-19 14:44:21', '2026-07-19 14:44:21', 0);

-- ----------------------------
-- Table structure for exchange_request
-- ----------------------------
DROP TABLE IF EXISTS `exchange_request`;
CREATE TABLE `exchange_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `volunteer_id` bigint NOT NULL COMMENT '志愿者ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '兑换物品名称',
  `cost_points` int NOT NULL COMMENT '消耗积分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态(pending/approved/rejected)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_volunteer`(`volunteer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分兑换申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exchange_request
-- ----------------------------
INSERT INTO `exchange_request` VALUES (1, 1, '宠物零食大礼包', 200, 'approved', '2026-07-03 14:30:00', '2026-07-18 11:08:55', 0);
INSERT INTO `exchange_request` VALUES (2, 2, '合作商家优惠券', 100, 'rejected', '2026-07-02 09:15:00', '2026-07-18 11:07:38', 0);

-- ----------------------------
-- Table structure for expense_record
-- ----------------------------
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支出类别：MEDICAL-医疗费用 FOOD-饲料费用 OPERATION-运营费用 OTHER-其他',
  `amount` decimal(12, 2) NOT NULL COMMENT '金额',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用途说明',
  `related_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联类型：RESCUE-救助 MEDICAL-医疗 ADOPTION-领养 OTHER-其他',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `applicant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请人',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'APPROVED' COMMENT '审批状态：PENDING-待审批 APPROVED-已通过 REJECTED-已拒绝',
  `approval_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `voucher_images` json NULL COMMENT '凭证图片',
  `expense_date` date NULL DEFAULT NULL COMMENT '支出日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_expense_date`(`expense_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支出记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expense_record
-- ----------------------------
INSERT INTO `expense_record` VALUES (1, 'MEDICAL', 580.00, '小橘右前腿骨折治疗', 'MEDICAL', 1, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-01-01', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (2, 'MEDICAL', 80.00, '小橘首针疫苗', 'MEDICAL', 2, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-01-05', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (3, 'MEDICAL', 200.00, '花花体检', 'MEDICAL', 3, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-01-01', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (4, 'MEDICAL', 120.00, '花花驱虫', 'MEDICAL', 4, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-01-02', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (5, 'FOOD', 350.00, '救助站猫粮狗粮采购', 'OTHER', NULL, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-07-01', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (6, 'OPERATION', 1200.00, '宣传物料制作（海报、传单）', 'OTHER', NULL, '系统管理员', 'APPROVED', NULL, NULL, NULL, '2026-07-05', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (7, 'MEDICAL', 150.00, '雪球奶猫营养补充', 'MEDICAL', 5, '王队长', 'APPROVED', NULL, NULL, NULL, '2026-07-02', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);
INSERT INTO `expense_record` VALUES (8, 'FOOD', 280.00, '流浪猫投喂点饲料补充', 'OTHER', NULL, '张三', 'PENDING', NULL, NULL, NULL, '2026-07-10', '2026-07-14 15:21:55', '2026-07-14 15:21:55', 0);

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '医院名称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '医院地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资质编号',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属区域',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '合作状态：1-合作中 0-已暂停 2-已终止',
  `discount_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '优惠项目说明',
  `price_standard` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '结算价格标准',
  `monthly_visits` int NOT NULL DEFAULT 0 COMMENT '本月接诊数',
  `total_visits` int NOT NULL DEFAULT 0 COMMENT '累计接诊数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_district`(`district` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '合作医院表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hospital
-- ----------------------------
INSERT INTO `hospital` VALUES (1, '爱心宠物医院', '朝阳区建国路88号', '010-88886666', '张院长', 'BJ-VET-2024-001', '朝阳区', 116.4287630, 39.9120150, 1, '绝育手术8折优惠', '[{\"name\":\"1\",\"price\":50,\"unit\":\"次\",\"remark\":\"\"}]', 0, 0, '2026-07-06 08:21:42', '2026-07-14 10:57:34', 0);
INSERT INTO `hospital` VALUES (2, '仁心动物诊所', '海淀区中关村大街66号', '010-66668888', '李主任', 'BJ-VET-2024-002', '海淀区', 116.3100000, 39.9560000, 1, '流浪动物免费体检', '基础检查100元/次，手术500元起', 0, 0, '2026-07-06 08:21:42', '2026-07-14 10:57:34', 0);
INSERT INTO `hospital` VALUES (3, '1', '1', '010-55557777', '王院长', 'BJ-VET-2024-003', '丰台区', 116.3500000, 39.8800000, 1, '救助组织7折结算', '综合诊疗200元起', 0, 0, '2026-07-06 08:21:42', '2026-07-18 16:16:59', 1);
INSERT INTO `hospital` VALUES (4, '2', '2', '22222222222222', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 0, 0, '2026-07-18 11:14:19', '2026-07-18 16:16:54', 1);
INSERT INTO `hospital` VALUES (5, '这是一家测试医院', '', '1', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 0, 0, '2026-07-19 09:59:45', '2026-07-19 10:01:53', 1);

-- ----------------------------
-- Table structure for medical_record
-- ----------------------------
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` bigint NOT NULL COMMENT '关联动物ID',
  `hospital_id` bigint NULL DEFAULT NULL COMMENT '关联医院ID',
  `record_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录类型：vaccine-疫苗 diagnosis-诊断 deworming-驱虫 surgery-手术',
  `diagnosis` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '诊断结果',
  `treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '治疗方案',
  `medication` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用药信息',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主治医生',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '费用（元）',
  `record_date` date NULL DEFAULT NULL COMMENT '记录日期',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_animal_id`(`animal_id` ASC) USING BTREE,
  INDEX `idx_hospital_id`(`hospital_id` ASC) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '医疗记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of medical_record
-- ----------------------------
INSERT INTO `medical_record` VALUES (1, 1, 1, 'diagnosis', '右前腿骨折', '清创、固定、抗感染治疗', '阿莫西林 每日两次', '刘医生', 580.00, '2026-01-01', '骨折较严重，需观察两周', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `medical_record` VALUES (2, 1, 1, 'vaccine', '已完成首针疫苗', '注射猫三联疫苗第一针', NULL, '刘医生', 80.00, '2026-01-05', '状态良好，可以接种', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `medical_record` VALUES (3, 2, 2, 'diagnosis', '体检正常', '基础体检各项指标正常', '驱虫药一次', '李主任', 200.00, '2026-01-01', '健康状况良好，适合领养', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `medical_record` VALUES (4, 2, 2, 'deworming', '常规驱虫', '体内驱虫+体外驱虫', '拜耳驱虫药', '李主任', 120.00, '2026-01-02', '下次驱虫三个月后', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `medical_record` VALUES (5, 3, 1, 'diagnosis', '体质偏弱', '补充营养液，保温观察', '营养膏 每日一次', '刘医生', 150.00, '2026-01-02', '奶猫需特别护理', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for points_rule
-- ----------------------------
DROP TABLE IF EXISTS `points_rule`;
CREATE TABLE `points_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则标识',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称',
  `points_value` decimal(10, 2) NOT NULL COMMENT '积分值',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '单位(分/小时等)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_key`(`rule_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_rule
-- ----------------------------
INSERT INTO `points_rule` VALUES (1, 'rescue_task', '完成救助任务', 50.00, '分', '2026-07-18 10:47:39', '2026-07-18 10:47:39');
INSERT INTO `points_rule` VALUES (2, 'tnr_task', '参与TNR行动', 30.00, '分', '2026-07-18 10:47:39', '2026-07-18 10:47:39');
INSERT INTO `points_rule` VALUES (3, 'hour_rate', '服务时长奖励', 2.00, '分/小时', '2026-07-18 10:47:39', '2026-07-18 10:47:39');
INSERT INTO `points_rule` VALUES (4, 'checkin', '连续签到', 5.00, '分/天', '2026-07-18 10:47:39', '2026-07-18 10:47:39');

-- ----------------------------
-- Table structure for rescue_order
-- ----------------------------
DROP TABLE IF EXISTS `rescue_order`;
CREATE TABLE `rescue_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '工单编号',
  `report_id` bigint NULL DEFAULT NULL COMMENT '关联上报记录ID',
  `animal_id` bigint NULL DEFAULT NULL COMMENT '关联动物档案ID',
  `volunteer_id` bigint NULL DEFAULT NULL COMMENT '指派志愿者ID',
  `hospital_id` bigint NULL DEFAULT NULL COMMENT '指派医院ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending' COMMENT '工单状态：pending-待处理 responded-已响应 catching-捕捉中 treating-治疗中 recovering-恢复中 adoptable-可领养 adopted-已领养 closed-已关闭',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属区域',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '处理描述',
  `assigned_time` datetime NULL DEFAULT NULL COMMENT '指派时间',
  `respond_time` datetime NULL DEFAULT NULL COMMENT '响应时间',
  `close_time` datetime NULL DEFAULT NULL COMMENT '关闭时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_report_id`(`report_id` ASC) USING BTREE,
  INDEX `idx_animal_id`(`animal_id` ASC) USING BTREE,
  INDEX `idx_volunteer_id`(`volunteer_id` ASC) USING BTREE,
  INDEX `idx_hospital_id`(`hospital_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_district`(`district` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '救助工单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of rescue_order
-- ----------------------------
INSERT INTO `rescue_order` VALUES (1, 'RO20260101001', 1, 1, 1, 1, 'recovering', '朝阳区', '志愿者张三已捕捉，送往爱心宠物医院治疗', '2026-01-01 10:30:00', '2026-01-01 09:15:00', NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `rescue_order` VALUES (2, 'RO20260101002', 2, 2, 3, 2, 'adoptable', '海淀区', '志愿者王伟已捕捉，送至仁心动物诊所体检完毕', '2026-01-01 14:00:00', '2026-01-01 12:30:00', NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `rescue_order` VALUES (3, 'RO20260102001', 3, 3, 1, 1, 'treating', '丰台区', '两只奶猫已捕捉，其中白色奶猫正在治疗', '2026-01-02 09:00:00', '2026-01-02 08:20:00', NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `rescue_order` VALUES (4, 'RO20260719001', 5, NULL, NULL, NULL, 'pending', NULL, NULL, NULL, NULL, NULL, '2026-07-19 10:22:26', '2026-07-19 10:46:13', 1);
INSERT INTO `rescue_order` VALUES (5, 'RO20260719002', 6, NULL, NULL, NULL, 'pending', NULL, NULL, NULL, NULL, NULL, '2026-07-19 10:36:41', '2026-07-19 10:46:13', 1);

-- ----------------------------
-- Table structure for revisit_record
-- ----------------------------
DROP TABLE IF EXISTS `revisit_record`;
CREATE TABLE `revisit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_id` bigint NOT NULL COMMENT '关联领养申请ID',
  `revisit_date` date NULL DEFAULT NULL COMMENT '回访日期',
  `condition_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '近况描述',
  `photos` json NULL COMMENT '回访照片',
  `health_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '健康状态',
  `is_normal` tinyint NULL DEFAULT 1 COMMENT '是否正常：0-异常 1-正常',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_apply_id`(`apply_id` ASC) USING BTREE,
  INDEX `idx_revisit_date`(`revisit_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回访记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of revisit_record
-- ----------------------------
INSERT INTO `revisit_record` VALUES (1, 1, '2026-01-15', '花花适应良好，活泼好动，饮食正常，已熟悉新环境', NULL, '健康', 1, '领养人反馈非常积极', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `revisit_record` VALUES (2, 1, '2026-02-15', '花花状态良好，已完成第二次疫苗接种', NULL, '健康', 1, '一切正常', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for story_like
-- ----------------------------
DROP TABLE IF EXISTS `story_like`;
CREATE TABLE `story_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `story_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_story_user`(`story_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_story_id`(`story_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '故事点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of story_like
-- ----------------------------
INSERT INTO `story_like` VALUES (2, 1, 1, '2026-07-19 15:34:01');

-- ----------------------------
-- Table structure for stray_report
-- ----------------------------
DROP TABLE IF EXISTS `stray_report`;
CREATE TABLE `stray_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '上报用户ID',
  `report_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '上报编号（RS+日期+序号）',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属区域',
  `animal_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '动物类型：cat-猫 dog-狗 other-其他',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '外观描述',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `is_injured` tinyint NULL DEFAULT 0 COMMENT '是否受伤：0-否 1-是',
  `is_friendly` tinyint NULL DEFAULT 0 COMMENT '是否亲人：0-否 1-是',
  `photos` json NULL COMMENT '照片URL列表',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态：0-待处理 1-已处理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_report_no`(`report_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_district`(`district` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流浪动物上报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of stray_report
-- ----------------------------
INSERT INTO `stray_report` VALUES (1, 2, 'RS20260101001', 116.4075260, 39.9040300, '朝阳区建国路万达广场北侧', '朝阳区', 'cat', '橘色成年猫，约3岁，右前腿有伤口', 1, 1, 1, '[\"https://example.com/cat1.jpg\"]', 1, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `stray_report` VALUES (2, 3, 'RS20260101002', 116.3100000, 39.9560000, '海淀区中关村软件园二期东门', '海淀区', 'dog', '黑白花小型犬，性格温顺，戴项圈', 1, 0, 1, '[\"https://example.com/dog1.jpg\"]', 1, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `stray_report` VALUES (3, 2, 'RS20260102001', 116.3500000, 39.8800000, '丰台区南三环草桥地铁站附近', '丰台区', 'cat', '两只小奶猫，一白一灰，约2个月大', 2, 0, 1, '[\"https://example.com/kitten1.jpg\"]', 0, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `stray_report` VALUES (4, 3, 'RS20260102002', 116.4500000, 39.9200000, '朝阳区望京SOHO地下停车场', '朝阳区', 'dog', '黄色中华田园犬，体型中等，怕人', 1, 0, 0, NULL, 0, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `stray_report` VALUES (5, 1, 'RS20260719001', 113.4740182, 34.7983028, '河南省郑州市附近', NULL, NULL, '【紧急求助】', 1, 0, 0, '[]', 0, '2026-07-19 10:22:26', '2026-07-19 10:46:13', 1);
INSERT INTO `stray_report` VALUES (6, 1, 'RS20260719002', 113.4739871, 34.7982916, '河南省郑州市附近', NULL, 'cat', '', 1, 0, 0, '[\"/uploads/2026/07/19/5a48c18d19fa4e8c891f0b6612da2ac1.jpg\"]', 0, '2026-07-19 10:36:41', '2026-07-19 10:46:13', 1);
INSERT INTO `stray_report` VALUES (25, 1, 'RS20260719003', 113.7122848, 34.7397812, '河南省郑州市附近', NULL, 'cat', '这是一条测试数据\n', 1, 0, 0, '[\"/uploads/2026/07/19/4bc957ec17f2462c93fdda6217a7fb81.jpg\"]', 0, '2026-07-19 16:28:51', '2026-07-19 16:28:51', 0);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_id` bigint NOT NULL COMMENT '字典类型ID',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典标签',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典值',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认：0-否 1-是',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_type_id`(`dict_type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '治疗中', 'treating', 1, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-18 20:33:04', 1);
INSERT INTO `sys_dict_data` VALUES (2, 1, '恢复中', 'recovering', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-18 20:33:04', 1);
INSERT INTO `sys_dict_data` VALUES (3, 1, '可领养', 'adoptable', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-18 20:33:04', 1);
INSERT INTO `sys_dict_data` VALUES (4, 1, '已领养', 'adopted', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-18 20:33:04', 1);
INSERT INTO `sys_dict_data` VALUES (5, 1, '已死亡', 'deceased', 5, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-18 20:33:04', 1);
INSERT INTO `sys_dict_data` VALUES (6, 2, '已提交', 'submitted', 1, 1, 1, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (7, 2, '审核中', 'reviewing', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (8, 2, '已通过', 'approved', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (9, 2, '已拒绝', 'rejected', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (10, 2, '家访中', 'visiting', 5, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (11, 2, '试养中', 'trial', 6, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (12, 2, '已领养', 'adopted', 7, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (13, 3, '待审核', '0', 1, 1, 1, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (14, 3, '已认证', '1', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (15, 3, '已拒绝', '2', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (16, 3, '已禁用', '3', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (17, 4, '待处理', 'pending', 1, 1, 1, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (18, 4, '已响应', 'responded', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (19, 4, '捕捉中', 'catching', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (20, 4, '治疗中', 'treating', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (21, 4, '恢复中', 'recovering', 5, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (22, 4, '可领养', 'adoptable', 6, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (23, 4, '已领养', 'adopted', 7, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (24, 4, '已关闭', 'closed', 8, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (25, 5, '响应', 'respond', 1, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (26, 5, '捕捉', 'catch', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (27, 5, '转运', 'transport', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (28, 5, '寄养', 'foster', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (29, 6, '可领取', 'available', 1, 1, 1, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (30, 6, '已接受', 'accepted', 2, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (31, 6, '进行中', 'in_progress', 3, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (32, 6, '已完成', 'completed', 4, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (33, 6, '已取消', 'cancelled', 5, 1, 0, NULL, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_data` VALUES (34, 7, '猫', 'cat', 1, 1, 1, NULL, '2026-07-18 20:15:58', '2026-07-18 20:15:58', 0);
INSERT INTO `sys_dict_data` VALUES (35, 7, '狗', 'dog', 2, 1, 0, NULL, '2026-07-18 20:15:58', '2026-07-18 20:15:58', 0);
INSERT INTO `sys_dict_data` VALUES (36, 7, '其他', 'other', 3, 1, 0, NULL, '2026-07-18 20:15:58', '2026-07-18 20:15:58', 0);
INSERT INTO `sys_dict_data` VALUES (47, 1, '治疗中', 'treating', 1, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_data` VALUES (48, 1, '恢复中', 'recovering', 2, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_data` VALUES (49, 1, '可领养', 'adoptable', 3, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_data` VALUES (50, 1, '已领养', 'adopted', 4, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_data` VALUES (51, 1, '已死亡', 'deceased', 5, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_data` VALUES (52, 1, '1', '1', 6, 1, 0, NULL, '2026-07-18 20:33:05', '2026-07-18 20:33:05', 0);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '动物健康状态', 'animal_health_status', '动物的健康与收容状态', 1, '2026-07-14 15:22:41', '2026-07-18 20:33:05', 0);
INSERT INTO `sys_dict_type` VALUES (2, '领养申请阶段', 'adoption_stage', '领养申请的审批阶段', 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_type` VALUES (3, '志愿者认证状态', 'volunteer_auth_status', '志愿者认证状态', 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_type` VALUES (4, '救助工单状态', 'rescue_order_status', '救助工单的处理状态', 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_type` VALUES (5, '任务类型', 'task_type', '志愿任务的类型', 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_type` VALUES (6, '任务状态', 'task_status', '志愿任务的状态', 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_dict_type` VALUES (7, '动物种类', 'animal_species', '动物物种分类', 1, '2026-07-18 20:07:19', '2026-07-18 20:07:19', 0);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作用户名',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作模块',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作类型：LOGIN/CREATE/UPDATE/DELETE/EXPORT',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作内容',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求URL',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作IP',
  `duration` int NULL DEFAULT NULL COMMENT '耗时(毫秒)',
  `success` tinyint NOT NULL DEFAULT 1 COMMENT '是否成功：1-成功 0-失败',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:41:57');
INSERT INTO `sys_log` VALUES (2, 2, 'zhangsan', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:42:18');
INSERT INTO `sys_log` VALUES (3, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:47:00');
INSERT INTO `sys_log` VALUES (4, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:47:31');
INSERT INTO `sys_log` VALUES (5, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:50:46');
INSERT INTO `sys_log` VALUES (6, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:50:57');
INSERT INTO `sys_log` VALUES (7, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:53:15');
INSERT INTO `sys_log` VALUES (8, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:54:12');
INSERT INTO `sys_log` VALUES (9, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 09:57:07');
INSERT INTO `sys_log` VALUES (10, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:03:16');
INSERT INTO `sys_log` VALUES (11, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:04:43');
INSERT INTO `sys_log` VALUES (12, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:05:21');
INSERT INTO `sys_log` VALUES (13, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:05:59');
INSERT INTO `sys_log` VALUES (14, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:06:11');
INSERT INTO `sys_log` VALUES (15, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:27:37');
INSERT INTO `sys_log` VALUES (16, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:28:15');
INSERT INTO `sys_log` VALUES (17, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:32:03');
INSERT INTO `sys_log` VALUES (18, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:32:54');
INSERT INTO `sys_log` VALUES (19, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:42:11');
INSERT INTO `sys_log` VALUES (20, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:42:15');
INSERT INTO `sys_log` VALUES (21, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:45:16');
INSERT INTO `sys_log` VALUES (22, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:45:31');
INSERT INTO `sys_log` VALUES (23, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-15 10:56:52');
INSERT INTO `sys_log` VALUES (24, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 08:53:14');
INSERT INTO `sys_log` VALUES (25, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 10:37:06');
INSERT INTO `sys_log` VALUES (26, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 11:07:29');
INSERT INTO `sys_log` VALUES (27, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 15:37:11');
INSERT INTO `sys_log` VALUES (28, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 15:46:00');
INSERT INTO `sys_log` VALUES (29, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-18 15:49:29');
INSERT INTO `sys_log` VALUES (30, 1, 'admin', '角色管理', 'CREATE', '保存角色', 'POST', '/api/role/save', '0:0:0:0:0:0:0:1', 17, 1, NULL, '2026-07-19 08:36:55');
INSERT INTO `sys_log` VALUES (31, 1, 'admin', '角色管理', 'DELETE', '删除角色', 'DELETE', '/api/role/5', '0:0:0:0:0:0:0:1', 16, 1, NULL, '2026-07-19 08:37:05');
INSERT INTO `sys_log` VALUES (32, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-19 08:53:49');
INSERT INTO `sys_log` VALUES (33, NULL, NULL, '用户管理', 'LOGIN', '用户登录', 'POST', '/api/user/login', '0:0:0:0:0:0:0:1', 165, 1, NULL, '2026-07-19 08:53:49');
INSERT INTO `sys_log` VALUES (34, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-19 09:26:29');
INSERT INTO `sys_log` VALUES (35, NULL, NULL, '用户管理', 'LOGIN', '用户登录', 'POST', '/api/user/login', '0:0:0:0:0:0:0:1', 132, 1, NULL, '2026-07-19 09:26:29');
INSERT INTO `sys_log` VALUES (36, 1, 'admin', '合作医院', 'CREATE', '新增合作医院', 'POST', '/api/hospital/save', '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-07-19 09:59:45');
INSERT INTO `sys_log` VALUES (37, 1, 'admin', '合作医院', 'DELETE', '删除合作医院', 'DELETE', '/api/hospital/5', '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-07-19 10:01:53');
INSERT INTO `sys_log` VALUES (38, 1, 'admin', '内容管理', 'DELETE', '删除公告', 'DELETE', '/api/content/notice/5', '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-07-19 10:37:09');
INSERT INTO `sys_log` VALUES (39, 1, 'admin', '财务管理', 'CREATE', '保存捐赠项目', 'POST', '/api/finance/project/save', '0:0:0:0:0:0:0:1', 12, 1, NULL, '2026-07-19 10:52:02');
INSERT INTO `sys_log` VALUES (40, 1, 'admin', '财务管理', 'CREATE', '用户捐款', 'POST', '/api/finance/donate', '0:0:0:0:0:0:0:1', 42, 1, NULL, '2026-07-19 14:37:50');
INSERT INTO `sys_log` VALUES (41, 1, 'admin', '财务管理', 'CREATE', '用户捐款', 'POST', '/api/finance/donate', '0:0:0:0:0:0:0:1', 15, 1, NULL, '2026-07-19 14:38:04');
INSERT INTO `sys_log` VALUES (42, 1, 'admin', '财务管理', 'CREATE', '用户捐款', 'POST', '/api/finance/donate', '0:0:0:0:0:0:0:1', 15, 1, NULL, '2026-07-19 14:39:17');
INSERT INTO `sys_log` VALUES (43, 1, 'admin', '财务管理', 'CREATE', '用户捐款', 'POST', '/api/finance/donate', '0:0:0:0:0:0:0:1', 17, 1, NULL, '2026-07-19 14:44:21');
INSERT INTO `sys_log` VALUES (44, 1, 'admin', '财务管理', 'DELETE', '删除捐赠项目', 'DELETE', '/api/finance/project/9', '0:0:0:0:0:0:0:1', 18, 1, NULL, '2026-07-19 14:45:04');
INSERT INTO `sys_log` VALUES (45, 1, 'admin', '内容管理', 'CREATE', '提交举报', 'POST', '/api/content/report/save', '0:0:0:0:0:0:0:1', 30, 1, NULL, '2026-07-19 15:03:00');
INSERT INTO `sys_log` VALUES (46, 1, 'admin', '内容管理', 'UPDATE', '处理举报', 'POST', '/api/content/report/4/handle', '0:0:0:0:0:0:0:1', 18, 1, NULL, '2026-07-19 15:03:09');
INSERT INTO `sys_log` VALUES (47, 1, 'admin', '用户管理', 'LOGIN', '用户登录成功', NULL, NULL, NULL, NULL, 1, NULL, '2026-07-19 15:48:50');
INSERT INTO `sys_log` VALUES (48, NULL, NULL, '用户管理', 'LOGIN', '用户登录', 'POST', '/api/user/login', '0:0:0:0:0:0:0:1', 297, 1, NULL, '2026-07-19 15:48:49');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，0为顶级',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '类型：0-目录 1-菜单 2-按钮',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `visible` tinyint NOT NULL DEFAULT 1 COMMENT '是否可见：1-是 0-否',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (30, 0, '数据大屏', 'DataAnalysis', '/dashboard', NULL, NULL, 1, 1, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (31, 0, '地图总览', 'MapLocation', '/map', NULL, NULL, 1, 2, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (32, 0, '救助工单管理', 'List', '/rescue-order', NULL, NULL, 1, 3, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (33, 0, '动物档案管理', 'Grid', '/animal', NULL, NULL, 1, 4, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (34, 0, '领养管理', 'ChatLineSquare', '/adoption', NULL, NULL, 1, 5, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (35, 0, '志愿者管理', 'UserFilled', '/volunteer', NULL, NULL, 0, 6, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (36, 35, '志愿者列表', 'UserFilled', '/volunteer', NULL, NULL, 1, 1, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (37, 35, '排班日历', 'Calendar', '/volunteer/schedule', NULL, NULL, 1, 2, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (38, 35, '认证审核', 'UserFilled', '/volunteer/certify', NULL, NULL, 1, 3, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (39, 35, '积分管理', 'Coin', '/volunteer/points', NULL, NULL, 1, 4, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (40, 0, '合作医院管理', 'OfficeBuilding', '/hospital', NULL, NULL, 1, 7, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (41, 0, '财务管理', 'Money', '/finance', NULL, NULL, 0, 8, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (42, 41, '捐款流水', 'Money', '/finance/donation', NULL, NULL, 1, 1, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (43, 41, '捐款项目', 'Coin', '/finance/project', NULL, NULL, 1, 2, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (44, 41, '财务报表', 'DataLine', '/finance/report', NULL, NULL, 1, 3, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (45, 0, '内容管理', 'Picture', '/content', NULL, NULL, 0, 9, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (46, 45, '轮播图管理', 'Picture', '/content/banner', NULL, NULL, 1, 1, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (47, 45, '救助故事审核', 'Reading', '/content/stories', NULL, NULL, 1, 2, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (48, 45, '公告管理', 'Bell', '/content/notice', NULL, NULL, 1, 3, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (49, 45, '举报处理', 'Warning', '/content/report', NULL, NULL, 1, 4, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (50, 45, '知识库', 'Notebook', '/content/knowledge', NULL, NULL, 1, 5, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (51, 0, '系统管理', 'Setting', '/system', NULL, NULL, 0, 10, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (52, 51, '用户管理', 'User', '/system/user', NULL, NULL, 1, 1, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (53, 51, '角色管理', 'Avatar', '/system/role', NULL, NULL, 1, 2, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (54, 51, '菜单管理', 'Menu', '/system/menu', NULL, NULL, 1, 3, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (55, 51, '字典管理', 'Setting', '/system/dict', NULL, NULL, 1, 4, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (56, 51, '系统日志', 'Document', '/system/log', NULL, NULL, 1, 5, 1, '2026-07-18 19:20:44', '2026-07-18 19:20:44', 0);
INSERT INTO `sys_menu` VALUES (57, 0, '这是一条测试数据', '', '', '', NULL, 1, 0, 1, '2026-07-18 19:29:49', '2026-07-18 19:29:57', 1);
INSERT INTO `sys_menu` VALUES (58, 0, '这是一条测试数据', 'Star', '/dashboard', '', NULL, 1, 11, 1, '2026-07-18 19:32:50', '2026-07-18 19:36:54', 1);
INSERT INTO `sys_menu` VALUES (59, 0, '这是一条测试数据', 'Star', '/test', '', NULL, 0, 11, 1, '2026-07-18 19:48:53', '2026-07-18 19:50:18', 1);

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '接收用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型:SYSTEM/ADOPTION/RESCUE/STORY/DONATION/VOLUNTEER',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联业务类型',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站内通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notification
-- ----------------------------
INSERT INTO `sys_notification` VALUES (1, 1, '系统通知', '欢迎使用城流浪管理平台！', 'SYSTEM', NULL, NULL, 1, '2026-07-14 15:22:53', '2026-07-19 14:46:19', 0);
INSERT INTO `sys_notification` VALUES (2, 1, '领养审核提醒', '您有3条待审核的领养申请，请及时处理。', 'ADOPTION', NULL, NULL, 1, '2026-07-14 15:22:53', '2026-07-19 14:46:20', 0);
INSERT INTO `sys_notification` VALUES (3, 1, '救助工单更新', '工单 #RO20260705001 已分配志愿者。', 'RESCUE', NULL, NULL, 1, '2026-07-14 15:22:53', NULL, 0);
INSERT INTO `sys_notification` VALUES (4, 2, '故事审核通知', '您发布的故事《花花的新生》审核结果：approved', 'STORY', 'STORY', 2, 0, '2026-07-18 14:01:03', NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', '拥有全部权限', 1, 1, '2026-07-14 15:22:41', '2026-07-18 17:04:10', 0);
INSERT INTO `sys_role` VALUES (2, '救助站管理员', 'rescue_admin', '管理救助工单和志愿者', 2, 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_role` VALUES (3, '医院管理员', 'hospital_admin', '管理医院和医疗记录', 3, 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_role` VALUES (4, '普通用户', 'user', '可浏览和提交领养申请', 4, 1, '2026-07-14 15:22:41', '2026-07-14 15:22:41', 0);
INSERT INTO `sys_role` VALUES (5, '这是一位测试角色', 'test', '', 0, 1, '2026-07-19 08:36:55', '2026-07-19 08:37:04', 1);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (46, 1, 30);
INSERT INTO `sys_role_menu` VALUES (47, 1, 31);
INSERT INTO `sys_role_menu` VALUES (48, 1, 32);
INSERT INTO `sys_role_menu` VALUES (49, 1, 33);
INSERT INTO `sys_role_menu` VALUES (50, 1, 34);
INSERT INTO `sys_role_menu` VALUES (51, 1, 35);
INSERT INTO `sys_role_menu` VALUES (52, 1, 36);
INSERT INTO `sys_role_menu` VALUES (53, 1, 37);
INSERT INTO `sys_role_menu` VALUES (54, 1, 38);
INSERT INTO `sys_role_menu` VALUES (55, 1, 39);
INSERT INTO `sys_role_menu` VALUES (56, 1, 40);
INSERT INTO `sys_role_menu` VALUES (57, 1, 41);
INSERT INTO `sys_role_menu` VALUES (58, 1, 42);
INSERT INTO `sys_role_menu` VALUES (59, 1, 43);
INSERT INTO `sys_role_menu` VALUES (60, 1, 44);
INSERT INTO `sys_role_menu` VALUES (61, 1, 45);
INSERT INTO `sys_role_menu` VALUES (62, 1, 46);
INSERT INTO `sys_role_menu` VALUES (63, 1, 47);
INSERT INTO `sys_role_menu` VALUES (64, 1, 48);
INSERT INTO `sys_role_menu` VALUES (65, 1, 49);
INSERT INTO `sys_role_menu` VALUES (66, 1, 50);
INSERT INTO `sys_role_menu` VALUES (67, 1, 51);
INSERT INTO `sys_role_menu` VALUES (68, 1, 52);
INSERT INTO `sys_role_menu` VALUES (69, 1, 53);
INSERT INTO `sys_role_menu` VALUES (70, 1, 54);
INSERT INTO `sys_role_menu` VALUES (71, 1, 55);
INSERT INTO `sys_role_menu` VALUES (72, 1, 56);
INSERT INTO `sys_role_menu` VALUES (91, 1, 58);
INSERT INTO `sys_role_menu` VALUES (92, 1, 59);
INSERT INTO `sys_role_menu` VALUES (77, 2, 30);
INSERT INTO `sys_role_menu` VALUES (78, 2, 31);
INSERT INTO `sys_role_menu` VALUES (79, 2, 32);
INSERT INTO `sys_role_menu` VALUES (80, 2, 33);
INSERT INTO `sys_role_menu` VALUES (81, 2, 34);
INSERT INTO `sys_role_menu` VALUES (82, 2, 35);
INSERT INTO `sys_role_menu` VALUES (83, 2, 36);
INSERT INTO `sys_role_menu` VALUES (84, 2, 37);
INSERT INTO `sys_role_menu` VALUES (85, 2, 38);
INSERT INTO `sys_role_menu` VALUES (86, 2, 39);
INSERT INTO `sys_role_menu` VALUES (87, 2, 40);
INSERT INTO `sys_role_menu` VALUES (88, 3, 30);
INSERT INTO `sys_role_menu` VALUES (89, 3, 33);
INSERT INTO `sys_role_menu` VALUES (90, 4, 30);

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联救助工单ID',
  `volunteer_id` bigint NULL DEFAULT NULL COMMENT '指派志愿者ID',
  `task_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务类型：respond-响应 catch-捕捉 transport-转运 foster-寄养',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'available' COMMENT '任务状态：available-可领取 accepted-已接受 in_progress-进行中 completed-已完成 cancelled-已取消',
  `points_reward` int NOT NULL DEFAULT 10 COMMENT '积分奖励',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务描述',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务地点',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_volunteer_id`(`volunteer_id` ASC) USING BTREE,
  INDEX `idx_task_type`(`task_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '志愿任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (1, 1, 1, 'catch', 'completed', 20, '前往万达广场北侧捕捉受伤橘猫', '朝阳区建国路万达广场北侧', 116.4075260, 39.9040300, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `task` VALUES (2, 1, 1, 'transport', 'completed', 10, '将受伤橘猫转运至爱心宠物医院', '朝阳区建国路', 116.4075260, 39.9040300, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `task` VALUES (3, 2, 3, 'catch', 'completed', 20, '前往中关村软件园捕捉流浪犬', '海淀区中关村软件园二期', 116.3100000, 39.9560000, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `task` VALUES (4, 3, 1, 'catch', 'completed', 20, '前往草桥地铁站附近捕捉两只奶猫', '丰台区南三环草桥地铁站', 116.3500000, 39.8800000, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `task` VALUES (5, 3, 1, 'transport', 'completed', 10, '将奶猫转运至爱心宠物医院', '丰台区南三环', 116.3500000, 39.8800000, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);

-- ----------------------------
-- Table structure for task_log
-- ----------------------------
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NULL DEFAULT NULL,
  `volunteer_id` bigint NOT NULL COMMENT '操作志愿者ID',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志内容',
  `photos` json NULL COMMENT '相关照片',
  `service_hours` decimal(4, 1) NULL DEFAULT NULL COMMENT '本次服务时长（小时）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_volunteer_id`(`volunteer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of task_log
-- ----------------------------
INSERT INTO `task_log` VALUES (1, 1, 1, '接受任务', '已确认前往现场', NULL, 0.0, '2026-07-06 08:21:42', 0);
INSERT INTO `task_log` VALUES (2, 1, 1, '到达现场', '已到达万达广场北侧，发现目标橘猫', NULL, 0.0, '2026-07-06 08:21:42', 0);
INSERT INTO `task_log` VALUES (3, 1, 1, '完成捕捉', '使用诱捕笼成功捕捉，猫咪右前腿有伤', NULL, 2.5, '2026-07-06 08:21:42', 0);
INSERT INTO `task_log` VALUES (4, 2, 1, '完成转运', '已将猫咪安全送达爱心宠物医院', NULL, 1.0, '2026-07-06 08:21:42', 0);
INSERT INTO `task_log` VALUES (5, 3, 3, '完成捕捉', '花花很温顺，直接用牵引绳带走', NULL, 1.5, '2026-07-06 08:21:42', 0);
INSERT INTO `task_log` VALUES (7, NULL, 1, 'points', '兑换:宠物零食大礼包|-200', NULL, NULL, '2026-07-18 11:09:09', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信openid',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码（加密存储）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user' COMMENT '角色：user-普通用户 admin-管理员 rescue_admin-救助站管理员 hospital_admin-医院管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常 0-禁用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ox_test_admin_001', 'admin', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13800000001', '系统管理员', NULL, 'admin', 1, '2026-07-19 15:48:50', '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `user` VALUES (2, 'ox_test_user_001', 'zhangsan', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13800000002', '张三', NULL, 'user', 1, '2026-07-15 09:42:18', '2026-07-06 08:21:42', '2026-07-12 10:14:10', 0);
INSERT INTO `user` VALUES (3, 'ox_test_user_002', 'lisi', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13800000003', '李四', NULL, 'user', 1, NULL, '2026-07-06 08:21:42', '2026-07-12 10:14:10', 0);
INSERT INTO `user` VALUES (4, 'ox_test_rescue_001', 'wangwei', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13800000004', '王队长', NULL, 'rescue_admin', 1, NULL, '2026-07-06 08:21:42', '2026-07-12 10:14:10', 0);
INSERT INTO `user` VALUES (5, 'ox_test_hosp_001', 'doctorliu', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13800000005', '刘医生', NULL, 'hospital_admin', 1, NULL, '2026-07-06 08:21:42', '2026-07-12 10:14:10', 0);
INSERT INTO `user` VALUES (6, NULL, 'xiaoming', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13900001111', '小明', NULL, 'USER', 0, NULL, '2026-07-12 10:47:36', '2026-07-18 17:01:59', 0);
INSERT INTO `user` VALUES (7, NULL, 'xiaohong', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '13900002222', '小红', NULL, 'USER', 1, NULL, '2026-07-12 10:47:36', '2026-07-12 10:47:36', 0);

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系手机号',
  `skill_tags` json NULL COMMENT '技能标签，如[\"捕捉\",\"急救\",\"驾驶\"]',
  `total_hours` decimal(8, 1) NULL DEFAULT 0.0 COMMENT '累计服务时长（小时）',
  `points` int NULL DEFAULT 0 COMMENT '积分余额',
  `auth_status` tinyint NOT NULL DEFAULT 0 COMMENT '认证状态：0-待审核 1-已认证 2-已拒绝 3-已禁用',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_auth_status`(`auth_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '志愿者扩展表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (1, 2, '张三', '110101199001011234', '13800000002', '[\"捕捉\", \"驾驶\"]', 48.5, -80, 0, NULL, '2026-07-06 08:21:42', '2026-07-18 11:08:55', 0);
INSERT INTO `volunteer` VALUES (2, 3, '李四', '110101199205052345', '13800000003', '[\"急救\", \"护理\"]', 32.0, 85, 0, NULL, '2026-07-06 08:21:42', '2026-07-18 10:16:49', 0);
INSERT INTO `volunteer` VALUES (3, 4, '王伟', '110101198803033456', '13800000004', '[\"捕捉\", \"急救\", \"驾驶\"]', 96.0, 260, 1, NULL, '2026-07-06 08:21:42', '2026-07-06 08:21:42', 0);
INSERT INTO `volunteer` VALUES (4, 6, '赵小明', '110101199501011234', '13900001111', '[\"动物急救\", \"驾驶\"]', 0.0, 0, 2, '1', '2026-07-12 10:47:36', '2026-07-12 10:47:36', 0);
INSERT INTO `volunteer` VALUES (5, 7, '周小红', '110101199806152345', '13900002222', '[\"宠物护理\", \"摄影\"]', 0.0, 0, 1, NULL, '2026-07-12 10:47:36', '2026-07-18 10:16:49', 0);

-- ----------------------------
-- Table structure for volunteer_schedule
-- ----------------------------
DROP TABLE IF EXISTS `volunteer_schedule`;
CREATE TABLE `volunteer_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `volunteer_id` bigint NOT NULL COMMENT '志愿者ID',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `shift_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班次(早班/中班/夜班)',
  `region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '区域',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_volunteer_date`(`volunteer_id` ASC, `schedule_date` ASC) USING BTREE,
  INDEX `idx_date`(`schedule_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '志愿者排班表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of volunteer_schedule
-- ----------------------------
INSERT INTO `volunteer_schedule` VALUES (1, 3, '2026-07-18', '早班', '朝阳区', '', '2026-07-18 10:30:15', '2026-07-18 10:30:15', 0);
INSERT INTO `volunteer_schedule` VALUES (2, 3, '2026-07-18', '早班', '朝阳区', '', '2026-07-18 10:35:14', '2026-07-18 10:35:14', 0);
INSERT INTO `volunteer_schedule` VALUES (3, 5, '2026-07-17', '早班', '', '', '2026-07-18 10:35:37', '2026-07-18 10:35:37', 0);
INSERT INTO `volunteer_schedule` VALUES (4, 5, '2026-06-17', '早班', '', '', '2026-07-18 10:43:40', '2026-07-18 10:43:40', 0);

-- ----------------------------
-- Table structure for wx_subscribe_record
-- ----------------------------
DROP TABLE IF EXISTS `wx_subscribe_record`;
CREATE TABLE `wx_subscribe_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'openid',
  `template_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `data_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板数据JSON',
  `page` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转页面',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/SENT/FAILED',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信订阅消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wx_subscribe_record
-- ----------------------------
INSERT INTO `wx_subscribe_record` VALUES (1, 2, 'ox_test_user_001', 'YOUR_STORY_TEMPLATE_ID', '故事审核通知', NULL, 'SKIPPED', '模板ID未配置', '2026-07-18 14:01:03', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
