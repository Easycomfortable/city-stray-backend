-- 重建 sys_menu 数据，使其与侧边栏一致
-- 图标使用 Element Plus 组件名，前端可直接渲染

-- 1. 清空旧数据
DELETE FROM sys_role_menu;
DELETE FROM sys_menu;

-- 2. 插入菜单（ID从30起，避免与旧数据冲突）
INSERT INTO sys_menu (id, parent_id, name, icon, path, component, type, sort, visible, deleted) VALUES
-- 顶层菜单
(30, 0, '数据大屏',     'DataAnalysis',    '/dashboard',          NULL, 1, 1, 1, 0),
(31, 0, '地图总览',     'MapLocation',     '/map',                NULL, 1, 2, 1, 0),
(32, 0, '救助工单管理', 'List',            '/rescue-order',       NULL, 1, 3, 1, 0),
(33, 0, '动物档案管理', 'Grid',            '/animal',             NULL, 1, 4, 1, 0),
(34, 0, '领养管理',     'ChatLineSquare',  '/adoption',           NULL, 1, 5, 1, 0),

-- 志愿者管理（目录）
(35, 0,  '志愿者管理',   'UserFilled',      '/volunteer',          NULL, 0, 6, 1, 0),
(36, 35, '志愿者列表',   'UserFilled',      '/volunteer',          NULL, 1, 1, 1, 0),
(37, 35, '排班日历',     'Calendar',        '/volunteer/schedule', NULL, 1, 2, 1, 0),
(38, 35, '认证审核',     'UserFilled',      '/volunteer/certify',  NULL, 1, 3, 1, 0),
(39, 35, '积分管理',     'Coin',            '/volunteer/points',   NULL, 1, 4, 1, 0),

-- 合作医院管理
(40, 0, '合作医院管理', 'OfficeBuilding',  '/hospital',           NULL, 1, 7, 1, 0),

-- 财务管理（目录）
(41, 0,  '财务管理',   'Money',   '/finance',           NULL, 0, 8, 1, 0),
(42, 41, '捐款流水',   'Money',   '/finance/donation',  NULL, 1, 1, 1, 0),
(43, 41, '捐款项目',   'Coin',    '/finance/project',   NULL, 1, 2, 1, 0),
(44, 41, '财务报表',   'DataLine','/finance/report',    NULL, 1, 3, 1, 0),

-- 内容管理（目录）
(45, 0,  '内容管理',       'Picture',  '/content',           NULL, 0, 9,  1, 0),
(46, 45, '轮播图管理',     'Picture',  '/content/banner',    NULL, 1, 1,  1, 0),
(47, 45, '救助故事审核',   'Reading',  '/content/stories',   NULL, 1, 2,  1, 0),
(48, 45, '公告管理',       'Bell',     '/content/notice',    NULL, 1, 3,  1, 0),
(49, 45, '举报处理',       'Warning',  '/content/report',    NULL, 1, 4,  1, 0),
(50, 45, '知识库',         'Notebook', '/content/knowledge', NULL, 1, 5,  1, 0),

-- 系统管理（目录）
(51, 0,  '系统管理',   'Setting',  '/system',       NULL, 0, 10, 1, 0),
(52, 51, '用户管理',   'User',     '/system/user',  NULL, 1, 1,  1, 0),
(53, 51, '角色管理',   'Avatar',   '/system/role',  NULL, 1, 2,  1, 0),
(54, 51, '菜单管理',   'Menu',     '/system/menu',  NULL, 1, 3,  1, 0),
(55, 51, '字典管理',   'Setting',  '/system/dict',  NULL, 1, 4,  1, 0),
(56, 51, '系统日志',   'Document', '/system/log',   NULL, 1, 5,  1, 0);

-- 3. 分配角色-菜单关联
-- admin(ID=1): 全部菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0;

-- rescue_admin(ID=2): 数据大屏/地图/工单/动物/领养/志愿者/医院
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 30), (2, 31), (2, 32), (2, 33), (2, 34),
(2, 35), (2, 36), (2, 37), (2, 38), (2, 39),
(2, 40);

-- hospital_admin(ID=3): 数据大屏/动物
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3, 30), (3, 33);

-- user(ID=4): 数据大屏
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(4, 30);
