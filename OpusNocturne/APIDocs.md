# OpusNocturne API 接口文档

本文档详细描述了 OpusNocturne 博客系统的后端 API 接口。

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

---

## 1. 通用说明

### 基础 URL
开发环境：`http://localhost:8080`

### 认证方式
除“前台展示”类接口外，大多数管理接口需要进行 JWT 认证。
请在 HTTP 请求头中携带 Token：
```http
Authorization: Bearer <Your-Token>
```

### 统一响应结构
所有接口均返回统一的 JSON 格式，基于 `Result` 类设计：

**成功 (HTTP 200)**
```json
{
  "code": 200,
  "message": "成功",
  "data": { ... } // 具体数据
}
```

**成功（无数据）(HTTP 200)**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败 (HTTP 400/401/403/500)**
```json
{
  "code": 401, // 错误码：401-未认证，403-无权限，500-系统错误
  "message": "用户名或密码错误", // 错误信息
  "data": null
}
```

### 分页规则
分页查询接口通常包含以下查询参数：
- `current`: 当前页码，默认 1
- `size`: 每页条数，默认 10

分页响应数据结构：
```json
{
  "records": [ ... ], // 数据列表
  "total": 100,       // 总记录数
  "size": 10,         // 每页条数
  "current": 1,       // 当前页码
  "pages": 10         // 总页数
}
```

---

## 2. 认证模块 (Auth)

### 2.1 用户登录

- **接口路径**: `/api/admin/auth/login`
- **HTTP 方法**: `POST`
- **功能描述**: 管理员或普通用户登录，获取 JWT Token
- **是否认证**: 否

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 | 示例 |
|:---|:---|:---|:---|:---|
| username | string | 是 | 用户名 | `admin` |
| password | string | 是 | 密码 | `123456` |

**前端调用示例 (Axios)**
```javascript
import axios from 'axios';

axios.post('/api/admin/auth/login', {
  username: 'admin',
  password: 'your_password'
}).then(response => {
  const token = response.data.data.token;
  localStorage.setItem('token', token);
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer "
  }
}
```

**失败响应**
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

### 2.2 退出登录

- **接口路径**: `/api/admin/auth/logout`
- **HTTP 方法**: `POST`
- **功能描述**: 注销当前用户，使 Token 失效（从 Redis 中删除）
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

---

### 2.3 获取当前用户信息

- **接口路径**: `/api/admin/auth/info`
- **HTTP 方法**: `GET`
- **功能描述**: 获取当前登录用户的详细信息（包括昵称、头像、权限列表等）
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": "...",
    "email": "...",
    "permissions": ["blog:article:add", "blog:article:edit"]
  }
}
```

**失败响应**
```json
{
  "code": 401,
  "message": "未登录或登录已过期",
  "data": null
}
```

---

### 2.4 刷新 Token

- **接口路径**: `/api/admin/auth/refresh`
- **HTTP 方法**: `POST`
- **功能描述**: 刷新 JWT Token，避免用户频繁登录
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer "
  }
}
```

**失败响应**
```json
{
  "code": 401,
  "message": "Token 已过期",
  "data": null
}
```

---

## 3. 系统用户管理 (System User)

### 3.1 分页获取用户列表

