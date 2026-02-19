-- ============================================================
-- OpusNocturne 博客系统 - 超级管理员初始化脚本
-- 
-- 功能：创建超级管理员账号 + 角色 + 全量权限 + 系统设置
-- 使用：在 BlogSQL.sql 建表完成后执行本脚本
-- 
-- 默认账号：admin
-- 默认密码：admin123
-- ⚠️ 请在首次登录后立即修改默认密码！
-- ============================================================

-- ==================== 1. 超级管理员角色 ====================

INSERT INTO sys_role (id, role_name, role_code, description, status)
VALUES (1, '超级管理员', 'super_admin', '拥有系统全部权限，不可删除', 1)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- ==================== 2. 超级管理员用户 ====================
-- 密码: admin123（BCrypt 加密）
-- 如需更换默认密码，可在 Java 中执行：
--   System.out.println(new BCryptPasswordEncoder().encode("你的新密码"));
-- 然后替换下面的 password 值

INSERT INTO sys_user (id, username, password, nickname, email, avatar, status)
VALUES (1, 'admin',
        '$2a$10$rr.lS2zxvSlN6XgBQ95xNedscGUr3Gzz2fRS5IO9ClSxKI3QBadVa',
        '超级管理员',
        'admin@opusnocturne.com',
        NULL,
        1)
ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password);

-- ==================== 3. 用户 - 角色绑定 ====================

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- ==================== 4. 系统权限菜单（全量） ====================

-- 4.1 一级菜单
INSERT INTO sys_permission (id, parent_id, name, code, type, path, icon, sort, status) VALUES
(1,  0, '仪表盘',     'dashboard',          1, '/dashboard',      'DashboardOutlined',   1, 1),
(2,  0, '文章管理',   'article:manage',     1, '/article',        'FileTextOutlined',    2, 1),
(3,  0, '分类管理',   'category:manage',    1, '/category',       'FolderOutlined',      3, 1),
(4,  0, '标签管理',   'tag:manage',         1, '/tag',            'TagOutlined',         4, 1),
(5,  0, '评论管理',   'comment:manage',     1, '/comment',        'CommentOutlined',     5, 1),
(6,  0, '用户管理',   'user:manage',        1, '/user',           'UserOutlined',        6, 1),
(7,  0, '角色管理',   'role:manage',        1, '/role',           'TeamOutlined',        7, 1),
(8,  0, '权限管理',   'permission:manage',  1, '/permission',     'LockOutlined',        8, 1),
(9,  0, '系统设置',   'setting:manage',     1, '/setting',        'SettingOutlined',     9, 1),
(10, 0, '附件管理',   'attachment:manage',  1, '/attachment',     'PaperClipOutlined',  10, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.2 文章操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(101, 2, '查看文章',   'article:list',     2,  1, 1),
(102, 2, '创建文章',   'article:create',   2,  2, 1),
(103, 2, '编辑文章',   'article:update',   2,  3, 1),
(104, 2, '删除文章',   'article:delete',   2,  4, 1),
(105, 2, '发布/下架',  'article:publish',  2,  5, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.3 分类操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(201, 3, '查看分类',   'category:list',    2,  1, 1),
(202, 3, '创建分类',   'category:create',  2,  2, 1),
(203, 3, '编辑分类',   'category:update',  2,  3, 1),
(204, 3, '删除分类',   'category:delete',  2,  4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.4 标签操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(301, 4, '查看标签',   'tag:list',         2,  1, 1),
(302, 4, '创建标签',   'tag:create',       2,  2, 1),
(303, 4, '编辑标签',   'tag:update',       2,  3, 1),
(304, 4, '删除标签',   'tag:delete',       2,  4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.5 评论操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(401, 5, '查看评论',   'comment:list',     2,  1, 1),
(402, 5, '审核评论',   'comment:audit',    2,  2, 1),
(403, 5, '删除评论',   'comment:delete',   2,  3, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.6 用户操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(501, 6, '查看用户',   'user:list',        2,  1, 1),
(502, 6, '创建用户',   'user:create',      2,  2, 1),
(503, 6, '编辑用户',   'user:update',      2,  3, 1),
(504, 6, '删除用户',   'user:delete',      2,  4, 1),
(505, 6, '重置密码',   'user:reset-pwd',   2,  5, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.7 角色操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(601, 7, '查看角色',   'role:list',        2,  1, 1),
(602, 7, '创建角色',   'role:create',      2,  2, 1),
(603, 7, '编辑角色',   'role:update',      2,  3, 1),
(604, 7, '删除角色',   'role:delete',      2,  4, 1),
(605, 7, '分配权限',   'role:assign',      2,  5, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.8 权限操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(701, 8, '查看权限',   'permission:list',   2,  1, 1),
(702, 8, '创建权限',   'permission:create', 2,  2, 1),
(703, 8, '编辑权限',   'permission:update', 2,  3, 1),
(704, 8, '删除权限',   'permission:delete', 2,  4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.9 系统设置操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(801, 9, '查看设置',   'setting:list',     2,  1, 1),
(802, 9, '更新设置',   'setting:update',   2,  2, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4.10 附件操作按钮
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(901, 10, '查看附件',  'attachment:list',   2,  1, 1),
(902, 10, '上传附件',  'attachment:upload', 2,  2, 1),
(903, 10, '删除附件',  'attachment:delete', 2,  3, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ==================== 5. 超级管理员拥有全部权限 ====================
-- 将所有已启用的权限绑定到超级管理员角色

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE status = 1 AND is_delete = 0
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- ==================== 6. 初始化系统设置 ====================

INSERT INTO sys_setting (id, site_name, site_description, site_keywords, footer_text, admin_email, comment_audit, article_page_size, comment_page_size)
VALUES (1, 'OpusNocturne', '个人技术博客', 'Java,Spring Boot,前端', '© 2026 OpusNocturne', 'admin@opusnocturne.com', 1, 10, 20)
ON DUPLICATE KEY UPDATE id = VALUES(id);

-- ============================================================
-- ✅ 初始化完成！
-- 
-- 超级管理员账号：admin
-- 超级管理员密码：admin123
-- 
-- 权限清单：
--   ├── 仪表盘
--   ├── 文章管理（查看/创建/编辑/删除/发布）
--   ├── 分类管理（查看/创建/编辑/删除）
--   ├── 标签管理（查看/创建/编辑/删除）
--   ├── 评论管理（查看/审核/删除）
--   ├── 用户管理（查看/创建/编辑/删除/重置密码）
--   ├── 角色管理（查看/创建/编辑/删除/分配权限）
--   ├── 权限管理（查看/创建/编辑/删除）
--   ├── 系统设置（查看/更新）
--   └── 附件管理（查看/上传/删除）
-- ============================================================
