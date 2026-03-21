# kuma-cloud-mq 自研消息队列 — 详细设计解析

> 本文档以 project9 为切入点，系统拆解 kuma-cloud-mq 从网络传输、协议设计、生产消费流程到核心并发机制的全部实现细节。

---

## 目录

1. [整体架构](#1-整体架构)
2. [模块划分](#2-模块划分)
3. [传输层：Netty + 分隔符帧协议](#3-传输层netty--分隔符帧协议)
4. [通信协议：RpcMessageDto](#4-通信协议rpcmessagedto)
5. [方法类型（MethodType）](#5-方法类型methodtype)
6. [Broker 服务端](#6-broker-服务端)
7. [生产者（Producer）流程](#7-生产者producer流程)
8. [消费者（Consumer）流程](#8-消费者consumer流程)
9. [核心并发机制：InvokeService](#9-核心并发机制invokeservice)
10. [消息推送：Broker → Consumer](#10-消息推送broker--consumer)
11. [持久化层](#11-持久化层)
12. [project9 的接入方式](#12-project9-的接入方式)
13. [完整消息流程追踪](#13-完整消息流程追踪)
14. [设计取舍与已知限制](#14-设计取舍与已知限制)

---

## 1. 整体架构

```
┌─────────────────────────────────────────────────┐
│                   project9                       │
│                                                  │
│  MqProducerService          MqConsumerService    │
│       │                           │              │
│  MqProducer (Thread)     MqConsumerPush (Thread) │
│       │                           │              │
│  ProducerBrokerService   ConsumerBrokerService   │
│       │                           │              │
│  DefaultInvokeService    DefaultInvokeService    │
│       │                           │              │
│  MqProducerHandler       MqConsumerHandler       │
└───────┼───────────────────────────┼──────────────┘
        │      Netty TCP :9999      │
        └─────────────┬─────────────┘
                      │
         ┌────────────▼────────────┐
         │    kuma-cloud-mq-broker  │
         │                         │
         │  MqBrokerHandler        │
         │  ├─ BrokerProducerService│
         │  ├─ BrokerConsumerService│
         │  ├─ LocalMqBrokerPersist │
         │  └─ DefaultBrokerPushService│
         └─────────────────────────┘
```

三个角色均通过同一个 **Netty TCP 长连接**（端口 9999）通信：

| 角色 | 类 | 说明 |
|------|----|------|
| **Broker** | `MqBroker` (Thread) | 独立进程，Netty Server，监听 9999 |
| **Producer** | `MqProducer` (Thread) | Netty Client，主动连接 Broker |
| **Consumer** | `MqConsumerPush` (Thread) | Netty Client，主动连接 Broker，被动接收推送 |

Broker 不主动连接任何人；Producer 和 Consumer 各自建立到 Broker 的长连接，所有消息（注册、发送、推送）都在这两条连接上复用。

---

## 2. 模块划分

```
kuma-cloud-mq/
├── kuma-cloud-mq-common        # 公共：协议 DTO、工具、InvokeService、重试、负载均衡
├── kuma-cloud-mq-broker        # Broker 服务端：Netty Server、消费者/生产者管理、推送、持久化
├── kuma-cloud-mq-client        # 客户端：Producer + Consumer 的 Netty Client
├── kuma-cloud-mq-cluster       # 集群扩展（占位）
├── kuma-cloud-mq-consistency   # 一致性扩展：Raft 协议实现
├── kuma-cloud-mq-metrics       # 指标扩展（占位）
├── kuma-cloud-mq-plugin        # 插件扩展（占位）
└── kuma-cloud-mq-proxy         # 代理扩展（占位）
```

当前 project9 只用到 `common` + `broker` + `client` 三个模块。

---

## 3. 传输层：Netty + 分隔符帧协议

### 3.1 为什么需要帧协议

TCP 是字节流协议，没有消息边界。如果直接传 JSON，接收方无法判断"一条消息"到哪里结束。kuma-cloud-mq 选择**分隔符方案（DelimiterBasedFrameDecoder）**：在每条消息末尾追加固定分隔符，接收端按分隔符切割字节流还原消息边界。

### 3.2 分隔符定义

```java
// DelimiterUtil.java
public static final String DELIMITER = "~!@#$%^&*";
public static final int LENGTH = 65535;          // 最大帧长度
```

选用 9 字节的特殊符号串，降低正文中出现的概率。

### 3.3 发送侧：拼接分隔符

```java
// DelimiterUtil.getMessageDelimiterBuffer()
String json = JSON.toJSONString(rpcMessageDto);
String jsonDelimiter = json + DELIMITER;
return Unpooled.copiedBuffer(jsonDelimiter.getBytes());
```

发出去的字节 = **JSON 正文 + `~!@#$%^&*`**，如在日志中看到：

```
{"methodType":"C_SUBSCRIBE","request":true,...}~!@#$%^&*
```

### 3.4 接收侧：切割帧

**Broker（服务端）管道：**

```java
// MqBroker.run()
ch.pipeline()
    .addLast(new DelimiterBasedFrameDecoder(DelimiterUtil.LENGTH, delimiterBuf))
    .addLast(initChannelHandler());   // MqBrokerHandler
```

**Consumer（客户端）管道：**

```java
// ChannelFutureUtils → DefaultConsumerBrokerService.initChannelHandler()
ch.pipeline()
    .addLast(new LoggingHandler(LogLevel.INFO))   // 调试日志
    .addLast(channelInitializer);                  // 内层 ChannelInitializer:
                                                   //   DelimiterBasedFrameDecoder
                                                   //   MqConsumerHandler
```

`DelimiterBasedFrameDecoder` 将字节流切割成一个个完整 JSON 帧，再交给后续 Handler 处理，后续 Handler 拿到的 `ByteBuf` 已经是**完整的一条消息**，不含分隔符。

---

## 4. 通信协议：RpcMessageDto

所有通信（无论方向、无论消息类型）都统一用这一个信封类：

```java
public class RpcMessageDto implements Serializable {
    private long   requestTime;   // 发送时间戳
    private String traceId;       // 全局唯一请求 ID（32 位随机串）
    private String methodType;    // 消息类型，如 "P_SEND_MSG"、"C_SUBSCRIBE"
    private boolean isRequest;    // true=请求，false=响应
    private String respCode;      // 响应码（仅超时等特殊情况使用）
    private String respMsg;       // 响应描述
    private String json;          // 业务 payload（序列化为 JSON 字符串）
}
```

**双向复用同一信封的设计要点：**

- `isRequest=true`：发送方发出请求，接收方要处理并回复。
- `isRequest=false`：接收方把处理结果用原始 `traceId` 回写，发送方凭 `traceId` 匹配响应。
- `json` 字段是业务体二次序列化的字符串（嵌套 JSON），Broker/Client 根据 `methodType` 反序列化为对应的 DTO。

**fastjson2 序列化注意点：**

`isRequest` 字段的 getter 是 `isRequest()`（is 前缀），fastjson2 序列化时去掉 `is` 前缀，JSON key 为 `"request"`；反序列化时通过 setter `setRequest()` 还原。因此线上 JSON 中看到的是 `"request":true/false` 而非 `"isRequest"`。

---

## 5. 方法类型（MethodType）

所有消息类型的常量定义，前缀含义：`P_` = Producer，`C_` = Consumer，`B_` = Broker 主动推送。

| 常量 | 方向 | 说明 |
|------|------|------|
| `P_REGISTER` | Producer → Broker | 生产者注册（建立连接后必须先注册） |
| `P_UN_REGISTER` | Producer → Broker | 生产者注销 |
| `P_SEND_MSG` | Producer → Broker | 同步发送单条消息（等待 Broker 确认） |
| `P_SEND_MSG_ONE_WAY` | Producer → Broker | 单向发送（不等待确认） |
| `P_SEND_MSG_BATCH` | Producer → Broker | 批量同步发送 |
| `P_SEND_MSG_ONE_WAY_BATCH` | Producer → Broker | 批量单向发送 |
| `C_REGISTER` | Consumer → Broker | 消费者注册 |
| `C_UN_REGISTER` | Consumer → Broker | 消费者注销 |
| `C_SUBSCRIBE` | Consumer → Broker | 订阅 Topic（携带 tag 正则） |
| `C_UN_SUBSCRIBE` | Consumer → Broker | 取消订阅 |
| `C_MESSAGE_PULL` | Consumer → Broker | 主动拉取消息（Pull 模式） |
| `C_HEARTBEAT` | Consumer → Broker | 心跳（每 5 秒一次，单向，Broker 不回复） |
| `C_CONSUMER_STATUS` | Consumer → Broker | 消费结果 ACK（单条） |
| `C_CONSUMER_STATUS_BATCH` | Consumer → Broker | 消费结果 ACK（批量） |
| `B_MESSAGE_PUSH` | Broker → Consumer | Broker 主动推送消息（Push 模式） |

---

## 6. Broker 服务端

### 6.1 启动流程

`MqBroker` 继承 `Thread`，`run()` 方法用 Netty `ServerBootstrap` 绑定 9999 端口：

```java
// MqBroker.run()
EventLoopGroup bossGroup  = new NioEventLoopGroup();  // 接受连接
EventLoopGroup workerGroup = new NioEventLoopGroup(); // 读写 IO

serverBootstrap
    .group(bossGroup, workerGroup)
    .channel(NioServerSocketChannel.class)
    .childHandler(new ChannelInitializer<Channel>() {
        protected void initChannel(Channel ch) {
            ch.pipeline()
                .addLast(new DelimiterBasedFrameDecoder(LENGTH, delimiterBuf))
                .addLast(initChannelHandler());   // 共用同一个 MqBrokerHandler 实例
        }
    });
```

**注意：** `initChannelHandler()` 每次调用返回**同一个** `MqBrokerHandler` 实例（它被 `@ChannelHandler.Sharable` 复用于所有连接）。

### 6.2 MqBrokerHandler：消息分发入口

每条到来的帧（已去分隔符）交给 `channelRead0()`：

```
收到字节帧
    │
    ├─ JSON 反序列化 → RpcMessageDto
    │
    ├─ isRequest == true
    │       │
    │       └─ dispatch(rpcMessageDto, ctx)
    │               │
    │               ├─ P_REGISTER / P_UN_REGISTER
    │               ├─ P_SEND_MSG / P_SEND_MSG_ONE_WAY / ...
    │               ├─ C_REGISTER / C_UN_REGISTER
    │               ├─ C_SUBSCRIBE / C_UN_SUBSCRIBE
    │               ├─ C_HEARTBEAT   → null（不回复）
    │               ├─ C_MESSAGE_PULL
    │               └─ C_CONSUMER_STATUS / _BATCH
    │
    │       dispatch 结果 != null → writeResponse()（原 traceId 回包，isRequest=false）
    │       dispatch 结果 == null → 不回包（单向/心跳）
    │
    └─ isRequest == false
            └─ invokeService.addResponse(traceId, dto)
               （Broker 自己也有 InvokeService，用于等待 Consumer 的消费 ACK）
```

### 6.3 Broker 内部服务组件

| 组件 | 实现类 | 职责 |
|------|--------|------|
| `BrokerProducerService` | `LocalBrokerProducerService` | 维护生产者 channelId → ServiceEntry 映射，校验合法性 |
| `BrokerConsumerService` | `LocalBrokerConsumerService` | 维护消费者注册列表、订阅关系（topic+tag → Channel 列表）、心跳续约 |
| `MqBrokerPersist` | `LocalMqBrokerPersist` | 内存存储消息（`ConcurrentHashMap<topic, List<MqMessagePersistPut>>`），维护消息状态 |
| `BrokerPushService` | `DefaultBrokerPushService` | 异步线程池推送消息给 Consumer，带重试逻辑 |
| `BrokerRegisterValidService` | `DefaultBrokerRegisterValidService` | 校验 appKey/appSecret（当前默认全部放行） |

---

## 7. 生产者（Producer）流程

### 7.1 启动与注册

```
MqProducer.start()        ← 启动 Thread
    │
    └─ run()
          │
          ├─ 1. ProducerBrokerService.initChannelFutureList()
          │      └─ Netty Bootstrap 连接 Broker:9999
          │         管道：MqProducerHandler（处理 Broker 的响应）
          │
          ├─ 2. registerToBroker()
          │      └─ callServer(P_REGISTER, ...)
          │            └─ 同步等待 Broker 返回 respCode=0000
          │
          ├─ 3. statusManager.status(true)   ← 标记可用
          └─ 4. 注册 ShutdownHook
```

`MqProducerService.init()` 调用 `mqProducer.start()` 后 `Thread.sleep(3000)` 等待连接建立，是一个粗糙的就绪等待。

### 7.2 发送消息（同步）

```java
// MqProducerService.send()
MqMessage message = new MqMessage();
message.setTopic("DEMO_TOPIC");
message.setTags(List.of("TAG_A"));
message.setPayload(payload);
SendResult result = mqProducer.send(message);
```

内部调用链：

```
MqProducer.send(mqMessage)
    │
    └─ ProducerBrokerService.send(mqMessage)
          └─ Retryer（最多 3 次）
                └─ callServer(channel, req, MqCommonResp.class)
                      │
                      ├─ 1. invokeService.addRequest(traceId, 5000ms)
                      │      └─ requestMap.put(traceId, expireTime)
                      │
                      ├─ 2. channel.writeAndFlush(JSON + 分隔符)
                      │      → Broker 收到 P_SEND_MSG
                      │      → 持久化 + 异步推送 Consumer
                      │      → Broker 回写响应（isRequest=false）
                      │
                      └─ 3. invokeService.getResponse(traceId)
                             └─ 阻塞等待（synchronized wait）
                             └─ MqProducerHandler.channelRead0() 收到响应
                                → invokeService.addResponse(traceId, dto)
                                → notifyAll() 唤醒主线程
                             └─ 解析 JSON → SendResult
```

### 7.3 单向发送（One-Way）

```java
mqProducer.sendOneWay(message);
// → callServer(channel, req, null)
//   respClass=null 时，写完数据立即返回，不等待响应
```

发给 Broker 的 `methodType = P_SEND_MSG_ONE_WAY`，Broker 的 `dispatch()` 返回 `null`，不回包。客户端不需要 `invokeService` 等待。

---

## 8. 消费者（Consumer）流程

### 8.1 启动、注册、订阅

```
MqConsumerPush.start()      ← 启动 Thread
    │
    └─ run()
          │
          ├─ 1. ConsumerBrokerService.initChannelFutureList()
          │      ├─ Netty Bootstrap 连接 Broker:9999
          │      │   管道：LoggingHandler + DelimiterDecoder + MqConsumerHandler
          │      └─ initHeartbeat()：ScheduledExecutor 每 5 秒发 C_HEARTBEAT
          │
          ├─ 2. registerToBroker()
          │      └─ callServer(C_REGISTER, ...)  同步等 Broker 确认
          │
          └─ 3. statusManager.status(true)
```

```
MqConsumerPush.subscribe("DEMO_TOPIC", "*")
    │
    └─ ConsumerBrokerService.subscribe(topicName, tagRegex, "PUSH")
          └─ Retryer（最多 3 次）
                └─ callServer(channel, C_SUBSCRIBE_REQ, MqCommonResp.class)
                      └─ Broker 记录：topic=DEMO_TOPIC, tagRegex=*, channel=当前连接
```

### 8.2 监听器注册

```java
mqConsumerPush.registerListener((mqMessage, context) -> {
    log.info("收到消息: {}", mqMessage.getPayload());
    return ConsumerStatus.SUCCESS;
});
```

监听器存储在 `DefaultMqListenerService` 的列表中，Broker 推送消息时被回调。

### 8.3 接收 Broker 推送（Push 模式）

Broker 推送 `B_MESSAGE_PUSH` 消息时，`MqConsumerHandler.channelRead0()` 收到帧：

```
isRequest == true（Broker 发来的是"请求"）
    │
    └─ dispatch()
          └─ methodType == B_MESSAGE_PUSH
                └─ consumer(json)
                      ├─ JSON 反序列化 → MqMessage
                      ├─ mqListenerService.consumer(mqMessage, context)
                      │      └─ 回调业务 listener → 返回 ConsumerStatus
                      └─ 构造 MqConsumerResultResp 回包给 Broker
                            └─ writeResponse(isRequest=false)
```

**关键：** Broker 推送过来的 `B_MESSAGE_PUSH` 帧，`isRequest=true`（因为 Broker 是请求方，等待 Consumer ACK）。Consumer 处理完后写回响应（`isRequest=false`），Broker 的 `DefaultBrokerPushService` 的 `callServer` 收到后更新消息状态。

### 8.4 心跳机制

```java
// ConsumerBrokerService.initHeartbeat()，每 5 秒触发
private void heartbeat() {
    callServer(channel, C_HEARTBEAT_REQ, null);  // respClass=null，不等待响应
}
```

Broker `dispatch()` 处理 `C_HEARTBEAT` 时：
- 调用 `registerConsumerService.heartbeat(req, channel)` 更新最后心跳时间
- 返回 `null`，不回包
- Broker 侧日志：`当前消息为 null，忽略处理`

---

## 9. 核心并发机制：InvokeService

这是整个 MQ 框架最核心的并发设计，用于解决 **"发送线程 → Netty IO 线程 → 发送线程"** 的跨线程响应匹配问题。

### 9.1 数据结构

```java
public class DefaultInvokeService implements InvokeService {
    // 请求追踪：traceId → 过期时间戳
    private final ConcurrentHashMap<String, Long> requestMap;

    // 响应存储：traceId → 响应 DTO
    private final ConcurrentHashMap<String, RpcMessageDto> responseMap;
}
```

### 9.2 请求-响应配对流程

```
发送线程（kmc_main）                    Netty IO 线程（nioEventLoopGroup-x-1）
─────────────────────                   ────────────────────────────────────
addRequest(traceId, timeout)
  requestMap.put(traceId, expireTime)

writeAndFlush(消息)  ──────────────────→  网络传输 → 对端处理

getResponse(traceId)
  synchronized(this) {
    check responseMap                   channelRead0(ctx, msg)
    if null → this.wait()   ←─ 阻塞 ─→    addResponse(traceId, dto)
  }                                         responseMap.putIfAbsent(traceId, dto)
                                            requestMap.remove(traceId)
                                            synchronized(this) {
                                              notifyAll()  ──────→  唤醒
                                            }
  check responseMap → 找到
  return rpcResponse
```

### 9.3 正确的 wait/notify 模式

**关键设计：** `responseMap` 的检查必须在 `synchronized` 块内进行，否则会发生"检查完 → 响应到达 → notifyAll → wait"的顺序导致永久挂起。

```java
// getResponse() — 正确实现
public RpcMessageDto getResponse(String seqId) {
    try {
        while (true) {
            synchronized (this) {
                RpcMessageDto rpcResponse = responseMap.get(seqId);
                if (ObjectUtils.isNotNull(rpcResponse)) {
                    return rpcResponse;             // 有响应直接返回
                }
                this.wait();                        // 无响应则释放锁并阻塞
            }
            // notifyAll() 唤醒后，重新进入 synchronized 检查
        }
    } catch (InterruptedException e) {
        throw new MqException(MqCommonRespCode.RPC_GET_RESP_FAILED);
    }
}

// addResponse() — 正确实现
public InvokeService addResponse(String seqId, RpcMessageDto rpcResponse) {
    Long expireTime = requestMap.get(seqId);
    if (ObjectUtils.isNull(expireTime)) {
        return this;                                // 超时已被清理，直接丢弃
    }
    if (System.currentTimeMillis() > expireTime) {
        rpcResponse = RpcMessageDto.timeout();
    }

    responseMap.putIfAbsent(seqId, rpcResponse);   // 先放入（无锁）
    requestMap.remove(seqId);

    synchronized (this) {
        this.notifyAll();                           // 再唤醒所有等待方
    }
    return this;
}
```

### 9.4 超时清理

`DefaultInvokeService` 构造时启动定时线程，每 60 秒清理 `requestMap` 中已过期的条目：

```java
Executors.newScheduledThreadPool(1)
    .scheduleAtFixedRate(new TimeoutCheckThread(requestMap, responseMap), 60, 60, TimeUnit.SECONDS);
```

若请求超时（`expireTime < now`），`addResponse()` 会将响应替换为 `RpcMessageDto.timeout()`，`callServer()` 收到后抛出 `MqException(TIMEOUT)`。

### 9.5 已修复的 Bug：condition 标志位导致 Netty IO 线程饥饿

**原始问题代码：**

```java
// 错误：一个 boolean 被所有请求共享，且 addResponse 后永远不重置
private boolean condition = false;

// getResponse：condition=true 后永不调用 wait()，形成忙等循环
synchronized (this) {
    while (!condition) {   // ← 一旦 condition=true，永远不进 wait()
        this.wait();
    }
}

// addResponse：设为 true 后永不重置
synchronized (this) {
    condition = true;
    this.notifyAll();
}
```

**故障链：**

1. 第一个请求（C_REGISTER）`addResponse` 将 `condition=true`
2. 第二个请求（C_SUBSCRIBE）`getResponse` 进入 `synchronized` 块：`!condition` 为 false → 立刻退出，**不调用 wait()**
3. 外层 while 循环变成 **100% CPU 忙等**，持续抢占 `synchronized(this)` 的锁
4. Netty IO 线程（`nioEventLoopGroup-x-1`）想调用 `addResponse` 也需要 `synchronized(this)`，但被主线程饥饿，无法调度
5. 由于 IO 线程无法运行 `Selector.select()`，无法读取网络缓冲区，C_SUBSCRIBE 响应字节永远停在 OS 内核 TCP buffer 中
6. `channelRead0` 永不触发，`addResponse` 永不被调用，死锁

**现象：** 客户端日志中 `wait has notified!` 高频输出（每秒数百次），但始终 `对应结果已经获取: null`，Netty 无任何 `RECEIVED` 日志。

---

## 10. 消息推送：Broker → Consumer

当 Broker 收到 `P_SEND_MSG` 后，持久化完成，立即触发 **异步推送**：

```java
// MqBrokerHandler.handleProducerSendMsg()
MqCommonResp commonResp = mqBrokerPersist.put(persistPut);   // 同步持久化
this.asyncHandleMessage(persistPut);                          // 异步推送，不阻塞响应
return commonResp;                                            // 立即回复 Producer
```

```java
// asyncHandleMessage()
List<ChannelGroupNameDto> channelList =
    registerConsumerService.getPushSubscribeList(mqMessage);   // 按 topic+tag 过滤
if (channelList.isEmpty()) return;

BrokerPushContext context = BrokerPushContext.newInstance()
    .channelList(channelList)
    .mqMessagePersistPut(persistPut)
    .invokeService(invokeService)
    .respTimeoutMills(5000)
    .pushMaxAttempt(3);

brokerPushService.asyncPush(context);   // 提交给单线程池
```

`DefaultBrokerPushService` 使用**单线程池**（`Executors.newSingleThreadExecutor()`）逐一处理推送任务：

```
对每个订阅该 topic 的 Consumer Channel：
    │
    ├─ updateStatus(messageId, TO_CONSUMER_PROCESS)
    │
    └─ Retryer（最多 3 次）
          └─ callServer(channel, B_MESSAGE_PUSH, MqConsumerResultResp.class, invokeService)
                ├─ 构造 RpcMessageDto（isRequest=true）
                ├─ invokeService.addRequest(traceId, 5000)   ← Broker 侧的 InvokeService
                ├─ channel.writeAndFlush() → Consumer 收到 B_MESSAGE_PUSH
                │      └─ MqConsumerHandler.dispatch() → 业务 listener → 返回 ConsumerStatus
                │      └─ Consumer 回写响应（isRequest=false）
                └─ invokeService.getResponse(traceId)        ← 阻塞等待 Consumer ACK
          │
          └─ 根据 consumerStatus 更新消息状态（SUCCESS/FAILED）
```

**Tag 过滤逻辑：**

```java
// LocalBrokerConsumerService.getPushSubscribeList()
// 消费者订阅时存储的 tagRegex 与消息的 tags 列表做正则匹配
RegexUtil.hasMatch(mqMessage.getTags(), subscriber.getTagRegex())
```

---

## 11. 持久化层

`LocalMqBrokerPersist` 是**内存实现**，仅用于演示和测试：

```java
// 核心数据结构
Map<String, List<MqMessagePersistPut>> map = new ConcurrentHashMap<>();
// key = topic，value = 该 topic 下的所有消息
```

**消息状态机：**

```
WAIT_CONSUMER           ← 刚入队，等待被消费
    │
    ▼
TO_CONSUMER_PROCESS     ← Broker 正在推送/Consumer 正在拉取
    │
    ├─ → ConsumerStatus.SUCCESS     ← 消费成功
    ├─ → TO_CONSUMER_FAILED         ← 消费失败（重试耗尽）
    └─ → CONSUMER_LATER             ← 消费者返回"稍后重试"
```

`Pull` 模式下，`pull()` 只拉取状态为 `WAIT_CONSUMER` 或 `CONSUMER_LATER` 的消息。

**生产局限：** 当前实现是纯内存，进程重启数据丢失；`updateStatus` 的全表扫描性能差；不支持持久化到 MySQL/RocketMQ 等。

---

## 12. project9 的接入方式

### 12.1 Spring Boot 自动装配

`@ComponentScan` 扫描 `com.kuma.cloud.mq.client`，由 `MqClientAutoConfiguration`（`@ConditionalOnProperty`）注入 `MqProducer` 和 `MqConsumerPush` Bean。

配置项（`application-dev.yml`）：

```yaml
kuma:
  mq:
    client:
      enabled: true
      broker-address: "127.0.0.1:9999"
      group-name: "project9Group"
      resp-timeout-mills: 5000
      check: false   # false=Broker 未就绪时不阻断启动
```

### 12.2 生产者接入

```java
@Service
public class MqProducerService {
    private final MqProducer mqProducer;

    @PostConstruct
    public void init() throws InterruptedException {
        mqProducer.start();        // 启动 Netty 连接 + 注册
        Thread.sleep(3000L);       // 等待连接就绪
    }

    public SendResult send(String payload, List<String> tags, String bizKey) {
        MqMessage message = new MqMessage();
        message.setTopic("DEMO_TOPIC");
        message.setTags(tags);
        message.setPayload(payload);
        message.setBizKey(bizKey);
        return mqProducer.send(message);
    }
}
```

### 12.3 消费者接入

```java
@Service
public class MqConsumerService {
    private final MqConsumerPush mqConsumerPush;

    @PostConstruct
    public void init() {
        mqConsumerPush.start();                          // 连接 Broker + 注册
        mqConsumerPush.subscribe("DEMO_TOPIC", "*");     // 订阅（tag 正则）
        mqConsumerPush.registerListener((msg, ctx) -> {
            log.info("收到消息: {}", msg.getPayload());
            return ConsumerStatus.SUCCESS;
        });
    }
}
```

### 12.4 HTTP 接口

| 接口 | 说明 |
|------|------|
| `GET /mq/send?payload=hello&tag=TAG_A&bizKey=BK001` | 同步发送单条消息 |
| `GET /mq/send-one-way?payload=fire&tag=TAG_B` | 单向发送，不等待确认 |
| `GET /mq/send-batch?count=5` | 连续发送 N 条消息 |

---

## 13. 完整消息流程追踪

以 `GET /mq/send?payload=hello` 为例，端到端全路径：

```
HTTP 请求
    │
    ▼
MqDemoController.send()
    │
    ▼
MqProducerService.send("hello", ["TAG_A"], null)
    │  构造 MqMessage{topic=DEMO_TOPIC, tags=[TAG_A], payload=hello}
    ▼
MqProducer.send(mqMessage)
    │
    ▼
DefaultProducerBrokerService.send()
    │  Retryer.retryCall() → callServer(channel, P_SEND_MSG_REQ)
    │  ├─ invokeService.addRequest(traceId)
    │  ├─ writeAndFlush: {"methodType":"P_SEND_MSG","request":true,...}~!@#$%^&*
    │  └─ invokeService.getResponse(traceId) → synchronized wait()
    │
    │  [Netty TCP 传输]
    │
    ▼
MqBrokerHandler.channelRead0()                    [Broker 进程]
    │  isRequest=true → dispatch(P_SEND_MSG)
    │  ├─ 验证生产者 channelId 合法
    │  ├─ MqBrokerPersist.put(message)             持久化到内存 map
    │  ├─ asyncHandleMessage(persistPut)            提交到单线程池
    │  └─ return MqCommonResp{respCode=0000}
    │
    ▼
MqBrokerHandler.writeResponse()
    │  构造 RpcMessageDto{isRequest=false, traceId=原始traceId}
    │  writeAndFlush 回 Producer
    │
    ▼
MqProducerHandler.channelRead0()                  [Producer Netty IO 线程]
    │  isRequest=false → invokeService.addResponse(traceId, dto)
    │  ├─ responseMap.put(traceId, dto)
    │  └─ synchronized { notifyAll() }
    │
    ▼
DefaultProducerBrokerService.callServer()         [Producer 发送线程，被唤醒]
    │  getResponse() 返回 dto
    │  JSON 反序列化 → SendResult{status=SUCCESS}
    │
    ▼
HTTP 响应: "发送结果: status=SUCCESS, messageId=xxx"


─── 同时，异步推送线程执行 ─────────────────────────────────────

DefaultBrokerPushService.asyncPush()              [Broker 推送线程]
    │  registerConsumerService.getPushSubscribeList(DEMO_TOPIC, TAG_A)
    │  → 命中 Consumer 的订阅（tagRegex=* 匹配 TAG_A）
    │  → 获取 Consumer 的 Channel
    │
    ▼
DefaultBrokerPushService.callServer(channel, B_MESSAGE_PUSH)
    │  ├─ brokerInvokeService.addRequest(traceId)
    │  ├─ writeAndFlush: {"methodType":"B_MESSAGE_PUSH","request":true,...}~!@#$%^&*
    │  └─ brokerInvokeService.getResponse(traceId) → wait()
    │
    │  [Netty TCP 传输]
    │
    ▼
MqConsumerHandler.channelRead0()                  [Consumer Netty IO 线程]
    │  isRequest=true → dispatch(B_MESSAGE_PUSH)
    │  ├─ JSON 反序列化 MqMessage
    │  ├─ mqListenerService.consumer(mqMessage, ctx)
    │  │      → 业务 listener: 打印日志，return ConsumerStatus.SUCCESS
    │  └─ return MqConsumerResultResp{respCode=0000, consumerStatus=SUCCESS}
    │
    ▼
MqConsumerHandler.writeResponse()
    │  构造 RpcMessageDto{isRequest=false} 回写 Broker
    │
    ▼
MqBrokerHandler.channelRead0()                    [Broker Netty IO 线程]
    │  isRequest=false → brokerInvokeService.addResponse(traceId, dto)
    │  → notifyAll()
    │
    ▼
DefaultBrokerPushService.callServer()             [Broker 推送线程，被唤醒]
    │  consumerStatus=SUCCESS
    │  → mqBrokerPersist.updateStatus(messageId, SUCCESS)
    │
完成
```

---

## 14. 设计取舍与已知限制

### 优点

| 特性 | 说明 |
|------|------|
| **纯 Netty 自研** | 无需依赖 Kafka/RocketMQ，可完整理解每一层实现 |
| **协议统一** | 请求/响应/推送复用同一个 `RpcMessageDto` 信封 |
| **重试内置** | 发送、推送、订阅均有 `Retryer` 保底 |
| **Tag 过滤** | 消费者可按正则订阅，Broker 侧精确匹配 |
| **Push + Pull 双模式** | `MqConsumerPush` + `MqConsumerPull` 均已实现 |
| **负载均衡** | 多 Broker 时通过 `WeightRoundRobbin` 分发 |

### 当前限制

| 限制 | 原因 | 生产改进方向 |
|------|------|-------------|
| **内存持久化** | `LocalMqBrokerPersist` 用 `ConcurrentHashMap`，重启丢失 | 接入 MySQL / RocksDB / 文件追加写 |
| **Broker 单点** | 无集群，宕机全部失效 | 已有 `kuma-cloud-mq-consistency` Raft 模块，待接入 |
| **推送单线程** | `DefaultBrokerPushService` 用 `newSingleThreadExecutor()`，消息量大时积压 | 改为有界线程池 + 背压 |
| **无消息顺序保证** | `asyncHandleMessage` 异步提交，顺序不确定 | `shardingKey` 字段已预留，待接入顺序队列 |
| **无 DLQ** | 超过 `pushMaxAttempt` 重试失败后仅标记状态，无死信队列 | 加死信 Topic 机制 |
| **心跳无超时剔除** | `LocalBrokerConsumerService` 记录心跳时间，但无定时清理下线 Consumer 的逻辑 | 加定时任务扫描超时 Channel |
| **appKey 未校验** | `DefaultBrokerRegisterValidService` 直接放行 | 接入 appKey/appSecret 验证体系 |
