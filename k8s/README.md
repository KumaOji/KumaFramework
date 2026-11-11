# K8s 环境配置

顶层只按环境分为两个目录，各环境的资源与 Kustomize 引用均在自身目录内：

```text
k8s/
├── local/
│   ├── observability/        # WSL：Collector、SkyWalking、Elasticsearch
│   └── monitoring/           # WSL：Grafana、Loki、Prometheus、Alertmanager
│       └── resources/        # 本地监控资源与仪表盘
└── server/
    ├── base/                 # 服务器 base 命名空间资源与应用
    ├── blog/                 # 服务器 blog 命名空间资源与应用
    ├── observability/        # 服务器观测栈覆盖配置
    └── monitoring/           # 服务器监控覆盖配置
        └── resources/        # 服务器监控资源与仪表盘
```

`base`、`blog` 是服务器内部的命名空间分类，环境通过 `local`、`server` 区分。两套监控资源独立维护；公共指标或仪表盘需要两边同时更新。

- 本地监控：[local/monitoring/README.md](local/monitoring/README.md)。
- 服务器部署：[server/monitoring/README.md](server/monitoring/README.md)。
- 服务器应用清单由对应 deploy 工作流发布，基础设施由 Apply K8s Configs 工作流发布；本地目录变更不会触发服务器 Apply 工作流。

## OTel Collector + SkyWalking

本地 WSL K3s 使用 `local/observability/observability.yaml`，服务器使用 `server/base/observability.yaml`，分别部署 Collector、SkyWalking OAP/UI 和独立 Elasticsearch 存储。
服务器通过 `server/observability/kustomization.yaml` 覆盖镜像地址为现有阿里云 ACR，并配置 `observability-registry` 拉取凭据；本地继续使用官方镜像。
版本固定为 SkyWalking 10.2.0、Collector Contrib 0.123.0、Elasticsearch 8.17.3。

## 部署和验证

需要已有 `base` 命名空间、可用的默认 StorageClass（K3s 默认 `local-path`），以及约 2.4Gi 可调度内存；组件内存限制合计 4.75Gi。
Elasticsearch 使用 20Gi PVC；单副本用于目前单机 K3s 环境。保留追踪记录 3 天、指标 7 天，PVC 在删除 StatefulSet 后保留。

仓库根目录执行：

```bash
bash scripts/deploy-observability.sh
python3 scripts/check-observability.py
```

WSL 可从 Windows 执行（路径按实际仓库位置调整）：

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/IDEA_project/KumaFramework/scripts/deploy-observability.sh
wsl -d Ubuntu-24.04 -u root -- python3 /mnt/d/IDEA_project/KumaFramework/scripts/check-observability.py
```

`check-observability.py` 上报一条测试 trace，并从 OAP 查询确认 Collector、OAP 和存储整条链路。
Apply K8s 工作流在 `master` 上的 `k8s/server/**`、验证脚本或工作流文件变更时触发，使用已有 GitHub Secrets 同步文件、部署、等待组件就绪并运行测试 trace 验证；服务器需要 Python 3。
流水线先将固定版本的官方镜像同步到 ACR（已有标签则跳过），使用现有 `ALIYUN_REGISTRY_USERNAME/PASSWORD` 创建服务器拉取凭据，再应用服务器覆盖配置。
也可只复制此文件到服务器并 `kubectl apply -f observability.yaml`，不会重部署业务应用。

## 访问

默认 Service 均为 ClusterIP。通过端口转发访问 UI，无需开放服务器公网端口：

```bash
kubectl -n base port-forward svc/skywalking-ui 12880:8080 --address=127.0.0.1
kubectl -n base port-forward svc/otel-collector 4317:4317 4318:4318 --address=127.0.0.1
kubectl -n base port-forward svc/skywalking-oap 11800:11800 9412:9412 --address=127.0.0.1
```

三个命令需分别保持运行。WSL 下浏览器打开 http://localhost:12880 。服务器上可配合 SSH 隧道：

```bash
ssh -L 12880:127.0.0.1:12880 user@server
```

本地 WSL 也可安装自动重连、随 WSL systemd 启动的转发服务：

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/IDEA_project/KumaFramework/scripts/expose-observability-wsl.sh
```

服务名称为 `kuma-observability-ui`、`kuma-observability-collector`、`kuma-observability-oap`。
可通过 `systemctl disable --now <服务名>` 停用。这些服务仅用于 WSL 本地访问，服务器无需安装。

SkyWalking 10.2 的 OTLP traces 会转换为 Zipkin 格式，在 UI 的 Zipkin Trace / Lens 页面查看；原生 Agent traces 在普通服务和追踪页面查看。

## 应用接入

Blog/UAA/Gateway 已使用 Boot 4 原生 OpenTelemetry 接入；服务器四组件监控、
Lab 发布、UI 隧道与完整验证见 [服务器同步说明](server/monitoring/README.md)。

本清单启用 **traces** 管道。部署成功不代表业务应用已经接入；示例 trace 的服务名为 `kuma-observability-smoke`。

### 现有 OTel starter

业务应用引入：

```groovy
api project(':kuma-boot-framework:kuma-boot-starter-otel')
```

在启动时可读取的配置中启用（生产建议先采样 10%）：

```yaml
kuma:
  boot:
    otel:
      enabled: true
      endpoint: http://otel-collector.base.svc.cluster.local:4318/v1/traces
      sampling:
        probability: 0.1
```

从 Windows/IDE 启动时，端口转发后 endpoint 改为 `http://localhost:4318/v1/traces`。
容器环境也可通过 `KUMA_BOOT_OTEL_ENABLED=true` 和 `KUMA_BOOT_OTEL_ENDPOINT` 设置。

### SkyWalking Java Agent

Agent 文件需单独安装或用 initContainer 挂载，并添加 JVM 参数 `-javaagent:/path/to/skywalking-agent.jar`。
设置 `SW_AGENT_NAME=kuma-cloud-uaa`（每个应用使用不同名称）、`SW_AGENT_COLLECTOR_BACKEND_SERVICES=skywalking-oap.base.svc.cluster.local:11800`。
IDE 本地接入时使用转发后的 `localhost:11800`。选择与应用 JDK 和框架兼容的 Agent 版本后再挂载。
同一应用先选择一种自动采集方式，避免重复采集；Collector 和 OAP 可以同时接收不同应用的两种数据。

## 维护

Collector ConfigMap 修改后执行 `kubectl -n base rollout restart deployment/otel-collector`。
可手动运行 GitHub Actions 的 `Inspect Observability` 工作流，只读检查服务器 Pod、PVC、资源和事件。
Collector 的重试队列在内存中，重启会丢失未发送的数据；Elasticsearch 数据持久化。
此部署未启用遥测接收鉴权，不创建公网 Ingress；对外发布 UI 时需配置访问控制。

参考：[SkyWalking OTLP traces](https://skywalking.apache.org/docs/main/v10.2.0/en/setup/backend/otlp-trace/)、[SkyWalking Docker](https://skywalking.apache.org/docs/main/v10.2.0/en/setup/backend/backend-docker/)、[Collector 配置](https://opentelemetry.io/docs/collector/configuration/)。
