-- 内容管理模块 DDL
-- 1. 轮播图表
CREATE TABLE IF NOT EXISTS content_banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) DEFAULT '' COMMENT '标题',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(500) DEFAULT '' COMMENT '跳转链接',
    sort INT DEFAULT 0 COMMENT '排序(越小越前)',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 2. 救助故事表
CREATE TABLE IF NOT EXISTS content_story (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL COMMENT '发布用户ID',
    author_name VARCHAR(50) DEFAULT '' COMMENT '作者昵称',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '故事内容',
    cover_image VARCHAR(500) DEFAULT '' COMMENT '封面图',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/APPROVED/REJECTED',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_status (status),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='救助故事表';

-- 3. 公告表
CREATE TABLE IF NOT EXISTS content_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 4. 内容举报表
CREATE TABLE IF NOT EXISTS content_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT DEFAULT NULL COMMENT '举报人ID',
    reporter_name VARCHAR(50) DEFAULT '' COMMENT '举报人昵称',
    target_type VARCHAR(20) DEFAULT 'POST' COMMENT '举报对象类型:POST/COMMENT',
    target_id BIGINT DEFAULT NULL COMMENT '举报对象ID',
    target_content VARCHAR(1000) DEFAULT '' COMMENT '被举报内容摘要',
    reason VARCHAR(500) DEFAULT '' COMMENT '举报原因',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/RESOLVED/DISMISSED',
    handler_name VARCHAR(50) DEFAULT '' COMMENT '处理人',
    handle_remark VARCHAR(500) DEFAULT '' COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    deleted INT DEFAULT 0,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容举报表';

-- 5. 知识科普文章表
CREATE TABLE IF NOT EXISTS content_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    category VARCHAR(20) DEFAULT 'GUIDE' COMMENT '分类:GUIDE/RESCUE/TNR/MEDICAL',
    summary VARCHAR(500) DEFAULT '' COMMENT '摘要',
    content TEXT COMMENT '正文内容',
    cover_image VARCHAR(500) DEFAULT '' COMMENT '封面图',
    tags VARCHAR(500) DEFAULT '' COMMENT '标签(JSON数组)',
    author VARCHAR(50) DEFAULT '' COMMENT '作者',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态:DRAFT/PUBLISHED',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识科普文章表';

-- ===== 初始数据 =====
INSERT INTO content_banner (title, image_url, link_url, sort, enabled) VALUES
('关爱流浪动物', '/uploads/banner/banner1.jpg', '/pages/about/index', 1, 1),
('领养代替购买', '/uploads/banner/banner2.jpg', '/pages/adoption/index', 2, 1),
('志愿者招募中', '/uploads/banner/banner3.jpg', '/pages/volunteer/index', 3, 1);

INSERT INTO content_story (user_id, author_name, title, content, status, view_count) VALUES
(1, '志愿者小王', '小橘的救助故事', '那是一个下雨的傍晚，我在校园角落里发现了一只瑟瑟发抖的橘猫...', 'APPROVED', 256),
(2, '志愿者小李', '花花的新生', '花花被救助时只有三个月大，瘦得皮包骨头...', 'PENDING', 0),
(3, '爱心人士小张', '从流浪到幸福', '在社区喂猫点认识了一只三花猫，决定带它去医院检查...', 'APPROVED', 189),
(NULL, '匿名用户', '雨中的相遇', '暴雨天在停车场发现一家五口的流浪猫家庭...', 'REJECTED', 0);

INSERT INTO content_notice (title, content, status, publish_time, view_count) VALUES
('关于暑期领养活动的通知', '为鼓励更多爱心人士参与流浪动物领养，平台将在暑期推出领养优惠活动...', 'PUBLISHED', '2026-06-20 10:00:00', 512),
('志愿者招募公告', '因救助站运营需要，现面向社会公开招募志愿者若干名...', 'DRAFT', NULL, 0),
('平台系统升级公告', '为提升用户体验，平台将于本周六凌晨2点进行系统维护升级...', 'PUBLISHED', '2026-07-05 09:00:00', 324);

INSERT INTO content_report (reporter_id, reporter_name, target_type, target_id, target_content, reason, status) VALUES
(1, '用户A', 'POST', 10, '这是一条广告内容...', '涉嫌广告', 'PENDING'),
(2, '用户B', 'COMMENT', 25, '不当评论内容...', '不当言论', 'RESOLVED'),
(3, '用户C', 'POST', 15, '疑似虚假信息...', '虚假信息', 'PENDING');

INSERT INTO content_article (title, category, summary, content, author, status, view_count, publish_time) VALUES
('新手养猫指南：从接猫回家到日常照料', 'GUIDE', '本文详细介绍新手养猫的必备知识和注意事项', '一、接猫回家前的准备...', '管理员', 'PUBLISHED', 1256, '2026-06-15 10:00:00'),
('发现流浪猫受伤了该怎么办？', 'RESCUE', '发现受伤的流浪动物时的正确处理流程', '第一步：观察伤情...', '管理员', 'PUBLISHED', 892, '2026-06-10 14:30:00'),
('TNR项目介绍：什么是捕捉-绝育-放归', 'TNR', 'TNR是目前国际公认的流浪动物人道管理方法', 'TNR是Trap-Neuter-Return的缩写...', '志愿者阿强', 'PUBLISHED', 567, '2026-06-05 09:00:00'),
('常见猫咪皮肤病的识别与处理', 'MEDICAL', '了解猫咪常见皮肤病的症状和初步处理方法', '一、猫癣 症状：圆形脱毛...', '管理员', 'DRAFT', 0, NULL),
('如何正确喂养流浪猫', 'GUIDE', '科学喂养流浪猫的方法和建议', '选择合适的猫粮，避免喂食人类食物...', '管理员', 'PUBLISHED', 723, '2026-05-28 16:00:00');
