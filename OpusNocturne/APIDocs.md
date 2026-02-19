# OpusNocturne API 接口文档

本文档详细描述了 OpusNocturne 博客系统的后端 API 接口。

> **最后更新**: 2026-02-20
> **版本**: v2.0

## 目录

1. [通用说明](#1-通用说明)
2. [认证模块 (Auth)](#2-认证模块-auth)
3. [系统用户管理 (System User)](#3-系统用户管理-system-user)
4. [文章管理 (Blog Article)](#4-文章管理-blog-article)
5. [分类与标签 (Category & Tag)](#5-分类与标签-category--tag)
6. [评论互动 (Comment)](#6-评论互动-comment)
7. [文件上传 (File Upload)](#7-文件上传-file-upload)
8. [系统角色管理 (System Role)](#8-系统角色管理-system-role)
9. [系统权限管理 (System Permission)](#9-系统权限管理-system-permission)
10. [系统设置 (System Setting)](#10-系统设置-system-setting)
11. [站点统计 (Site Statistics)](#11-站点统计-site-statistics)
12. [待实现接口 (TODO)](#12-待实现接口-todo)

---

## 1. 通用说明

### 基础 URL
开发环境：`http://localhost:8080`

### 认证方式
除"前台展示"类接口外，大多数管理接口需要进行 JWT 认证。
请在 HTTP 请求头中携带 Token：
```http
Authorization: Bearer <Your-Token>
```

### HTTP 状态码策略
**所有接口统一返回 HTTP 200**，由响应体中的 `code` 字段区分业务是否成功：
- `code = 0` → 业务成功
- `code ≠ 0` → 业务失败（前端应展示 `message` 给用户）

### 统一响应结构
所有接口均返回统一的 JSON 格式，基于 `Result` 类设计：

**成功（带数据）**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": { ... }
}
```

**成功（无数据）**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败**
```json
{
  "code": 2001,
  "message": "请先登录后再操作",
  "data": null
}
```

### 错误码一览表

| 错误码 | 枚举名 | 提示信息 | 使用场景 |
|:---:|:---|:---|:---|
| **0** | SUCCESS | 操作成功 | 所有成功响应 |
| **1000** | SYSTEM_ERROR | 系统繁忙，请稍后再试 | 未预期的系统异常 |
| **1001** | PARAM_ERROR | 请求参数有误，请检查后重试 | 参数校验失败 |
| **1002** | NOT_FOUND | 您访问的内容不存在 | 404 资源不存在 |
| **1004** | METHOD_NOT_ALLOWED | 不支持该请求方式 | 405 请求方法错误 |
| **1005** | TOO_MANY_REQUESTS | 请求过于频繁，请稍后再试 | 限流拦截 |
| **1006** | DATA_ALREADY_EXISTS | 数据已存在，请勿重复操作 | 唯一约束冲突 |
| **2001** | UNAUTHORIZED | 请先登录后再操作 | 未登录或 Token 无效 |
| **2003** | FORBIDDEN | 抱歉，您没有权限执行此操作 | 权限不足 |
| **2004** | LOGIN_FAILED | 用户名或密码错误，请重新输入 | 登录失败 |
| **2005** | TOKEN_EXPIRED | 登录已过期，请重新登录 | Token 过期 |
| **2006** | TOKEN_INVALID | 登录凭证无效，请重新登录 | Token 解析失败 |
| **2007** | TOKEN_REPLACED | 您的账号已在其他设备登录... | 多端登录被顶替 |
| **3001** | USER_NOT_FOUND | 用户不存在 | 用户查询失败 |
| **3002** | USER_DISABLED | 该账号已被禁用，请联系管理员 | 账号被禁 |
| **3003** | USER_EXISTS | 该用户名已被注册 | 用户名重复 |
| **3005** | OLD_PASSWORD_ERROR | 原密码不正确，请重新输入 | 修改密码校验 |
| **4001** | ROLE_NOT_FOUND | 角色不存在 | 角色查询失败 |
| **4002** | ROLE_EXISTS | 该角色名称已存在 | 角色名重复 |
| **4003** | PERMISSION_NOT_FOUND | 权限不存在 | 权限查询失败 |
| **5001** | ARTICLE_NOT_FOUND | 文章不存在或已被删除 | 文章查询失败 |
| **5002** | CATEGORY_NOT_FOUND | 分类不存在 | 分类查询失败 |
| **5003** | TAG_NOT_FOUND | 标签不存在 | 标签查询失败 |
| **5004** | CATEGORY_EXISTS | 该分类名称已存在 | 分类名重复 |
| **5005** | TAG_EXISTS | 该标签名称已存在 | 标签名重复 |
| **5006** | CATEGORY_HAS_ARTICLES | 该分类下还有文章，无法删除 | 删除分类时存在关联文章 |
| **6001** | COMMENT_NOT_FOUND | 评论不存在或已被删除 | 评论查询失败 |
| **6003** | COMMENT_CONTENT_EMPTY | 评论内容不能为空 | 评论校验 |
| **7001** | FILE_UPLOAD_FAILED | 文件上传失败，请稍后再试 | 文件上传异常 |
| **7002** | FILE_TYPE_ERROR | 不支持该文件格式... | 文件类型校验 |
| **7003** | FILE_SIZE_EXCEEDED | 文件大小超出限制，请压缩后重试 | 文件过大 |

### 分页规则
分页查询接口通常包含以下查询参数：
- `current`: 当前页码，默认 1
- `size`: 每页条数，默认 10

分页响应数据结构：
```json
{
  "records": [ ... ],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

---

## 2. 认证模块 (Auth)

### 2.1 用户登录

- **接口路径**: `POST /api/admin/auth/login`
- **是否认证**: 否

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 | 示例 |
|:---|:---|:---|:---|:---|
| username | string | 是 | 用户名 | `admin` |
| password | string | 是 | 密码 | `admin123` |

**前端调用示例**
```javascript
axios.post('/api/admin/auth/login', {
  username: 'admin',
  password: 'admin123'
}).then(response => {
  const token = response.data.data.token;
  localStorage.setItem('token', token);
});
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer "
  }
}
```

**失败响应**
```json
{
  "code": 2004,
  "message": "用户名或密码错误，请重新输入",
  "data": null
}
```

---

### 2.2 退出登录

- **接口路径**: `POST /api/admin/auth/logout`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 2.3 获取当前用户信息

- **接口路径**: `GET /api/admin/auth/info`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": "...",
    "email": "...",
    "permissions": ["article:create", "article:update"]
  }
}
```

**失败响应**
```json
{
  "code": 2001,
  "message": "请先登录后再操作",
  "data": null
}
```

---

### 2.4 刷新 Token

- **接口路径**: `POST /api/admin/auth/refresh`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer "
  }
}
```

**失败响应**
```json
{
  "code": 2005,
  "message": "登录已过期，请重新登录",
  "data": null
}
```

---

## 3. 系统用户管理 (System User)

### 3.1 分页获取用户列表

- **接口路径**: `GET /api/admin/user/page`
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| username | string | 否 | `admin` | 用户名搜索 |
| nickname | string | 否 | `管理员` | 昵称搜索 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "超级管理员",
        "avatar": "http://...",
        "email": "admin@example.com",
        "status": 1,
        "createTime": "2026-01-01 12:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 3.2 创建用户

- **接口路径**: `POST /api/admin/user`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| username | string | 是 | 用户名，不可重复 |
| password | string | 是 | 初始密码 |
| nickname | string | 否 | 昵称 |
| email | string | 否 | 邮箱 |
| roleIds | array | 否 | 关联角色ID列表，如 `[1, 2]` |
| status | int | 否 | 状态：1-启用，0-禁用 (默认1) |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 3003,
  "message": "该用户名已被注册",
  "data": null
}
```

---

### 3.3 更新用户

- **接口路径**: `PUT /api/admin/user/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| nickname | string | 否 | 昵称 |
| email | string | 否 | 邮箱 |
| roleIds | array | 否 | 关联角色ID列表 |
| status | int | 否 | 状态：1-启用，0-禁用 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 3001,
  "message": "用户不存在",
  "data": null
}
```

---

### 3.4 删除用户

- **接口路径**: `DELETE /api/admin/user/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 3.5 获取用户详情

- **接口路径**: `GET /api/admin/user/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": "http://...",
    "email": "admin@example.com",
    "status": 1,
    "roleIds": [1],
    "createTime": "2026-01-01 12:00:00"
  }
}
```

---

### 3.6 重置用户密码

- **接口路径**: `PUT /api/admin/user/{id}/reset-password`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| password | string | 是 | 新密码 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 3001,
  "message": "用户不存在",
  "data": null
}
```

---

## 4. 文章管理 (Blog Article)

### 4.1 创建文章

- **接口路径**: `POST /api/admin/article`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| title | string | 是 | 文章标题 |
| content | string | 是 | 文章内容 (Markdown格式) |
| summary | string | 否 | 摘要 |
| categoryId | long | 是 | 分类ID |
| tagIds | array | 否 | 标签ID列表，如 `[1, 3]` |
| coverImg | string | 否 | 封面图片URL |
| isTop | int | 否 | 是否置顶：1-是，0-否 |
| status | int | 是 | 状态：0-草稿，1-发布，2-下架 |

**标签关联说明**
- 通过 `tagIds` 参数管理 `article_tag` 关联关系
- 创建时根据 `tagIds` 生成关联记录
- 更新时会先删除旧关联再重建

**前端调用示例**
```javascript
axios.post('/api/admin/article', {
  title: 'Spring Boot 实战',
  content: '# Hello World\n...',
  categoryId: 1,
  tagIds: [101, 102],
  status: 1
});
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 100,
    "title": "Spring Boot 实战"
  }
}
```

**失败响应**
```json
{
  "code": 1001,
  "message": "标题不能为空",
  "data": null
}
```

---

### 4.2 后台文章列表

- **接口路径**: `GET /api/admin/article/page`
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| title | string | 否 | `Spring` | 文章标题搜索 |
| categoryId | long | 否 | `1` | 按分类筛选 |
| status | int | 否 | `1` | 按状态筛选 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 100,
        "title": "Spring Boot 实战",
        "summary": "本文介绍...",
        "coverImg": "http://...",
        "viewCount": 120,
        "isTop": 0,
        "status": 1,
        "categoryName": "后端技术",
        "authorNickname": "Admin",
        "publishTime": "2026-02-01 10:00:00",
        "createTime": "2026-01-30 16:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 4.3 更新文章

- **接口路径**: `PUT /api/admin/article/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| title | string | 是 | 文章标题 |
| content | string | 是 | 文章内容 (Markdown格式) |
| summary | string | 否 | 摘要 |
| categoryId | long | 是 | 分类ID |
| tagIds | array | 否 | 标签ID列表 |
| coverImg | string | 否 | 封面图片URL |
| isTop | int | 否 | 是否置顶 |
| status | int | 是 | 状态：0-草稿，1-发布，2-下架 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5001,
  "message": "文章不存在或已被删除",
  "data": null
}
```

---

### 4.4 删除文章

- **接口路径**: `DELETE /api/admin/article/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 4.5 文章详情 (后台)

- **接口路径**: `GET /api/admin/article/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 100,
    "title": "Spring Boot 实战",
    "content": "# 详细内容...",
    "summary": "本文介绍...",
    "categoryId": 1,
    "categoryName": "后端技术",
    "tagIds": [1, 2, 3],
    "tags": [
      { "id": 1, "name": "Java" },
      { "id": 2, "name": "Spring Boot" }
    ],
    "coverImg": "http://...",
    "isTop": 0,
    "status": 1,
    "publishTime": "2026-02-01 10:00:00",
    "createTime": "2026-01-30 16:00:00",
    "updateTime": "2026-02-01 10:00:00"
  }
}
```

---

### 4.6 文章置顶/取消置顶

- **接口路径**: `PUT /api/admin/article/{id}/top`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| isTop | int | 是 | 是否置顶：1-是，0-否 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 4.7 更新文章状态

- **接口路径**: `PUT /api/admin/article/{id}/status`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| status | int | 是 | 状态：0-草稿，1-发布，2-下架 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 4.8 前台文章列表 (Portal)

- **接口路径**: `GET /api/blog/article/page`
- **是否认证**: 否

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| categoryId | long | 否 | `1` | 按分类筛选 |
| tagId | long | 否 | `5` | 按标签筛选 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 100,
        "title": "Spring Boot 实战",
        "summary": "本文介绍...",
        "coverImg": "http://...",
        "viewCount": 120,
        "publishTime": "2026-02-01 10:00:00",
        "categoryName": "后端技术",
        "tags": [
           { "id": 1, "name": "Java" }
        ]
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 4.9 前台文章详情 (Portal)

- **接口路径**: `GET /api/blog/article/{id}`
- **是否认证**: 否

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 100,
    "title": "Spring Boot 实战",
    "content": "# 详细内容...",
    "summary": "本文介绍...",
    "coverImg": "http://...",
    "viewCount": 121,
    "publishTime": "2026-02-01 10:00:00",
    "categoryId": 1,
    "categoryName": "后端技术",
    "authorNickname": "Admin",
    "tags": [ ... ],
    "prevArticle": { "id": 99, "title": "上一篇" },
    "nextArticle": { "id": 101, "title": "下一篇" }
  }
}
```

**失败响应**
```json
{
  "code": 5001,
  "message": "文章不存在或已被删除",
  "data": null
}
```

---

## 5. 分类与标签 (Category & Tag)

### 5.1 获取全部分类 (Portal)

- **接口路径**: `GET /api/blog/category/list`
- **是否认证**: 否

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "articleCount": 15
    },
    {
      "id": 2,
      "name": "随笔",
      "articleCount": 3
    }
  ]
}
```

---

### 5.2 后台分类管理接口

#### 5.2.1 分页获取分类列表

- **接口路径**: `GET /api/admin/category/list`
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| name | string | 否 | `Java` | 分类名称搜索 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "Java",
        "description": "Java相关技术",
        "sort": 0,
        "status": 1,
        "createTime": "2026-01-01 12:00:00",
        "updateTime": "2026-01-01 12:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 5.2.2 创建分类

- **接口路径**: `POST /api/admin/category`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| name | string | 是 | 分类名称 |
| description | string | 否 | 描述 |
| sort | int | 否 | 排序（升序） |
| status | int | 否 | 状态：1-启用，0-禁用 (默认1) |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5004,
  "message": "该分类名称已存在",
  "data": null
}
```

#### 5.2.3 更新分类

- **接口路径**: `PUT /api/admin/category/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 分类ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| name | string | 是 | 分类名称 |
| description | string | 否 | 描述 |
| sort | int | 否 | 排序（升序） |
| status | int | 否 | 状态：1-启用，0-禁用 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5002,
  "message": "分类不存在",
  "data": null
}
```

#### 5.2.4 删除分类

- **接口路径**: `DELETE /api/admin/category/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 分类ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5006,
  "message": "该分类下还有文章，无法删除",
  "data": null
}
```

#### 5.2.5 批量删除分类

- **接口路径**: `DELETE /api/admin/category/batch-delete`
- **是否认证**: 是

**请求体 (JSON)**

直接传递 ID 数组：
```json
[1, 2, 3]
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5006,
  "message": "分类【Java】下存在文章，无法删除",
  "data": null
}
```

---

### 5.3 标签管理接口

#### 5.3.1 获取所有标签 (Portal)

- **接口路径**: `GET /api/blog/tag/list`
- **是否认证**: 否

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Spring Boot",
      "color": "#1890ff",
      "articleCount": 10
    }
  ]
}
```

