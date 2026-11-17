# PostgreSQL MVCC、行锁与表锁

继续使用WSL PostgreSQL的`kuma_lab`专用库。每条命令创建一个`lab_pg_concurrency_<uuid>` schema；同次完整运行的六项实验共用这个schema，分别使用固定名称的独立表，避免不同实验的状态相互影响。退出后保留表和`observations`证据，入口登记在`public.lab_runs`，不清空先前的实验。

console → Lab搜索“MVCC”“行锁”“表锁”“阻塞链”或“可串行化”。在仓库根目录使用JDK25：

```powershell
# 六项完整实验，共用本次schema
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group postgres

# 单项：mvcc / phantom / row-locks / table-locks / lock-monitor / serializable
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group postgres -Lesson row-locks

# 数据库操作、事务、连接池和本组实验一起运行
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group all
```

脚本复用现有PostgreSQL Pod和凭据，只有专用库不存在时才创建。Java核对实际数据库必须为`kuma_lab`。本组只支持真实PostgreSQL，不提供H2替代；无需启动LabApplication。等待使用会话锁超时、JDBC查询超时和Future上限，finally回滚未完成事务并关闭线程与连接。

| 单项 | 表 | 真实操作与预期 |
| --- | --- | --- |
| mvcc | mvcc_accounts | RR读者取得旧版本100；写者更新未提交时普通读仍返回100；写者提交后，RR仍读100，新语句读110；记录xmin、ctid与pg_current_snapshot；旧事务结束后读110 |
| phantom | phantom_rc / phantom_rr | 两连接固定交错：条件查询先有2行，再插入提交1行。RC新语句读3行，PostgreSQL RR当前事务保持2行；新事务均读3行 |
| row-locks | row_accounts | FOR UPDATE锁行1；普通SELECT仍可读100；竞争同一行的NOWAIT返回55P03；SKIP LOCKED仅返回行2，行2仍可更新 |
| table-locks | table_accounts | SHARE表锁允许普通SELECT，但竞争UPDATE锁超时；ACCESS EXCLUSIVE也阻塞普通SELECT；捕获55P03并rollback，释放持有者后读取恢复 |
| lock-monitor | monitor_accounts | 真实工作线程更新已锁行，观察者记录pg_blocking_pids、pg_locks与pg_stat_activity；观察到Lock等待和未授予锁后释放持有者，任务更新到101且阻塞链消失 |
| serializable | skew_rr / skew_serializable | 两事务均读取2人值班，然后分别让不同人下班；RR允许写偏差导致0人，SERIALIZABLE拒绝一个事务并返回40001；新事务重新检查条件后保留1人 |

## MVCC：通过不同会话看同一行的版本

`mvcc`会同时保留RR读者事务、写者事务和普通观察者连接。普通读不等待本例的未提交行更新；写者提交后，读者仍可根据旧快照读取先前版本，而新事务读取新版本。证据同时打印业务值、xmin、ctid和快照文本。

xmin和ctid仅用于本次机制观察，不作为业务主键或永久版本标识。实验不使用pageinspect、不读取已不可见元组的底层页、不控制VACUUM，也不从一次结果推断旧版本的物理回收时间。机制说明见[PostgreSQL MVCC介绍](https://www.postgresql.org/docs/18/mvcc-intro.html)。

`phantom`补充新增行的可见性：PostgreSQL RR在本例中保持原查询集合，RC按新语句取得新快照。`serializable`则说明RR稳定快照仍可能发生跨行约束的写偏差；可串行化冲突要回滚整个失败事务，在新事务中重新检查条件，而不是只重放最后一条UPDATE。此处仅执行一次明确的业务条件重读，不提供无限自动重试。

## 行锁与表锁的区别

`row-locks`通过NOWAIT获取立即失败的结果，失败后先rollback，才能继续使用该事务连接。SKIP LOCKED跳过行1返回行2，适合演示队列任务领取，但不能把跳过后的结果视为完整业务数据集，也不保证公平性。

`table-locks`显式对实验表执行LOCK TABLE。SHARE与UPDATE自动需要的表级锁冲突，而普通SELECT可继续；ACCESS EXCLUSIVE与普通SELECT需要的ACCESS SHARE也冲突。名字含Row的RowShareLock/RowExclusiveLock仍是表级锁模式，不能按名字误解成元组行锁。模式及冲突关系见[PostgreSQL显式锁文档](https://www.postgresql.org/docs/18/explicit-locking.html)。

因此，“普通MVCC读不等待本例行更新”并不表示所有SELECT永远不等锁。表级ACCESS EXCLUSIVE、DDL及显式锁请求需要单独分析。

## 阻塞链与历史证据

`lock-monitor`用三个独立会话：持有者、等待者、观察者。等待连接的会话lock_timeout临时设置为4秒，观察者在2秒内轮询真正的阻塞链，取得证据后立即释放锁；不是仅靠sleep推测任务已经进入等待。其余实验沿用500ms锁等待上限。

`pg_locks`中的行更新竞争可能表现为transactionid等待，不保证把每个元组锁都列成tuple记录。判断依赖后端PID、未授予锁、Lock等待事件和pg_blocking_pids共同提供的证据。函数含义见[PostgreSQL系统信息函数](https://www.postgresql.org/docs/18/functions-info.html)。

实时系统视图在实验结束后会变化，所以程序将当时的观察文本写入本次schema的observations表。该表记录观察，不代替命令退出码或check断言结果。数据表保留的是最终已提交状态，历史版本见observations文本。

```sql
SELECT schema_name, experiment_kind, lesson, opened_at
FROM public.lab_runs
WHERE experiment_kind = 'pg_concurrency'
ORDER BY opened_at DESC;

-- 将日志或上面查询到的实际schema名称代入
SELECT step, details, observed_at
FROM lab_pg_concurrency_<uuid>.observations ORDER BY id;

SELECT * FROM lab_pg_concurrency_<uuid>.mvcc_accounts ORDER BY id;
SELECT * FROM lab_pg_concurrency_<uuid>.skew_serializable ORDER BY id;
```

同次`-Group postgres`完整运行会留下上述全部表；单项运行只创建相应表和observations。数据与连接方式详见[数据库与事务教程](database-transaction-labs.md)。
