# OpusNocturne 项目结构文档

本文档详细描述了 OpusNocturne 博客系统的项目结构，包括业务模块和通用模块的组织情况，基于对项目代码、API文档和数据库结构的分析。

## 项目概览

OpusNocturne 是一个基于 Java 的博客系统，采用分层架构设计，包含以下主要模块：

1. **通用模块 (ON-common)**：提供基础组件、工具类、配置、拦截器、异常处理和枚举等
2. **实体模块 (ON-entity)**：定义数据模型和传输对象
3. **服务模块 (ON-service)**：实现业务逻辑和API接口

### 技术栈

- **后端框架**：Spring Boot 3.4.2
- **API文档**：Knife4j 4.5.0
- **数据库**：MySQL 8.4.0
- **ORM框架**：MyBatis-Plus 3.5.9
- **数据库连接池**：Druid 1.2.23
- **认证**：JWT (JSON Web Token)
- **权限管理**：RBAC (基于角色的访问控制)
- **工具库**：Hutool 5.8.32
- **参数校验**：Spring Validation
- **缓存**：Redis
- **前端调用**：Axios (从API文档示例中推断)
- **密码加密**：BCrypt (从数据库脚本中推断)
- **文件存储**：本地文件系统 (从API响应中推断)

## 目录结构

```
OpusNocturne/
├── ON-common/             # 通用模块
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── xuan/
│   │                   └── common/
│   │                       ├── config/         # 配置类
│   │                       │   ├── Knife4jConfig.java    # Knife4j配置类
│   │                       │   ├── MyBatisPlusConfig.java # MyBatis-Plus配置类
│   │                       │   ├── RedisConfig.java       # Redis配置类
│   │                       │   ├── SecurityConfig.java    # 安全配置类
│   │                       │   └── WebConfig.java         # Web配置类
│   │                       ├── domain/        # 基础领域类
│   │                       │   ├── BaseEntity.java         # 基础实体类
│   │                       │   ├── BasePageQueryDTO.java    # 基础分页查询DTO
│   │                       │   └── Result.java              # 统一响应结果类
│   │                       ├── enum/          # 枚举类
│   │                       │   ├── ArticleStatusEnum.java   # 文章状态枚举
│   │                       │   ├── CommentStatusEnum.java   # 评论状态枚举
│   │                       │   ├── PermissionTypeEnum.java  # 权限类型枚举
│   │                       │   └── UserStatusEnum.java      # 用户状态枚举
│   │                       ├── exceptions/     # 异常处理
│   │                       │   ├── GlobalExceptionHandler.java  # 全局异常处理器
│   │                       │   └── business/    # 业务异常
│   │                       ├── interceptor/     # 拦截器
│   │                       │   ├── JwtInterceptor.java     # JWT认证拦截器
│   │                       │   └── LoginInterceptor.java   # 登录拦截器
│   │                       └── utils/           # 工具类
│   │                           ├── JwtUtils.java        # JWT工具类
│   │                           ├── PasswordUtils.java   # 密码工具类
│   │                           └── UploadUtils.java     # 文件上传工具类
│   └── pom.xml            # 模块POM文件
├── ON-entity/             # 实体模块
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── xuan/
│   │                   └── entity/
│   │                       ├── dto/            # 数据传输对象
│   │                       │   ├── article/     # 文章相关DTO
│   │                       │   ├── auth/        # 认证相关DTO
│   │                       │   ├── category/    # 分类相关DTO
│   │                       │   ├── comment/     # 评论相关DTO
│   │                       │   ├── system/      # 系统设置相关DTO
│   │                       │   ├── tag/         # 标签相关DTO
│   │                       │   ├── upload/      # 文件上传相关DTO
│   │                       │   └── user/        # 用户相关DTO
│   │                       ├── po/              # 数据库实体类
│   │                       │   ├── blog/        # 博客相关实体
│   │                       │   ├── interact/    # 互动相关实体
│   │                       │   └── sys/         # 系统相关实体
│   │                       └── vo/              # 视图对象
│   │                           ├── article/     # 文章相关VO
│   │                           ├── auth/        # 认证相关VO
│   │                           ├── category/    # 分类相关VO
│   │                           ├── comment/     # 评论相关VO
│   │                           ├── system/      # 系统设置相关VO
│   │                           ├── tag/         # 标签相关VO
│   │                           ├── upload/      # 文件上传相关VO
│   │                           └── user/        # 用户相关VO
│   └── pom.xml            # 模块POM文件
├── ON-service/            # 服务模块
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── xuan/
│   │       │           └── service/
│   │       │               ├── aop/            # AOP切面
│   │       │               │   ├── LogAspect.java         # 日志切面
│   │       │               │   └── PermissionAspect.java  # 权限切面
│   │       │               ├── controller/      # 控制器
│   │       │               │   ├── admin/       # 后台控制器
│   │       │               │   │   ├── AuthController.java         # 认证控制器
│   │       │               │   │   ├── ArticleController.java      # 文章管理控制器
│   │       │               │   │   ├── CategoryController.java     # 分类管理控制器
│   │       │               │   │   ├── CommentController.java      # 评论管理控制器
│   │       │               │   │   ├── PermissionController.java   # 权限管理控制器
│   │       │               │   │   ├── RoleController.java         # 角色管理控制器
│   │       │               │   │   ├── SettingController.java      # 系统设置控制器
│   │       │               │   │   ├── StatisticsController.java   # 站点统计控制器
│   │       │               │   │   ├── TagController.java          # 标签管理控制器
│   │       │               │   │   ├── UploadController.java       # 文件上传控制器
│   │       │               │   │   └── UserController.java         # 用户管理控制器
│   │       │               │   └── blog/        # 前台控制器
│   │       │               │       ├── ArticleController.java      # 文章前台控制器
│   │       │               │       ├── CategoryController.java     # 分类前台控制器
│   │       │               │       ├── CommentController.java      # 评论前台控制器
│   │       │               │       └── TagController.java          # 标签前台控制器
│   │       │               ├── mapper/          # 数据访问
│   │       │               ├── service/         # 业务服务
│   │       │               │   ├── impl/        # 服务实现
│   │       │               │   └── interfaces/  # 服务接口
│   │       │               └── OpusNocturneApplication.java  # 应用程序主类
│   │       └── resources/
│   │           ├── mapper/            # MyBatis映射文件
│   │           ├── application-dev.yaml    # 开发环境配置
│   │           └── application.yaml        # 主配置文件
│   └── pom.xml            # 模块POM文件
├── APIDocs.md             # API接口文档
├── BlogSQL.sql            # 数据库脚本
├── pom.xml                # 项目父POM文件
└── .gitignore             # Git忽略文件
```