#### 5.3.2 分页获取标签列表 (后台)

- **接口路径**: `GET /api/admin/tag/list`
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| name | string | 否 | `Spring` | 标签名称搜索 |

#### 5.3.3 创建标签

- **接口路径**: `POST /api/admin/tag`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| name | string | 是 | 标签名称 |
| color | string | 否 | 标签颜色，默认 `#1890ff` |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5005,
  "message": "该标签名称已存在",
  "data": null
}
```

#### 5.3.4 更新标签

- **接口路径**: `PUT /api/admin/tag/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 标签ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| name | string | 是 | 标签名称 |
| color | string | 否 | 标签颜色 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 5003,
  "message": "标签不存在",
  "data": null
}
```

#### 5.3.5 删除标签

- **接口路径**: `DELETE /api/admin/tag/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 标签ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

#### 5.3.6 批量删除标签

- **接口路径**: `DELETE /api/admin/tag/batch-delete`
- **是否认证**: 是

**请求体 (JSON)**

直接传递 ID 数组：
```json
[1, 2, 3]
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

## 6. 评论互动 (Comment)

### 6.1 获取文章评论树 (Portal)

- **接口路径**: `GET /api/blog/comment/tree/{articleId}`
- **是否认证**: 否

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| articleId | `100` | 文章ID。若为 `0` 则获取留言板评论 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "id": 501,
      "nickname": "用户A",
      "content": "写的真好！",
      "createTime": "2026-02-17T10:00:00",
      "children": [
        {
          "id": 502,
          "nickname": "作者",
          "replyNickname": "用户A",
          "content": "谢谢支持",
          "createTime": "2026-02-17T10:30:00"
        }
      ]
    }
  ]
}
```

