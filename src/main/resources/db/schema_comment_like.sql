-- =============================================
-- 社区评论 + 点赞功能建表SQL
-- 城市流浪动物管理系统
-- =============================================

-- 1. 评论表
CREATE TABLE IF NOT EXISTS `content_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `story_id` BIGINT NOT NULL COMMENT '故事/帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '评论用户昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '评论用户头像',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID(回复用)',
  `reply_to` VARCHAR(50) DEFAULT NULL COMMENT '被回复用户昵称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_story_id` (`story_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 2. 点赞表
CREATE TABLE IF NOT EXISTS `story_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `story_id` BIGINT NOT NULL COMMENT '故事/帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_story_user` (`story_id`, `user_id`),
  KEY `idx_story_id` (`story_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故事点赞表';

-- 3. 给 content_story 表增加 like_count 和 comment_count 字段
ALTER TABLE `content_story` ADD COLUMN `like_count` INT DEFAULT 0 COMMENT '点赞数' AFTER `view_count`;
ALTER TABLE `content_story` ADD COLUMN `comment_count` INT DEFAULT 0 COMMENT '评论数' AFTER `like_count`;
