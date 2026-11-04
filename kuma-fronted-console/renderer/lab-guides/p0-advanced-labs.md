# MySQL、Redis 与线程池进阶实验

每项返回 `lesson`、逐步 `steps`（解释及实际证据）和 `checks`。检查失败会抛出异常，不能把 HTTP 可达当作实验成功。所有实验均由手动请求触发。

## 启动与接口

已启动 Lab 时，在控制台「Lab 实验室」选择对应接口，或向下面的地址发送空正文 POST。
默认本地地址 `http://127.0.0.1:19090/api`；实际端口以当前 Lab 配置为准。

| 组别 | 完整场景 | 单项后缀 |
| --- | --- | --- |
| MySQL | `/lab/mysql/advanced/scenario` | `indexes`、`isolation`、`locks`、`deadlock`、`propagation`、`optimistic` |
| Redis | `/lab/redis/advanced/scenario` | `cache`、`avalanche`、`consistency`、`locks`、`lua`、`fencing` |
| 线程池 | `/lab/threadpool/scenario` | `queue`、`exceptions`、`cancellation`、`starvation`、`context`、`bulkhead` |

单项后缀替换 `scenario`。HTTP 入口复用 Lab 已配置的 DataSource 和 Redis 连接工厂。
MySQL 需要支持 InnoDB，账号需有固定实验库 `kuma_p0_lab` 的建库、建表、读写和删表权限。
死锁实验要求 `innodb_deadlock_detect=ON`，不满足时明确报错，不修改全局配置。

## 不启动 Spring Boot，直接运行

在仓库根目录，用 JDK25 运行：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:threadPoolLab
./gradlew.bat :kuma-project:kuma-project-lab:threadPoolLab -Pp0Lesson=context
```

MySQL / Redis 可以通过现有 WSL 中间件运行，脚本从 Kubernetes 配置读取凭据，仅传入当前进程环境，不打印密码：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-p0-labs-wsl.ps1 -Group mysql
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-p0-labs-wsl.ps1 -Group redis
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-p0-labs-wsl.ps1 -Group all -Test
```

`-Lesson locks` 等可选择单项，`-Distribution` 指定已运行的 WSL 发行版。脚本只查询现有中间件，不启动或重启服务器组件。
也可自行设置进程环境后运行 `mysqlAdvancedLab` / `redisAdvancedLab`：

| 环境变量 | 用途 |
| --- | --- |
| `KUMA_LAB_P0_JDBC_URL` | `jdbc:mysql://地址:3306/?useSSL=false&allowPublicKeyRetrieval=true`，仅实验连接；按环境配置 TLS |
| `KUMA_LAB_P0_JDBC_USER` / `KUMA_LAB_P0_JDBC_PASSWORD` | MySQL 账号和密码 |
| `KUMA_LAB_P0_REDIS_HOST` / `KUMA_LAB_P0_REDIS_PORT` | Redis 地址和端口（默认6379） |
| `KUMA_LAB_P0_REDIS_USERNAME` / `KUMA_LAB_P0_REDIS_PASSWORD` | 可选 ACL 用户、密码 |
| `KUMA_LAB_P0_REDIS_DATABASE` | 可选数据库编号，默认0 |

Redis 的 `cache`、`consistency`、`fencing` 和完整场景同时需要 MySQL；`locks`、`lua`、`avalanche` 仅需 Redis。

## MySQL：先看执行计划，再看并发交错

1. **indexes**：联合索引 `(dept,score)`，对照只选择索引列与额外选择 `payload` 的 EXPLAIN。覆盖/回表比较显式使用同一个索引；另保留最左前缀/后缀条件下优化器自然选择的计划。小表可能全扫，不断言后缀条件永远不走索引。
2. **isolation**：两个真实连接依次读取、更新并提交、再次读取。RR 快照读保留第一次值，RC 看到新提交；`FOR UPDATE` 当前读看到最新值。结果报告三个实际读值。
3. **locks**：持有主键行锁，另一连接更新同一行收到1205，其他行可写；RR 锁定空主键区间阻塞插入，回滚后插入成功。锁等待设为会话级1秒，关闭连接前恢复原值。
4. **deadlock**：两连接先各自锁一行，通过屏障同时反向更新。预期一个1213死锁牺牲者、一个成功提交，不依赖谁先被选中。
5. **propagation**：独立 Spring 事务代理和真实 JDBC，验证 REQUIRED 一起回滚、REQUIRES_NEW 独立提交、NESTED 保存点局部回滚。内层 REQUIRED 抛出异常后，即使外层捕获异常，共享事务仍然被标记 rollback-only，外层提交会抛 `UnexpectedRollbackException`，两侧写入都回滚。再验证同对象自调用绕过代理，以及受检异常默认提交与 `rollbackFor` 显式回滚。REQUIRES_NEW 的独立提交也意味着外层失败不能撤销它，需要按业务原子性要求选择。
6. **optimistic**：两个连接都读到100，分别减10和20再覆盖写，最终80，暴露丢失更新。加入version后第二个旧版本写影响0行，重新读取后有限重试，最终70；再对照条件原子UPDATE也得到70。观察冲突不等于解决业务重试语义，外部副作用不可盲目重放。

