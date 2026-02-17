-- 模块一：系统权限（RBAC+菜单）（RBAC:角色-权限-用户）
-- 1. 系统用户表
drop table if exists sys_user;
create table sys_user (
    id bigint not null auto_increment comment '主键id',
    username varchar(50) not null comment '用户名',
    password varchar(100) not null comment '加密后的密码（BCrypt）',
    nickname varchar(50) comment '昵称',
    email varchar(100) comment '邮箱',
    avatar varchar(255) comment '头像URL',
    status tinyint not null default 1 comment '状态：1-启用；0-禁用',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    last_login_time datetime default null comment '最后登录时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    unique key uk_username (username),
    unique key uk_email (email)
) engine = innodb default charset = utf8mb4 comment = '系统用户表';

-- 2. 系统角色表
drop table if exists sys_role;
create table sys_role (
    id bigint not null auto_increment comment '主键id',
    role_name varchar(50) not null comment '角色名称(如：管理员)',
    role_code varchar(50) not null comment '角色编码(如：admin）',
    description varchar(255) comment '描述',
    status tinyint not null default 1 comment '状态：1-启用；0-禁用',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    unique key uk_role_code (role_code),
    unique key uk_role_name (role_name)
) engine = innodb default charset = utf8mb4 comment = '系统角色表';

-- 3. 菜单权限表（二合一）
drop table if exists sys_permission;
create table sys_permission (
    id bigint not null auto_increment comment '主键id',
    parent_id bigint not null default 0 comment '父级id(0表示顶级)',
    name varchar(50) not null comment '菜单/权限名称(如：用户管理)',
    code varchar(50) default null comment '权限标识（user:add）',
    type tinyint not null default 1 comment '类型：1-菜单；2-按钮',
    path varchar(255) default null comment '路由地址',
    component varchar(255) default null comment '组件路径',
    icon varchar(50) default null comment '图标',
    sort int not null default 0 comment '排序',
    status tinyint not null default 1 comment '状态：1-启用；0-禁用',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    unique key uk_code (code),-- 唯一索引
    key idx_parent_id (parent_id)
) engine = innodb default charset = utf8mb4 comment = '系统权限/菜单表';

-- 4. 用户-角色关联
drop table if exists sys_user_role;
create table sys_user_role (
  id bigint not null auto_increment comment '主键id',
  user_id bigint not null comment '用户id',
  role_id bigint not null comment '角色id',
  create_time datetime not null default current_timestamp,
  primary key (id),-- 主键
  unique key uk_user_role (user_id, role_id), -- 联合唯一索引
  key idx_role_id (role_id) -- 索引
) engine=innodb default charset=utf8mb4 comment='用户-角色关联表'; -- 使用InnoDB存储引擎，并且设置字符集为utf8mb4，避免中文乱码，添加注释

-- 5. 角色-权限关联
drop table if exists sys_role_permission;
create table sys_role_permission (
  id bigint not null auto_increment comment '主键id',
  role_id bigint not null comment '角色id',
  permission_id bigint not null comment '权限id',
  create_time datetime not null default current_timestamp,
  primary key (id),-- 主键
  unique key uk_role_permission (role_id, permission_id), -- 联合唯一索引
  key idx_permission_id (permission_id) -- 索引
) engine=innodb default charset=utf8mb4 comment='角色-权限关联表';

-- 模块二：博客核心（博客文章、博客分类、博客标签、博客评论）
-- 1. 文章分类表
drop table if exists category;
create table category (
  id bigint not null auto_increment comment '主键id',
  name varchar(50) not null comment '分类名称',
  description varchar(255) default null comment '描述',
  sort int not null default 0 comment '排序（升序）',
  status tinyint not null default 1 comment '状态：1-启用；0-禁用',
  is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
  create_time datetime not null default current_timestamp comment '创建时间',
  update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_name (name)
) engine = innodb default charset = utf8mb4 comment = '文章分类表';

-- 2. 文章标签表
drop table if exists tag;
create table tag (
  id bigint not null auto_increment comment '主键id',
  name varchar(50) not null comment '标签名称',
  color varchar(20) default '#1890ff' comment '标签颜色',
  is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
  create_time datetime not null default current_timestamp comment '创建时间',
  update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_name (name)-- 唯一索引
) engine = innodb default charset = utf8mb4 comment = '文章标签表';

