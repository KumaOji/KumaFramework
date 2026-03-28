# Blog 上公网前检查清单

> 每次部署前过一遍，全部打勾后再放流量。

---

## 一、必须修复的代码问题

### 1. 移除 `repkgPath` 用户可控字段
- [ ] `RepkgRequest.java` 中删除 `repkgPath` 字段
- [ ] `ToolServiceImpl.java` 中始终调用 `getDefaultRepkgPath()`，不再从请求中读取路径
- [ ] 确认 `/tool/repkg` 接口不对公网用户暴露（或仅内网访问）

### 2. 公开写入接口防刷
- [ ] `/article/{id}/view`、`/article/{id}/like`、`/article/{id}/comment` 加 IP 限速
- [ ] `/music/{id}/play`、`/music/{id}/like` 加 IP 限速
- [ ] 推荐在 Nginx 层用 `limit_req_zone` 实现，无需改动 Java 代码

---

## 二、Nginx / 反向代理配置

### 2.1 HTTPS
- [ ] 已配置 SSL 证书（推荐 Let's Encrypt）
- [ ] HTTP 自动跳转 HTTPS（`return 301 https://$host$request_uri`）
- [ ] 证书有效期 > 30 天，已设置自动续期

### 2.2 可信代理 IP（修复限速绕过）
```nginx
# 在 nginx.conf 中声明可信代理，防止 X-Forwarded-For 伪造
set_real_ip_from 127.0.0.1;
set_real_ip_from 10.0.0.0/8;      # 内网段，按实际修改
real_ip_header X-Forwarded-For;
real_ip_recursive on;
```
- [ ] 已配置 `set_real_ip_from`
- [ ] 已配置 `real_ip_header X-Forwarded-For`

### 2.3 请求限速（防刷）
```nginx
# 在 http{} 块中定义
limit_req_zone $binary_remote_addr zone=api_write:10m rate=10r/m;

# 在 location 块中应用
location ~* ^/api/(article|music)/[^/]+/(view|like|comment|play)$ {
    limit_req zone=api_write burst=5 nodelay;
    proxy_pass http://127.0.0.1:9000;
}
```
- [ ] 已配置写接口限速
- [ ] 已配置登录接口额外限速（建议 5r/m）

### 2.4 请求体大小限制
```nginx
client_max_body_size 510m;   # 略大于 Spring 的 500MB 设置
```
- [ ] 已在 Nginx 设置 `client_max_body_size`

### 2.5 安全响应头
```nginx
add_header X-Content-Type-Options "nosniff" always;
add_header X-Frame-Options "DENY" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
```
- [ ] 已配置安全响应头

---

## 三、环境变量（application-prod.yml 依赖项）

| 环境变量 | 说明 | 示例 |
|----------|------|------|
| `ADMIN_INITIAL_PASSWORD` | Admin 初始密码，**必须设置** | `MyStr0ng!Pass` |
| `mysql_url` | MySQL JDBC URL | `jdbc:mysql://db:3306/blog` |
| `mysql_username` | MySQL 用户名 | `blog_user` |
| `mysql_password` | MySQL 密码 | `...` |
| `redis_host` | Redis 地址 | `127.0.0.1` |
| `redis_port` | Redis 端口 | `6379` |
| `redis_database` | Redis 数据库编号 | `0` |
| `KAFKA_BROKERS` | Kafka 地址（可选） | `kafka:9092` |

- [ ] `ADMIN_INITIAL_PASSWORD` 已设置，且密码强度足够（大小写+数字+符号）
- [ ] MySQL 相关环境变量已设置
- [ ] Redis 相关环境变量已设置
- [ ] 确认 Kafka 是否实际使用，不使用则在 prod 配置中 exclude Kafka 自动配置

---

## 四、数据库

- [ ] 生产数据库已单独创建（不与开发共用）
- [ ] 数据库账号仅有 blog 库的读写权限，无 `DROP`/`CREATE DATABASE` 权限
- [ ] MySQL 未对公网暴露（只允许应用服务器内网访问）
- [ ] 已手动执行 `sql/blog_schema.sql` 完成建表（`sql.init.mode: never`，不会自动执行）
- [ ] 确认无 admin 默认密码数据（`blog_data.sql` 中的 `admin123` hash 仅用于开发）

---

## 五、Redis

- [ ] Redis 已设置 `requirepass` 访问密码
- [ ] Redis 未对公网暴露（bind 127.0.0.1 或内网 IP）
- [ ] 如使用云 Redis，已配置白名单 IP

---

## 六、Spring Boot 配置验证

- [ ] 启动 profile 为 `prod`（`--spring.profiles.active=prod` 或环境变量 `SPRING_PROFILES_ACTIVE=prod`）
- [ ] `blog.reset-admin-password: false`（prod 配置已设置，再次确认）
- [ ] `springdoc.api-docs.enabled: false`（Swagger 已关闭）
- [ ] `spring.sql.init.mode: never`（不自动执行 SQL）
- [ ] `management.endpoints.web.exposure.include: health`（Actuator 仅暴露 health）
- [ ] `blog.cookie-secure: true`（Cookie 强制 HTTPS）

---

## 七、文件系统 / 资源目录

- [ ] `/data/music` 目录已创建
- [ ] `/data/music` 目录权限：应用用户可读写，其他用户不可访问（`chmod 750`）
- [ ] RePKG 工具目录已配置（若不使用 `/tool/repkg` 接口可跳过）
- [ ] 日志目录已创建，磁盘空间充足

---

## 八、CORS（若前端与后端不同源）

若前端部署在独立域名（如 `https://blog.example.com`），后端 API 在 `https://api.example.com`：
- [ ] 在 `WebMvcConfig.java` 中添加 `addCorsMappings`，**显式配置** `allowedOrigins`
- [ ] 绝不使用 `allowedOrigins("*")`（会导致 Cookie 无法发送）
- [ ] 确认 `allowCredentials(true)` 已启用

若前端与后端同源（如 Nginx 反代到同一域名下的不同路径），此项跳过。

---

## 九、启动后冒烟测试

```bash
# 1. 健康检查
curl https://your-domain.com/api/actuator/health
# 期望: {"status":"UP"}

# 2. 登录
curl -X POST https://your-domain.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"你的密码"}' -c cookies.txt
# 期望: 返回 token，Set-Cookie 包含 blog_token

# 3. Swagger 应已关闭（返回 404）
curl https://your-domain.com/api/v3/api-docs
# 期望: 404 或 403

# 4. 公开文章列表
curl https://your-domain.com/api/article/list
# 期望: 正常返回

# 5. 无 token 访问受保护接口（应返回 401）
curl https://your-domain.com/api/permission
# 期望: 401

# 6. 登录限速（连续 6 次错误密码，第 6 次应返回限速提示）
for i in {1..6}; do
  curl -X POST https://your-domain.com/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"wrong"}'
done
# 期望: 第 6 次返回 "登录尝试过于频繁"
```

- [ ] 健康检查通过
- [ ] 登录功能正常，Cookie 设置正确（HttpOnly、Secure、SameSite=Strict）
- [ ] Swagger 已关闭
- [ ] 未认证访问受保护接口返回 401
- [ ] 登录限速生效

---

## 十、上线后监控

- [ ] 应用日志（`logs/` 目录）收集到日志平台或定期归档
- [ ] 配置告警：5xx 错误率突增、登录频繁失败
- [ ] 定期检查磁盘使用（音乐文件 + 日志 + RepKG 临时文件）
- [ ] 定期轮换 `ADMIN_INITIAL_PASSWORD`（登录后在系统内修改密码）

---

*最后更新：2026-03-28*