## 通用模块 (ON-common)

通用模块提供了系统中使用的基础组件、工具类、配置、拦截器、异常处理和枚举等，是其他模块的依赖基础，实现了业务逻辑与配置的解耦。

### 目录结构

```
ON-common/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── xuan/
│                   └── common/
│                       ├── config/         # 配置类
│                       │   ├── Knife4jConfig.java    # Knife4j配置类
│                       │   ├── MyBatisPlusConfig.java # MyBatis-Plus配置类
│                       │   ├── RedisConfig.java       # Redis配置类
│                       │   ├── SecurityConfig.java    # 安全配置类
│                       │   └── WebConfig.java         # Web配置类
│                       ├── domain/        # 基础领域类
│                       │   ├── BaseEntity.java         # 基础实体类
│                       │   ├── BasePageQueryDTO.java    # 基础分页查询DTO
│                       │   └── Result.java              # 统一响应结果类
│                       ├── enum/          # 枚举类
│                       │   ├── ArticleStatusEnum.java   # 文章状态枚举
│                       │   ├── CommentStatusEnum.java   # 评论状态枚举
│                       │   ├── PermissionTypeEnum.java  # 权限类型枚举
│                       │   └── UserStatusEnum.java      # 用户状态枚举
│                       ├── exceptions/     # 异常处理
│                       │   ├── GlobalExceptionHandler.java  # 全局异常处理器
│                       │   └── business/    # 业务异常
│                       ├── interceptor/     # 拦截器
│                       │   ├── JwtInterceptor.java     # JWT认证拦截器
│                       │   └── LoginInterceptor.java   # 登录拦截器
│                       └── utils/           # 工具类
│                           ├── JwtUtils.java        # JWT工具类
│                           ├── PasswordUtils.java   # 密码工具类
│                           └── UploadUtils.java     # 文件上传工具类
└── pom.xml                # 模块POM文件
```

