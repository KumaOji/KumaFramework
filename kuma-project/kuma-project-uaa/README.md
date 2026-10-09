# UAA 本地调试

与 Blog、Gateway 共用 Loki、Prometheus、Grafana、OTel Collector 和 SkyWalking，
联合启动与验证见 [三服务可观测性说明](../../../k8s/local/monitoring/SERVICES.md)。

从仓库根目录单独启动 UAA：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-services-local.ps1 -Services uaa -Build -Restart
```

业务端口 `33336`；独立管理端口 `19002`，仅绑定 Windows 的 WSL NAT 网卡；
IDEA Remote JVM Debug 连接 `localhost:5006`。数据库使用 WSL MySQL 的 `kuma_uaa`。
OAuth issuer 为 `http://localhost:33336`，网关访问前缀为 `/uaa`。

未登录或权限不足的 JSON 接口保留原有 `Result` 错误结构，HTTP 状态分别为
401 和 403，便于客户端与监控识别拒绝访问；浏览器登录页跳转保持原有行为。
日志与 PID 位于 `logs/uaa-local/`。