- **接口路径**: `/api/admin/user/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取系统用户列表，支持按用户名、昵称模糊搜索
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| username | string | 否 | `admin` | 用户名搜索 |
| nickname | string | 否 | `管理员` | 昵称搜索 |

**前端调用示例**
```javascript
axios.get('/api/admin/user/page', {
  params: {
    current: 1,
    size: 10,
    username: 'test'
  }
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "超级管理员",
        "avatar": "http://...",
        "email": "admin@example.com",
        "status": 1,
        "createTime": "2023-01-01 12:00:00"
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

- **接口路径**: `/api/admin/user`
- **HTTP 方法**: `POST`
- **功能描述**: 新增系统用户
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

**前端调用示例**
```javascript
axios.post('/api/admin/user', {
  username: 'new_user',
  password: 'Password123',
  nickname: '新员工',
  roleIds: [2]
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

---

### 3.3 更新用户

- **接口路径**: `/api/admin/user/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新用户信息
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
| roleIds | array | 否 | 关联角色ID列表，如 `[1, 2]` |
| status | int | 否 | 状态：1-启用，0-禁用 |

**前端调用示例**
```javascript
axios.put('/api/admin/user/1', {
  nickname: '管理员',
  email: 'admin@example.com',
  roleIds: [1],
  status: 1
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

---

### 3.4 删除用户

- **接口路径**: `/api/admin/user/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除用户（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

---

### 3.5 获取用户详情

- **接口路径**: `/api/admin/user/{id}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取用户详细信息
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": "http://...",
    "email": "admin@example.com",
    "status": 1,
    "roleIds": [1],
    "createTime": "2023-01-01 12:00:00"
  }
}
```

---

### 3.6 重置用户密码

- **接口路径**: `/api/admin/user/{id}/reset-password`
- **HTTP 方法**: `PUT`
- **功能描述**: 重置用户密码
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 用户ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| password | string | 是 | 新密码 |

**前端调用示例**
```javascript
axios.put('/api/admin/user/1/reset-password', {
  password: 'NewPassword123'
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

---

## 4. 文章管理 (Blog Article)

### 4.1 发布/创建文章

- **接口路径**: `/api/admin/article`
- **HTTP 方法**: `POST`
- **功能描述**: 创建新的博客文章
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 | 对应数据库表 |
|:---|:---|:---|:---|:---|
| title | string | 是 | 文章标题 | `article` |
| content | string | 是 | 文章内容 (Markdown格式) | `article` |
| summary | string | 否 | 摘要 | `article` |
| categoryId | long | 是 | 分类ID | `article` |
| tagIds | array | 否 | 标签ID列表，如 `[1, 3]` | `article_tag` |
| coverImg | string | 否 | 封面图片URL | `article` |
| isTop | int | 否 | 是否置顶：1-是，0-否 | `article` |
| status | int | 是 | 状态：0-草稿，1-发布，2-下架 | `article` |

**标签关联管理说明**
- 通过 `tagIds` 参数传递文章关联的标签ID列表
- 系统会自动处理 `article_tag` 表中的关联关系
- 创建文章时，会根据 `tagIds` 生成对应的文章-标签关联记录
- 这种方式简化了标签管理流程，无需单独的接口来管理关联关系

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
  "code": 200,
  "message": "成功",
  "data": {
    "id": 100,
    "title": "Spring Boot 实战"
  }
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "标题不能为空",
  "data": null
}
```

---

### 4.2 后台文章列表

- **接口路径**: `/api/admin/article/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取文章列表，支持分页和搜索
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
  "code": 200,
  "message": "成功",
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
        "publishTime": "2023-10-01 10:00:00",
        "createTime": "2023-09-30 16:00:00"
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

- **接口路径**: `/api/admin/article/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新文章信息
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 | 对应数据库表 |
|:---|:---|:---|:---|:---|
| title | string | 是 | 文章标题 | `article` |
| content | string | 是 | 文章内容 (Markdown格式) | `article` |
| summary | string | 否 | 摘要 | `article` |
| categoryId | long | 是 | 分类ID | `article` |
| tagIds | array | 否 | 标签ID列表，如 `[1, 3]` | `article_tag` |
| coverImg | string | 否 | 封面图片URL | `article` |
| isTop | int | 否 | 是否置顶：1-是，0-否 | `article` |
| status | int | 是 | 状态：0-草稿，1-发布，2-下架 | `article` |

**标签关联管理说明**
- 通过 `tagIds` 参数传递文章关联的标签ID列表
- 系统会自动处理 `article_tag` 表中的关联关系
- 更新文章时，会先删除原有的标签关联记录，再根据 `tagIds` 重新生成新的关联记录
- 这种方式简化了标签管理流程，无需单独的接口来管理关联关系

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

---

### 4.4 删除文章

- **接口路径**: `/api/admin/article/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除文章（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

---

### 4.5 文章详情 (后台)

- **接口路径**: `/api/admin/article/{id}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取文章详细信息，用于编辑
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
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
    "publishTime": "2023-10-01 10:00:00",
    "createTime": "2023-09-30 16:00:00",
    "updateTime": "2023-10-01 10:00:00"
  }
}
```

---

### 4.6 前台文章列表 (Portal)

- **接口路径**: `/api/blog/article/page`
- **HTTP 方法**: `GET`
- **功能描述**: 博客前台展示文章列表
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
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 100,
        "title": "Spring Boot 实战",
        "summary": "本文介绍...",
        "coverImg": "http://...",
        "viewCount": 120,
        "publishTime": "2023-10-01 10:00:00",
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

### 4.7 前台文章详情 (Portal)

- **接口路径**: `/api/blog/article/{id}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取文章详细内容，并自动增加浏览量
- **是否认证**: 否

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `100` | 文章ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 100,
    "title": "Spring Boot 实战",
    "content": "# 详细内容...",
    "summary": "本文介绍...",
    "coverImg": "http://...",
    "viewCount": 121,
    "publishTime": "2023-10-01 10:00:00",
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
  "code": 404,
  "message": "文章不存在或已下架",
  "data": null
}
```

---

### 4.8 文章置顶/取消置顶

- **接口路径**: `/api/admin/article/{id}/top`
- **HTTP 方法**: `PUT`
- **功能描述**: 设置文章是否置顶
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

---

### 4.9 更新文章状态

- **接口路径**: `/api/admin/article/{id}/status`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新文章发布状态
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "文章不存在",
  "data": null
}
```

