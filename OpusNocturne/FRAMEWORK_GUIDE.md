# OpusNocturne 博客系统框架配置指导手册

## 1. 项目结构

OpusNocturne 博客系统采用多模块架构，主要包含以下模块：

```
OpusNocturne/
├── ON-common/          # 通用模块，包含配置、工具类等
│   ├── src/main/java/com/xuan/common/
│   │   ├── config/       # 框架配置类
│   │   ├── domain/       # 通用领域模型
│   │   ├── enums/        # 枚举类
│   │   ├── exceptions/   # 异常类
│   │   ├── handle/       # 处理器
│   │   ├── interceptor/  # 拦截器
│   │   └── utils/        # 工具类
├── ON-entity/           # 实体模块，包含DTO、PO、VO等
│   ├── src/main/java/com/xuan/entity/
│   │   ├── dto/          # 数据传输对象
│   │   ├── po/            # 持久化对象
│   │   └── vo/            # 视图对象
├── ON-service/           # 服务模块，包含业务逻辑、控制器等
│   ├── src/main/java/com/xuan/service/
│   │   ├── aop/           # 切面
│   │   ├── controller/    # 控制器
│   │   ├── mapper/        # 数据访问层
│   │   ├── service/       # 业务逻辑层
│   │   └── OpusNocturneApplication.java  # 应用启动类
│   ├── src/main/resources/
│   │   ├── application.yaml        # 主配置文件
│   │   ├── application-dev.yaml    # 开发环境配置
│   │   ├── mapper/                  # MyBatis XML映射文件
│   │   └── static/                  # 静态资源
├── pom.xml              # 父项目依赖管理
└── FRAMEWORK_GUIDE.md   # 框架配置指导手册
```

## 2. 框架配置

### 2.1 核心配置文件

#### application.yaml
```yaml
spring:
  application:
    name: OpusNocturne
  # 环境配置
  profiles:
    active: dev
```

#### application-dev.yaml
主要配置项包括：
- 数据源配置（MySQL）
- Redis配置
- 文件上传配置
- MyBatis-Plus配置
- JWT配置
- 日志配置

### 2.2 框架配置类

#### WebConfig.java
- 配置静态资源访问路径
- 配置上传文件访问路径
- 配置Swagger文档访问路径

#### SecurityConfig.java
- 配置Spring Security安全过滤链
- 配置JWT拦截器
- 配置异常处理

#### RedisConfig.java
- 配置RedisTemplate
- 优化序列化方式
- 支持复杂对象和Java 8时间类型

#### MybatisPlusConfig.java
- 配置MyBatis-Plus分页插件

#### Knife4jConfig.java
- 配置API文档生成
- 支持OpenAPI 3.0规范

### 2.3 拦截器

#### JwtInterceptor.java
- 验证JWT token
- 提取用户信息
- 将用户信息存储到请求上下文中

### 2.4 处理器

#### GlobalExceptionHandler.java
- 统一处理各种异常
- 返回标准化的错误响应

#### AuthenticationEntryPointImpl.java
- 处理未认证异常

#### AccessDeniedHandlerImpl.java
- 处理无权限异常

### 2.5 工具类

#### JwtUtils.java
- 生成JWT token
- 解析JWT token
- 验证JWT token

#### PasswordUtils.java
- 密码加密
- 密码验证

#### UploadUtils.java
- 文件上传处理
- 生成文件路径

## 3. 环境搭建

### 3.1 前置条件

- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 6.0+

### 3.2 数据库配置

1. 创建数据库 `opusnocturne`
2. 执行 `BlogSQL.sql` 文件初始化表结构和数据

### 3.3 依赖安装

```bash
# 在项目根目录执行
mvn clean install
```

## 4. 运行项目

### 4.1 开发环境运行

```bash
# 在 ON-service 模块目录执行
mvn spring-boot:run
```

### 4.2 打包部署

```bash
# 在项目根目录执行
mvn clean package

# 运行打包后的jar文件
java -jar ON-service/target/OpusNocturne-0.0.1-SNAPSHOT.jar
```

