# Socket 与 HTTP Session 实验

Socket 是通信端点，HTTP Session 是应用层的服务端会话。本组实验把两个层次放在一起对照学习。
所有服务器只监听 `127.0.0.1` 的随机端口，无需 Nacos、WSL 或数据库，不使用业务用户的 Cookie 或 Session。

## 快速运行

JDK25，从仓库根目录执行：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:socketLab
./gradlew.bat :kuma-project:kuma-project-lab:sessionLab
```

完整运行 Socket 或 Session；以下命令选择专项：

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:socketLab -PsocketLesson=lifecycle
./gradlew.bat :kuma-project:kuma-project-lab:sessionLab -PsessionLesson=isolation
./gradlew.bat :kuma-project:kuma-project-lab:sessionLab -PsessionLesson=lifecycle
./gradlew.bat :kuma-project:kuma-project-lab:sessionLab -PsessionLesson=expiry
```

Socket 生命周期类 `com.kuma.cloud.lab.network.SocketLifecycleLesson` 只依赖 JDK，可直接运行 main。
Session 类 `com.kuma.cloud.lab.session.SessionLearningDemo` 使用已有 Tomcat / Servlet 依赖，通过 Gradle 或 IDE 的模块 classpath 运行。
Session 实验使用真实 Servlet 容器，不是手写 Map 模拟 Session，也无需启动 LabApplication。

## 1. Socket：长连接与重连

完整 Socket 实验先执行已有 TCP 长度分帧、半关闭、读取超时后继续使用连接、UDP 数据报实验，详见网络教程。
新增生命周期实验使用两个真实 TCP 连接：

| 步骤 | 操作 | 预期证据 |
|---|---|---|
| 建立连接 | connect，设置 connect/SO_TIMEOUT、TCP_NODELAY | local / remote 两端地址、connected 状态 |
| 连续收发 | 第一次连接连续发送三条 UTF 消息 | 回复为 `1:1:消息1`、`1:2:消息2`、`1:3:消息3` |
| 半关闭 | 空帧标记消息结束，然后 shutdownOutput | 接收 `END:3`，随后读到 EOF；本地尚未 close |
| 关闭 | close，然后尝试复用同一个 Socket | isClosed=true、isConnected=true，connect 被拒绝 |
| 重连 | 使用新的 Socket 连接同一服务端 | 新客户端端口，回复 `2:1:消息1`，连接内计数从1开始 |

服务端的连接编号用于对照，序号是每次 accept 后新建的连接内变量，不是全局用户 Session。
本实验使用 DataOutputStream.writeUTF / DataInputStream.readUTF，包含 Java 的长度前缀与 modified UTF-8，
适合 Java 间的小消息教学；不能当作任意语言通用的 UTF-8 协议，单条数据有长度限制。
空 UTF 帧是本实验协议终止标记，真正 EOF 由 shutdownOutput 产生，二者分别检查。

isConnected 表示曾成功建立连接，close 后仍可能为 true，因此不是连接存活探针。
读取 EOF 表示对端发送方向关闭；SO_TIMEOUT 表示这一轮阻塞读取超时，不自动关闭连接，也不代表整个请求总超时。
TCP_NODELAY 只是启用选项，本实验不据此声称测出了吞吐或延迟提升。
实验会等待服务线程的结果，关闭监听器和 Socket，并停止工作线程。

## 2. Session：Cookie 延续与客户端隔离

独立 Tomcat 使用内存 StandardManager，Session Cookie 名为 `KUMA_LAB_SESSION`。
每一步报告保存 Set-Cookie、服务端 Session ID、visits、isNew、requestedSessionIdValid 和请求的 remotePort。
这些 ID 只属于即将关闭的教学容器。

1. `GET /inspect` 使用 getSession(false)，Session 不存在时不创建、不发送 Session Cookie。
2. 客户端A `POST /visit` 创建 Session，visits=1、isNew=true，响应带 HttpOnly、SameSite=Lax Cookie。
3. A 的 CookieManager 自动带回 Cookie，再次访问 ID 相同、visits=2、isNew=false。
4. B 使用独立 CookieManager，获得不同 ID，visits=1。
5. 另一个 HttpClient 共享A的 CookieManager，创建不同 TCP 连接；报告 remotePort 变化，但 ID 相同、visits=3。
6. 不配置 CookieManager 的客户端不保存 Cookie，两次访问分别得到新 ID 与 visits=1。