### 6.2 发表评论 (Portal)

- **接口路径**: `POST /api/blog/comment`
- **是否认证**: 否（游客可评论，系统自动记录IP和UA）

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| articleId | long | 是 | 文章ID，留言板传 `0` |
| content | string | 是 | 评论内容 |
| parentId | long | 否 | 父评论ID，回复时必填 |
| rootParentId | long | 否 | 根评论ID，回复楼中楼时必填 |
| nickname | string | 是 | 昵称 |
| email | string | 否 | 邮箱 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 6003,
  "message": "评论内容不能为空",
  "data": null
}
```

---

### 6.3 后台评论管理

#### 6.3.1 分页获取评论列表

- **接口路径**: `GET /api/admin/comment/page`
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| articleId | long | 否 | `100` | 按文章ID筛选 |
| status | int | 否 | `0` | 按状态筛选：0-待审核，1-审核通过，2-审核未通过 |
| nickname | string | 否 | `用户` | 按昵称搜索 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 501,
        "articleId": 100,
        "articleTitle": "Spring Boot 实战",
        "nickname": "用户A",
        "email": "user@example.com",
        "content": "写的真好！",
        "status": 1,
        "ipAddress": "127.0.0.1",
        "createTime": "2026-02-01 10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 6.3.2 审核评论

- **接口路径**: `PUT /api/admin/comment/{id}/audit`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `501` | 评论ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| status | int | 是 | 审核状态：1-审核通过，2-审核未通过 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 6001,
  "message": "评论不存在或已被删除",
  "data": null
}
```

