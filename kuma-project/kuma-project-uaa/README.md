# UAA 本地调试

与 Blog、Gateway 共用 Loki、Prometheus、Grafana、OTel Collector 和 SkyWalking，
联合启动与验证见 [三服务可观测性说明](../../../k8s/local/monitoring/SERVICES.md)。

从仓库根目录单独启动 UAA：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-services-local.ps1 -Services uaa -Build -Restart
```

业务端口 `33336`；独立管理端口 `19002`，仅绑定 Windows 的 WSL NAT 网卡；
IDEA Remote JVM Debug 连接 `localhost:5006`。数据库使用 WSL MySQL 的 `kuma_uaa`。
OAuth issuer 为 `http://localhost:33336`，网关访问前缀为 `/uaa`。

未登录或权限不足的 JSON 接口保留原有 `Result` 错误结构，HTTP 状态分别为
401 和 403，便于客户端与监控识别拒绝访问；浏览器登录页跳转保持原有行为。
日志与 PID 位于 `logs/uaa-local/`。


## 框架配置导入

沿用 `kuma-project-reference` 的方式：启动类使用 `@SpringBootApplication`，仅扫描本模块业务包；
`config` 按能力通过 `@ImportAutoConfiguration` 显式导入 Kuma 配置，框架属性由对应配置的
`@EnableConfigurationProperties` 绑定，不再扫描整个 `com.kuma.boot`。

启动入口使用 `StartupSpringApplication`，通过 `spring.config.name=application,bootstrap`
加载本地配置和 Nacos Config Data 导入，不再依赖旧的 Spring Cloud bootstrap 上下文。

数据源、MyBatis、线程池、Web、Redis、日志、Actuator 和 Prometheus 按能力分别配置。
`FrameworkSecurityConfiguration` 导入安全属性和方法权限支持，并显式注册 `AuthorizeCheckService`；
HTTP 安全链和 JWT 解码由本模块配置管理。


## Local tracing and Kuma Console

Kuma Console starts blog, uaa and gateway through `scripts/start-services-local.ps1`.
The default observability launch loads the SkyWalking Java Agent before `-jar`, uses
`kuma-cloud-blog`, `kuma-cloud-uaa` and `kuma-cloud-gateway` as service names, and reports
to `localhost:11800`. Set `SKYWALKING_AGENT_JAR` (or `-SkywalkingAgent`) to an extracted
Agent JAR. The local fallback is `kuma-project/kuma-project-lab/build/tools/skywalking-agent/skywalking-agent.jar`;
the adjacent `config`, `plugins` and `activations` directories must exist. Override the OAP
address with `-SkywalkingBackend`. Use `-WithoutSkywalking` for OTEL-only startup or
`-WithoutObservability` to skip the observability profile and Agent.

OTEL uses the existing Spring Boot 4 starter and exports through `localhost:4318/v1/traces`.
Console logs use the shared framework MDC text layout; Loki continues to receive JSON.
Servlet applications import `WebMdcAutoConfiguration` and the shared SkyWalking task decorator.
Gateway preserves correlation fields in exchange/Reactor context and temporarily restores MDC
only while logging completion, including reactive failures, without leaving it on Netty threads.
The `kmc-trace-id`, `kmc-tenant-id` and `kmc-request-version` headers are propagated to downstream
services. Klog remains an unimplemented reserved field. A logged request tenant is correlation
metadata, not proof of an authenticated tenant.

After these changes, rebuild and restart the service from Kuma Console. Logs are in
`logs/<service>-local/stdout.log`; Agent diagnostics are in `logs/<service>-local/skywalking/`.
