# Life Deals

在 `life-deals` 命名空间运行青龙、RSSHub 和 Redis，使用固定摘要的容器镜像。
青龙与 Redis 分别使用 10Gi、1Gi PVC；RSSHub 使用独立 Redis 缓存。

## 部署与管理

从仓库根目录执行，环境前置条件见 [本地部署](../README.md)。
Windows 管理入口默认使用 WSL `Ubuntu-24.04`：

```powershell
.\k8s\local\life-deals\life-deals.ps1 start
.\k8s\local\life-deals\life-deals.ps1 status
.\k8s\local\life-deals\life-deals.ps1 stop
.\k8s\local\life-deals\life-deals.ps1 credentials
```

`start` 启动 k3s 并应用部署清单；`stop` 将三个 Deployment 缩容至 0，保留数据和 Service。
缩容状态在 WSL 重启后保留，再次执行 `start` 恢复副本。
青龙入口为 `http://<WSL节点IP>:5700`，RSSHub 为 `http://<WSL节点IP>:1200`，
由 ServiceLB 提供；Redis 仅集群内部访问。节点 IP 可能随 WSL 重启变化。
部署入口同时安装自动重连的本机转发，Windows 可使用 http://localhost:5700 和
http://localhost:1200。对应 systemd 服务为 `kuma-panel-qinglong`、`kuma-panel-rsshub`。

## 脚本职责

以下 Bash/Python 工具在 WSL 中运行，普通部署不会自动初始化业务任务。

| 文件 | 用途 |
|---|---|
| `deploy.sh` | 部署本命名空间、清理旧端口转发服务并等待 ServiceLB |
| `status.sh` | 查询资源、访问地址和健康状态 |
| `bootstrap-faker.sh` | 将本地 Faker 与 RSS 脚本同步到青龙并安装依赖 |
| `configure-panel.py` | 初始化面板账号并注册定时任务 |
| `rss_watch.py` | 抓取 RSS、去重、记录结果和可选通知 |
| `verify.sh` | 更新 RSS 脚本并触发任务，验证抓取和青龙调度 |

Faker 源码需另行准备。同步脚本默认使用 `/mnt/d/Vscode_project/life`，
其他位置通过 `LIFE_PROJECT_DIR` 指定，目录内需包含 `faker2/`。
同步会覆盖容器内 Faker 源文件，并保留已有 `feeds.json`。
面板初始化脚本使用账号 `kuma`，凭据保存于 `/home/kuma/.config/life-deals/qinglong.json`；
在其他用户环境中使用前应调整脚本内的用户路径和 UID。

## 订阅与数据

青龙中的 `/ql/data/scripts/life-deals/feeds.json` 管理订阅，`path` 填写 RSSHub 路由，
如 `/smzdm/keyword/SSD?limit=20`。容器内 RSSHub 地址为 `http://rsshub:1200`。
初始化配置的 RSS 任务每 15 分钟执行一次，首次抓取建立基线；通知由订阅配置控制。
账号任务默认停用，需自行配置账号并逐项启用。

去重状态保存于同目录 `rss-state.json`，最近成功抓取结果保存于 `latest-deals.json`。
备份时保留青龙完整 `/ql/data`、Redis 数据及面板凭据文件。
删除命名空间或 PVC 可能删除持久化数据，重启服务应使用启停入口。
