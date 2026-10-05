# Webhook 与 Socket 通信实验

示例仅依赖 JDK 25，绑定 `127.0.0.1` 的系统分配临时端口，退出时关闭服务和工作线程。
所有检查无需 `-ea`，失败抛异常。无需启动 Spring、Nacos，也无需配置第三方 Webhook。

## 运行方式

IDE 运行 `com.kuma.cloud.lab.network.NetworkLearningDemo.main()` 执行全部实验。
也可以分别运行 `WebhookLesson.main()` 和 `SocketLesson.main()`。
从仓库根目录使用 PowerShell：

```powershell
$lab = 'kuma-project/kuma-project-lab'
New-Item -ItemType Directory -Force "$lab/build/network-lab/classes" | Out-Null
$sources = Get-ChildItem "$lab/src/main/java/com/kuma/cloud/lab/network" -Filter '*.java' | ForEach-Object FullName
javac --release 25 -encoding UTF-8 -d "$lab/build/network-lab/classes" $sources
java -cp "$lab/build/network-lab/classes" com.kuma.cloud.lab.network.NetworkLearningDemo
```

## Webhook：发送方主动投递 HTTP 事件

本地 HttpServer 模拟接收方，HttpClient 模拟发送方。请求携带原始 UTF-8 JSON 字节，以及 `X-Lab-Event-Id`、`X-Lab-Timestamp`、`X-Lab-Signature`。
签名为 `HMAC-SHA256(secret, id + 换行 + timestamp + 换行 + 原始 body)`，十六进制编码。
这是自定义教学协议；GitHub 的签名仅针对原始 body，不能直接将本协议用于 GitHub 接收端。
body 必须按原始字节验签，不能先解析 JSON 再序列化后计算。

| 实验 | 必须检查的结果 |
| --- | --- |
| 合法事件首次投递 | 202，记录一份模拟业务结果 |
| 重复投递同一事件 | 200，不产生第二份结果 |
| 并发重复投递 | 一次 202、一次 200，原子去重 |
| body 篡改或事件 ID 篡改 | 401，不处理 |
| 无效签名 | 401 |
| 超过正负五分钟的时间戳 | 401，拒绝过期与过远未来事件 |
| 同一 ID 但合法签名的不同 body | 409，避免静默忽略冲突 |
| 临时失败后重试 | 首次 503，第二次 202；两次投递保持相同事件 ID |
| 请求缺少头、错误方法、body 超过 4 KiB | 分别 400、405、413 |

验签使用 MessageDigest.isEqual 比较摘要；去重使用 ConcurrentHashMap.putIfAbsent。
重试有三次上限，模拟 25ms、50ms 的指数退避；本例只注入 503，不演示断网或响应丢失。
拒绝请求、重复请求和第一次临时失败都不写业务结果，最终仅三个有效事件被记录。

本例的事件表就是模拟业务效果，仅在进程内保留。重启会丢失去重记录，不提供端到端 exactly-once。
真实业务需要持久化事件 ID，并把去重记录与业务变更纳入一致的事务或可靠队列流程；内存 putIfAbsent 后调用外部系统并不能保证事务性。
本例固定密钥仅用于本机实验，HTTP 明文仅用于回环通信。对接服务时遵循其真实签名、时间窗口、重试和响应状态约定。

## TCP：字节流与应用层分帧

原有 `javacore` 的 SocketLabSupport 演示单行回显。新增 SocketLesson 在一个连接上交换三个长度前缀帧：
四字节大端长度 + 对应数量的 payload 字节，上限 64 KiB。

1. 把第一条消息的头与数据拆成多次 write；把后两条消息合并成一次 write。接收端按长度解析，不能假定一次 read 对应一次 write。
2. 内容包括中文、换行、零字节和空帧，检查返回字节完全一致。分帧长度按 UTF-8 **字节数**，不是 Java 字符数。
3. 客户端 shutdownOutput 后继续读取回显；服务端在完整帧边界收到 EOF 并关闭，客户端最后也读到 EOF。
4. 用固定字节序列检查截断头、截断 body、负长度与超限长度会被拒绝，最大合法帧可解析。这部分单独验证与网络收发共用的解析器。
5. 服务端由门闩控制暂不发送数据，客户端设置 SO_TIMEOUT，检查 SocketTimeoutException。释放服务端后读取成功，证明读取超时未自动关闭连接。

拆分/合并 write 验证的是应用协议对字节流的处理，不保证操作系统真的以某种 TCP 分段发送。
SO_TIMEOUT 是阻塞读取超时，不是整条业务请求总时限；连接建立另设 connect 超时。
干净 EOF 与帧中途截断不同，不能将后者当成成功结束。

## UDP：独立数据报

通过 DatagramSocket 发送两条小数据报，服务端分别回显；接收按实际 packet length 提取内容，检查两个消息均收到，不假定顺序。
UDP connect 只是设置默认对端和过滤来源，不建立 TCP 式连接或可靠传输。
实验在回环环境要求收到两个数据报；失败会超时退出。成功不代表 UDP 在公网保证送达、顺序或去重。
接收缓冲区不足时数据报可能截断，本例缓冲区大于所有测试消息，不发送大数据报。

## 资料

- [GitHub Webhook 验签](https://docs.github.com/en/webhooks/using-webhooks/validating-webhook-deliveries)
- [GitHub Webhook 实践与重复投递](https://docs.github.com/en/webhooks/using-webhooks/best-practices-for-using-webhooks)
- [JDK Socket](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/Socket.html)
- [JDK DatagramSocket](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/DatagramSocket.html)
