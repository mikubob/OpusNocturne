# OpusNocturne 博客系统实施计划

## 实施顺序

### 阶段 1: 基础工具类 + 认证模块
1. PasswordUtils - BCrypt 密码加密工具
2. UploadUtils - 文件上传工具
3. 修复 JwtInterceptor (javax -> jakarta)
4. 完善 WebConfig (添加拦截器注册、CORS)
5. AuthController + Service (登录/登出/获取用户信息/刷新Token)

### 阶段 2: 分类 & 标签管理
6. CategoryService + CategoryController (后台+前台)
7. TagService + TagController (后台+前台)

### 阶段 3: 文章管理
8. ArticleMapper XML (批量插入/删除标签关联、联表查询)
9. ArticleService 补全 (更新/删除/分页/详情/搜索)
10. ArticleController (后台+前台)

### 阶段 4: 评论互动
11. CommentService + CommentController (前台+后台)

### 阶段 5: 文件上传
12. AttachmentService + UploadController

### 阶段 6: 系统管理 (用户/角色/权限)
13. SysUserService + UserController
14. SysRoleService + RoleController
15. SysPermissionService + PermissionController

### 阶段 7: 系统设置 & 统计
16. SysSettingService + SettingController
17. VisitLogService + StatisticsController

### 阶段 8: AOP 切面
18. LogAspect
19. PermissionAspect