### 文件说明

#### 配置类

| 文件 | 说明 |
|------|------|
| Knife4jConfig.java | Knife4j配置类，用于生成API文档，提供可视化的接口调试界面 |
| MyBatisPlusConfig.java | MyBatis-Plus配置类，配置分页插件、逻辑删除等 |
| RedisConfig.java | Redis配置类，配置RedisTemplate等 |
| SecurityConfig.java | 安全配置类，配置权限管理相关内容 |
| WebConfig.java | Web配置类，配置拦截器、跨域等 |

#### 基础领域类

| 文件 | 说明 |
|------|------|
| BaseEntity.java | 所有实体类的基类，包含通用字段如ID、创建时间、更新时间等 |
| BasePageQueryDTO.java | 基础分页查询数据传输对象，包含分页参数如页码、每页条数等 |
| Result.java | 统一响应结果类，用于API接口返回标准化的数据格式，包含code、message、data字段 |

#### 枚举类

| 文件 | 说明 |
|------|------|
| ArticleStatusEnum.java | 文章状态枚举，定义文章的状态如草稿、发布、下架等 |
| CommentStatusEnum.java | 评论状态枚举，定义评论的状态如待审核、审核通过、审核未通过等 |
| PermissionTypeEnum.java | 权限类型枚举，定义权限的类型如菜单、按钮等 |
| UserStatusEnum.java | 用户状态枚举，定义用户的状态如启用、禁用等 |

#### 异常处理

| 文件 | 说明 |
|------|------|
| GlobalExceptionHandler.java | 全局异常处理器，统一处理系统异常 |
| business/ | 业务异常目录，定义各种业务相关的异常类 |

#### 拦截器

| 文件 | 说明 |
|------|------|
| JwtInterceptor.java | JWT认证拦截器，验证JWT令牌 |
| LoginInterceptor.java | 登录拦截器，验证用户登录状态 |

#### 工具类

| 文件 | 说明 |
|------|------|
| JwtUtils.java | JWT工具类，处理JWT令牌的生成和解析 |
| PasswordUtils.java | 密码工具类，处理密码的加密和验证 |
| UploadUtils.java | 文件上传工具类，处理文件的上传和管理 |

## 实体模块 (ON-entity)

实体模块定义了系统中的数据模型，包括数据库实体(PO)、数据传输对象(DTO)和视图对象(VO)。

### 目录结构

```
ON-entity/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── xuan/
│                   └── entity/
│                       ├── dto/            # 数据传输对象
│                       │   ├── article/     # 文章相关DTO
│                       │   ├── auth/        # 认证相关DTO
│                       │   ├── category/    # 分类相关DTO
│                       │   ├── comment/     # 评论相关DTO
│                       │   ├── system/      # 系统设置相关DTO
│                       │   ├── tag/         # 标签相关DTO
│                       │   ├── upload/      # 文件上传相关DTO
│                       │   └── user/        # 用户相关DTO
│                       ├── po/              # 数据库实体类
│                       │   ├── blog/        # 博客相关实体
│                       │   ├── interact/    # 互动相关实体
│                       │   └── sys/         # 系统相关实体
│                       └── vo/              # 视图对象
│                           ├── article/     # 文章相关VO
│                           ├── auth/        # 认证相关VO
│                           ├── category/    # 分类相关VO
│                           ├── comment/     # 评论相关VO
│                           ├── system/      # 系统设置相关VO
│                           ├── tag/         # 标签相关VO
│                           ├── upload/      # 文件上传相关VO
│                           └── user/        # 用户相关VO
└── pom.xml                # 模块POM文件
```

### DTO 说明

