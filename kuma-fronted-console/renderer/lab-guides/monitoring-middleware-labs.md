# 六个可观测性中间件实验

这组实验使用现有 WSL 部署，实际调用组件 API，输出可查回的证据与 `checks`。
无需先启动 LabApplication；学习卡片可直接复制命令。Lab 启动后也提供对应 HTTP 接口。
每次使用独立 runId；只新增自己的日志、调用链和一分钟测试告警。

## UI 位置

| 组件 | 本地入口 | 到哪里观察 |
|---|---|---|
| Grafana | http://localhost:3000 | Dashboards → Kuma → Kuma Services；直接打开 http://localhost:3000/d/kuma-services |
| Loki | http://localhost:3000/explore | Explore 选择 **Loki** 数据源，输入报告中的 `logql`；Loki 本身是 API，不提供日志浏览 UI |
| Prometheus | http://localhost:9090 | 查询页输入 `up`；Targets 查看目标健康，Rules/Alerts 查看规则和告警 |
| Alertmanager | http://localhost:9093 | Alerts 按 `alertname=KumaLabAlert` 筛选；Silences 查看静默配置 |
| SkyWalking | http://localhost:12880 | 本实验使用 **Zipkin Trace / Lens** 按 traceId 查询；已有 Java Agent 实验使用原生 Trace 页面 |
| OTel Collector | OTLP HTTP `localhost:4318/v1/traces`，gRPC `localhost:4317` | 没有独立业务 UI；下游 SkyWalking 查回证明管道畅通，WSL `kubectl logs` 查看 Collector 日志 |

控制台的六张学习卡片也提供 UI 按钮和页面位置。这里的 localhost 是 Windows 本机，
WSL 的 systemd 转发服务须已运行。Grafana 用户 `admin` 的本地随机密码在 WSL
`/home/kuma/.config/kuma-observability/grafana-admin-password`；启动脚本读取到进程环境，
不写入实验报告、命令行或学习卡片。

## 一次运行全部实验

在仓库根目录：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-monitoring-labs-wsl.ps1
```

单独运行时添加 `-Component loki`、`prometheus`、`alertmanager`、`otel`、`skywalking` 或 `grafana`。
也可直接使用 Gradle：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:monitoringLab -PmonitoringComponent=loki
```

或者运行 `com.kuma.cloud.lab.observability.MonitoringMiddlewareLabs.main()`，参数为组件名；
无参数时运行全部。全部运行输出六份报告，所有 `checks` 应为 true。
实际组件不可用、协议返回错误或证据查回失败时，实验会失败，不将离线状态伪装成成功。

## 1. Loki：写入与查回

1. POST `/loki/api/v1/push` 写入一条 JSON 日志，时间使用 Unix 纳秒字符串。
2. 标签固定为 `service_name=kuma-lab-monitoring` 与 `environment=wsl`，runId 在日志正文中。
3. GET `/loki/api/v1/query_range` 用标签选择器和 runId 文本过滤查回本次日志。
4. 打开 Grafana Explore → Loki，复制报告 `logql`；时间选择最近五分钟。

预期：写入返回 204，`written`、`queriedBack` 为 true；UI 中看到相同 runId。
理解标签筛选与正文筛选的区别：高变化的 runId 不应当成为每条日志的新标签。

## 2. Prometheus：即时与范围查询

1. 查询 `up`，观察 `resultType=vector`，每个目标只有当前求值时刻的一个样本。
2. 对最近一分钟按 15 秒步长查询 `up`，观察 `resultType=matrix` 和每条序列的样本列表。
3. 在 UI 查询页重复表达式；到 Targets 对照 `job`、`instance` 与采集错误。

预期：`instantVector`、`rangeMatrix`、`hasTargets` 为 true。`up=0` 是真实失联证据，
并不代表查询 API 失败；实验保留该值供排障，不强行要求所有业务服务已经启动。

可继续观察真实业务指标：

```promql
sum by (application) (rate(http_server_requests_seconds_count{application=~"kuma-cloud-(blog|uaa|gateway)"}[5m]))
histogram_quantile(0.95, sum by (application, le) (rate(http_server_requests_seconds_bucket{application=~"kuma-cloud-(blog|uaa|gateway)"}[5m])))
```

counter 是累计值，速率要使用 `rate()`；直方图计算 P95 要按 `le` 聚合桶。
刚启动、没有请求或窗口内样本不足时，查询可能为空，不能解释为延迟为零。

## 3. Alertmanager：短时告警

1. 向 `/api/v2/alerts` 发送 `KumaLabAlert`，包含独立 run_id、注解与 startsAt/endsAt。
2. 按 run_id 查询 `/api/v2/alerts`，证明该条实验告警已被接收。
3. 立即打开 Alerts，以 `alertname=KumaLabAlert` 筛选，查看 labels、annotations 与过期时间。