---

### 4.10 前台文章搜索 (Portal)

- **接口路径**: `/api/blog/article/search`
- **HTTP 方法**: `GET`
- **功能描述**: 按关键词搜索文章
- **是否认证**: 否

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| keyword | string | 是 | `Spring Boot` | 搜索关键词 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 100,
        "title": "Spring Boot 实战",
        "summary": "本文介绍...",
        "coverImg": "http://...",
        "viewCount": 120,
        "publishTime": "2023-10-01 10:00:00",
        "categoryName": "后端技术",
        "tags": [ ... ]
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

## 5. 分类与标签 (Category & Tag)

### 5.1 获取全部分类 (Portal)

- **接口路径**: `/api/blog/category/list`
- **HTTP 方法**: `GET`
- **功能描述**: 获取所有分类及其文章数量，用于前台侧边栏
- **是否认证**: 否

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
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

- **接口路径**: `/api/admin/category/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取分类列表，支持分页和搜索
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
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "Java",
        "description": "Java相关技术",
        "sort": 0,
        "status": 1,
        "createTime": "2023-01-01 12:00:00",
        "updateTime": "2023-01-01 12:00:00"
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

- **接口路径**: `/api/admin/category`
- **HTTP 方法**: `POST`
- **功能描述**: 创建新的分类
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "分类名称已存在",
  "data": null
}
```

#### 5.2.3 更新分类

- **接口路径**: `/api/admin/category/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新分类信息
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "分类不存在",
  "data": null
}
```

#### 5.2.4 删除分类

- **接口路径**: `/api/admin/category/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除分类（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 分类ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "分类不存在",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "该分类下存在文章，无法删除",
  "data": null
}
```

#### 5.2.5 批量删除分类

- **接口路径**: `/api/admin/category/batch-delete`
- **HTTP 方法**: `DELETE`
- **功能描述**: 批量删除分类
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| ids | array | 是 | 分类ID列表，如 `[1, 2]` |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "请选择要删除的分类",
  "data": null
}
```

---

### 5.3 标签管理接口

#### 5.3.1 获取所有标签 (Portal)

- **接口路径**: `/api/blog/tag/list`
- **HTTP 方法**: `GET`
- **功能描述**: 获取所有标签及其文章数量，用于前台侧边栏
- **是否认证**: 否

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "name": "Spring Boot",
      "color": "#1890ff",
      "articleCount": 10
    },
    {
      "id": 2,
      "name": "Vue",
      "color": "#4fc08d",
      "articleCount": 5
    }
  ]
}
```

#### 5.3.2 后台标签管理

##### 5.3.2.1 分页获取标签列表

- **接口路径**: `/api/admin/tag/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取标签列表，支持分页和搜索
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| name | string | 否 | `Spring` | 标签名称搜索 |

##### 5.3.2.2 创建标签

- **接口路径**: `/api/admin/tag`
- **HTTP 方法**: `POST`
- **功能描述**: 创建新的标签
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| name | string | 是 | 标签名称 |
| color | string | 否 | 标签颜色，默认 `#1890ff` |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "标签名称已存在",
  "data": null
}
```

##### 5.3.2.3 更新标签

- **接口路径**: `/api/admin/tag/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新标签信息
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "标签不存在",
  "data": null
}
```

##### 5.3.2.4 删除标签

- **接口路径**: `/api/admin/tag/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除标签（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 标签ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "标签不存在",
  "data": null
}
```

##### 5.3.2.5 批量删除标签

- **接口路径**: `/api/admin/tag/batch-delete`
- **HTTP 方法**: `DELETE`
- **功能描述**: 批量删除标签
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| ids | array | 是 | 标签ID列表，如 `[1, 2]` |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "请选择要删除的标签",
  "data": null
}
```

