# Kubernetes 部署配置

按环境、命名空间管理部署文件；目录不再按 monitoring、observability 等功能分类。

```text
k8s/
├── local/
│   ├── base/          # 中间件、监控、链路追踪、Grafana 面板
│   ├── kube-system/   # Helm 安装、自定义配置、k3s 内置组件参考
│   ├── life-deals/    # 青龙、RSSHub、Redis、任务与运维脚本
│   ├── kumatts/       # AstrBot、语音服务、本地镜像与 GPU 工具
│   ├── k3s-config.yaml
│   └── kustomization.yaml
└── server/
    ├── base/          # 服务器中间件、应用、监控与链路追踪
    ├── blog/          # Blog 前后端、Ingress、网络策略
    ├── kube-system/   # 服务器 Traefik 配置
    └── kustomization.yaml
```

## 部署与操作说明

| 范围 | 文档 |
|---|---|
| WSL 总入口、凭据和数据边界 | [本地部署](local/README.md) |
| 本地监控、服务指标接入 | [监控](local/README.md#monitoring)、[服务接入](local/README.md#service-integration) |
| Collector、SkyWalking、OTel 接入 | [链路追踪](local/README.md#tracing) |
| 青龙、RSSHub、订阅任务 | [Life Deals](local/life-deals/README.md) |
| AstrBot、语音服务、GPU 和本地镜像 | [KumaTTS](local/kumatts/README.md) |
| 服务器发布、备份与 UI 隧道 | [服务器部署](server/README.md) |

## YAML 命名

文件名统一使用小写英文和连字符，扩展名为 `.yaml`；文件名描述部署组件或资源用途。
一个组件的工作负载、Service、ConfigMap 和必要 PVC 可以放在同一文件，避免无意义拆分。

| 类型 | 文件名示例 |
|---|---|
| 命名空间、Kustomize 入口 | `namespace.yaml`、`kustomization.yaml` |
| 应用与基础组件 | `mysql.yaml`、`otel-collector.yaml`、`skywalking-ui.yaml` |
| Grafana 面板 | `grafana-dashboard-gateway.yaml`、`grafana-dashboards-observability.yaml` |
| Helm 安装与覆盖配置 | `strimzi-helmchart.yaml`、`traefik-helmchart-config.yaml` |
| Ingress、网络策略 | `gateway-ingress.yaml`、`management-network-policy.yaml` |
| 密钥示例 | `secrets.example.yaml`，不加入 Kustomize |
| Blog 应用 | `blog-backend.yaml`、`blog-frontend.yaml` |

文件名与 Kubernetes 资源名相互独立。例如 Blog 文件名已修正，资源名称仍沿用
`blog-background`、`blog-fronted`，避免重新创建服务或改变现有工作负载。
`local/kube-system` 中的内置组件 YAML 是当前 k3s 版本的启动参考；仅 Kustomize 显式列出的文件参与日常部署。

## 常用入口

在 WSL 的仓库根目录执行：

```bash
# 检查所有本地资源，不修改集群。
python3 scripts/deploy-local-wsl.py
# 单独检查或部署一个命名空间。
python3 scripts/deploy-local-wsl.py --namespace life-deals
python3 scripts/deploy-local-wsl.py --namespace kumatts --apply
# 预览服务器通用配置；实际发布由 GitHub Actions 和服务器脚本执行。
kubectl kustomize k8s/server
```

本地文件变更不会触发服务器 Apply 工作流。服务器通用入口排除应用占位镜像和 Secret 示例，
应用镜像由各自的发布工作流管理。
原 Life、KumaTTS 项目的部署目录保留兼容转发入口，应用源码、模型、凭据与运行数据保持原位置。
