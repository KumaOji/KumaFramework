# 服务器部署

`base/` 保存中间件、应用、监控和链路追踪配置；`blog/` 保存 Blog 应用与网络策略；
`kube-system/` 保存 Traefik 配置。部署前按实际环境调整域名、镜像仓库及存储目录。

## 发布

```bash
# 仓库根目录预览通用配置。
kubectl kustomize k8s/server
# 服务器已安装部署脚本时执行。
bash /data/k8s/scripts/apply-k8s.sh
```

通用 Kustomize 入口排除 Secret 示例和独立发布的后端应用 Deployment。
前端清单由部署脚本单独应用，并沿用服务器当前镜像。应用镜像由各自工作流更新，
不要递归 apply 整个目录，否则可能应用占位镜像或 Secret 示例。

GitHub Actions 在 `master` 分支按路径触发，也支持 `workflow_dispatch`：

| 修改范围 | 工作流 |
|---|---|
| `k8s/server/**` | Apply K8s Configs |
| Blog 后端或 UAA、Gateway、Lab 应用清单 | 同时触发对应应用发布 |
| 部署、验证或等待脚本 | 触发引用该脚本的相关工作流 |
| `k8s/local/**` | 不触发服务器发布 |

具体触发条件以 `.github/workflows/` 为准。

## 凭据与备份

工作流需要 `BLOG_SERVER_HOST`、`BLOG_SERVER_USER`、`BLOG_SERVER_SSH_KEY`、
`ALIYUN_REGISTRY_USERNAME` 和 `ALIYUN_REGISTRY_PASSWORD`。
`NACOS_PASSWORD` 为可选覆盖，未设置时从服务器 `base/kuma-secret` 读取。
应用依赖 `base/kuma-secret`、`blog/blog-secret`，观测镜像使用 `base/observability-registry`。
Secret 示例只用于说明格式，真实值在仓库外创建和维护。

`scripts/prepare-monitoring-server.sh` 在部署前备份资源、Secret、生产 Nacos 配置
以及 Blog/UAA 数据库到 `/data/kuma-releases/<UTC时间>/`。
Grafana 随机密码保存在 `base/grafana-admin` 和
`/data/kuma-observability/grafana-admin-password`。备份包含敏感数据，应保存在私有存储。

## 验证与访问

```bash
python3 /data/k8s/scripts/check-monitoring-server.py
```

验证采集目标、业务调用链、日志、管理端口隔离、Grafana 配置与 Lab 实验。
`--infrastructure-only` 用于仅验证基础设施。

`scripts/expose-monitoring-server.sh` 创建仅监听服务器 `127.0.0.1` 的转发服务。
在客户端建立 SSH 隧道，将 `user@server` 替换为实际 SSH 目标：

```bash
ssh -N -o ExitOnForwardFailure=yes \
  -L 23000:127.0.0.1:3000 -L 29090:127.0.0.1:9090 \
  -L 29093:127.0.0.1:9093 -L 22880:127.0.0.1:12880 \
  -L 29091:127.0.0.1:19090 user@server
```

| 服务 | 客户端地址 |
|---|---|
| Grafana / Loki 日志 | http://localhost:23000 |
| Prometheus | http://localhost:29090 |
| Alertmanager | http://localhost:29093 |
| SkyWalking | http://localhost:22880 |
| Lab API | http://localhost:29091/api/lab/monitoring/guide |

Grafana 用户名为 `admin`。Console 连接远程 Lab 时设置
`KUMA_LAB_URL=http://127.0.0.1:29091/api`。
Lab 不提供公网入口；应用管理端口由 NetworkPolicy 限制为 Prometheus 采集。