-- 3.文章主表
drop table if exists article;
create table article (
    id bigint not null auto_increment comment '主键id',
    author_id bigint not null comment '作者id',
    category_id bigint not null comment '分类id',
    title varchar(200) not null comment '文章标题',
    slug varchar(200) default null comment 'URL别名(SEO)',
    summary varchar(500) default null comment '文章摘要',
    content longtext comment '文章内容(markdown)',
    cover_img varchar(255) default null comment '封面图片',
    keywords varchar(255) default null comment 'SEO关键词',
    view_count bigint not null default 0 comment '浏览次数(持久化用)',
    is_top tinyint not null default 0 comment '是否置顶：1-是；0-否',
    status tinyint not null default 0 comment '状态：0-草稿，1-发布，2-下架',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    publish_time datetime default null comment '发布时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),-- 主键
    unique key uk_slug (slug),-- 唯一索引
    key idx_author_id (author_id),-- 索引
    key idx_category (category_id),-- 索引
    key idx_publish_status (status, publish_time) comment '优化查询最新文章'
) engine = innodb default charset = utf8mb4 comment = '文章表';

-- 4.文章-标签关联表
drop table if exists article_tag;
create table article_tag (
    id bigint not null auto_increment comment '主键id',
    article_id bigint not null comment '文章id',
    tag_id bigint not null comment '标签id',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_article_tag (article_id, tag_id),
    key idx_tag_article (tag_id, article_id) comment '反向索引优化'
) engine = innodb default charset = utf8mb4 comment = '文章-标签关联表';

-- 模块三：互动与资源
-- 1.附件/资源表
drop table if exists attachment;
create table attachment (
    id bigint not null auto_increment comment '主键id',
    file_name varchar(255) not null comment '原文件名',
    file_url varchar(500) not null comment '访问URL',
    file_path varchar(500) default null comment '储存路径',
    file_type varchar(50) default null comment '文件类型',
    file_size bigint default 0 comment '文件大小(字节)',
    biz_type varchar(50) default 'article' comment '业务类型(article/avatar)',
    biz_id bigint default null comment '业务id',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    key idx_biz (biz_type, biz_id)
) engine = innodb default charset = utf8mb4 comment = '附件表';

-- 2.评论表
drop table if exists comment;
create table comment (
    id bigint not null auto_increment comment '主键id',
    article_id bigint default null comment '文章id(null为留言)',
    user_id bigint default null comment '评论人id(游客为null)',
    nickname varchar(50) not null comment '昵称',
    email varchar(100) default null comment '邮箱',
    content text not null comment '内容',
    root_parent_id bigint default null comment '根评论id',
    parent_id bigint default null comment '父评论id',
    reply_user_id bigint default null comment '被回复人id',
    ip_address varchar(50) default null comment 'IP地址',
    user_agent varchar(500) default null comment '设备信息',
    status tinyint not null default 0 comment '状态：0-待审核；1-审核通过；2-审核未通过',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    key idx_article_root (article_id, root_parent_id) comment '文章+根评论索引优化(查询文章评论树)'
) engine = innodb default charset = utf8mb4 comment = '评论表';

-- 模块四：系统设置
-- 系统设置表
drop table if exists sys_setting;
create table sys_setting (
    id bigint not null auto_increment comment '主键id',
    site_name varchar(100) default 'OpusNocturne' comment '站点名称',
    site_description varchar(255) default '个人技术博客' comment '站点描述',
    site_keywords varchar(255) default 'Java,Spring Boot,前端' comment '站点关键词',
    footer_text varchar(255) default '© 2026 OpusNocturne' comment '页脚文本',
    admin_email varchar(100) default null comment '管理员邮箱',
    comment_audit tinyint not null default 1 comment '评论是否需要审核：1-是；0-否',
    article_page_size int not null default 10 comment '文章列表每页条数',
    comment_page_size int not null default 20 comment '评论列表每页条数',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id)
) engine = innodb default charset = utf8mb4 comment = '系统设置表';

-- 模块五：访问统计
-- 访问记录表
drop table if exists visit_log;
create table visit_log (
    id bigint not null auto_increment comment '主键id',
    ip_address varchar(50) default null comment 'IP地址',
    user_agent varchar(500) default null comment '设备信息',
    visit_time datetime not null default current_timestamp comment '访问时间',
    page_url varchar(500) default null comment '访问页面URL',
    referer varchar(500) default null comment '来源URL',
    is_delete tinyint not null default 0 comment '逻辑删除：1-是；0-否',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    key idx_visit_time (visit_time),
    key idx_page_url (page_url)
) engine = innodb default charset = utf8mb4 comment = '访问记录表';

SET FOREIGN_KEY_CHECKS = 1;-- 开启外键
