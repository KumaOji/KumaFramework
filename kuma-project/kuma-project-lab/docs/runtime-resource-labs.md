# 类加载、内存、CPU、线程池与连接池实验

在console → Lab搜索“类加载”“内存”“CPU”“线程池”或“连接池”，选择完整组或单项。这里在已有Java基础、JMM、锁和线程池进阶示例上补充失败路径、生命周期与容量约束。

统一使用JDK25，在仓库根目录运行。所有实验有显式检查，失败抛异常，不依赖`-ea`；等待有超时或可回收的结束条件。CPU负载固定最多两个平台工作线程，内存分配为MiB级，连接池最多两个连接。运行时十二项无需外部中间件；连接池默认使用现有WSL PostgreSQL的`kuma_lab`专用库。无需启动LabApplication。

```powershell
# 类加载、内存、CPU、线程池共十二项
./gradlew.bat :kuma-project:kuma-project-lab:javaRuntimeLab

# 选择一组：classloading / memory / cpu / threadpool
./gradlew.bat :kuma-project:kuma-project-lab:javaRuntimeLab -PruntimeLesson=cpu

# 选择单项
./gradlew.bat :kuma-project:kuma-project-lab:javaRuntimeLab -PruntimeLesson=loader-failure

# 四项真实JDBC连接池实验
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group connectionpool
powershell -NoProfile -ExecutionPolicy Bypass -File kuma-project/kuma-project-lab/scripts/run-database-labs-wsl.ps1 -Group connectionpool -Lesson capacity
```

每次命令创建新JVM，尤其初始化失败实验不能在同一个定义加载器里重跑并期望第一次的结果。数据库连接默认使用PostgreSQL，HikariCP沿用项目已有JDBC依赖。只有显式指定`-PdatabaseEngine=h2`时独立数据库任务才增加H2运行依赖，它不进入Lab应用常规runtimeClasspath或bootJar。

## 类加载进阶

| 单项 | 操作与预期 |
| --- | --- |
| loader-failure | initialize=false不执行static。首次主动初始化抛ExceptionInInitializerError，其cause为原始异常；再次主动初始化抛NoClassDefFoundError，计数保持1 |
| loader-identity | 对payload定向定义类，其余类型默认委派。同一个加载器缓存Class，不同加载器同名类不兼容，String仍为同一核心类；缺失类抛ClassNotFoundException |
| loader-context | 临时URLClassLoader读取专属资源，显式通过当前线程TCCL查找；finally恢复原TCCL，关闭加载器并删除已知临时文件 |

初始化失败后的NoClassDefFoundError不等同于class文件不存在，应沿首次初始化失败的cause定位问题。类身份包含定义加载器。TCCL是查找上下文，只有采用它的代码/API才会遵循它；不要认为修改TCCL就能改变所有Class.forName或已加载类的身份。

## 内存区域与生命周期

| 单项 | 操作与预期 |
| --- | --- |
| memory-regions | 4MiB堆数组、2MiB直接ByteBuffer读写检查，打印Heap/Non-heap/BufferPool/GC计数 |
| memory-retention | 容器持有8块256KiB数组，弱引用仍能找到对象；clear后容器为空，请求GC后仅记录弱引用是否清除 |
| memory-lifetime | Arena.ofConfined分配16字节，读写long=42；越界抛IndexOutOfBoundsException，其他线程访问抛WrongThreadException，close后访问抛IllegalStateException |

内存使用量和GC次数是观测值，不要求分配前后固定增长。non-heap并不等于整个进程原生内存；直接缓冲区回收和RSS下降也没有固定时间保证。清除缓存是解除逻辑持有，不能单凭一次GC请求判断泄漏是否解决。此实验不制造OOM、不等待弱引用必然回收。

Arena部分对原生段的访问边界和生命周期做确定检查，跨线程访问在Arena仍存活时验证，随后关闭并验证scope失效。Arena的线程及生命周期规则见[JDK25 Arena文档](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Arena.html)。

## CPU与线程调度

