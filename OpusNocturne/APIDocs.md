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
所有接口均返回统一的 JSON 格式：

**成功 (HTTP 200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... } // 具体数据
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
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenHead": "Bearer "
  }
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
  "message": "操作成功",
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
  "message": "操作成功",
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

---

## 4. 文章管理 (Blog Article)

### 4.1 发布/创建文章

- **接口路径**: `/api/admin/article`
- **HTTP 方法**: `POST`
- **功能描述**: 创建新的博客文章
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
| status | int | 是 | 状态：0-草稿，1-发布 |

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

---

### 4.2 前台文章列表 (Portal)

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
    ]
    // ...
  }
}
```

---

### 4.3 前台文章详情 (Portal)

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
  "data": {
    "id": 100,
    "title": "Spring Boot 实战",
    "content": "# 详细内容...",
    "categoryId": 1,
    "categoryName": "后端技术",
    "authorNickname": "Admin",
    "tags": [ ... ],
    "prevArticle": { "id": 99, "title": "上一篇" },
    "nextArticle": { "id": 101, "title": "下一篇" }
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
  "data": [
    {
      "id": 501,
      "nickname": "用户A",
      "content": "写的真好！",
      "createTime": "...",
      "children": [
        {
          "id": 502,
          "nickname": "作者",
          "replyNickname": "用户A",
          "content": "谢谢支持",
          "createTime": "..."
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
| file | file | 是 | 文件对象 (max 10MB) |
| bizType | string | 否 | 业务类型，如 `article`, `avatar` |

**前端调用示例**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('bizType', 'article');

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
  "message": "操作成功",
  "data": {
    "id": 123,
    "fileName": "demo.png",
    "fileUrl": "http://localhost:8080/uploads/2023/10/demo_xxxx.png",
    "fileSize": 102400
  }
}
```