| 目录 | 说明 |
|------|------|
| article/ | 文章相关的数据传输对象，包括创建、更新、分页查询等，如ArticleCreateDTO、ArticleUpdateDTO等 |
| auth/ | 认证相关的数据传输对象，如登录请求LoginDTO |
| category/ | 分类相关的数据传输对象，包括创建、更新、分页查询等，如CategoryCreateDTO、CategoryUpdateDTO等 |
| comment/ | 评论相关的数据传输对象，包括创建、审核、分页查询等，如CommentCreateDTO、CommentAuditDTO等 |
| system/ | 系统设置相关的数据传输对象，如SystemSettingDTO |
| tag/ | 标签相关的数据传输对象，包括创建、更新、分页查询等，如TagDTO、TagPageQueryDTO等 |
| upload/ | 文件上传相关的数据传输对象，如UploadDTO、AttachmentPageQueryDTO等 |
| user/ | 用户相关的数据传输对象，包括创建、更新、重置密码等，如UserCreateDTO、UserUpdateDTO等 |

### PO 说明

| 目录 | 说明 |
|------|------|
| blog/ | 博客相关的数据库实体，包括Article、ArticleTag、Category、Tag |
| interact/ | 互动相关的数据库实体，包括Attachment、Comment |
| sys/ | 系统相关的数据库实体，包括SysPermission、SysRole、SysRolePermission、SysUser、SysUserRole |

### VO 说明

| 目录 | 说明 |
|------|------|
| article/ | 文章相关的视图对象，包括详情页ArticleDetailVO和列表页ArticleListVO |
| auth/ | 认证相关的视图对象，如登录响应LoginVO、用户信息UserInfoVO |
| category/ | 分类相关的视图对象，包括管理后台CategoryAdminVO和前台CategoryVO |
| comment/ | 评论相关的视图对象，包括管理后台CommentAdminVO和前台树形展示CommentTreeVO |
| system/ | 系统设置相关的视图对象，如SystemSettingVO |
| tag/ | 标签相关的视图对象，包括管理后台TagAdminVO和前台TagVO |
| upload/ | 文件上传相关的视图对象，如AttachmentVO、UploadVO |
| user/ | 用户相关的视图对象，如用户列表UserListVO |

## 服务模块 (ON-service)

服务模块是系统的核心，实现业务逻辑和API接口，是系统与外部交互的主要入口。

### 目录结构

```
ON-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── xuan/
│       │           └── service/
│       │               ├── aop/            # AOP切面
│       │               │   ├── LogAspect.java         # 日志切面
│       │               │   └── PermissionAspect.java  # 权限切面
│       │               ├── controller/      # 控制器
│       │               │   ├── admin/       # 后台控制器
│       │               │   │   ├── AuthController.java         # 认证控制器
│       │               │   │   ├── ArticleController.java      # 文章管理控制器
│       │               │   │   ├── CategoryController.java     # 分类管理控制器
│       │               │   │   ├── CommentController.java      # 评论管理控制器
│       │               │   │   ├── PermissionController.java   # 权限管理控制器
│       │               │   │   ├── RoleController.java         # 角色管理控制器
│       │               │   │   ├── SettingController.java      # 系统设置控制器
│       │               │   │   ├── StatisticsController.java   # 站点统计控制器
│       │               │   │   ├── TagController.java          # 标签管理控制器
│       │               │   │   ├── UploadController.java       # 文件上传控制器
│       │               │   │   └── UserController.java         # 用户管理控制器
│       │               │   └── blog/        # 前台控制器
│       │               │       ├── ArticleController.java      # 文章前台控制器
│       │               │       ├── CategoryController.java     # 分类前台控制器
│       │               │       ├── CommentController.java      # 评论前台控制器
│       │               │       └── TagController.java          # 标签前台控制器
│       │               ├── mapper/          # 数据访问
│       │               ├── service/         # 业务服务
│       │               │   ├── impl/        # 服务实现
│       │               │   └── interfaces/  # 服务接口
│       │               └── OpusNocturneApplication.java  # 应用程序主类
│       └── resources/
│           ├── mapper/            # MyBatis映射文件
│           ├── application-dev.yaml    # 开发环境配置
│           └── application.yaml        # 主配置文件
└── pom.xml                # 模块POM文件
```

### AOP 说明

