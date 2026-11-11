# WSL 本地优惠工具

已在现有 `Ubuntu-24.04` 的 k3s 中建立独立命名空间 `life-deals`。
运行环境使用官方容器镜像，清单固定镜像摘要；Windows 下的三个源码仓库保留供查看和开发。

部署文件统一维护在 `D:\KumaProject\KumaFramework\k8s\local\life-deals`。
原 `D:\Vscode_project\life\deploy\wsl` 只保留兼容转发入口，不再维护第二份清单。
源码仍在 `D:\Vscode_project\life`；Faker 同步脚本默认读取该目录，可用 WSL 的 `LIFE_PROJECT_DIR` 覆盖。

| 文件 | 内容 |
|---|---|
| `namespace.yaml` | 命名空间 |
| `qinglong.yaml` | 青龙 Deployment、Service、10Gi PVC |
| `redis.yaml` | 独立 Redis Deployment、Service、1Gi PVC |
| `rsshub.yaml` | RSSHub Deployment、Service、可选 rsshub-secrets 引用 |
| `deploy.sh` | 仅部署本命名空间，等待 ServiceLB 就绪并卸载旧端口转发服务 |
| `life-deals.ps1`、`status.sh` | 启停、状态和本机凭据查看入口 |
| `bootstrap-faker.sh`、`rss_watch.py`、`feeds.json` | 显式同步和初始化任务，保留容器内已有订阅 |
| `configure-panel.py`、`verify.sh` | 首次面板初始化和业务验证工具，普通部署不自动运行 |

## 访问与账号

- 青龙：`http://<WSL节点IP>:5700`，管理员用户名 `kuma`。
- RSSHub：`http://<WSL节点IP>:1200`。
- 两个入口均由 k3s `LoadBalancer` 提供，不依赖 `kubectl port-forward`。节点 IP 可能随 WSL 重启变化，运行 `life-deals.ps1 status` 查看当前地址。端口在 WSL 节点网络开放；Redis 保持 ClusterIP，仅集群内部访问。
- 管理员随机密码只保存在 WSL 的 `/home/kuma/.config/life-deals/qinglong.json`，文件权限为 `600`。
- 在本机 PowerShell 中执行以下命令查看登录信息，不必把密码发到聊天中：

```powershell
& D:\KumaProject\KumaFramework\k8s\local\life-deals\life-deals.ps1 credentials
```

## 已配置的功能

- 青龙、RSSHub、独立 Redis 缓存以及持久化数据卷。
- 时区 `Asia/Hong_Kong`。
- WSL 启动后，k3s 自动恢复应用和两个 ServiceLB 入口，不再安装本机端口转发服务。
- Faker 文件位于青龙容器的 `/ql/data/scripts/faker2`，其 Linux Node 和 Python 依赖已安装。
- 好价榜每 15 分钟抓取一次，记录去重状态；首次抓取只建立基线。
- 按用户选择，通知关闭；只抓取并记入青龙日志。
- 每次日志展示当前榜单的商品标题（含来源提供的价格条件）、详情链接与新增/已见标记；`0 new deals` 表示没有首次发现的条目，不表示榜单为空。
- 京东 Cookie 检查、日常任务和店铺签到已创建，保持停用。
- 禁止订阅同步时自动增加或删除任务，避免整库活动被自动启用。

好价榜 RSS 地址：

```text
http://<WSL节点IP>:1200/smzdm/ranking/pinlei/11/3?limit=20
```

关键词订阅示例：

```text
http://<WSL节点IP>:1200/smzdm/keyword/SSD?limit=20
```

在青龙「脚本管理」中修改 `life-deals/feeds.json`，可以增加关键词路由和标题排除词。
其中 `path` 只填写 `/smzdm/...` 路径，容器通过 `http://rsshub:1200` 访问。
本目录的 `feeds.json` 仅用于首次初始化，再次同步会保留容器中已修改的订阅配置。
运行状态 `rss-state.json` 保存在同一持久化目录；删除它会重新建立基线。
最近成功抓取的商品明细保存为同目录的 `latest-deals.json`，其中时间是抓取时间。
日志每个来源默认展示最多 20 条，可以通过 `max_log_items_per_feed` 调整；商品价格和优惠条件以原文及电商结算页为准。

## 账号功能的后续设置

如需使用京东任务，在青龙「环境变量」中填写本人 `JD_COOKIE`。
再逐个验证相关任务，成功后启用。Cookie 不写入源码、不放到本说明里。
当前只验证了依赖和调度链路，没有对京东账号执行签到、领券、评价等操作。

淘宝自动签到、领券不包含在当前三项目的已验证配置中，需要针对实际活动另行适配。
RSS 爆料聚合也不等于指定商品 SKU 的实时价格监控；后者需要提供商品链接再配置。

## 启动、查看状态和停止

在 Windows PowerShell 中运行：

```powershell
& D:\KumaProject\KumaFramework\k8s\local\life-deals\life-deals.ps1 start
& D:\KumaProject\KumaFramework\k8s\local\life-deals\life-deals.ps1 status
& D:\KumaProject\KumaFramework\k8s\local\life-deals\life-deals.ps1 stop
```

`stop` 将本命名空间的三个应用缩容为 0，保留数据、Service 和 ServiceLB，不停止现有 k3s 或其他应用。没有应用副本时，入口无法提供业务响应。
缩容状态在 WSL 重启后仍然保留；用 `start` 重新应用清单即可恢复为 1 个副本。
Windows 启动本身不保证 WSL 已启动；可执行上面的 `start` 命令唤起。

重新应用部署配置：

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/KumaProject/KumaFramework/k8s/local/life-deals/deploy.sh
```

同步当前本地 Faker 和维护脚本、安装 Linux 依赖：

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/KumaProject/KumaFramework/k8s/local/life-deals/bootstrap-faker.sh
```

同步会覆盖容器里的 Faker 源文件，先把需要保留的修改同步回本地。
如需更换已停止的应用，先执行 `start`；这些脚本不删除数据卷。

只检查 Kubernetes 配置、不部署或执行任务：

```bash
python3 /mnt/d/KumaProject/KumaFramework/scripts/deploy-local-wsl.py --namespace life-deals
```

## 验证与数据

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/KumaProject/KumaFramework/k8s/local/life-deals/status.sh
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/KumaProject/KumaFramework/k8s/local/life-deals/verify.sh
```

`verify.sh` 会验证抓取脚本、RSSHub 实际好价榜及青龙调度执行，并确认京东任务停用；不执行京东任务。
真实数据位于 WSL 的 k3s `local-path` 持久化卷：`qinglong-data`（10 GiB）和 `redis-data`（1 GiB）。
本地卷的容量声明不等同于磁盘配额；备份时应保留青龙完整 `/ql/data`，并另外备份管理员凭据文件。
不要删除命名空间或 PVC 来重启服务，因为默认存储策略可能一并删除数据。