---

## 6. 评论互动 (Comment)

### 6.1 获取文章评论树 (Portal)

- **接口路径**: `/api/blog/comment/tree/{articleId}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取指定文章的评论列表，以树形结构返回
- **是否认证**: 否

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| articleId | `100` | 文章ID。若为 `0` 则表示获取留言板评论 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
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

- **接口路径**: `/api/blog/comment`
- **HTTP 方法**: `POST`
- **功能描述**: 发表评论或回复
- **是否认证**: 是 (建议前端先登录，目前逻辑已放行但代码中有关联userId逻辑)

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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "评论内容不能为空",
  "data": null
}
```

---

### 6.3 后台评论管理

#### 6.3.1 分页获取评论列表

- **接口路径**: `/api/admin/comment/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取评论列表，支持分页和搜索
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
  "code": 200,
  "message": "成功",
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
        "createTime": "2023-10-01 10:00:00"
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

- **接口路径**: `/api/admin/comment/{id}/audit`
- **HTTP 方法**: `PUT`
- **功能描述**: 审核评论
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
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "评论不存在",
  "data": null
}
```

#### 6.3.3 删除评论

- **接口路径**: `/api/admin/comment/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除评论（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `501` | 评论ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "评论不存在",
  "data": null
}
```

#### 6.3.4 批量审核评论

- **接口路径**: `/api/admin/comment/batch-audit`
- **HTTP 方法**: `PUT`
- **功能描述**: 批量审核评论
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| ids | array | 是 | 评论ID列表，如 `[501, 502]` |
| status | int | 是 | 审核状态：1-审核通过，2-审核未通过 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "请选择要审核的评论",
  "data": null
}
```

#### 6.3.5 批量删除评论

- **接口路径**: `/api/admin/comment/batch-delete`
- **HTTP 方法**: `DELETE`
- **功能描述**: 批量删除评论
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| ids | array | 是 | 评论ID列表，如 `[501, 502]` |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "请选择要删除的评论",
  "data": null
}
```

---

### 6.4 评论统计 (Portal)

- **接口路径**: `/api/blog/comment/stats/{articleId}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取文章评论统计信息
- **是否认证**: 否

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| articleId | `100` | 文章ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "totalCount": 25,
    "approvedCount": 20,
    "pendingCount": 5
  }
}
```

---

## 7. 文件上传 (File Upload)

### 7.1 上传文件

- **接口路径**: `/api/admin/attachment/upload`
- **HTTP 方法**: `POST`
- **功能描述**: 上传图片或文档，返回访问 URL
- **是否认证**: 是
- **ContentType**: `multipart/form-data`

**请求参数**

| 名称 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| file | file | 是 | 文件对象 (最大 10MB) |
| bizType | string | 否 | 业务类型，如 `article`, `avatar` |
| bizId | long | 否 | 业务ID，如文章ID |

**支持的文件类型**
- 图片：jpg, jpeg, png, gif, webp
- 文档：md, txt, pdf
- 其他：zip, rar

**前端调用示例**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('bizType', 'article');
formData.append('bizId', '100');

axios.post('/api/admin/attachment/upload', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
}).then(res => {
  console.log('文件URL:', res.data.data.fileUrl);
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 123,
    "fileName": "demo.png",
    "fileUrl": "http://localhost:8080/uploads/2023/10/demo_xxxx.png",
    "fileSize": 102400,
    "fileType": "image/png"
  }
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "文件大小超过限制",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "不支持的文件类型",
  "data": null
}
```

---

### 7.2 附件管理

#### 7.2.1 分页获取附件列表

- **接口路径**: `/api/admin/attachment/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取附件列表，支持分页和搜索
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| fileName | string | 否 | `demo` | 文件名搜索 |
| bizType | string | 否 | `article` | 按业务类型筛选 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 123,
        "fileName": "demo.png",
        "fileUrl": "http://localhost:8080/uploads/2023/10/demo_xxxx.png",
        "fileType": "image/png",
        "fileSize": 102400,
        "bizType": "article",
        "bizId": 100,
        "createTime": "2023-10-01 10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 7.2.2 删除附件

