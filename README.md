# Happy Campus - 大学生学习资源共享平台

一个面向大学生的学习资源共享平台，包含资料市场、课程评价、学习搭子等功能。

## 📁 项目结构

```
happy runing/
├── backend/          # Spring Boot 后端 API
├── frontend/         # Vue 3 用户端网页
└── admin/            # Vue 3 后台管理系统
```

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行建表语句
source backend/src/main/resources/schema.sql

# 导入测试数据
source backend/src/main/resources/data.sql

# 如果是已有的数据库，执行更新脚本
source backend/src/main/resources/update-study.sql
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080

### 4. 启动用户端

```bash
cd frontend
npm install
npm run dev
```

用户端运行在 http://localhost:5173

### 5. 启动后台管理

```bash
cd admin
npm install
npm run dev
```

后台管理运行在 http://localhost:5174

## 👤 测试账号

| 角色 | 用户名（学生卡ID） | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | testuser | user123 |

### 注册说明
- 只需三步即可注册：学生卡ID + 昵称 + 密码
- 学生卡ID即为登录账号
- 注册后使用学生卡ID登录

## ✨ 功能特性

### 学习模块
- 📖 **资料市场**：免费/低价学习资料共享
- ⭐ **学长学姐说**：课程评价，帮学弟学妹选课避坑
- 👥 **找学习搭子**：找人一起自习、备考、学习

### 其他模块（敬请期待）
- ⚽ 体育模块
- 🎮 娱乐模块

### 后台管理（中文化）
- ✅ 控制台数据统计
- ✅ 商品管理
- ✅ 分类管理
- ✅ 订单管理
- ✅ 用户管理
- ✅ 管理员管理

## 🔧 技术栈

### 后端
- Java 17
- Spring Boot 3.2
- Spring Security
- MyBatis-Plus
- MySQL 8.0
- JWT 认证

### 前端
- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios

## 📝 API 文档

启动后端后访问：
- Swagger UI: http://localhost:8080/doc.html
- API Docs: http://localhost:8080/v3/api-docs

## 📄 License

MIT