Session 由 Cookie 中的标识与服务端状态关联，不由某条 TCP 连接的客户端端口绑定。
Cookie 保存在客户端，visits 属性保存在服务端；知道 ID 不等于把完整 Session 数据存进 Cookie。
HTTP keep-alive 的连接复用也不自动延续 Session：无 Cookie 的请求可以在同一连接上创建不同 Session。

## 3. Session：更换 ID、注销与重建

1. 建立 Session 并设置 visits。
2. changeSessionId 更换 ID，验证 visits 保留，新 Cookie 被客户端接收。
3. 独立客户端显式重放旧 Cookie，getSession(false) 返回空、requestedSessionIdValid=false。
4. 新 Cookie 可以访问保留的属性。
5. invalidate 删除服务端会话，同时响应 Cookie Max-Age=0 清除客户端 Cookie。
6. 分别用注销后的客户端和重放注销前 Cookie 的客户端查看，均不能恢复旧 Session。
7. 再次创建得到不同 ID、visits=1。

更换 ID 可用于登录后的会话固定防护，本实验只验证 Servlet 行为，不实现真实登录鉴权。
仅删除客户端 Cookie 不等于删除服务端 Session；实验同时调用 invalidate 并清除 Cookie。
SameSite/HttpOnly 检查来自真实 Set-Cookie 属性，但 Java HttpClient 不能证明浏览器的跨站 Cookie 行为或 JS 读取限制。
本机回环使用 HTTP，未加 Secure；正式 HTTPS 应按部署方式配置 Secure 和 Cookie 策略。

## 4. Session：空闲过期

1. 只对本次 Session 设置 maxInactiveInterval=1秒。
2. 无请求等待2.1秒，不轮询有效 Session，避免访问刷新空闲计时。
3. 使用仍保留的旧 Cookie 查看，容器拒绝过期会话。
4. 创建新会话，得到新 ID 和初始计数。

空闲有效期是两次访问间的允许间隔，不等于 Cookie 在客户端的有效期。
本例验证访问时容器判定过期，不声称后台清理线程一定在某个精确时刻释放数据。
实验约需几秒；有效期1秒用于教学，不是生产建议。
容器结束时关闭 Connector、清理内存 Session，并删除本次临时目录；不把会话持久化到磁盘。
这组实验不涵盖 Redis 分布式 Session、跨实例复制、WebSocket 会话或并发更新 Session 属性。

## 控制台与 Lab HTTP

控制台学习卡片无需启动 Lab，选择卡片不会自动执行实验，复制命令后在终端运行。
Lab 已运行时还可调用以下接口，均无需请求体：

| 方法 | 路径 | 实验 |
|---|---|---|
| POST | /api/lab/socket/scenario | 全部 Socket 实验 |
| POST | /api/lab/socket/lifecycle | Socket 生命周期 |
| POST | /api/lab/session/scenario | 全部 Session 实验 |
| POST | /api/lab/session/isolation | Cookie 与客户端隔离 |
| POST | /api/lab/session/lifecycle | ID 更换与注销 |
| POST | /api/lab/session/expiry | 空闲过期 |

这些 Session 接口在独立的临时容器中自测，不依赖控制台代理保存 Set-Cookie，
不创建或注销当前 Lab HTTP 请求的用户 Session。报告的 steps 与 checks 用来对照实际结果。
任意检查失败、Socket 超时或容器请求错误都会使实验失败。

## 验证

```powershell
./gradlew.bat :kuma-project:kuma-project-lab:test --tests '*SocketLifecycleLessonTest' --tests '*SessionLearningDemoTest'
```

参考：[JDK Socket](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/Socket.html)、
[Servlet HttpSession](https://tomcat.apache.org/tomcat-11.0-doc/servletapi/jakarta/servlet/http/HttpSession.html)。
