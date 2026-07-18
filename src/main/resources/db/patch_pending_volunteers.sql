-- 插入待认证志愿者测试数据（auth_status=0 表示待审核）
-- 在 Navicat 中选择 city_stray 数据库执行

-- 先插入对应的 user 记录（如果不存在）
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `role`) VALUES
(6, 'xiaoming', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '小明', '13900001111', 'USER'),
(7, 'xiaohong', '$2a$10$nnyWr2u8zcOl2Yc2T5JPYOu8iuQEdOZI1qgRLgF8q8tVXjLTtKAYK', '小红', '13900002222', 'USER')
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- 插入待认证志愿者
INSERT INTO `volunteer` (`user_id`, `real_name`, `id_card`, `phone`, `skill_tags`, `total_hours`, `points`, `auth_status`, `reject_reason`) VALUES
(6, '赵小明', '110101199501011234', '13900001111', '["动物急救","驾驶"]', 0.0, 0, 0, NULL),
(7, '周小红', '110101199806152345', '13900002222', '["宠物护理","摄影"]', 0.0, 0, 0, NULL);
