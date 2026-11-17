# 数据库操作与事务实验

console → Lab搜索“数据库”“SQL”“事务”“保存点”“隔离级别”或“Spring”，选择完整组或单项卡片。在仓库根目录使用JDK25运行，默认连接现有WSL PostgreSQL中的专用数据库`kuma_lab`，无需启动Lab服务或Spring容器。

```powershell
# 五项数据库操作和七项事务实验
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1

# 分组
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Lesson operations
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Lesson transactions

# 单项
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Lesson transfer
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Lesson isolation
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Lesson spring

# 数据库、连接池与PostgreSQL MVCC/锁三组一并运行
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group all
```

脚本默认使用`Ubuntu-24.04`中的`base/postgresql-0`和`postgresql-external`服务。它在库不存在时创建`kuma_lab`，已存在则复用；凭据从现有Pod的文件读取，仅注入本次子进程环境，结束后恢复。可用`-Distribution`选择其他WSL发行版，`-PrepareOnly`仅准备专用库。脚本不更改全局数据库参数、现有业务库或用户角色。

每项使用独立的`lab_database_<uuid>` schema和最多4连接的Hikari池。执行前核对实际引擎为PostgreSQL且实际数据库为`kuma_lab`，随后设置连接schema；SQL只操作本次schema内的实验表。关闭池后保留schema和数据供查询，`public.lab_runs`记录schema、实验类别、入口和开始时间。运行记录不单独表示检查成功，仍需查看命令的退出状态及check输出。失败检查显式抛异常，不依赖`-ea`。

公共辅助类设置JDBC查询超时5秒、连接获取超时2秒、PostgreSQL会话lock_timeout为500ms、驱动连接超时5秒和socket超时10秒。锁等待实验的工作线程有Future超时并在finally关闭。每项打印数据库版本、schema、SQL、影响行数或查询快照，以及check结果。金额用DECIMAL/BigDecimal，固定整数金额通过精确转换检查。

## 连接与查看保留结果

本机已验证的服务地址为`172.23.89.45:5432`，数据库为`kuma_lab`；脚本每次动态查询服务地址，不把它硬编码到Java中。数据库用户沿用现有PostgreSQL Pod的用户，密码不写入仓库。

在数据库客户端连接`kuma_lab`后查询：

```sql
SELECT schema_name, experiment_kind, lesson, opened_at
FROM public.lab_runs ORDER BY opened_at DESC;

-- 将日志或lab_runs中的schema_name填入下面的schema位置
SELECT id, owner, balance, version
FROM lab_database_<uuid>.accounts ORDER BY id;
```

schema按次保留，脚本不清空历史结果；需要手动清理时，先根据lab_runs确认要删除的那次实验schema。不要删除整个专用库来重跑实验。

MVCC、NOWAIT/SKIP LOCKED、表锁兼容性、阻塞链和可串行化写偏差另有六项真实PostgreSQL实验，使用同一专用库，本次运行共用一个schema并保留observations证据。运行脚本加`-Group postgres`，详见[PostgreSQL MVCC与锁教程](postgresql-mvcc-lock-labs.md)。

若不使用WSL包装脚本，可设置`KUMA_LAB_DATABASE_JDBC_URL`、`KUMA_LAB_DATABASE_JDBC_USER`和`KUMA_LAB_DATABASE_JDBC_PASSWORD`后直接执行`databaseLab`任务；目标仍必须是`kuma_lab`。缺少配置会明确报错，不自动退回内存库。

