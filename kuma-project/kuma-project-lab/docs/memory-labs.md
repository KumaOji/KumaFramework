# Java 内存与 Linux 空间实验

这些示例自带结果检查，失败返回异常或非零退出码。无需 Spring、Nacos、数据库或 root。

## 先区分三个层次

| 层次 | 要解决的问题 | 对应实验 |
| --- | --- | --- |
| Java 内存模型 JMM | 跨线程可见性、原子性、有序性 | lock + JmmLesson |
| JVM 运行时数据区域 | 堆、线程栈、方法区实现及堆外缓冲区 | JvmMemoryLesson |
| Linux / CPU | 用户虚拟地址、内核管理、执行权限与系统调用 | linux_memory_lab.c |

用户态/内核态是 CPU 执行权限；用户空间/内核空间是地址空间划分。
Java 堆、线程栈、元空间和直接缓冲区都属于进程用户空间，堆外不是内核空间。
JMM 的“工作内存”概念不能直接等同于 JVM 栈或 CPU 缓存。

## Java：运行入口

在 IDE 直接运行 `com.kuma.cloud.lab.memory.MemoryLearningDemo.main()`。
它先运行已有的 lock 实验，再运行新的 JMM、JVM 区域实验。
也可以从仓库根目录仅编译这些 JDK 示例（JDK 25）：

```powershell
$lab = 'kuma-project/kuma-project-lab'
New-Item -ItemType Directory -Force "$lab/build/memory-lab/classes" | Out-Null
$sources = Get-ChildItem "$lab/src/main/java/com/kuma/cloud/lab/lock","$lab/src/main/java/com/kuma/cloud/lab/memory" -Filter '*.java' | ForEach-Object FullName
javac --release 25 -encoding UTF-8 -d "$lab/build/memory-lab/classes" $sources
java -Xms32m -Xmx64m -XX:MaxDirectMemorySize=16m -cp "$lab/build/memory-lab/classes" com.kuma.cloud.lab.memory.MemoryLearningDemo
```

1. **可见性和原子性**：VolatileLesson 发布普通字段；协调两次读改写，稳定演示丢失更新。CAS、锁和门闩实验验证相应同步语义。
2. **重排序/Store buffering**：两线程各写一个字段，再读取对方字段，统计四种结果。plain 字段允许 `(0,0)`，但不保证每次出现；可能涉及编译器优化或硬件行为，单靠结果不能识别具体哪层发生重排。两个字段均为 volatile 时检查 `(0,0)` 必须为零。迭代屏障只协调每轮边界，不在轮内给两个线程建立先后顺序。
3. **release/acquire**：VarHandle 发布标记，在读取方检查之前普通字段已可见。检查发生在 reader 内部，不借助主线程的 Future.get 发布数据。
4. **堆与 GC 管理区域**：分配并触碰 8 MiB 数组，打印 Heap、Non-heap 和各内存池用量。池名称随 JVM/GC 而变；HotSpot 方法区常由元空间承载，不能把所有 non-heap 都叫方法区。分配计数受 GC、对齐及测量时机影响，不断言固定增量。
5. **线程栈与共享对象**：两个线程递归传递各自局部值，同时读取同一个堆数组。检查各自返回值不同。局部引用不代表对象也在栈上；JIT 可能内联、消除分配，该实验展示语义，不声称观测物理栈布局。不制造栈溢出或 OOM。
6. **堆外内存**：分配 4 MiB DirectByteBuffer，检查读写，打印 BufferPoolMXBean 用量。缓冲区的 Java 包装对象与原生存储不同；回收不保证即时发生。Non-heap MXBean 不涵盖全部 native 内存。

## Linux：系统调用与地址空间

在 Linux/WSL 安装好 gcc 后，从仓库根目录运行：

```bash
bash kuma-project/kuma-project-lab/scripts/run-linux-memory-lab.sh
# 单独执行：cpu、syscall、memory
bash kuma-project/kuma-project-lab/scripts/run-linux-memory-lab.sh memory
# 可选：需已有 strace；不自动安装软件
bash kuma-project/kuma-project-lab/scripts/run-linux-memory-lab.sh trace
```

脚本仅编译独立 C 程序，不修改 JNI 构建流程，也不更改服务器配置。

1. **用户态计算 vs 系统调用**：分别执行一百万次数学计算和显式 `syscall(SYS_getpid)`。getrusage 打印 user/system CPU 时间、上下文切换数，另打印墙钟时间。显式 syscall 避开可能缓存结果或通过 vDSO 完成的库函数。时间统计粒度、虚拟化和负载会影响数值，不要求某项一定更快或 system 一定大于零。
2. **权限切换 vs 调度切换**：进入内核不必切换进程；上下文切换计数不等于 syscall 次数。trace 模式统计 getpid 调用数；strace 本身显著改变耗时，不能与未跟踪结果做性能结论。
3. **匿名 mmap 与缺页**：映射 256 页，逐页写入，输出 minor fault 增量。页表、首次触页和透明大页会影响数量，不要求恰好 256。缺页可以进入内核，即使对应源码没有显式 syscall。
4. **用户虚拟地址布局**：打印 `/proc/self/maps`，观察代码、共享库、匿名映射、`[stack]`、`[heap]` 等。地址受 ASLR 影响，标签也受实现影响；该文件不显示内核地址空间或物理地址。
5. **pread 与 mmap**：在新建临时文件上创建共享/私有映射。MAP_SHARED 写入 42，msync 后 pread 到用户缓冲区并检查 42；MAP_PRIVATE 写入 99，检查文件和共享映射仍是 42，演示写时复制。程序退出前解除映射并关闭临时文件。
6. **用户缓冲区访问权限**：mprotect 把匿名映射改为 PROT_NONE，再请求 pread 写入该地址，检查内核返回 EFAULT；恢复读写权限后检查调用成功。演示内核校验用户指针，不需要制造进程崩溃。它验证用户映射的保护机制，不是直接读取内核地址的实验。

pread 通常通过内核将文件数据复制到用户缓冲区；mmap 建立用户虚拟地址映射，可以普通加载/存储访问文件页，仍涉及缺页、页缓存、写回和内核管理。该实验不直接测量复制次数，也不声称 mmap 没有任何内核工作。

## 原始资料

- [JLS 17.4.5 happens-before](https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html#jls-17.4.5)
- [VarHandle 内存访问模式](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/VarHandle.html)
- [JVMS 2.5 运行时数据区域](https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-2.html#jvms-2.5)
- [Linux getrusage](https://man7.org/linux/man-pages/man2/getrusage.2.html)
- [Linux mmap](https://man7.org/linux/man-pages/man2/mmap.2.html)
- [Linux proc_pid_maps](https://man7.org/linux/man-pages/man5/proc_pid_maps.5.html)