每次仅创建 `kuma_p0_lab.p0_<随机ID>` 表，种子200行；结束后删除本次表，保留空实验库。异常路径同样清理。

## Redis：从失效现象到修复对照

1. **cache**：不存在ID连续请求5次，对照无空值缓存5次回源与空值缓存1次；再让6个工作线程同时观察热点未命中，对照6次回源与锁内二次检查后的1次。回源为真实 MySQL 查询，不使用内存计数代替数据库。
2. **avalanche**：8个250ms TTL键真实集中到期，10～17秒错峰键在首波仍存在。报告实际TTL；这是到期分布实验，不是吞吐或容量测试。
3. **consistency**：固定执行交错，复现读者查到旧值、写者更新数据库并删缓存、读者又把旧值填回缓存。随后让读回填和写操作共用锁，验证写者不能越过活跃读者，最后数据库与缓存一致。
4. **locks**：等待A租约真实过期，B获得锁；故意错误DEL展示误释放，再用 Lua 比较token解锁。验证A不能给B续期、B可以手动续期。
5. **lua**：12个请求并发争抢5份库存，检查和扣减同一个Lua执行；预期5次成功、库存0。
6. **fencing**：A租约过期，B获得更大的token；先观察A仍能向MySQL写入，再把token纳入数据库条件UPDATE，拒绝A的陈旧写及相同token重放。本例采用每token单次写协议，不是通用幂等系统；生产token分配必须在故障切换后仍不回退，实验的临时Redis计数器不提供这个保证。

共用锁实验仅验证所展示的交错。进程崩溃、租约过期、数据库提交后Redis不可用等仍需版本控制、可靠失效事件等方案。token正确解锁不保证过期持有者不能继续写业务，手动续期也不是自动watchdog。
每次使用 `lab:p0:{随机ID}:` 前缀，键自带TTL，结束时只删除已登记键；不扫描或清空Redis。

## 线程池：使用确定的同步顺序

1. **queue**：1个工作线程+1个排队位置，第三个任务触发 AbortPolicy；切换 CallerRunsPolicy 后由提交线程执行，观察反压。
2. **exceptions**：`execute` 异常进入未捕获异常处理器，`submit` 异常由 `Future.get()` 的 ExecutionException 查回。
3. **cancellation**：`get(50ms)` 超时后任务仍在运行，显式 `cancel(true)`，任务观察到中断后退出。
4. **starvation**：单工作线程等待同池子任务，100ms超时暴露饥饿；非阻塞 CompletableFuture 阶段组合正常完成。
5. **context**：ThreadLocal不会自动传递，未清理值会留在复用线程；提交时捕获 ThreadLocal/MDC，finally恢复工作线程原上下文，并保留调用方MDC。
6. **bulkhead**：慢依赖占住共享池的唯一线程，使健康请求排队；放到独立隔离池的健康请求仍可完成。对慢调用设置CompletableFuture超时，观察Future已失败但底层任务仍占工作线程；队列满后拒绝额外请求，释放阻塞后才排空。返回超时不会自动回收资源，资源隔离、客户端超时与取消需要共同设计。

## 设计取舍与成立条件

| 机制 | 保证的范围 | 必须观察的失败条件 |
| --- | --- | --- |
| 数据库隔离 | 数据库事务内的读取/锁定语义 | 应用层读改写仍可能丢失更新 |
| 乐观版本检查 | 同一记录上符合协议的条件更新 | 冲突识别后不能盲目重复有副作用的业务 |
| Redis租约锁 | 租约有效期间的持有者协调 | 过期持有者仍能向下游写入 |
| fencing条件写 | 下游拒绝更旧token | token回退、绕过协议的写入、每token多次写的语义 |
| 缓存回填互斥 | 参与同一锁协议的回填/失效顺序 | 进程崩溃、租约失效、数据库提交后缓存失效失败 |
| 依赖资源隔离 | 局部拥塞不占用其他依赖的工作池 | 总线程/连接预算、队列容量、拒绝策略与降级成本 |
| Future超时 | 等待结果的截止时刻 | 底层I/O、事务和任务可能仍在执行 |

结果报告保留故障交错与实际断言。时间、吞吐和容量结论必须来自相应负载与环境，不能由小规模机制实验外推。

每次创建独立小型线程池，每个等待有上限，finally关闭并检查终止。每组实验同一Lab进程最多运行一个，不占用应用线程池。

## 验证

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:test --tests 'com.kuma.cloud.lab.p0.*'
```

不配置中间件环境时，真实集成测试明确跳过；线程池实验与调用方上下文恢复测试照常执行。
使用上面的 WSL `-Group all -Test` 执行真实MySQL/Redis集成测试，并检查本次表和键已清理。
