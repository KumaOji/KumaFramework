# 日志审计实验

本实验复用 `kuma-boot-starter-audit-log` 的真实 `@AuditLog`、Spring AOP 切面、操作人 Provider 和存储接口。每次运行创建并关闭独立 Spring 上下文，报告保留审计记录快照和检查结果，无需 Nacos、数据库、WSL 或 Agent。

## 运行

在仓库根目录运行完整实验：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:auditLogLab
```

使用 `-PauditLesson=success`、`failure`、`content`、`proxy` 或 `storage` 运行单项，例如：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:auditLogLab -PauditLesson=content
```

也可直接运行 `com.kuma.cloud.lab.auditlog.AuditLogLearningDemo.main()`。控制台的“日志审计实验”组提供 HTTP 入口与学习卡片。

## 实验步骤与预期

| 实验 | 操作 | 观察结果 |
|---|---|---|
| success | 通过代理确认 `ORDER-001` | 新增成功审计，原返回值不变；`bizId=ORDER-001`，SpEL 描述为“确认订单:ORDER-001” |
| failure | 模拟订单已经确认 | 新增 `success=false` 记录，保存异常消息且不保存成功返回值；原异常继续抛出 |
| content | 更新教学密钥，再传入400字符文本 | 密钥操作的 `params/result` 为空；普通大文本保留前160字符并追加 `...(truncated)` |
| proxy | 对照同对象自调用与外部代理调用 | `this.confirm()` 无新增审计；经代理调用相同方法新增一条 |
| storage | 模拟 `AuditLogStore.save()` 失败 | 框架记录诊断错误，业务仍返回成功；报告确认没有保存记录 |

完整实验返回5条审计记录：成功1条、业务失败1条、内容策略2条、代理调用1条；存储故障没有保存记录。报告中的 `steps` 解释各步，`records` 是实际切面交给存储的快照，`checks` 全部为 true。

教学服务示例：

```java
@AuditLog(module = "lab.audit.orders", type = OperateType.UPDATE,
        bizId = "#id", description = "'确认订单:' + #id")
public String confirm(String id) { return "CONFIRMED:" + id; }
```

`recordParams=false, recordResult=false` 用于关闭敏感内容记录。截断只限制长度，不会识别或脱敏密码、Token 等数据。

## HTTP 入口

启动 Lab 后使用以下 POST 接口，路径前缀沿用应用配置的 `/api`：

| 路径 | 用途 |
|---|---|
| `/api/lab/audit-log/scenario` | 完整实验 |
| `/api/lab/audit-log/success` | 成功记录与 SpEL |
| `/api/lab/audit-log/failure` | 失败记录与异常传播 |
| `/api/lab/audit-log/content` | 内容开关和截断 |
| `/api/lab/audit-log/proxy` | 代理边界 |
| `/api/lab/audit-log/storage` | 审计存储故障 |

失败与存储故障是实验内部的受控场景，检查通过后 HTTP 仍返回成功实验报告，不能把外层 HTTP 状态当作被审计业务的结果。

## 记录、日志与边界

- `RecordingStore` 在本次运行内保存不可变快照，同时委托框架 `Slf4jAuditLogStore` 输出到 `com.kuma.boot.auditlog.AUDIT`。没有写入正式订单或审计数据库表，报告不会混入其他实验的记录。
- 操作人由实验专用 `AuditOperatorProvider` 固定为 `lab-user / Lab Learner`，用于理解扩展接口；它不读取访问 Lab 的用户，也不代表认证成功。
- 本实验调用独立服务代理，未模拟内部 HTTP 请求，因此审计条目没有内部请求 URI/IP 证据。经 Lab HTTP 入口运行时，也不应将外层实验请求误认为每步业务请求。
- 使用 `async=false` 保证报告生成前已完成审计；它不演示异步队列、持久化可靠性或事务提交一致性。存储故障时业务继续的策略可能导致审计丢失，应按业务要求选择。
- AOP 必须经过 Spring 代理；手动 `new` 和同对象自调用都不能直接依赖该切面。将需审计的用例放到独立 Bean，通过注入的代理调用。
- HTTP 入口沿用运行中 Lab 的日志配置。若已启用 Loki，可在 Grafana Explore 查询 `{service_name="kuma-cloud-lab"} | json | logger_name="com.kuma.boot.auditlog.AUDIT"`；命令行实验默认观察本地日志与返回报告，不承诺远程采集。

真实订单与 Grafana 联动示例见 reference 的 `OBSERVABILITY.md`；本实验聚焦审计机制，不会自动启动或修改监控服务。
