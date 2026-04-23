## 🚀 快速启动 (Getting Started)

### 1. 环境要求

- **Node.js** >= 18.x
  
- **Java JDK** >= 21
  
- **MySQL** >= 8.0
  

### 2. 数据库初始化

1. 打开 MySQL 管理工具（如 Navicat, DataGrip）。
  
2. 执行以下 SQL 创建数据库：
  
  SQL
  
  ```
  CREATE DATABASE bili CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
  
3. 在新建的 `bili` 数据库中，执行 `apps/api/src/main/resources/db/schema.sql` 脚本完成表结构初始化。
  

### 3. 启动后端 (API)

1. 进入后端目录：
  
  Bash
  
  ```
  cd apps/api
  ```
  
2. 修改数据库配置：编辑 `src/main/resources/application.yml`，填入真实的数据库用户名与密码。
  
3. 运行 Spring Boot 服务：
  
  Bash
  
  ```
  ./mvnw spring-boot:run
  ```
  
  *(Windows 用户可执行 `.\\mvnw spring-boot:run`，或直接通过 IDE 启动 `BiliApiApplication`)*
  

### 4. 启动前端 (Web)

1. 打开一个新的终端窗口，进入前端目录：
  
  Bash
  
  ```
  cd apps/web
  ```
  
2. 安装依赖并启动本地开发服务器：
  
  Bash
  
  ```
  npm install
  npm run dev
  ```
  
3. 在浏览器中访问 `http://localhost:5173` 预览项目。
  

---

## 💡 AI Agent 开发最佳实践建议

本项目在开发过程中沉淀了有效的 AI 提效工作流：

1. **技能抽离**：将 UI 规范抽离至 `.cline/skills/`，使 Agent 形成记忆，极大节省上下文 Token。
  
2. **状态重置**：处理大架构变动（如分片上传）时，及时开启 New Task 清除历史负担，通过阅读文件重新构建上下文，有效控制 API 成本。
  
3. **精准提示词**：每次下达指令明确说明“读取文件范围”、“规范约束”、“需要生成的代码”及“不要执行的命令（如防止进程挂死）”。