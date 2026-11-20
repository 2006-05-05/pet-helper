# Java1234 微信小程序宠物领养系统（本科毕业设计）

一套完整的宠物领养平台解决方案，包含 **Spring Boot 4 后端服务**、**Vue3 + Element Plus + ECharts 管理后台**、**微信小程序端**以及 **MySQL8 建表与测试数据脚本**。

> 核心理念：领养代替购买，让每一只流浪动物都能找到温暖的家 🐾

---

## 一、项目结构

```
e:\sys\sys1
├─ server/                    后端服务（Spring Boot 4，端口 8080）
│  ├─ pom.xml
│  └─ src/main/
│     ├─ java/com/java1234/
│     │  ├─ config/           CorsConfig、SecurityConfig、JwtUtils、MybatisPlusConfig、WebMvcConfig
│     │  ├─ entity/           8 个实体类
│     │  ├─ mapper/           8 个 Mapper
│     │  ├─ service/ + impl/  业务层
│     │  ├─ controller/       11 个控制器
│     │  ├─ common/           Result、PageResult、GlobalExceptionHandler、BusinessException
│     │  └─ dto/              请求参数对象
│     └─ resources/application.yml
├─ client/                    管理后台（Vue3 + Vite，端口 5173）
│  └─ src/
│     ├─ api/                 auth、user、pet、category、apply、notice、comment、stats、file
│     ├─ router/              路由与登录守卫
│     ├─ utils/               request.js（axios 封装）、auth.js
│     ├─ layout/              左侧渐变菜单 + 顶部 Header
│     ├─ components/          ChartCard.vue（ECharts 图表卡片）
│     ├─ views/               login、home、pet、category、apply、user、notice、comment、profile
│     └─ styles/index.scss    全局渐变 / 圆角 / 阴影 / 表格美化
├─ weixin/                    微信小程序（TypeScript + SCSS）
│  ├─ project.config.json
│  └─ miniprogram/
│     ├─ utils/               request.ts（wx.request 封装）、auth.ts
│     ├─ pages/               index、category、detail、login、publish、my、apply、notice、favorite
│     └─ app.ts / app.json / app.scss
├─ sql/pet_adoption.sql       MySQL8 建库 + 8 张表 + 测试数据
└─ D:\uploads\                上传文件根目录（图片统一存放）
```

---

## 二、环境要求

| 组件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17 及以上（推荐 21） | 后端运行所需 |
| Maven | 3.6 及以上 | 已在 `server/mvnw.cmd` 内置 wrapper |
| MySQL | 8.0 | 端口 **3308**，账号 root / 123456 |
| Node.js | 18 及以上 | 前端构建与小程序类型检查 |
| 微信开发者工具 | 最新稳定版 | 运行小程序端 |

---

## 三、快速启动

### 1. 导入数据库

```bash
# 方式一：命令行导入
mysql -h 127.0.0.1 -P 3308 -u root -p123456 --default-character-set=utf8mb4 < sql/pet_adoption.sql

# 方式二：使用 Navicat / DataGrip 连接 127.0.0.1:3308 后执行脚本
```

脚本会自动创建 `pet_adoption` 库、8 张表，并写入约 60 条测试数据。

### 2. 启动后端服务

```bash
cd server
mvn spring-boot:run          # 或使用 mvnw.cmd spring-boot:run
```

服务启动后监听 `http://localhost:8080`，上传文件根目录为 `D:\uploads`。

> 若本机 MySQL 未使用 3308 端口，请修改 `server/src/main/resources/application.yml` 中的 `spring.datasource.url`。

### 3. 启动管理后台

```bash
cd client
npm install
npm run dev                  # 访问 http://localhost:5173
```

Vite 已配置代理，`/api` 与 `/uploads` 会自动转发到 `http://localhost:8080`。

### 4. 运行微信小程序

