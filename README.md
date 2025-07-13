# 志愿者服务平台 - 前端项目

## 📋 项目简介

这是一个基于 Vue 3 + Vite 构建的现代化志愿者服务平台前端项目。该平台旨在为志愿者、组织和社区提供一个高效、便捷、互动的在线服务平台，通过游戏化的激励机制和创新的UI/UX设计，提升志愿服务的参与度和用户体验。

### 🏗️ 项目架构

本项目采用前后端分离架构：
- **前端**：Vue 3 + Vite，提供现代化的用户界面
- **后端**：Spring Boot + MyBatis，提供RESTful API服务
- **数据库**：MySQL，存储用户、活动、报名等核心数据
- **部署**：Docker容器化部署，支持快速部署和扩展

## ✨ 功能特色

### 🎮 游戏化激励机制
- **成长树系统**：志愿者通过参与活动获得积分，用于培养虚拟成长树
- **徽章系统**：根据服务时长和贡献解锁特殊徽章和称号
- **等级进阶**：从志愿新手到公益领袖的成长路径

### 🗺️ 核心功能模块
- **首页**：平台概览、统计数据、特色功能展示
- **活动广场**：活动浏览、搜索筛选、在线报名
- **积分商城**：积分兑换、虚拟物品、实物礼品
- **社区故事**：志愿者故事分享、互动交流
- **个人中心**：个人资料、活动记录、成长轨迹

### 🎨 设计亮点
- **响应式设计**：完美适配桌面、平板、手机等设备
- **现代化UI**：渐变色彩、卡片布局、圆角设计
- **微动画效果**：流畅的交互动画和状态反馈
- **组件化架构**：模块化设计，便于维护和扩展

## 🛠️ 技术栈

- **框架**：Vue 3 (Composition API)
- **构建工具**：Vite 6.x
- **路由**：Vue Router 4
- **样式**：原生CSS + Flexbox + Grid
- **开发语言**：JavaScript (ES6+)
- **包管理**：npm

## 📁 项目结构

```
frontend/volunteeeeer/
├── public/                 # 静态资源
├── src/
│   ├── components/         # Vue组件
│   │   ├── Navigation.vue     # 导航组件
│   │   ├── HomePage.vue       # 首页组件
│   │   ├── ActivitiesPage.vue # 活动广场组件
│   │   ├── StorePage.vue      # 积分商城组件
│   │   ├── CommunityPage.vue  # 社区故事组件
│   │   └── ProfilePage.vue    # 个人中心组件
│   ├── router/             # 路由配置
│   │   └── index.js           # 路由定义
│   ├── App.vue             # 根组件
│   └── main.js             # 应用入口
├── index.html              # HTML模板
├── package.json            # 项目配置
├── vite.config.js          # Vite配置
└── README.md               # 项目说明
```

## 🚀 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖

```bash
# 进入项目目录
cd frontend/volunteeeeer

# 安装项目依赖
npm install
```

### 开发环境运行

```bash
# 启动开发服务器
npm run dev
```

启动成功后，在浏览器中访问：http://localhost:5173

### 生产环境构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 🎯 页面功能说明

### 🏠 首页 (HomePage)
- 平台介绍和特色展示
- 实时统计数据（志愿者数量、活动数量、服务时长）
- 最新活动推荐
- 快速导航入口

### 📅 活动广场 (ActivitiesPage)
- 活动列表展示（卡片式布局）
- 多维度筛选（类型、地区、时间、状态）
- 关键词搜索功能
- 活动详情查看和在线报名

### 🛍️ 积分商城 (StorePage)
- 个人积分余额显示
- 商品分类浏览
- 积分兑换功能
- 兑换记录查询

### 💬 社区故事 (CommunityPage)
- 志愿者故事发布
- 图文内容展示
- 点赞评论互动
- 故事分类和排序

### 👤 个人中心 (ProfilePage)
- **活动记录**：参与过的活动历史，支持状态筛选和详情查看
- **成长树**：可视化成长轨迹和里程碑，优化的响应式布局
- **积分记录**：积分获取和消费明细，支持分类统计
- **个人设置**：资料编辑、通知偏好、隐私设置，实时保存

## 🔗 后端集成

### API 接口
本前端项目与Spring Boot后端无缝集成，主要API包括：

