# WSL 本地部署

需要已运行的 k3s、kubectl、Python 3 和 PyYAML（Ubuntu 包名 `python3-yaml`）。
以下 Bash 命令在 WSL 的仓库根目录执行。

## 部署

```bash
# 默认执行前置检查和服务端 dry-run。
python3 scripts/deploy-local-wsl.py
# 只渲染清单。
python3 scripts/deploy-local-wsl.py --render
# 按命名空间部署，可重复指定 --namespace。
python3 scripts/deploy-local-wsl.py --namespace base --apply
python3 scripts/deploy-local-wsl.py --namespace life-deals --apply
python3 scripts/deploy-local-wsl.py --namespace kumatts --apply
```

部署 `base` 时先安装 Strimzi 并等待 CRD 和 Operator。脚本刷新 Kafka 的
`advertisedHost` 和主机指标 EndpointSlice；可用 `--node-ip`、`--windows-host` 覆盖地址。
默认 Windows 地址来自 WSL NAT 网关，其他网络模式需提供 Pod 可访问的地址。
直接 `kubectl apply -k k8s/local` 不包含这些准备步骤。

新环境先创建 `base` 命名空间，参考 `base/secrets.example.yaml` 在仓库外准备真实 Secret。
部署脚本检查所需 Secret、存储类、hostPath、RuntimeClass 和本地镜像，不初始化业务数据。
数据库、应用账号和授权需另行初始化或恢复。部署前核对清单中的主机目录、代理和地址。

`k3s-config.yaml` 是 k3s 主机配置参考，不能用 kubectl 应用。本地启用 Traefik 和 ServiceLB。
`kube-system` 中未加入 Kustomize 的内置组件 YAML 仅供核对，由 k3s 自动管理。
专用部署说明：[Life Deals](life-deals/README.md)、[KumaTTS](kumatts/README.md)。

<a id="monitoring"></a>

## 监控

`base` 包含 Prometheus、Grafana、Loki 和 Alertmanager，配置与仪表盘随清单维护。

```bash
bash scripts/deploy-monitoring-wsl.sh
bash scripts/expose-monitoring-wsl.sh
python3 scripts/check-monitoring-wsl.py
```

| 组件 | 本地入口 |
|---|---|
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| Alertmanager | http://localhost:9093 |
| Loki API | http://localhost:3100 |

Grafana 用户名为 `admin`，密码保存于部署用户的
`~/.config/kuma-observability/grafana-admin-password`；脚本默认用户为 `kuma`，
可用 `MONITORING_LOCAL_USER` 调整。日志通过 Grafana Explore 查询。

转发服务为 `kuma-monitoring-{loki,grafana,prometheus,alertmanager}`，仅监听本机；
用 `systemctl disable --now <服务名>` 停用。外部告警通知需自行配置接收器。
监控数据使用 PVC；local-path 的容量声明不等同于磁盘配额。
Grafana 初始化后，修改 Secret 不会自动修改数据库中的管理员密码。

<a id="service-integration"></a>

## 服务接入

应用通过 Prometheus 提供指标，通过 Loki4j 发送日志，通过 OpenTelemetry 发送调用链。
Grafana 的 `/d/kuma-services`、`/d/kuma-gateway` 提供服务面板。
本地默认注册 UAA、Gateway、Reference 指标入口；Blog 指标采集默认停用，
完整三服务指标验证前需恢复 Blog 的采集配置和入口。

从仓库根目录的 Windows PowerShell 启动应用：

```powershell
.\scripts\start-services-local.ps1 -Build -Restart
.\scripts\start-services-local.ps1 -Services gateway
```

应用需要各自的本地 Nacos 配置与中间件，具体见对应模块 README。
管理端口为 Gateway `18081`、Blog `19001`、UAA `19002`；
Windows 服务的管理地址由启动脚本注册为 EndpointSlice，WSL 地址改变后需重新运行。
日志和 PID 位于 `logs/<服务>-local/`。在 WSL 中验证真实请求、指标、日志和调用链：

```bash
python3 scripts/check-services-observability-wsl.py
```

<a id="tracing"></a>

## 链路追踪

`base` 部署 OTel Collector、SkyWalking OAP、存储和 UI。

```bash
bash scripts/expose-observability-wsl.sh
python3 scripts/check-observability.py
```

UI 为 http://localhost:12880，Collector OTLP 端口为 `4317`（gRPC）和 `4318`（HTTP）。
应用引入 `kuma-boot-starter-otel` 后配置：

```yaml
kuma:
  boot:
    otel:
      enabled: true
      endpoint: http://otel-collector.base.svc.cluster.local:4318/v1/traces
      sampling:
        probability: 0.1
```

Windows/IDE 运行时将 endpoint 改为转发后的 `http://localhost:4318/v1/traces`。
OTLP traces 在 SkyWalking 的 Zipkin Trace / Lens 或 Grafana Zipkin 数据源中查看。
Collector 配置修改后需重启 `deployment/otel-collector`；内存重试队列不会跨重启保留。
接收端未配置遥测鉴权，应限制在本地或受控网络内访问。