1. 打开微信开发者工具 → 导入项目 → 选择 `weixin` 目录；
2. AppID 选择「测试号」或填入自己的 AppID；
3. 详情 → 本地设置 → 勾选 **不校验合法域名**（开发阶段必需）；
4. 后端地址在 `weixin/miniprogram/utils/request.ts` 中的 `BASE_URL` 修改。

---

## 四、默认账号

| 账号 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| `admin` | `123456` | 管理员 | 后台管理登录 |
| `zhangsan` | `123456` | 普通用户 | 小程序登录 |
| `lisi` | `123456` | 普通用户 | 小程序登录 |
| `wangwu` | `123456` | 普通用户 | 小程序登录 |
| `zhaoliu` | `123456` | 普通用户 | 小程序登录 |
| `sunqi` | `123456` | 普通用户 | 小程序登录 |

---

## 五、功能清单

### 5.1 管理后台（client）

- **数据首页**：4 张渐变统计卡片（用户总数 / 宠物总数 / 申请总数 / 领养成功率）+ 4 组 ECharts 图表（分类玫瑰饼图、近 7 日申请趋势组合图、申请状态环形图、城市 TOP10 横向柱状图）+ 最新申请表格 + 分类排行榜；
- **宠物管理**：关键字 / 分类 / 城市 / 状态多条件筛选，图片上传（多图 + 封面），上下架切换，删除；
- **分类管理**：卡片式分类维护，emoji 图标选择，排序设置；
- **领养审核**：状态标签页（待审核 / 已通过 / 已驳回 / 已取消 + 数量统计），审核通过后宠物自动置为「已领养」，支持填写审核备注；
- **用户管理**：分页 + 关键字 + 角色筛选，新增 / 编辑 / 启用禁用 / 删除；
- **公告管理**：类型（通知 / 活动 / 攻略）、封面上传、正文编辑、前台预览；
- **留言管理**：留言列表查看与删除；
- **个人中心**：资料修改、系统信息展示。

### 5.2 微信小程序（weixin）

- **首页**：渐变头部 + 搜索框 + 快捷入口 + 公告轮播 + 分类快捷筛选 + 宠物卡片列表（下拉刷新 / 上拉分页）；
- **分类页**：分类 / 性别 / 城市三维筛选，网格布局结果；
- **详情页**：图片轮播、健康档案（疫苗 / 绝育 / 驱虫）、详细介绍、用户留言与评论、收藏 / 取消收藏、领养申请弹窗；
- **登录 / 注册**：账号密码登录注册 + 微信一键登录（演示模式）；
- **发布页**：完整表单 + 多图上传（图片存 `D:\uploads`）+ 分类 / 性别 / 防疫选项；
- **我的**：个人资料卡、数据统计、我的发布 / 我的申请 / 我的收藏 / 领养公告入口、退出登录；
- **我的申请**：申请进度列表（状态配色），支持取消待审核申请；
- **我的收藏**：收藏宠物网格，支持取消收藏。

---

## 六、接口一览

所有接口统一返回 `{ code, msg, data }`，`code = 200` 表示成功。

### 认证
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录，返回 token 与用户信息 |
| POST | `/api/auth/register` | 注册普通用户 |
| GET | `/api/auth/info` | 获取当前登录用户信息 |