预期：`accepted`、`queriedBack`、`boundedLifetime` 为 true，告警约一分钟后过期。
本地 receiver 当前为空接收器，不发送外部通知；实验只验证 Alertmanager 的接收与查询，
不宣称已经触发 Prometheus 规则或成功送达邮件。真实规则可在 Prometheus Rules/Alerts 查看。
实验不会修改路由、静默或真实服务告警。

## 4. OTel：SDK → Collector → 后端

使用已有真实 SDK 实验生成父子 span、W3C 传播、异步断链对照、采样与本地 metrics/logs。
将 traces 通过 OTLP HTTP 导出，随后到 SkyWalking 验证同一 trace 下六个 span ID 全部存在。
报告中的孤立异步 span 用于对照断链，不属于六个关联 span。

预期：`exportedAndStored`、`sdkChecksPassed` 为 true。Collector 接收成功只是中间步骤，
最终查回才能证明下游存储完成。当前 Collector 只启用 traces 管道；本实验的 metrics/logs
仍由内存 exporter 捕获，不能在 Loki 或 Prometheus 中假装查到它们。
后端异步存储最多等待90秒，控制台对这两个调用链实验允许120秒请求时间；其他实验仍为30秒。

Collector 不提供业务 UI。在 WSL 可查看：

```powershell
wsl -d Ubuntu-24.04 -u root -- kubectl -n base logs deployment/otel-collector --tail=50
```

## 5. SkyWalking：按 ID 查回 span

独立执行一次 OTLP 实验，返回真实 `traceId` 和 `storedSpans`。打开 SkyWalking 的
Zipkin Trace / Lens 页面，以 traceId 查询，展开树结构并比较正常、慢调用和异常 span。
也可在 Grafana Explore 选择 **SkyWalking traces** 数据源按 traceId 查询。

预期：六个关联 span 均查回，报告还保留完整的后端 span 列表。
OTLP trace 使用 Zipkin 查询路径；已有 `skywalkingLab` Agent 实验走原生 APM，二者的 UI 页面不同。
真实 Blog → UAA → Gateway 接入情况见仓库 `k8s/local/monitoring/SERVICES.md`。

## 6. Grafana：数据源与面板

1. `/api/health` 确认 Grafana 自身数据库健康。
2. 有本地凭据时读取 `kuma-services` 面板，检查面板存在，并验证 Prometheus、Loki、SkyWalking 数据源健康。
3. 打开 Dashboards → Kuma → Kuma Services，选择服务；对照流量、P95、JVM、连接池和日志。

使用 WSL 脚本会读取现有凭据，预期各个 `*Healthy`、`dashboardHasPanels` 为 true。
直接运行而未提供密码时，只检查健康与未登录边界，报告提示如何继续；
`dashboardRequiresLogin=true` 不能当作已经验证面板或数据源。报告不包含用户名密码或 Authorization。

## Lab HTTP 接口与地址配置

启动 LabApplication 后使用 `POST /api/lab/monitoring/{组件名}`，
`GET /api/lab/monitoring/guide` 获取入口地址与页面位置，控制台 API 目录已经内置七个入口。
每次接口只执行一个组件实验，不会加载页面就自动写入实验数据。

注意原有 Lab 默认业务端口 **9090** 与 Prometheus 的 Windows 转发端口重叠。
本地一起使用时将 Lab 启动参数设为 `--server.port=19090`，控制台设置
`KUMA_LAB_URL=http://127.0.0.1:19090/api`。独立 Gradle 实验不占用 Lab 业务端口。

可通过环境变量覆盖 `KUMA_LAB_MONITORING_LOKI_URL`、`PROMETHEUS_URL`、
`ALERTMANAGER_URL`、`GRAFANA_URL`、`OTLP_URL`、`SKYWALKING_QUERY_URL`、
`SKYWALKING_UI_URL`（每个名称都需加 `KUMA_LAB_MONITORING_` 前缀）。
Grafana 凭据使用同前缀的 `GRAFANA_USER` 与 `GRAFANA_PASSWORD`。
默认地址与上表一致；更换环境后，报告 `uiLinks` 会随实际配置更新。

参考：[Loki HTTP API](https://grafana.com/docs/loki/latest/reference/loki-http-api/)、
[Prometheus HTTP API](https://prometheus.io/docs/prometheus/latest/querying/api/)、
[Alertmanager Alerts API](https://prometheus.io/docs/alerting/latest/alerts_api/)、
[Spring Boot tracing](https://docs.spring.io/spring-boot/4.0/reference/actuator/tracing.html)。
