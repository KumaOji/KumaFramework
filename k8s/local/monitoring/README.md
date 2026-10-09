# WSL 本地监控

部署 Loki、Grafana、Prometheus 和 Alertmanager，命名空间为 `base`。
本目录用于本地 K3s，现有远程部署流水线仅应用 `base`、`blog` 和服务器覆盖配置。

固定版本：Loki 3.7.8、Grafana 13.2.3、Prometheus 3.15.0、Alertmanager 0.34.1。

## 部署

```powershell
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/IDEA_project/KumaFramework/scripts/deploy-monitoring-wsl.sh
wsl -d Ubuntu-24.04 -u root -- bash /mnt/d/IDEA_project/KumaFramework/scripts/expose-monitoring-wsl.sh
wsl -d Ubuntu-24.04 -u root -- python3 /mnt/d/IDEA_project/KumaFramework/scripts/check-monitoring-wsl.py
```

需要默认 StorageClass 和可访问集群的 kubectl。部署脚本默认为 WSL 用户 `kuma` 保存凭据，其他用户可设置 `MONITORING_LOCAL_USER`。

## 访问

| 组件 | 本地地址 | 集群地址 |
|---|---|---|
| Grafana | http://localhost:3000 | http://grafana.base:3000 |
| Prometheus | http://localhost:9090 | http://prometheus.base:9090 |
| Alertmanager | http://localhost:9093 | http://alertmanager.base:9093 |
| Loki API | http://localhost:3100 | http://loki.base:3100 |

Grafana 用户名：`admin`。随机密码保存在 WSL 的 `~/.config/kuma-observability/grafana-admin-password`，文件权限为 600。
在 Windows 读取：

```powershell
wsl -d Ubuntu-24.04 -- cat /home/kuma/.config/kuma-observability/grafana-admin-password
```

Grafana 已配置 Prometheus（默认）、Loki、Alertmanager 三个数据源；在 Explore 中选择数据源查询。
Prometheus 查询 `up` 可看到八个采集目标（含 Gateway、Blog、UAA），Loki 查询 `{service_name="kuma-monitoring-smoke"}` 可看到监控验证脚本的日志。
Loki 是日志 API 服务，其日志界面由 Grafana 提供。

## 当前采集范围

Blog、UAA、Gateway 的联合接入、启动与验证见 [SERVICES.md](SERVICES.md)。
三服务面板：http://localhost:3000/d/kuma-services。

Prometheus 每 15 秒采集自身、Alertmanager、Loki、Grafana 和现有 SkyWalking OAP 的指标。
Gateway 接入后，还通过 `gateway-local:18081` 采集主机上的 Gateway，Grafana 增加 `Kuma / Kuma Gateway` 面板。启动和验证说明见 `kuma-project/kuma-project-gateway/README.md`。
`MonitoringTargetDown` 在目标连续两分钟无法采集时触发。
Alertmanager 当前使用本地空接收器，告警可在页面查看，外部通知渠道需按需要配置。
验证脚本发送一条测试日志及一分钟后过期的测试告警，检查 Grafana 数据源、Prometheus 目标和 Alertmanager。

当前已接入 Gateway、Blog、UAA。主机与 MySQL/Redis 服务端指标、容器日志自动采集需另行配置；本次接入不更改现有 Collector 的 traces 管道。

## 持久化与维护

| 组件 | PVC 容量 | 内容 |
|---|---|---|
| Loki | 10Gi | TSDB 索引、日志块、WAL、保留任务状态；日志保留 7 天 |
| Prometheus | 10Gi | 指标保留最多 7 天或 8GB，先达到的限制生效 |
| Grafana | 2Gi | SQLite 数据库、用户和面板 |
| Alertmanager | 1Gi | 静默及通知状态 |

所有服务为单副本 StatefulSet，PVC 在删除 StatefulSet 后保留。K3s local-path 的容量申请不是磁盘配额，需关注 WSL 实际磁盘使用。
本地转发使用 systemd 的 `kuma-monitoring-{loki,grafana,prometheus,alertmanager}` 服务，自动启动、断线重连，仅监听 127.0.0.1。
停用转发可执行 `systemctl disable --now kuma-monitoring-<组件名>`。

修改 ConfigMap 后可执行 `kubectl -n base rollout restart statefulset/<组件名>`；Grafana 管理员密码初始化后保存于数据库，修改 Secret 不会自动重置已初始化账户。

参考：[Loki 存储](https://grafana.com/docs/loki/latest/configure/storage/)、[Prometheus 配置](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)、[Alertmanager](https://prometheus.io/docs/alerting/latest/alertmanager/)。