- **接口路径**: `/api/admin/attachment/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除附件（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `123` | 附件ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "附件不存在",
  "data": null
}
```

---

## 8. 系统角色管理 (System Role)

### 8.1 分页获取角色列表

- **接口路径**: `/api/admin/role/page`
- **HTTP 方法**: `GET`
- **功能描述**: 获取系统角色列表，支持分页和搜索
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| current | int | 否 | `1` | 页码 |
| size | int | 否 | `10` | 每页条数 |
| roleName | string | 否 | `管理员` | 角色名称搜索 |
| roleCode | string | 否 | `admin` | 角色编码搜索 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 1,
        "roleName": "超级管理员",
        "roleCode": "admin",
        "description": "系统最高权限",
        "status": 1,
        "createTime": "2023-01-01 12:00:00"
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

### 8.2 创建角色

- **接口路径**: `/api/admin/role`
- **HTTP 方法**: `POST`
- **功能描述**: 新增系统角色
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| roleName | string | 是 | 角色名称，不可重复 |
| roleCode | string | 是 | 角色编码，不可重复 |
| description | string | 否 | 描述 |
| permissionIds | array | 否 | 关联权限ID列表，如 `[1, 2]` |
| status | int | 否 | 状态：1-启用，0-禁用 (默认1) |

**前端调用示例**
```javascript
axios.post('/api/admin/role', {
  roleName: '编辑',
  roleCode: 'editor',
  description: '文章编辑权限',
  permissionIds: [1, 2, 3],
  status: 1
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "角色名称已存在",
  "data": null
}
```

---

### 8.3 更新角色

- **接口路径**: `/api/admin/role/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新角色信息
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 |
|:---|:---|:---|:---|
| roleName | string | 是 | 角色名称，不可重复 |
| roleCode | string | 是 | 角色编码，不可重复 |
| description | string | 否 | 描述 |
| permissionIds | array | 否 | 关联权限ID列表，如 `[1, 2]` |
| status | int | 否 | 状态：1-启用，0-禁用 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "角色不存在",
  "data": null
}
```

---

### 8.4 删除角色

- **接口路径**: `/api/admin/role/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除角色（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "角色不存在",
  "data": null
}
```

---

### 8.5 获取角色详情

- **接口路径**: `/api/admin/role/{id}`
- **HTTP 方法**: `GET`
- **功能描述**: 获取角色详细信息，包括关联的权限列表
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "roleName": "超级管理员",
    "roleCode": "admin",
    "description": "系统最高权限",
    "permissionIds": [1, 2, 3],
    "status": 1,
    "createTime": "2023-01-01 12:00:00",
    "updateTime": "2023-01-01 12:00:00"
  }
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "角色不存在",
  "data": null
}
```

---

### 8.6 获取角色权限树

- **接口路径**: `/api/admin/role/{id}/permissions`
- **HTTP 方法**: `GET`
- **功能描述**: 获取角色已分配的权限树，用于权限编辑界面
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 角色ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "roleId": 1,
    "checkedKeys": [1, 2, 3],
    "permissionTree": [
      {
        "id": 1,
        "name": "系统管理",
        "children": [
          {
            "id": 2,
            "name": "用户管理"
          }
        ]
      }
    ]
  }
}
```

---

## 9. 系统权限管理 (System Permission)

### 9.1 获取权限列表

- **接口路径**: `/api/admin/permission/list`
- **HTTP 方法**: `GET`
- **功能描述**: 获取系统权限列表，支持树形结构
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "name": "系统管理",
      "code": "system",
      "type": 1,
      "path": "/system",
      "component": "system/index",
      "icon": "setting",
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "name": "用户管理",
          "code": "user",
          "type": 1,
          "path": "/system/user",
          "component": "system/user/index",
          "icon": "user",
          "sort": 1,
          "status": 1
        }
      ]
    }
  ]
}
```

---

### 9.2 创建权限

- **接口路径**: `/api/admin/permission`
- **HTTP 方法**: `POST`
- **功能描述**: 新增系统权限
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

**前端调用示例**
```javascript
axios.post('/api/admin/permission', {
  parentId: 1,
  name: '文章管理',
  code: 'article',
  type: 1,
  path: '/blog/article',
  component: 'blog/article/index',
  icon: 'file-text',
  sort: 1,
  status: 1
});
```

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 400,
  "message": "权限名称已存在",
  "data": null
}
```