### 4.3 访问地址

- 应用地址：http://localhost:8080
- API文档地址：http://localhost:8080/doc.html
- Druid监控地址：http://localhost:8080/druid

## 5. 核心功能

### 5.1 认证与授权

- 基于JWT的无状态认证
- 基于Spring Security的权限控制
- 支持角色和权限管理

### 5.2 数据访问

- 使用MyBatis-Plus简化数据库操作
- 支持分页查询
- 支持逻辑删除

### 5.3 文件上传

- 支持图片、文档等多种文件类型
- 配置文件大小限制
- 自动生成文件存储路径

### 5.4 API文档

- 基于Knife4j和OpenAPI 3.0
- 自动生成API文档
- 支持在线调试

### 5.5 日志管理

- 配置日志文件存储
- 支持日志滚动
- 分级日志记录

## 6. 配置项说明

### 6.1 JWT配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| jwt.secret | JWT签名密钥 | opusnocturne_jwt_secret_key_2026 |
| jwt.expiration | Token过期时间（秒） | 86400 |
| jwt.header | 请求头名称 | Authorization |
| jwt.token-prefix | Token前缀 | Bearer |

### 6.2 数据库配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| spring.datasource.url | 数据库连接URL | jdbc:mysql://localhost:3306/opusnocturne |
| spring.datasource.username | 数据库用户名 | root |
| spring.datasource.password | 数据库密码 | 1234 |

### 6.3 Redis配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| spring.data.redis.host | Redis主机地址 | localhost |
| spring.data.redis.port | Redis端口 | 6379 |
| spring.data.redis.password | Redis密码 | 123456 |
| spring.data.redis.database | Redis数据库索引 | 4 |

### 6.4 文件上传配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| spring.servlet.multipart.max-file-size | 单个文件最大大小 | 10MB |
| spring.servlet.multipart.max-request-size | 整个请求最大大小 | 10MB |

## 7. 常见问题及解决方案

### 7.1 数据库连接失败

**症状**：启动项目时出现数据库连接异常

**解决方案**：
- 检查MySQL服务是否启动
- 检查数据库连接配置是否正确
- 检查数据库用户权限是否正确

### 7.2 Redis连接失败

**症状**：启动项目时出现Redis连接异常

**解决方案**：
- 检查Redis服务是否启动
- 检查Redis连接配置是否正确
- 检查Redis密码是否正确

### 7.3 JWT验证失败

**症状**：访问需要认证的接口时出现401错误

**解决方案**：
- 检查请求头中是否正确携带了Authorization头
- 检查Token是否过期
- 检查Token是否被篡改

### 7.4 文件上传失败

**症状**：上传文件时出现错误

**解决方案**：
- 检查文件大小是否超过限制
- 检查文件类型是否被允许
- 检查上传目录权限是否正确

## 8. 开发规范

### 8.1 代码规范

- 遵循Java编码规范
- 使用Lombok简化代码
- 合理使用注解

### 8.2 目录结构规范

- 按照功能模块组织代码
- 遵循Maven项目结构规范
- 保持包名和目录结构一致

### 8.3 命名规范

- 类名使用大驼峰命名法
- 方法名和变量名使用小驼峰命名法
- 常量使用全大写加下划线命名法

## 9. 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.2 | 应用框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL | 8.4.0 | 数据库 |
| Redis | 6.0+ | 缓存 |
| JWT | - | 认证 |
| Knife4j | 4.5.0 | API文档 |
| Hutool | 5.8.32 | 工具库 |
| FastJSON2 | 2.0.54 | JSON处理 |

## 10. 总结

OpusNocturne 博客系统采用了现代化的Java技术栈，具有以下特点：

- 多模块架构，职责清晰
- 基于Spring Boot 3.4.2，采用最新技术
- 完善的认证与授权机制
- 统一的异常处理和响应格式
- 丰富的配置选项
- 易于部署和维护

通过本指导手册，开发者可以快速了解项目的框架配置和使用方法，从而更好地进行开发和维护工作。