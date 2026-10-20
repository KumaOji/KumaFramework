# Blog 本地调试

Windows 运行 Blog，连接 Ubuntu-24.04 WSL 中的 Nacos、MySQL、Redis 和 Qdrant。
连接凭据与业务配置由 Nacos 的 `base` 命名空间、`DEFAULT_GROUP` 下的
`kuma-shared-dev.yaml`、`kuma-cloud-blog-dev.yaml` 提供；本地 OAuth 地址在
`src/main/resources/bootstrap-local.yml` 中配置。

在仓库根目录启动：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-blog-local.ps1 -Build
```

已构建时可省略 `-Build`。脚本自动从 WSL NAT 路由获取 Windows 注册地址，
避免 Nacos 自动选择 `Default Switch` 网卡后，WSL 网关无法访问 Blog。
脚本读取当前 WSL IP，覆盖 Nacos、MySQL、Redis 和 Qdrant 地址，不修改共享配置。
适用于当前 WSL NAT 网络；更改为 mirrored 网络时需重新检查地址选择。

| 用途 | 地址 |
|---|---|
| Blog API | http://localhost:9000/api/article/list |
| 独立管理端口 | Windows WSL 网卡地址的 `19001` 端口 |
| 网关访问 | http://localhost:18080/blog/article/list |
| IDEA Remote JVM Debug | localhost:5005 |

IDEA 新建 **Remote JVM Debug**，选择 **Attach to remote JVM**，主机 `localhost`、
端口 `5005`，连接后可在控制器或服务方法设置断点。调试端口只监听 Windows 本机。
传入 `-DebugPort 0` 可禁用调试，`-Distribution` 可指定 WSL 发行版。

日志和 PID 保存在 `logs/blog-local/`。停止本次启动的实例：

```powershell
Stop-Process -Id (Get-Content logs/blog-local/process-id.txt)
```

启动前需保证 WSL 中间件就绪，WSL Gateway 服务 `kuma-gateway-local` 已启动。
OAuth 登录另需启动 UAA（`localhost:33336`）；健康检查与公开文章接口可独立验证。
本脚本默认启用 `dev,observability`，日志进入 Loki，调用链经 OTel Collector
进入 SkyWalking，Prometheus 从独立管理端口采集指标。传入 `-WithoutObservability`
可只使用 `dev`。三服务联合启动、面板与验证见 [可观测性说明](../../../k8s/local/monitoring/SERVICES.md)。

## Nacos 配置

Blog 不保留 `application*.yml`。`bootstrap.yml` 负责应用名、运行环境和 Nacos 连接，
默认开关和业务配置由 `kuma-cloud-blog-dev.yaml` / `kuma-cloud-blog-prod.yaml` 提供。
启用 `observability` profile 时，`bootstrap-observability.yml` 额外导入
`kuma-cloud-blog-observability-dev.yaml` / `kuma-cloud-blog-observability-prod.yaml`。
以上 Data ID 均位于 `base` 命名空间、`DEFAULT_GROUP` 分组。

`KUMA_CONFIG_PROFILE` 指定 Nacos 配置环境，未设置时使用当前 active profile；
同时启用多个 profile 时必须显式设置为 `dev` 或 `prod`，现有启动脚本和部署清单已设置。
Nacos 导入为必需项，配置中心连接失败时启动失败。
配置源文件位于仓库 `.nacos-tmp/base/`，dev 配置通过 `scripts/publish-nacos-dev.sh` 发布，
prod 配置通过 `Publish Nacos Prod Configs` 工作流发布。先发布配置，再重启 Blog。


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