- **用户管理**：`/api/users` - 用户注册、登录、信息管理
- **活动管理**：`/api/activities` - 活动CRUD、搜索、报名
- **报名系统**：`/api/registrations` - 报名记录、状态管理
- **积分商城**：`/api/store` - 商品管理、积分兑换

### 数据流
1. 前端发送HTTP请求到后端API
2. 后端处理业务逻辑并操作数据库
3. 返回JSON格式数据给前端
4. 前端更新UI状态和用户界面

## 🎨 UI/UX 设计特色

### 色彩方案
- **主色调**：渐变紫色 (#667eea → #764ba2)
- **辅助色**：绿色（成功）、橙色（警告）、红色（错误）
- **中性色**：灰色系列用于文本和背景

### 交互设计
- **悬停效果**：按钮和卡片的动态反馈
- **加载状态**：数据加载时的友好提示
- **表单验证**：实时输入验证和错误提示
- **模态框**：详情查看和操作确认
- **微动画**：流畅的过渡效果和状态变化

### 响应式适配
- **桌面端**：>=1024px，多列布局，丰富交互
- **平板端**：768px-1023px，适中布局，触控优化
- **手机端**：<768px，单列布局，手势友好

### 最新优化
- **成长树布局**：重新设计的成长树组件，支持更好的响应式体验
- **卡片设计**：统一的卡片样式和阴影效果
- **动画效果**：优化的悬停和点击动画

## 🔧 开发指南

### 推荐IDE设置

- **编辑器**：[VSCode](https://code.visualstudio.com/)
- **插件**：[Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (请禁用 Vetur)

### 代码规范

- 使用 Vue 3 Composition API
- 组件采用 `<script setup>` 语法
- CSS 使用 scoped 样式
- 遵循 Vue 官方风格指南

### 自定义配置

参考 [Vite 配置文档](https://vite.dev/config/) 进行项目配置。

## 📦 依赖说明

### 核心依赖
- `vue`: Vue 3 框架
- `vue-router`: 官方路由管理器

### 开发依赖
- `@vitejs/plugin-vue`: Vue 单文件组件支持
- `vite`: 构建工具和开发服务器

### 推荐扩展
考虑添加以下依赖来增强项目功能：
- `axios`: HTTP客户端，用于API调用
- `pinia`: 状态管理库
- `element-plus`: UI组件库（可选）
- `dayjs`: 日期处理库
- `echarts`: 数据可视化（用于统计图表）

## 🚀 部署说明

### 构建产物
运行 `npm run build` 后，构建产物将生成在 `dist/` 目录中，包含：
- 优化后的 HTML、CSS、JavaScript 文件
- 静态资源文件
- 源码映射文件（可选）

### 部署方式
1. **静态托管**：将 `dist/` 目录部署到 Nginx、Apache 等 Web 服务器
2. **CDN 部署**：上传到阿里云 OSS、腾讯云 COS 等对象存储
3. **容器化部署**：使用 Docker 容器化部署

### Docker 部署
项目根目录提供了 `docker-compose.yml` 文件，支持一键部署：

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

### 环境配置
- **开发环境**：`http://localhost:5173`
- **生产环境**：配置反向代理指向后端API
- **API地址**：确保前端能正确访问后端服务地址

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔄 更新日志

### v1.2.0 (最新)
- ✨ 优化成长树组件的响应式布局
- 🎨 改进卡片设计和动画效果
- 🐛 修复移动端布局问题
- 📱 增强移动端用户体验

### v1.1.0
- 🔗 完成后端API集成
- 📊 添加数据统计功能
- 🎮 完善游戏化激励机制

### v1.0.0
- 🎉 项目初始版本发布
- 📱 完成所有核心页面开发
- 🎨 实现响应式设计

## 📞 联系我们

如有问题或建议，请通过以下方式联系：

- 项目地址：[GitHub Repository](https://github.com/your-username/volunteeeeer)
- 问题反馈：[Issues](https://github.com/your-username/volunteeeeer/issues)
- 邮箱：volunteer-platform@example.com

## 🤝 贡献者

感谢所有为这个项目做出贡献的开发者！

---

**感谢您对志愿者服务平台的关注和支持！** 🙏