| 单项 | 操作与预期 |
| --- | --- |
| cpu-time | 对有限计算及60ms带超时Latch等待，分别输出wall纳秒与当前平台线程CPU纳秒；不支持计时时输出-1 |
| cpu-parallel | 将200万项计算拆为两个100万项任务，合并结果必须等于串行；打印两种耗时 |
| cpu-states | 持有monitor时观察另一个线程BLOCKED，并核对ThreadInfo锁持有者ID；释放后任务继续。另一个任务等待Latch时观察TIMED_WAITING，通知后完成 |

输出availableProcessors用于理解当前JVM可见的处理器数量，不据此无限创建线程。CPU计时是否支持由ThreadMXBean检查；实验临时启用计时并恢复原状态。[ThreadMXBean文档](https://docs.oracle.com/en/java/javase/25/docs/api/java.management/java/lang/management/ThreadMXBean.html)说明它监控平台线程，CPU时间具有纳秒表示精度但不保证纳秒准确度。

wall time含调度、阻塞和运行过程；RUNNABLE也不直接表示线程占满CPU。计时受JIT、调度及系统负载影响，实验不要求固定busy/wait比例或并行更快，也不把一次运行作为性能基准。平台不支持CPU计时时，其他正确性检查仍继续执行。

## 线程池容量与关闭

| 单项 | 操作与预期 |
| --- | --- |
| pool-growth | core=1、max=2、queue=1。第一个任务占核心线程，第二个先排队，第三个使队列已满时扩到max，第四个被拒绝。释放后结果1+2+3=6 |
| pool-shutdown | shutdown拒绝新任务但完成已接受任务；shutdownNow中断运行任务并返回未开始的FutureTask。返回的排队Future原本未完成，实验显式cancel，避免调用方永久等待 |
| pool-virtual | 创建32个虚拟线程任务，以Semaphore限制下游占用3；观测峰值3，所有任务均为虚拟线程，结束时许可全部归还 |

shutdownNow是中断请求，需要任务协作响应；它返回的未开始任务不应被误认为已经取消。本例显式管理这些Future，并等待执行器终止。虚拟线程执行器为每个任务创建虚拟线程，并不采用固定工作线程复用；任务数和数据库等下游容量是不同的约束。

## HikariCP与PostgreSQL真实连接池

每项创建独立HikariDataSource，在现有WSL PostgreSQL的`kuma_lab`内创建`lab_connection_pool_<uuid>` schema，真实执行SQL。通过try-with-resources归还逻辑连接并关闭池，保留schema和数据。脚本运行时从既有Pod读取凭据，不使用应用DataSource。连接及结果查询方法见[数据库与事务实验教程](database-transaction-labs.md)。

| connectionPoolLesson | 操作与预期 |
| --- | --- |
| reuse | 最大1连接，两次借用不同逻辑代理。逻辑close后句柄已关闭，但unwrap得到的实际PostgreSQL连接相同且仍打开 |
| timeout | 持有唯一连接，其他线程获取连接超时抛SQLTransientConnectionException；归还后重新借用且SELECT 42成功 |
| transaction | 关闭未提交的脏连接后插入被回滚，下一借用者的autoCommit和隔离级别恢复；随后显式commit一行、rollback另一行，查询仅一行 |
| capacity | 四个工作线程竞争最多两个连接，使用Latch固定前两项持有状态，通过池指标观察等待者。释放后四项均插入成功，四行提交、连接占用峰值2 |

连接获取超时与SQL执行超时、网络读超时不同。本例timeout配置300ms，只验证获取超时异常，不断言精确耗时。capacity使用3000ms获取上限，在等待者出现后立即放行。

连接close通常是归还逻辑句柄，不应直接操作unwrap后的物理连接；unwrap仅用于本实验验证身份。事务复位实验验证Hikari在本例跟踪的JDBC属性，不保证任意SQL会话变量都能清理。默认使用真实PostgreSQL；显式离线模式可执行`./gradlew.bat :kuma-project:kuma-project-lab:javaConnectionPoolLab -PdatabaseEngine=h2`，但它不验证PostgreSQL引擎行为。配置含义见[HikariCP官方说明](https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby)。

工作线程多于连接数时，部分线程会等待连接；虚拟线程也不会增加物理连接容量。持有外层连接再等待需要新连接的嵌套任务，可能耗尽池；本实验展示容量边界，不主动留下永久的连接池死锁。