#### 6.3.3 删除评论

- **接口路径**: `DELETE /api/admin/comment/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `501` | 评论ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

#### 6.3.4 批量审核评论

- **接口路径**: `PUT /api/admin/comment/batch-audit`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| ids | array | 是 | 评论ID列表，如 `[501, 502]` |
| status | int | 是 | 审核状态：1-审核通过，2-审核未通过 |

**前端调用示例**
```javascript
axios.put('/api/admin/comment/batch-audit', {
  ids: [501, 502, 503],
  status: 1
});
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 1001,
  "message": "请选择要审核的评论",
  "data": null
}
```

#### 6.3.5 批量删除评论

- **接口路径**: `DELETE /api/admin/comment/batch-delete`
- **是否认证**: 是

**请求体 (JSON)**

直接传递 ID 数组：
```json
[501, 502, 503]
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 1001,
  "message": "请选择要删除的评论",
  "data": null
}
```

---

## 7. 文件上传 (File Upload)

### 7.1 上传文件

- **接口路径**: `POST /api/admin/upload`
- **是否认证**: 是
- **ContentType**: `multipart/form-data`

**请求参数**

| 名称 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| file | file | 是 | 文件对象 |

**前端调用示例**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

axios.post('/api/admin/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
}).then(res => {
  console.log('文件URL:', res.data.data);
});
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": "http://localhost:8080/uploads/2026/02/demo_xxxx.png"
}
```

