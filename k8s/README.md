# Kubernetes 部署

清单按环境和命名空间组织，使用 Kustomize 管理入口。

| 目录 | 内容 | 说明 |
|---|---|---|
| `local/base` | 本地数据库、中间件、监控与链路追踪 | [本地部署](local/README.md) |
| `local/kube-system` | Strimzi、Traefik 与 k3s 内置组件参考 | [本地部署](local/README.md) |
| `local/life-deals` | 青龙、RSSHub、Redis 与订阅任务 | [Life Deals](local/life-deals/README.md) |
| `local/kumatts` | AstrBot、语音服务与 GPU 工具 | [KumaTTS](local/kumatts/README.md) |
| `server` | 服务器应用、基础设施与网络配置 | [服务器部署](server/README.md) |

在仓库根目录执行：

```bash
# 检查本地资源；添加 --apply 才会部署。
python3 scripts/deploy-local-wsl.py
python3 scripts/deploy-local-wsl.py --namespace life-deals --apply
# 预览服务器通用配置。
kubectl kustomize k8s/server
```

清单包含项目开发环境的域名、存储路径和镜像配置，部署到其他环境前需调整。
`secrets.example.yaml` 仅描述 Secret 格式，不参与 Kustomize；真实凭据应在仓库外管理。
本地修改不会触发服务器发布，服务器应用镜像由对应 GitHub Actions 工作流管理。
