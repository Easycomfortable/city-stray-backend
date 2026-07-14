-- 消息通知模块 DDL

-- 1. 站内通知表
CREATE TABLE IF NOT EXISTS sys_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content VARCHAR(1000) DEFAULT '' COMMENT '通知内容',
    type VARCHAR(30) DEFAULT 'SYSTEM' COMMENT '类型:SYSTEM/ADOPTION/RESCUE/STORY/DONATION/VOLUNTEER',
    related_type VARCHAR(30) DEFAULT '' COMMENT '关联业务类型',
    related_id BIGINT DEFAULT NULL COMMENT '关联业务ID',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读:0-未读 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    deleted INT DEFAULT 0,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知表';

-- 2. 微信消息订阅记录表
CREATE TABLE IF NOT EXISTS wx_subscribe_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    openid VARCHAR(64) NOT NULL COMMENT 'openid',
    template_id VARCHAR(64) NOT NULL COMMENT '模板ID',
    data_json VARCHAR(2000) DEFAULT '' COMMENT '模板数据JSON',
    page VARCHAR(200) DEFAULT '' COMMENT '跳转页面',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/SENT/FAILED',
    error_msg VARCHAR(500) DEFAULT '' COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    send_time DATETIME DEFAULT NULL,
    deleted INT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信订阅消息记录表';

-- ===== 初始数据(示例通知) =====
INSERT INTO sys_notification (user_id, title, content, type, is_read) VALUES
(1, '系统通知', '欢迎使用城流浪管理平台！', 'SYSTEM', 0),
(1, '领养审核提醒', '您有3条待审核的领养申请，请及时处理。', 'ADOPTION', 0),
(1, '救助工单更新', '工单 #RO20260705001 已分配志愿者。', 'RESCUE', 1);