> ⚠️ **注意**：返回值 `data` 直接为文件访问 URL 字符串，非对象。

**失败响应**
```json
{
  "code": 7003,
  "message": "文件大小超出限制，请压缩后重试",
  "data": null
}
```

---

## 8. 系统角色管理 (System Role)

### 8.1 获取所有角色

- **接口路径**: `GET /api/admin/role/list`
- **是否认证**: 是

> ⚠️ **注意**：返回全量角色列表（`List`），非分页。

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roleName": "超级管理员",
      "roleCode": "super_admin",
      "description": "拥有系统全部权限",
      "status": 1,
      "createTime": "2026-01-01 12:00:00"
    },
    {
      "id": 2,
      "roleName": "编辑",
      "roleCode": "editor",
      "description": "文章编辑权限",
      "status": 1,
      "createTime": "2026-01-01 12:00:00"
    }
  ]
}
```

---

### 8.2 获取角色详情

- **接口路径**: `GET /api/admin/role/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 1,
    "roleName": "超级管理员",
    "roleCode": "super_admin",
    "description": "拥有系统全部权限",
    "status": 1,
    "createTime": "2026-01-01 12:00:00",
    "updateTime": "2026-01-01 12:00:00"
  }
}
```

---

### 8.3 创建角色

- **接口路径**: `POST /api/admin/role`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| roleName | string | 是 | 角色名称，不可重复 |
| roleCode | string | 是 | 角色编码，不可重复 |
| description | string | 否 | 描述 |
| status | int | 否 | 状态：1-启用，0-禁用 (默认1) |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 8.4 更新角色

- **接口路径**: `PUT /api/admin/role/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| roleName | string | 是 | 角色名称 |
| roleCode | string | 是 | 角色编码 |
| description | string | 否 | 描述 |
| status | int | 否 | 状态：1-启用，0-禁用 |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 8.5 删除角色

