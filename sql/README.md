# 框架数据库初始化

此目录仅保留框架初始化 SQL，业务权限迁移和历史数据库清理脚本不在此维护。

| 脚本 | 用途 | 默认数据库 |
| --- | --- | --- |
| `framework-init.sql` | 幂等、数据库延迟消息、Seata AT、领域事件、JustAuth、多租户数据源表 | `kuma-framework` |
| `kuma-cloud-job-mysql.sql` | 任务调度服务的应用、任务、实例和分布式锁表 | `kuma-cloud-kmcjob` |

这些表按启用的框架功能选用，不要求每个业务服务全部创建。执行前应按部署配置
调整脚本中的建库和 `USE` 语句；Seata AT 的 `undo_log` 需要建在参与事务的业务库中。

Blog 默认权限由 `BlogPermissionInitializer` 在应用启动时整理，详见
[Blog 权限说明](../kuma-project/kuma-project-blog/PERMISSIONS.md)。