| 文件 | 说明 |
|------|------|
| LogAspect.java | 日志切面，记录API请求日志 |
| PermissionAspect.java | 权限切面，处理权限验证 |

### 控制器说明

#### 后台控制器

| 文件 | 说明 |
|------|------|
| AuthController.java | 认证控制器，处理登录、退出登录、获取用户信息等 |
| ArticleController.java | 文章管理控制器，处理文章的CRUD操作 |
| CategoryController.java | 分类管理控制器，处理分类的CRUD操作 |
| CommentController.java | 评论管理控制器，处理评论的审核、删除等操作 |
| PermissionController.java | 权限管理控制器，处理权限的CRUD操作 |
| RoleController.java | 角色管理控制器，处理角色的CRUD操作和权限分配 |
| SettingController.java | 系统设置控制器，处理系统配置的读取和更新 |
| StatisticsController.java | 站点统计控制器，处理站点数据统计 |
| TagController.java | 标签管理控制器，处理标签的CRUD操作 |
| UploadController.java | 文件上传控制器，处理文件的上传和管理 |
| UserController.java | 用户管理控制器，处理用户的CRUD操作和密码重置 |

#### 前台控制器

| 文件 | 说明 |
|------|------|
| ArticleController.java | 文章前台控制器，处理前台文章列表、详情、搜索等 |
| CategoryController.java | 分类前台控制器，处理前台分类列表等 |
| CommentController.java | 评论前台控制器，处理前台评论的发表和获取 |
| TagController.java | 标签前台控制器，处理前台标签列表等 |

## 业务模块结构

基于实体模块和服务模块的组织，OpusNocturne 系统包含以下业务模块：

### 1. 认证模块 (Auth)

**功能**：用户登录、退出登录、获取用户信息、刷新Token

**API接口**：
- POST /api/admin/auth/login - 用户登录
- POST /api/admin/auth/logout - 退出登录
- GET /api/admin/auth/info - 获取当前用户信息
- POST /api/admin/auth/refresh - 刷新Token

**相关文件**：
- DTO: `entity/dto/auth/LoginDTO.java`
- VO: `entity/vo/auth/LoginVO.java`, `entity/vo/auth/UserInfoVO.java`
- PO: `entity/po/sys/SysUser.java`
- 控制器: `service/controller/admin/AuthController.java`
- 工具类: `common/utils/JwtUtils.java`

### 2. 文章管理模块 (Blog Article)

**功能**：文章的创建、更新、删除、列表查询、详情查询、置顶、状态管理、搜索

**API接口**：
- POST /api/admin/article - 创建文章
- GET /api/admin/article/page - 后台文章列表
- PUT /api/admin/article/{id} - 更新文章
- DELETE /api/admin/article/{id} - 删除文章
- GET /api/admin/article/{id} - 文章详情(后台)
- GET /api/blog/article/page - 前台文章列表
- GET /api/blog/article/{id} - 前台文章详情
- PUT /api/admin/article/{id}/top - 文章置顶/取消置顶
- PUT /api/admin/article/{id}/status - 更新文章状态
- GET /api/blog/article/search - 前台文章搜索

**相关文件**：
- DTO: `entity/dto/article/` 目录下的文件
- PO: `entity/po/blog/Article.java`, `entity/po/blog/ArticleTag.java`
- VO: `entity/vo/article/` 目录下的文件
- 控制器: `service/controller/admin/ArticleController.java`, `service/controller/blog/ArticleController.java`
- 枚举: `common/enum/ArticleStatusEnum.java`

### 3. 分类与标签模块 (Category & Tag)

**功能**：分类和标签的创建、更新、删除、列表查询

**API接口**：
- GET /api/blog/category/list - 获取全部分类(前台)
- GET /api/admin/category/page - 后台分类列表
- POST /api/admin/category - 创建分类
- PUT /api/admin/category/{id} - 更新分类
- DELETE /api/admin/category/{id} - 删除分类
- DELETE /api/admin/category/batch-delete - 批量删除分类
- GET /api/blog/tag/list - 获取所有标签(前台)
- GET /api/admin/tag/page - 后台标签列表
- POST /api/admin/tag - 创建标签
- PUT /api/admin/tag/{id} - 更新标签
- DELETE /api/admin/tag/{id} - 删除标签
- DELETE /api/admin/tag/batch-delete - 批量删除标签