- **接口路径**: `DELETE /api/admin/role/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 8.6 分配角色权限

- **接口路径**: `PUT /api/admin/role/{id}/permissions`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**请求体 (JSON)**

直接传递权限 ID 数组：
```json
[1, 2, 101, 102, 201]
```

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 8.7 获取角色权限ID列表

- **接口路径**: `GET /api/admin/role/{id}/permissions`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

> ⚠️ **注意**：返回该角色拥有的权限 ID 列表（`List<Long>`），前端可配合权限树实现勾选。

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [1, 2, 101, 102, 201, 202, 301]
}
```

---

## 9. 系统权限管理 (System Permission)

### 9.1 获取权限列表

- **接口路径**: `GET /api/admin/permission/tree`
- **是否认证**: 是

> ⚠️ **注意**：当前返回平铺的权限列表（`List<SysPermission>`），前端需自行根据 `parentId` 构建树形结构。

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "name": "仪表盘",
      "code": "dashboard",
      "type": 1,
      "path": "/dashboard",
      "icon": "DashboardOutlined",
      "sort": 1,
      "status": 1
    },
    {
      "id": 2,
      "parentId": 0,
      "name": "文章管理",
      "code": "article:manage",
      "type": 1,
      "path": "/article",
      "icon": "FileTextOutlined",
      "sort": 2,
      "status": 1
    },
    {
      "id": 101,
      "parentId": 2,
      "name": "查看文章",
      "code": "article:list",
      "type": 2,
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

### 9.2 创建权限

- **接口路径**: `POST /api/admin/permission`
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| parentId | long | 是 | 父级ID，0表示顶级 |
| name | string | 是 | 权限名称 |
| code | string | 否 | 权限标识，按钮类型必填 |
| type | int | 是 | 类型：1-菜单，2-按钮 |
| path | string | 否 | 路由地址，菜单类型必填 |
| component | string | 否 | 组件路径，菜单类型必填 |
| icon | string | 否 | 图标 |
| sort | int | 否 | 排序 |
| status | int | 否 | 状态：1-启用，0-禁用 (默认1) |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 9.3 更新权限

- **接口路径**: `PUT /api/admin/permission/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 权限ID |

**请求体 (JSON)**: 同 9.2 创建权限

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

### 9.4 删除权限

- **接口路径**: `DELETE /api/admin/permission/{id}`
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 权限ID |

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

## 10. 系统设置 (System Setting)

### 10.1 获取系统设置

- **接口路径**: `GET /api/admin/setting`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "siteName": "OpusNocturne",
    "siteDescription": "个人技术博客",
    "siteKeywords": "Java,Spring Boot,前端",
    "footerText": "© 2026 OpusNocturne",
    "adminEmail": "admin@opusnocturne.com",
    "commentAudit": true,
    "articlePageSize": 10,
    "commentPageSize": 20
  }
}
```

**字段说明**
| 字段名 | 类型 | 说明 | 对应数据库字段 |
|:---|:---|:---|:---|
| siteName | string | 站点名称 | `site_name` |
| siteDescription | string | 站点描述 | `site_description` |
| siteKeywords | string | 站点关键词 | `site_keywords` |
| footerText | string | 页脚文本 | `footer_text` |
| adminEmail | string | 管理员邮箱 | `admin_email` |
| commentAudit | boolean | 评论是否需要审核 | `comment_audit` |
| articlePageSize | int | 文章列表每页条数 | `article_page_size` |
| commentPageSize | int | 评论列表每页条数 | `comment_page_size` |

---

### 10.2 更新系统设置

- **接口路径**: `PUT /api/admin/setting`
- **是否认证**: 是

**请求体 (JSON)**: 同 10.1 字段说明表中的字段，所有字段均为选填。

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

---

## 11. 站点统计 (Site Statistics)

### 11.1 获取站点概览统计

- **接口路径**: `GET /api/admin/statistics/overview`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "articleCount": 100,
    "categoryCount": 10,
    "tagCount": 50,
    "commentCount": 200,
    "userCount": 5,
    "totalViewCount": 5000
  }
}
```

---

### 11.2 获取文章发布趋势

- **接口路径**: `GET /api/admin/statistics/article-trend`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "labels": ["1月", "2月", "3月"],
    "data": [10, 15, 8]
  }
}
```

