
-- 2026-02-2x 更新：新增关于我字段
ALTER TABLE sys_setting ADD COLUMN about_me TEXT COMMENT '关于我';

-- 2026-02-2x 更新：新增文章点赞数字段
ALTER TABLE article ADD COLUMN like_count BIGINT DEFAULT 0 COMMENT '点赞数';

-- 2026-02-2x 更新：新增操作日志表
drop table if exists sys_oper_log;
create table sys_oper_log (
    id bigint not null auto_increment comment '主键id',
    title varchar(50) default '' comment '模块标题',
    business_type varchar(20) default '' comment '业务类型（0其它 1新增 2修改 3删除）',
    method varchar(255) default '' comment '方法名称',
    request_method varchar(10) default '' comment '请求方式',
    operator_type varchar(20) default '' comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name varchar(50) default '' comment '操作人员',
    oper_url varchar(255) default '' comment '请求URL',
    oper_ip varchar(128) default '' comment '主机地址',
    oper_location varchar(255) default '' comment '操作地点',
    oper_param text comment '请求参数',
    json_result text comment '返回参数',
    status tinyint default 0 comment '操作状态（1正常 0异常）',
    error_msg varchar(2000) default '' comment '错误消息',
    oper_time datetime default null comment '操作时间',
    cost_time bigint default 0 comment '消耗时间',
    primary key (id),
    key idx_oper_time (oper_time)
) engine = innodb default charset = utf8mb4 comment = '操作日志记录';
