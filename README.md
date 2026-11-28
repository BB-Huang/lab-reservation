# 实验室设备预约系统

面向高校师生的实验室设备预约平台。把线下口头协调和 Excel 登记，变成线上可查、可约、可审批的流程，解决"设备谁在用、哪个时段空着、怎么避免抢重"的问题。

> 状态: 🚧 开发中（2026.11 起）

## 核心功能

- 用户注册 / 登录（JWT）
- 设备分类与设备的增删改查
- 按日期查询设备可用时段
- 创建预约（时间段冲突检测 + 并发抢约防护）
- 预约审批流（教师 / 管理员）
- 我的预约、取消预约
- 设备使用率与预约趋势统计（管理员）
- 水平越权（IDOR）防护

## 技术栈

| 层 | 选型 | 为什么 |
| --- | --- | --- |
| JDK | 17 (LTS) | Spring Boot 4 要求 Java 17+，长期支持 |
| 框架 | Spring Boot 4.1.2 | 主流后端框架，生态完整 |
| 安全 | Spring Security 7 + JWT（JJWT 0.13） | 无状态认证，面试高频 |
| ORM | MyBatis-Plus 3.5.18（boot4 starter） | 国内后端主流，减少样板代码 |
| 数据库 | MySQL 8.0 | 预约数据需要事务与强一致 |
| 缓存 / 锁 | Redis + Redisson | 分布式锁解决并发抢约 |
| 接口文档 | SpringDoc 3.2.0 (Swagger UI) | 自动生成，调试方便；Knife4j 暂不支持 Boot 4 |
| 部署 | Docker Compose | 一条命令起 MySQL + Redis |
| 构建 | Maven | — |

## 系统架构

```mermaid
flowchart LR
    C[浏览器 / Postman] --> S[Spring Security + JWT]
    S --> Ctl[Controller]
    Ctl --> Svc[Service]
    Svc --> M[(MySQL 8)]
    Svc --> R[(Redis / Redisson)]
```

## 核心设计

### 1. 时间段冲突检测

两个区间 `[a1, a2)` 与 `[b1, b2)` 重叠，当且仅当 `a1 < b2 AND a2 > b1`。因此一条 SQL 即可判断，不需要逐段比较:

```sql
SELECT COUNT(*) FROM reservation
WHERE device_id = #{deviceId}
  AND status IN ('PENDING', 'APPROVED')
  AND start_time < #{endTime}
  AND end_time   > #{startTime}
```

### 2. 并发抢约

两个请求同时通过检测，会双双落库。用 Redisson 分布式锁，锁粒度为 `lock:device:{deviceId}`，在锁内完成"检测 + 插入"；获取锁失败返回 409。

### 3. 水平越权防护

预约详情、取消等接口只能操作本人数据，管理员例外；越权访问返回 403。

## 本地运行

```bash
# 1. 起 MySQL + Redis
docker compose up -d

# 2. 建表（首次或重建环境时执行）
docker exec -i lab-mysql mysql --default-character-set=utf8mb4 \
  -uroot -proot123 lab_reservation < sql/schema.sql

# 3. 启动应用
./mvnw spring-boot:run

# 4. 接口文档：http://localhost:8080/swagger-ui.html
```

MySQL 首次启动需要 20–40 秒。默认账号仅用于本地开发，不要用于生产环境。

> **安全提醒:** `application.yml` 中的 JWT secret 仅用于本地开发，生产环境必须通过环境变量注入，不能提交到仓库。

## 已完成接口

| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| GET | `/api/ping` | 健康检查 | 公开 |
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录，返回 JWT | 公开 |
| GET | `/api/users/me` | 当前用户信息 | 登录 |
| GET | `/api/categories` | 分类列表 | 登录 |
| POST | `/api/categories` | 新增分类 | 管理员 |
| PUT | `/api/categories/{id}` | 修改分类 | 管理员 |
| DELETE | `/api/categories/{id}` | 删除分类 | 管理员 |
| GET | `/api/devices` | 设备分页（`page` / `size` / `categoryId` / `keyword`） | 登录 |
| POST | `/api/devices` | 新增设备 | 管理员 |
| GET | `/api/devices/{id}` | 设备详情 | 登录 |
| PUT | `/api/devices/{id}` | 修改设备 | 管理员 |
| DELETE | `/api/devices/{id}` | 删除设备 | 管理员 |
| GET | `/api/devices/{id}/availability?date=YYYY-MM-DD` | 某日已占用时段 | 登录 |
| POST | `/api/reservations` | 创建预约（冲突检测 + 分布式锁） | 登录 |
| GET | `/api/reservations/mine` | 我的预约（`page` / `size` / `status`） | 登录 |
| GET | `/api/reservations/{id}` | 预约详情 | 本人或管理员 |
| PUT | `/api/reservations/{id}/cancel` | 取消预约 | 本人 |
| GET | `/api/reservations/pending` | 待审批列表 | 教师 / 管理员 |
| PUT | `/api/reservations/{id}/approve` | 审批通过 | 教师 / 管理员 |
| PUT | `/api/reservations/{id}/reject` | 审批驳回 | 教师 / 管理员 |

除公开接口外，请求头需带 `Authorization: Bearer <token>`。

## 里程碑

| 周 | 内容 | 状态 |
| --- | --- | --- |
| 第 1 周 | 环境 + 用户模块（注册 / 登录 / JWT） | ✅ 完成 |
| 第 2 周 | 设备 + 分类 + 权限 | ✅ 完成 |
| 第 3 周 | 预约 + 冲突检测 + 分布式锁 + 审批 | ✅ 完成 |
| 第 4 周 | 统计 + 部署 + 压测 + 文档 | ⬜ |