---

### 11.3 获取访问统计

- **接口路径**: `GET /api/admin/statistics/visit`
- **是否认证**: 是

**成功响应**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "totalVisits": 1000,
    "totalPageViews": 2000,
    "trend": [
      { "date": "2026-02-10", "visits": 120, "pageViews": 250 },
      { "date": "2026-02-11", "visits": 150, "pageViews": 300 }
    ],
    "topPages": [
      { "pageUrl": "/blog/article/1", "views": 500 },
      { "pageUrl": "/blog/article/2", "views": 300 }
    ]
  }
}
```

---

## 12. 待实现接口 (TODO)

以下接口已规划但尚未在代码中实现，将在后续版本中完成：

| 接口 | 路径（规划）| 说明 |
|:---|:---|:---|
| 前台文章搜索 | `GET /api/blog/article/search` | 按关键词全文搜索文章 |
| 评论统计 | `GET /api/blog/comment/stats/{articleId}` | 获取文章评论数统计 |
| 附件分页列表 | `GET /api/admin/attachment/page` | 附件管理后台列表 |
| 删除附件 | `DELETE /api/admin/attachment/{id}` | 删除附件记录及文件 |
| 用户修改密码 | `PUT /api/admin/auth/change-password` | 已登录用户修改自己密码（旧密码+新密码） |

---