**相关文件**：
- DTO: `entity/dto/category/` 和 `entity/dto/tag/` 目录下的文件
- PO: `entity/po/blog/Category.java`, `entity/po/blog/Tag.java`
- VO: `entity/vo/category/` 和 `entity/vo/tag/` 目录下的文件
- 控制器: `service/controller/admin/CategoryController.java`, `service/controller/blog/CategoryController.java`, `service/controller/admin/TagController.java`, `service/controller/blog/TagController.java`

### 4. 评论互动模块 (Comment)

**功能**：评论的创建、审核、删除、列表查询、树形展示、评论统计

**API接口**：
- GET /api/blog/comment/tree/{articleId} - 获取文章评论树(前台)
- POST /api/blog/comment - 发表评论(前台)
- GET /api/admin/comment/page - 后台评论列表
- PUT /api/admin/comment/{id}/audit - 审核评论
- DELETE /api/admin/comment/{id} - 删除评论
- PUT /api/admin/comment/batch-audit - 批量审核评论
- DELETE /api/admin/comment/batch-delete - 批量删除评论
- GET /api/blog/comment/stats/{articleId} - 获取文章评论统计(前台)

**相关文件**：
- DTO: `entity/dto/comment/` 目录下的文件
- PO: `entity/po/interact/Comment.java`
- VO: `entity/vo/comment/` 目录下的文件
- 控制器: `service/controller/admin/CommentController.java`, `service/controller/blog/CommentController.java`
- 枚举: `common/enum/CommentStatusEnum.java`

### 5. 文件上传模块 (File Upload)

**功能**：文件的上传、管理、删除

**API接口**：
- POST /api/admin/attachment/upload - 上传文件
- GET /api/admin/attachment/page - 后台附件列表
- DELETE /api/admin/attachment/{id} - 删除附件

**相关文件**：
- DTO: `entity/dto/upload/` 目录下的文件
- PO: `entity/po/interact/Attachment.java`
- VO: `entity/vo/upload/` 目录下的文件
- 控制器: `service/controller/admin/UploadController.java`
- 工具类: `common/utils/UploadUtils.java`

### 6. 系统用户管理模块 (System User)

**功能**：用户的创建、更新、删除、列表查询、重置密码

**API接口**：
- GET /api/admin/user/page - 分页获取用户列表
- POST /api/admin/user - 创建用户
- PUT /api/admin/user/{id} - 更新用户
- DELETE /api/admin/user/{id} - 删除用户
- GET /api/admin/user/{id} - 获取用户详情
- PUT /api/admin/user/{id}/reset-password - 重置用户密码

**相关文件**：
- DTO: `entity/dto/user/` 目录下的文件
- PO: `entity/po/sys/SysUser.java`, `entity/po/sys/SysUserRole.java`
- VO: `entity/vo/user/` 目录下的文件
- 控制器: `service/controller/admin/UserController.java`
- 工具类: `common/utils/PasswordUtils.java`
- 枚举: `common/enum/UserStatusEnum.java`

### 7. 系统角色管理模块 (System Role)

**功能**：角色的创建、更新、删除、列表查询、权限分配

**API接口**：
- GET /api/admin/role/page - 分页获取角色列表
- POST /api/admin/role - 创建角色
- PUT /api/admin/role/{id} - 更新角色
- DELETE /api/admin/role/{id} - 删除角色
- GET /api/admin/role/{id} - 获取角色详情
- GET /api/admin/role/{id}/permissions - 获取角色权限树

**相关文件**：
- PO: `entity/po/sys/SysRole.java`, `entity/po/sys/SysRolePermission.java`
- 控制器: `service/controller/admin/RoleController.java`

### 8. 系统权限管理模块 (System Permission)

**功能**：权限的创建、更新、删除、列表查询

**API接口**：
- GET /api/admin/permission/list - 获取权限列表
- POST /api/admin/permission - 创建权限
- PUT /api/admin/permission/{id} - 更新权限
- DELETE /api/admin/permission/{id} - 删除权限

