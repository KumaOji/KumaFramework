/**
 * Java 并发与锁学习路线：从 LockLearningDemo.main 运行全部实验，无需 Spring 或中间件。
 *
 * <h2>先区分三个问题</h2>
 * <ul>
 *   <li>原子性：操作是否不可分割？count++ 是读取、计算、写回三步。</li>
 *   <li>可见性：一个线程写入后，另一个线程通过什么同步关系观察到它？</li>
 *   <li>有序性：编译器与 CPU 可以重排操作，但须遵守 Java 内存模型的约束。</li>
 * </ul>
 * happens-before 是可见性与顺序规则，不是墙上时钟的先后顺序。
 * 同一监视器的释放到后续获取、volatile 写到后续读、线程 start 和 join
 * 都能建立相应关系。Thread.sleep 只暂停线程，不提供共享数据的同步保障。
 *
 * <h2>按使用、原理、源码顺序学习</h2>
 * <ol>
 *   <li>VolatileLesson：发布普通字段，稳定复现 volatile 计数的丢失更新。</li>
 *   <li>CasLesson：CAS 重试、原子计数、ABA 和版本号。</li>
 *   <li>ReentrantLockLesson：可重入、互斥、超时获取和 Condition。</li>
 *   <li>AqsLesson：state、CAS、独占获取、等待队列、阻塞和唤醒。</li>
 *   <li>CountDownLatchLesson：共享模式、一次性门闩、完成通知与结果发布。</li>
 * </ol>
 * volatile 提供相关可见性和顺序约束，CAS 提供比较并更新的原子操作，
 * AQS 管理同步状态和等待线程。ReentrantLock 使用独占模式，CountDownLatch 使用共享模式。
 * 使用 CAS 不等于整个算法无锁：CAS 自旋锁仍依赖持锁线程释放，持锁者停顿会阻碍其他线程。
 * lock-free 要求系统整体持续取得进展；wait-free 要求每个操作在有界步数内完成。
 *
 * <h2>使用边界</h2>
 * 状态标记可用 volatile，独立计数可用原子类，多字段业务不变量通常用锁保护，
 * 等待一组任务结束可用 CountDownLatch。volatile 引用不会让其内部对象自动线程安全；
 * 多个原子字段也不会形成一个原子快照。所有访问必须遵守同一种同步协议。
 * synchronized 提供互斥、可重入与可见性，语言保证释放监视器；ReentrantLock
 * 提供超时、中断获取和多个 Condition，须手动配对释放。
 * 实验中的协调器用于控制执行交错，不能据此推断业务可以省略同步。
 *
 * <p>源码阅读以实际 JDK 为准：先读 ReentrantLock.Sync 的获取/释放，
 * 再读 AQS 获取失败的排队流程，最后读 CountDownLatch.Sync 的共享获取/释放。
 * AQS 节点字段与实现会随版本变化，避免照搬旧版本逐行解释。</p>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-17.html">JLS 17：内存模型</a>
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/locks/AbstractQueuedSynchronizer.html">AQS 文档</a>
 */
package com.kuma.cloud.lab.lock;
