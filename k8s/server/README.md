# 服务器部署清单

目录按命名空间划分：

```text
server/
├── base/          # 数据库、中间件、应用清单、监控、链路追踪和仪表盘
├── blog/          # Blog 前后端、Ingress、管理端口网络策略
├── kube-system/   # Traefik HelmChartConfig
└── kustomization.yaml
```

通用入口只包含配置与基础设施，应用镜像由对应 GitHub Actions 工作流单独发布，Secret 示例不参与部署。
监控和链路追踪的原 overlay 已合并进 `base` 下的最终 YAML，保持 ACR 镜像、拉取凭据、采集目标及网络策略不变。

预览：`kubectl kustomize k8s/server`。
现有服务器部署入口：`bash /data/k8s/scripts/apply-k8s.sh`。

## 服务器应用与中间件同步

服务器 `117.72.13.197` 的 K3s 部署 Blog、UAA、Gateway、内网 Lab，以及
Loki、Prometheus、Grafana、Alertmanager、OTel Collector、SkyWalking。
MySQL、Redis、Nacos、Qdrant、Ollama 保持现有持久化目录与已有清单。

`k8s/server/base` 保存 base 命名空间的中间件、应用、监控、链路追踪和仪表盘清单。
观测组件的 ACR 镜像和 `observability-registry` 拉取凭据已直接写入对应工作负载，
Prometheus 的三个业务目标使用集群 Service，不使用 WSL EndpointSlice。
应用使用 `prod,observability`，Nacos 配置仍为 `prod`；管理端口不经公网 Ingress，
NetworkPolicy 只允许 Prometheus Pod 采集。指标环境为 `server`，日志与 traceId 关联。

### 发布

服务器目录只按命名空间划分：`base/`、`blog/`、`kube-system/`。
Blog 的管理端口网络策略位于 `blog/management-network-policy.yaml`，
Traefik 的 HelmChartConfig 位于 `kube-system/traefik-helmchart-config.yaml`。
`k8s/server/kustomization.yaml` 是通用配置入口；各命名空间入口显式排除
Secret 示例及由独立工作流发布的 Blog、UAA、Gateway、Lab 应用 Deployment，避免应用 placeholder 镜像。

在已有服务器上，`bash /data/k8s/scripts/apply-k8s.sh` 先备份并准备凭据，
再渲染该入口并通过 PVC/探针兼容助手应用清单。不要直接递归 apply 整个服务器目录。
工作流完成备份后用 `--skip-prepare` 调用同一脚本，避免重复备份。
此入口仅更新 Kubernetes 配置；它不重启 k3s，也不部署本地 WSL 配置。

推送 master 后，Apply K8s、Publish Nacos Prod 与四个应用部署工作流同步执行。
Apply 工作流镜像同步八个观测组件，创建服务器专用 Grafana 凭据，部署监控并验证基础设施。
应用工作流各自复制当前发布的清单，以原子方式更新镜像、环境和探针，避免 placeholder 镜像。
Lab 不创建公网入口，使用独立 `kuma_lab` 数据库，Kafka/Binlog 默认关闭。

部署前 `scripts/prepare-monitoring-server.sh` 保存当前资源、Secret、生产 Nacos 配置与
Blog/UAA 数据库到 `/data/kuma-releases/<UTC时间>/`，目录和文件仅 root 可读。
Grafana 随机密码由服务器生成，保存到 Secret 与 `/data/kuma-observability/grafana-admin-password`，
不进入 Git。备份中的 Secret、数据库和配置不要复制到公开仓库。

全部部署完成后在服务器运行：

```bash
python3 /data/k8s/scripts/check-monitoring-server.py
```

验证八个采集目标、三服务真实请求与调用链、关联日志、管理端口隔离、Grafana 数据源/面板、
告警规则及六项服务器 Lab 实验。`--infrastructure-only` 供独立配置工作流使用，
不把仍在发布中的应用当作基础设施故障。

### 从 Windows 打开服务器 UI

服务器端 `scripts/expose-monitoring-server.sh` 创建自动重连的 systemd 转发，仅监听 `127.0.0.1`。
Windows 新开一个终端，建立 SSH 隧道；使用不同本机端口，避免与 WSL 实例冲突：

```powershell
ssh -N -o ExitOnForwardFailure=yes -i C:\Users\Kuma\Desktop\server\Kumakey.pem -L 23000:127.0.0.1:3000 -L 29090:127.0.0.1:9090 -L 29093:127.0.0.1:9093 -L 22880:127.0.0.1:12880 -L 29091:127.0.0.1:19090 root@117.72.13.197
```

| UI | Windows 地址 | 页面位置 |
|---|---|---|
| Grafana | http://localhost:23000/d/kuma-services | Dashboards → Kuma → Kuma Services |
| Loki | http://localhost:23000/explore | Explore → Loki；日志 traceId 可跳转 SkyWalking traces |
| Prometheus | http://localhost:29090 | 查询 up；Targets、Rules、Alerts |
| Alertmanager | http://localhost:29093 | Alerts、Silences |
| SkyWalking | http://localhost:22880 | Zipkin Trace / Lens 查 OTLP trace；原生 Trace 查 Agent |
| Lab | http://localhost:29091/api/lab/monitoring/guide | 返回中间件 UI 位置；POST 同路径下组件名执行实验 |

OTel Collector 没有业务 UI，通过后端 trace 查回和 Collector 日志验证。
登录 Grafana 使用用户名 `admin`，密码在服务器的上述私有文件；通过 SSH 读取即可。
外部告警通知渠道仍为空接收器，Alertmanager 页面可以查看告警。

控制台要连接远程 Lab 时，设置 `KUMA_LAB_URL=http://127.0.0.1:29091/api` 后重新启动。
UI 隧道断开不影响应用或中间件运行；公网继续使用原有 Blog/UAA/Gateway 域名。