### 小程序 / 公共
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/pet/list` | 分页查询宠物（keyword / categoryId / city / gender / status） |
| GET | `/api/pet/detail/{id}` | 宠物详情（含相册、收藏状态） |
| GET | `/api/pet/my` | 我发布的宠物 |
| POST | `/api/pet` | 发布宠物 |
| PUT | `/api/pet/{id}` | 修改宠物 |
| PUT | `/api/pet/status/{id}` | 上下架切换 |
| GET | `/api/category/list` | 分类列表 |
| GET | `/api/notice/list` | 公告分页 |
| GET | `/api/notice/detail/{id}` | 公告详情 |
| GET | `/api/comment/list/{petId}` | 某宠物的留言 |
| POST | `/api/comment` | 新增留言 |
| GET | `/api/favorite/my` | 我的收藏 |
| POST | `/api/favorite/{petId}` | 收藏 |
| DELETE | `/api/favorite/{petId}` | 取消收藏 |
| GET | `/api/apply/my` | 我的申请 |
| POST | `/api/apply` | 提交领养申请 |
| PUT | `/api/apply/cancel/{id}` | 取消申请 |
| GET | `/api/stats/dashboard` | 首页仪表盘全部统计数据 |
| POST | `/api/file/upload` | 图片上传（存 `D:\uploads`） |

### 管理后台（需 role=admin）
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/user/page` | 用户分页 |
| POST/PUT/DELETE | `/api/admin/user/**` | 用户增删改、状态切换 |
| GET | `/api/apply/page` | 领养申请分页 |
| PUT | `/api/apply/audit/{id}` | 审核申请（status=1 通过 / 2 驳回） |

### 权限说明

- 白名单（无需登录）：`/api/auth/login`、`/api/auth/register`、`/uploads/**`、`/api/pet/list`、`/api/pet/detail/**`、`/api/category/list`、`/api/notice/list`、`/api/notice/detail/**`、`/api/stats/overview`；
- `/api/admin/**` 需要 `ROLE_ADMIN`；
- 其余接口需要有效 JWT（请求头 `Authorization: Bearer <token>`）。

---

## 七、数据库设计

| 表名 | 说明 | 主要字段 |
| --- | --- | --- |
| `sys_user` | 用户表（管理员与小程序用户共用，`role` 区分） | username / password / nickname / avatar / phone / role / status |
| `pet_category` | 宠物分类 | name / icon / description / sort |
| `pet` | 宠物信息 | name / category_id / age / gender / city / price / health / cover_img / vaccine / sterilized / deworm / status / view_count / user_id |
| `pet_image` | 宠物相册 | pet_id / url / sort |
| `adopt_apply` | 领养申请 | pet_id / user_id / reason / address / phone / experience / status / audit_remark |
| `notice` | 公告资讯 | title / content / cover / type / view_count / status |
| `comment` | 留言评论 | pet_id / user_id / content / status |
| `favorite` | 我的收藏 | user_id / pet_id |

- 申请状态：`0 待审核 / 1 已通过 / 2 已驳回 / 3 已取消`
- 宠物状态：`1 待领养 / 2 已领养 / 0 已下架`
- 所有表均带 `deleted` 逻辑删除标记，MyBatis-Plus 已配置全局逻辑删除。

---

## 八、构建与验证

```bash
# 后端编译
cd server && mvnw.cmd compile

# 前端构建
cd client && npm run build

# 小程序类型检查
cd weixin && npm run typecheck
```

三项均已验证通过。

---

## 九、常见问题

1. **后端启动报数据库连接失败**：确认 MySQL 已在 3308 端口启动，且 `application.yml` 中的账号密码与实际一致；
2. **小程序提示域名不合法**：在微信开发者工具中勾选「不校验合法域名」；
3. **图片无法显示**：确认 `D:\uploads` 目录存在且后端 `WebMvcConfig` 已把 `/uploads/**` 映射到该目录；
4. **登录提示 401**：Token 已过期或本地缓存被清除，重新登录即可；
5. **管理后台跨域报错**：开发环境已配置 Vite 代理与后端 `CorsConfig`，如自行部署请在 Nginx 中转发 `/api` 与 `/uploads`。

---

## 十、技术栈

- **后端**：Spring Boot 4.0.9、Spring Security、JWT (jjwt 0.12.6)、MyBatis-Plus 3.5.18、MySQL 8、Lombok、Validation
- **前端**：Vue 3、Vite、Element Plus、Vue Router、Axios、ECharts 5、Sass
- **小程序**：微信小程序原生框架 + TypeScript + SCSS
- **数据库**：MySQL 8（端口 3308，库名 `pet_adoption`）

---

> 作者：Java1234 · 本科毕业设计