**相关文件**：
- PO: `entity/po/sys/SysPermission.java`
- 控制器: `service/controller/admin/PermissionController.java`
- 枚举: `common/enum/PermissionTypeEnum.java`

### 9. 系统设置模块 (System Setting)

**功能**：系统配置的读取和更新

**API接口**：
- GET /api/admin/setting - 获取系统设置
- PUT /api/admin/setting - 更新系统设置

**相关文件**：
- DTO: `entity/dto/system/SystemSettingDTO.java`
- VO: `entity/vo/system/SystemSettingVO.java`
- 控制器: `service/controller/admin/SettingController.java`

### 10. 站点统计模块 (Site Statistics)

**功能**：获取站点概览统计、文章发布趋势、访问统计

**API接口**：
- GET /api/admin/statistics/overview - 获取站点概览统计
- GET /api/admin/statistics/article-trend - 获取文章发布趋势
- GET /api/admin/statistics/visit - 获取访问统计

**相关文件**：
- 控制器: `service/controller/admin/StatisticsController.java`

## API 接口结构

系统API接口采用RESTful风格，主要分为两大类：

1. **后台管理接口**：路径前缀为 `/api/admin/`，需要JWT认证
2. **前台展示接口**：路径前缀为 `/api/blog/`，大部分不需要认证

### 统一响应格式

所有API接口返回统一的JSON格式：

```json
{
  "code": 200,      // 状态码：200-成功，400-参数错误，401-未认证，403-无权限，404-资源不存在，500-系统错误
  "message": "成功", // 响应消息
  "data": { ... }   // 响应数据，成功时返回具体数据，失败时返回null
}
```

### 分页响应格式

分页查询接口返回的数据格式：

```json
{
  "records": [ ... ], // 数据列表
  "total": 100,       // 总记录数
  "size": 10,         // 每页条数
  "current": 1,       // 当前页码
  "pages": 10         // 总页数
}
```

## 技术栈缺漏分析

基于对pom.xml和application配置文件的分析，以下是可能需要补充的技术或配置：

1. **JWT依赖**：目前项目中使用了JWT进行认证，但pom.xml中未明确列出JWT相关依赖，建议添加：
   ```xml
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt</artifactId>
       <version>0.12.6</version>
   </dependency>
   ```

2. **CORS配置**：项目中未明确配置跨域资源共享(CORS)，建议在WebConfig中添加CORS配置

3. **文件上传路径配置**：虽然配置了文件上传大小限制，但未明确配置上传文件的存储路径，建议在application配置中添加：
   ```yaml
   # 文件上传路径配置
   file:
     upload:
       path: ./uploads
       url-prefix: /uploads
   ```

4. **JWT配置**：建议在application配置中添加JWT相关配置：
   ```yaml
   # JWT配置
   jwt:
     secret: your-secret-key
     expiration: 3600000
     header: Authorization
   ```

5. **Swagger/OpenAPI配置**：虽然使用了Knife4j，但建议在application配置中添加相关配置：
   ```yaml
   # Knife4j配置
   knife4j:
     enable: true
     setting:
       language: zh_cn
   ```

6. **健康检查端点**：建议添加Spring Boot Actuator依赖，用于健康检查：
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

7. **缓存键前缀配置**：建议在Redis配置中添加缓存键前缀：
   ```yaml
   # Redis缓存配置
   spring:
     cache:
       redis:
         key-prefix: opusnocturne:
         time-to-live: 3600000
   ```

## 总结

OpusNocturne 项目采用了清晰的分层架构设计，将通用功能、数据模型和业务逻辑分离，便于维护和扩展。系统包含完整的博客功能，同时集成了用户权限管理、评论互动等特性，是一个功能完善的博客系统解决方案。

项目的技术栈选择合理，使用了Spring Boot作为后端框架，MySQL作为数据库，MyBatis-Plus作为ORM框架，JWT作为认证方式，Knife4j作为API文档工具，这些技术都是当前Java生态中较为流行和成熟的选择。

通过本文档的描述，开发者可以快速了解OpusNocturne项目的整体结构和各个模块的功能，为后续的开发和维护工作提供参考。