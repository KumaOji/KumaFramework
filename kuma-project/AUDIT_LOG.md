# Blog、UAA 与 Gateway 操作审计

三者复用 `kuma-boot-starter-audit-log` 的记录模型、存储接口和专用 logger：`com.kuma.boot.auditlog.AUDIT`。审计以 JSON 写入现有日志链路；启用 `observability` profile 后沿用 Loki 与 Grafana 配置。

## 覆盖范围

| 应用 | 审计操作 | 实现 |
|---|---|---|
| Blog | 文章、项目、音乐、友链的创建/修改/删除；发布、审核、留言管理、聊天室管理、权限授予/撤销、MFA、刷新与退出 | Controller 上的 `@AuditLog` |
| Blog | OAuth2 登录成功、失败及回调中明确拒绝的结果 | 认证处理器显式写入结果 |
| UAA | 用户、角色、权限、客户端管理，密码修改/重置、角色分配、强制下线、MFA 绑定/解绑 | Controller 上的 `@AuditLog` |
| UAA | 登录成功/失败、统一退出 | 保留原登录审计落库，并输出统一操作审计日志 |
| Gateway | POST/PUT/PATCH/DELETE、退出请求，以及失败的读取请求 | WebFilter 在完成、错误或取消时记录网络结果 |

Blog/UAA 的普通列表、阅读量和播放量等高频读取/计数不使用业务审计。Gateway 的 Actuator 请求排除在操作审计之外，仍可使用既有访问日志和指标。

## 身份与结果

- Blog/UAA 的操作人取自 `SecurityContextHolder` 中已认证主体的名称，作为 `operatorId/operatorName`，不信任客户端的 `X-User-Id` 或 `X-User-Name`。切面在调用前保存身份，修改密码清空上下文后仍可追溯操作人。
- 登录失败记录 `success=false`，不把尝试登录的用户名当作已验证操作人；UAA 既有登录表继续保留其原有登录尝试信息。
- Gateway 在 Spring Security 的认证 exchange 包装之后提取已验证主体，写入当前请求的属性；外层审计过滤器在终止信号中读取，避免使用 Netty 线程的 ThreadLocal 作为身份来源。鉴权未通过时操作人为空。
- 业务方法抛异常、返回 `Result.fail(...)`、`Result.success(false)` 或 `false` 时记为失败。切面位于方法权限检查之外，因此进入代理后被权限拒绝的操作也会记录。校验和安全过滤器在调用方法前拒绝的请求不产生该方法的审计记录。
- Gateway 描述中的 `HTTP POST 201` 等信息表示网络状态；取消记为499，异常使用对应 HTTP 状态或500。HTTP 200 内的业务失败以 Blog/UAA 的业务审计为准，网关不读取响应正文来判断业务含义。

## 内容策略

三个应用使用 `MetadataOnlyAuditLogStore`：关闭入参、出参记录，并将异常消息替换为 `operation_failed`。正文、密码、JWT、Refresh Token、客户端密钥、MFA 密钥与动态码均不作为审计载荷采集。

业务注解仅指定模块、操作类型、固定动作名称和必要的目标标识；授权记录包含目标用户/权限 ID。创建接口尚无目标 ID 时可不设置 `bizId`。Gateway 只记录路径、路由 ID、状态、操作人、IP、耗时及链路标识，不读取请求/响应正文，不记录查询字符串、Cookie、Authorization 或 User-Agent。

Blog 的 OAuth2 失败诊断也改为只输出路径和异常类型，避免回调查询中的 code/state 进入日志。审计存储失败会记录通用诊断，业务响应保持原行为；日志输出不等同于可靠持久化或事务提交证明。

## 配置与查看

六份 dev/prod Nacos 配置模板已包含：

```yaml
kuma:
  boot:
    audit-log:
      enabled: true
      async: false
```

`enabled=false` 可关闭操作审计；Blog/UAA 的认证回调使用空存储，UAA 原有登录表审计仍保留。Servlet 切面支持异步输出时的 MDC 捕获与恢复；当前应用模板选择同步生成日志，远端发送沿用日志组件的批量机制。

配置源位于 `.nacos-tmp/base/kuma-cloud-{blog,uaa,gateway}-{dev,prod}.yaml`。按应用已有流程发布配置并重启更新后的服务。本次代码接入不代表运行中服务已重启或线上 Nacos 已更新。

在 [Grafana Explore](http://localhost:3000/explore) 选择 Loki 查询三个服务的审计：

```logql
{service_name=~"kuma-cloud-(blog|uaa|gateway)"}
  | json
  | logger_name="com.kuma.boot.auditlog.AUDIT"
```

展开日志的 `message` 查看 `module`、`type`、`operatorId`、`bizId`、`success` 等字段；启用链路采集时可通过外层 `traceId` 跳转 SkyWalking。业务审计模块以 `blog.`、`uaa.` 开头，网关模块为 `gateway.http`。同一次请求的网关记录和下游业务记录表达不同层面的结果，可按 Trace ID 关联。