离线模式需要显式选择，仍会运行同一组断言，但不会保留数据或验证PostgreSQL行为：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:databaseLab -PdatabaseEngine=h2
```

H2依赖只在明确选择该模式时加入独立任务运行路径，不进入Lab应用常规runtimeClasspath或bootJar。

## 数据库操作

| databaseLesson | 内容 | 预期 |
| --- | --- | --- |
| crud | 建表结构、自增主键、PreparedStatement、增查改删、影响行数 | 生成主键后读取book；改价15；首次删除1行，再删0行，最终无商品 |
| binding | 引号与注入形状输入、参数绑定、NULL、DECIMAL | `x' OR '1'='1`按字面值处理，不匹配其他账户；作为商品名可正确读回；category为NULL、价格1.25 |
| batch | addBatch/executeBatch，成功提交与失败整批回滚 | 首批3行提交；次批第一条可成功、第二条CHECK失败；回滚后第一条也不存在，仍为3行 |
| queries | LEFT/INNER JOIN、GROUP BY/HAVING、COALESCE、ROW_NUMBER、偏移与游标分页 | 未售商品保留且销量0；food收入15；价格排名book/rice/apple；两种分页在固定数据上均得到id2、3 |
| constraints | 主键、唯一、非空、CHECK、外键拒绝 | 五次失败均为23类SQLState，最终账户仍2行，Alice余额100、销售表为空 |

值参数使用占位符，表名、列名、排序方向不能作为普通值绑定，应由固定SQL或允许列表控制。NULL用IS NULL判断，不能把`= NULL`当作相等比较。批处理是一种执行方式，本身不提供整批原子性；这里通过显式事务控制部分执行后的回滚，驱动返回的updateCounts只记录观察，不要求所有驱动完全一致。

分页始终使用唯一id形成稳定排序。本例为固定数据集，不推断并发修改下偏移分页和游标分页都不会重复或遗漏。约束实验在自动提交模式逐条执行失败语句，不代表每种数据库在显式事务内遇错后的状态都相同。

## JDBC事务

账户初始余额Alice=100、Bob=50；每项重新初始化，不依赖其他实验的结果。DDL和种子数据都在事务演示前创建完成，不把建表/删表纳入回滚承诺。

| databaseLesson | 内容 | 预期 |
| --- | --- | --- |
| boundaries | 自动提交、未提交可见性、commit、rollback | 自动提交扣5后其他连接读95；关闭自动提交再扣10时自身85、其他连接95；commit后85；再次扣20后rollback仍85 |
| transfer | 条件扣款、收款、审计同一事务，扣款后注入主键冲突 | 成功转30后70/80；故障转10全部回滚，仍70/80、总额150、审计仅1行；余额不足不扣款 |
| savepoint | 局部回滚、释放保存点、继续外层事务 | 扣10后建立保存点，Bob临时加999；回滚保存点保留Alice90、Bob回50；补正常收款10再commit，最终90/60 |
| isolation | 两个真实连接的固定读写交错，对比RC/RR | 两种级别都不读未提交110；写者commit后RC的新语句读110，RR当前事务仍100；读者commit后新事务读110 |
| optimistic | 旧值覆盖与version条件更新、影响行数检查、有界重试 | 两次旧值写入导致余额80；版本方案第二次旧版本影响0行，刷新版本后重试一次，最终70、version2 |
| locking | SELECT FOR UPDATE、另一线程写入、等待超时、释放恢复 | PostgreSQL另一线程更新得到SQLState 55P03，rollback结束失败事务；持锁者commit后更新成功，余额101。显式H2模式检查错误码50200 |

隔离和乐观更新使用明确的双连接操作顺序模拟并发交错，不依赖随机调度。锁实验实际创建工作线程等待数据库行锁。默认结果来自真实PostgreSQL；不将其等同于MySQL的快照读/当前读、间隙锁、死锁牺牲者或全部幻读行为。真实MySQL实验继续使用[MySQL进阶教程](p0-advanced-labs.md)中的isolation、locks、deadlock、optimistic入口。

事务必须在同一连接/受管理的事务资源内完成相关操作。设置autoCommit=false后显式commit或rollback；实验finally中执行rollback结束未完成事务。保存点只允许回滚局部变化，不等于另一个已独立提交的事务。JDBC事务接口定义见[Connection文档](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html)，隔离机制见[PostgreSQL官方说明](https://www.postgresql.org/docs/current/transaction-iso.html)。

## Spring事务传播

`spring`使用DataSourceTransactionManager、JdbcTemplate与TransactionTemplate操作同一实验数据源，无需代理或容器。按以下顺序执行并检查：

1. **REQUIRES_NEW**：外层扣10，内层在独立事务写审计并提交，外层标记回滚。余额恢复100/50，审计保留1行。内层访问独立审计表，避免与外层账户行锁互相等待；池容量足以容纳外层和内层连接。
2. **NESTED**：外层扣10，内层通过保存点将Bob加999后抛异常，外层捕获；内层局部回滚后外层给Bob加10并提交。最终90/60。
3. **REQUIRED**：外层扣5，内层加入同一事务给Bob加5后抛异常。外层即使捕获异常，共享事务仍为rollback-only；提交时抛UnexpectedRollbackException，余额维持此前90/60。

这组验证显式事务模板的传播，不验证`@Transactional`代理、自调用、checked exception默认回滚规则或跨线程传播。相关代理行为见现有MySQL进阶propagation实验。模板用法见[Spring官方文档](https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html)。

## 运行结果的边界

首次完整验证运行在现有WSL PostgreSQL 18.4，数据库和连接池共16项通过，保留17个独立schema（RC/RR使用两个schema）。实验验证真实数据库连接上的DML、约束及事务可见性，不注入掉电或网络故障，不验证分布式事务或生产性能。显式H2模式不作为PostgreSQL行为的证明；MySQL特有结论继续使用对应的真实MySQL实验。