---

### 9.3 更新权限

- **接口路径**: `/api/admin/permission/{id}`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新权限信息
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 权限ID |

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
| status | int | 否 | 状态：1-启用，0-禁用 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "权限不存在",
  "data": null
}
```

---

### 9.4 删除权限

- **接口路径**: `/api/admin/permission/{id}`
- **HTTP 方法**: `DELETE`
- **功能描述**: 删除权限（逻辑删除）
- **是否认证**: 是

**路径参数**

| 名称 | 示例 | 说明 |
|:---|:---|:---|
| id | `1` | 权限ID |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

**失败响应**
```json
{
  "code": 404,
  "message": "权限不存在",
  "data": null
}
```

---

## 10. 系统设置 (System Setting)

### 10.1 获取系统设置

- **接口路径**: `/api/admin/setting`
- **HTTP 方法**: `GET`
- **功能描述**: 获取系统设置信息，从 `sys_setting` 表中读取配置
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "siteName": "OpusNocturne",
    "siteDescription": "个人技术博客",
    "siteKeywords": "Java,Spring Boot,前端",
    "footerText": "© 2026 OpusNocturne",
    "adminEmail": "admin@example.com",
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

- **接口路径**: `/api/admin/setting`
- **HTTP 方法**: `PUT`
- **功能描述**: 更新系统设置信息，将配置保存到 `sys_setting` 表
- **是否认证**: 是

**请求体 (JSON)**

| 字段名 | 类型 | 必填 | 说明 | 对应数据库字段 |
|:---|:---|:---|:---|:---|
| siteName | string | 否 | 站点名称 | `site_name` |
| siteDescription | string | 否 | 站点描述 | `site_description` |
| siteKeywords | string | 否 | 站点关键词 | `site_keywords` |
| footerText | string | 否 | 页脚文本 | `footer_text` |
| adminEmail | string | 否 | 管理员邮箱 | `admin_email` |
| commentAudit | boolean | 否 | 评论是否需要审核 | `comment_audit` |
| articlePageSize | int | 否 | 文章列表每页条数 | `article_page_size` |
| commentPageSize | int | 否 | 评论列表每页条数 | `comment_page_size` |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

---

## 11. 站点统计 (Site Statistics)

### 11.1 获取站点概览统计

- **接口路径**: `/api/admin/statistics/overview`
- **HTTP 方法**: `GET`
- **功能描述**: 获取站点总体统计信息
- **是否认证**: 是

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
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

- **接口路径**: `/api/admin/statistics/article-trend`
- **HTTP 方法**: `GET`
- **功能描述**: 获取最近一段时间的文章发布趋势
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| days | int | 否 | `30` | 查询天数，默认30天 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "labels": ["1月", "2月", "3月"],
    "data": [10, 15, 8]
  }
}
```

---

### 11.3 获取访问统计

- **接口路径**: `/api/admin/statistics/visit`
- **HTTP 方法**: `GET`
- **功能描述**: 获取站点访问统计信息，从 `visit_log` 表中分析数据，包括访问量、页面浏览量和访问趋势
- **是否认证**: 是

**查询参数**

| 名称 | 类型 | 必填 | 示例 | 说明 |
|:---|:---|:---|:---|:---|
| days | int | 否 | `7` | 查询天数，默认7天 |

**成功响应**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "totalVisits": 1000,
    "totalPageViews": 2000,
    "avgVisitTime": 300,
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

**字段说明**
| 字段名 | 类型 | 说明 | 对应数据库表 |
|:---|:---|:---|:---|
| totalVisits | int | 总访问次数 | `visit_log` |
| totalPageViews | int | 总页面浏览量 | `visit_log` |
| avgVisitTime | int | 平均访问时长(秒) | `visit_log` |
| trend | array | 访问趋势数据 | `visit_log` |
| topPages | array | 热门页面排行 | `visit_log` |

**数据来源说明**
- 访问数据从 `visit_log` 表中获取，该表存储了详细的站点访问记录
- 支持按时间范围查询，默认查询最近7天的数据
- 通过 `visit_time` 和 `page_url` 索引优化查询性能

---
