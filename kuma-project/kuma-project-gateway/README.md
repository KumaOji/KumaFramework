# Gateway 可观测性

Gateway 使用 Spring Boot 4 原生 OpenTelemetry/Micrometer 自动装配：

- traces：Gateway → OTel Collector → SkyWalking；传播 W3C `traceparent`，查询 SkyWalking 的 Zipkin Trace 页面。
- logs：异步 Loki4j → Loki；控制台与 Loki 使用 JSON，访问日志含 `traceId`、`spanId`、路由、状态和耗时，不记录查询串、请求体或 Authorization。
- metrics：Prometheus 读取独立管理端口 `/actuator/prometheus`，包括 JVM、HTTP 请求与 Gateway 路由指标和耗时直方图。
- Grafana：`Kuma / Kuma Gateway` 面板展示请求速率、错误率、P95、JVM、路由指标和日志。
- Alertmanager：Prometheus 管理的目标失联、持续 5xx 比例超过 5%、持续 P95 超过两秒规则；外部通知渠道仍需配置。

每个应用采用一套自动追踪方案；本 Gateway 使用 OTel SDK，无需再挂 Java Agent。

## WSL 启动

先完成 WSL 监控组件部署，再在仓库根目录构建：

```powershell
.\gradlew.bat :kuma-project:kuma-project-gateway:test :kuma-project:kuma-project-gateway:bootJar
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/IDEA_project/KumaFramework/scripts/start-gateway-observability-wsl.sh
```

启动脚本默认读取 `gateway-2026.10.jar`，可用第一个参数指定其他 bootJar。
服务 `kuma-gateway-local` 使用 WSL 用户 `kuma` 运行，读取现有 Nacos dev 配置；可编辑 `/etc/kuma/gateway-local.env` 设置 Nacos 凭据和观测参数后重启服务。
运行 jar 按 SHA256 复制到 `/var/lib/kuma/gateway`，后续 Gradle 构建不会破坏正在运行的实例。

| 用途 | 地址 |
|---|---|
| Gateway | http://localhost:18080 |
| 管理健康 | http://localhost:18081/actuator/health/readiness |
| Prometheus 指标 | http://localhost:18081/actuator/prometheus |
| Grafana 面板 | http://localhost:3000/d/kuma-gateway |
| SkyWalking | http://localhost:12880 |

WSL 启动脚本会注册当前节点 IP 到 `gateway-local` EndpointSlice，供集群里的 Prometheus 抓取主机上运行的 Gateway。适用于当前单节点 WSL K3s。
本地默认采样 10%，测试脚本采样 100%。Nacos 中若没有 UAA/Blog 实例，相关业务路由会走已有降级逻辑；不影响 Gateway 的观测接入。

## IDE / 其他环境

启用 `dev,observability` profile，并设置 `KUMA_CONFIG_PROFILE=dev`，使 Nacos dataId 保持 `*-dev.yaml`。
共享 Nacos 配置的优先级较高，需用环境变量明确覆盖管理端口与追踪开关，WSL 启动脚本已包含这些值：

```text
SPRING_PROFILES_ACTIVE=dev,observability
KUMA_CONFIG_PROFILE=dev
MANAGEMENT_SERVER_PORT=18081
MANAGEMENT_TRACING_ENABLED=true
MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT=http://localhost:4318/v1/traces
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,prometheus
SPRING_REACTOR_CONTEXT_PROPAGATION=auto
GATEWAY_LOKI_URL=http://localhost:3100/loki/api/v1/push
GATEWAY_ENVIRONMENT=local
```

管理端口默认只监听 127.0.0.1；WSL 采集使用 `MANAGEMENT_SERVER_ADDRESS=0.0.0.0`。
应用层仅在管理端口允许健康、info 和 Prometheus；业务端口拒绝访问指标。管理端口需保持在受控网络内。
追加 `no-loki` profile 可关闭 Loki 上报并保留 JSON 控制台日志。日志队列有界且异步，后端持续不可用或队列满时可能丢失日志。

服务器目前仅部署了 Collector 和 SkyWalking；本次集成及监控 YAML 在 WSL 验证，尚未部署 Gateway 变更到服务器。
以后启用生产接入时使用 `prod,observability` 和 `KUMA_CONFIG_PROFILE=prod`，将两个导出地址改为集群 Service，并调整管理端口探针和 Service。Loki/Prometheus/Grafana/Alertmanager 需先在该环境部署。

## 验证

```powershell
wsl -d Ubuntu-24.04 -u root -- python3 /mnt/d/IDEA_project/KumaFramework/kuma-project/kuma-project-gateway/scripts/check-observability-wsl.py /mnt/d/IDEA_project/KumaFramework/kuma-project/kuma-project-gateway/build/libs/gateway-2026.10.jar
wsl -d Ubuntu-24.04 -u root -- python3 /mnt/d/IDEA_project/KumaFramework/scripts/check-monitoring-wsl.py
```

端到端脚本运行一个隔离的 Gateway（18180/18181）和临时上游，验证真实转发、traceparent、关联响应、Loki 日志和 SkyWalking server/client spans，完成后停止临时进程。
告警规则测试文件为 `scripts/gateway-alerts.test.yaml`，在 Prometheus 容器中用 `promtool test rules` 执行，规则路径为 `/etc/prometheus/rules.yml`。
