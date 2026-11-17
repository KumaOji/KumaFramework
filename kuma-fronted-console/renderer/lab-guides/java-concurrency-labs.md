# Java 锁与并发实验

console → Lab 搜索“锁”“并发”或具体API名称，选择完整实验或单项卡片。统一使用JDK25，在仓库根目录运行，无需Lab服务或中间件。

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:javaConcurrencyLab
./gradlew.bat :kuma-project:kuma-project-lab:javaConcurrencyLab -PconcurrencyLesson=locks
./gradlew.bat :kuma-project:kuma-project-lab:javaConcurrencyLab -PconcurrencyLesson=concurrency
./gradlew.bat :kuma-project:kuma-project-lab:javaConcurrencyLab -PconcurrencyLesson=reentrant
```

`all`串联十二项；`locks`运行七项锁实验；`concurrency`运行五项并发实验。失败会抛异常并以非零状态退出，检查不依赖`-ea`。所有等待均有超时或可中断路径，线程池在finally中shutdownNow并等待回收。实验不比较吞吐或锁性能，不以线程调度顺序作为保证。

## 各种锁

| 单项 | 实际操作 | 预期证据 |
| --- | --- | --- |
| monitor | 两线程共享同一synchronized monitor，嵌套重入；持锁时抛异常 | 计数2000，异常退出后其他线程能获取monitor |
| reentrant | 公平ReentrantLock嵌套两次，另一线程timed tryLock和lockInterruptibly；Condition等待/通知 | holdCount=2，timed tryLock失败，被取消的等待者响应中断，Condition观察到ready=true |
| readwrite | 主线程持读锁，另一个线程尝试读/写；主线程将写锁降级 | 读锁可共享，写锁获取失败，降级后readHold=1 |
| stamped | 乐观读取旧值，另一个线程获取写锁修改值，校验旧stamp后回退 | 旧值1、validate=false、回退读锁得到2 |
| semaphore | 两个任务占满两个许可；第三个任务尝试进入 | active=2，第三个获取失败，结束后许可恢复为2 |
| waitnotify | 条件循环wait释放monitor，写者进入并更新值后notifyAll | 读者获得42；未持有monitor调用notifyAll抛预期IllegalMonitorStateException |
| deadlock | 两线程分别持不同ReentrantLock，再可中断地等待对方锁 | 双方均未完成；取消后至少一个等待被中断，线程退出且两把锁均释放 |

synchronized的互斥前提是参与方使用同一monitor。实例synchronized锁this，static synchronized锁对应Class；wait仅释放正在等待的monitor。wait/Condition都在while中检查条件，处理虚假唤醒；通知后等待者仍要重新获得锁。

ReentrantLock实验始终在finally中unlock。公平配置不保证固定的执行顺序，无参tryLock也可以插队；本例不尝试用多次运行的顺序统计证明公平性。等待锁的线程通过Future.cancel(true)请求中断，代码验证中断路径实际执行。

读写锁允许读共享；写锁降级时先获取读锁再释放写锁，以连续保护数据。不要持有读锁再阻塞升级写锁。StampedLock的乐观读没有锁住数据，校验失败后必须重新读取；它不是可重入锁。本例刻意安排写入使旧stamp失效，不用偶然竞争作为断言。

Semaphore是共享许可计数器，不记录线程所有权；只有成功获取许可才应release，避免许可膨胀。它用于限制同时占用资源的任务数量，本实验不把它作为每秒请求速率限制器。

交叉持锁实验用Latch固定“双方持有第一把锁”的状态，再调用lockInterruptibly形成循环等待；主线程取消Future来恢复，避免留下无法回收的monitor死锁。中断一方后另一方可能正常取得第二把锁，所以只断言至少一个响应中断，并验证所有锁释放。它演示本次循环等待的条件与恢复，不声称完成生产线程转储诊断。统一锁顺序可以避免本例中的循环等待。

## 并发实验

| 单项 | 实际操作 | 预期证据 |
| --- | --- | --- |
| atomic | 屏障固定两线程相同读取值再覆盖；AtomicInteger/ConcurrentHashMap.compute对照；模拟ABA | 普通计数100，原子和单键compute计数200；普通CAS接受ABA，旧stamp的CAS被拒绝 |
| coordination | Latch控制同时开始与完成，Barrier复用，Phaser注销 | 四次工作，Barrier两轮，Phaser注册数0并终止 |
| queue | 容量2的ArrayBlockingQueue，生产者和消费者传递1～5与终止标记 | 满时offer返回false，FIFO为1～5，消费完队列为空 |
| future | CompletableFuture组合和异常降级；ExecutorService Future取消 | thenCombine结果42，异常降级-1且cause保留；取消后get抛CancellationException，任务实际响应中断 |
| threadlocal | 单工作线程依次执行任务，对照主线程值、残留与remove | 不自动传播caller，复用线程读到task-one，finally.remove后下一任务为null |

原子更新实验用屏障明确固定读写交错，因此丢失更新次数是确定的；它不统计自然调度的概率，也不作为普通字段可见性实验。两个写线程的Future结果都检查，异步异常会传播到主线程。CAS的ABA对照用顺序操作模拟同一历史，版本戳必须随状态变化更新。ConcurrentHashMap.compute的原子范围在单键操作，不提供跨键事务。

CountDownLatch是一次性通知，CyclicBarrier可复用，Phaser支持注册与注销。所有协作均在有限等待后抛错，不能把一次成功执行解释为没有调度风险。

队列用限时offer/poll提供等待上限，满队列返回失败代表容量背压。单消费者使用一个终止标记，多消费者需要对应数量标记或独立生命周期协议。

Future取消只发出请求，需要任务响应中断。本实验验证的是ExecutorService返回的Future；CompletableFuture.cancel(true)不保证中断底层计算。异常降级只影响派生结果，原始future仍保留失败原因。

ThreadLocal绑定线程而非逻辑任务。示例故意让首个任务留下值，后续任务观察残留，再通过finally.remove验证清理。实际嵌套上下文可能需要保存并恢复旧值；此示例限定为最外层任务，复用行为由单工作线程保证。
