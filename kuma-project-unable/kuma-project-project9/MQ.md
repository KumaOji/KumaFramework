# kuma-cloud-mq 接入说明 — project9

## 架构概览

```
project9 (Producer + Consumer)
    │
    │  Netty TCP (port 9999)
    ▼
kuma-cloud-mq-broker (Broker)
    │  接收消息并推送给订阅者
    └─ 推送到同一连接的 Consumer
```

| 角色 | 模块 | 说明 |
|------|------|------|
| Broker（消息代理） | `kuma-cloud-mq-broker` | 独立进程，Netty 监听 **9999** |
| Producer（生产者） | `kuma-cloud-mq-client` | 引入此 starter，发送消息到 Broker |
| Consumer（消费者） | `kuma-cloud-mq-client` | 引入此 starter，从 Broker 订阅并推送接收消息 |

---

## 快速启动

### 1. 启动 Broker

运行 `kuma-cloud-mq-broker` 中的 Broker 主类，Netty 服务监听 `localhost:9999`。

### 2. 启动本应用

运行 `Project9Application`，启动日志出现：

```
[Consumer] 已订阅 DEMO_TOPIC，等待消息...
[Producer] 生产者启动完成，可以发送消息
```

---

## 启用配置

```yaml
# application-dev.yml
kuma:
  mq:
    client:
      enabled: true                    # 开启 MQ 客户端
      broker-address: "127.0.0.1:9999" # Broker 地址
      group-name: "project9Group"      # 消费者/生产者分组
      resp-timeout-mills: 5000          # 响应超时（ms）
      check: false                     # false=Broker 未启动时不阻断启动
```

`MqClientAutoConfiguration` 通过 `@ConditionalOnProperty` 控制，`enabled=true` 才生效。

---

## 核心 API

### 生产者

```java
// 同步发送（等待 Broker 确认）
SendResult result = mqProducer.send(mqMessage);

// 单向发送（fire-and-forget，不等待确认）
SendResult result = mqProducer.sendOneWay(mqMessage);

// 批量发送
SendBatchResult result = mqProducer.sendBatch(messageList);
```

### 消费者

```java
// 订阅主题（tag 支持正则，* = 全部）
mqConsumerPush.subscribe("TOPIC_NAME", "*");

// 注册监听器
mqConsumerPush.registerListener((mqMessage, context) -> {
    // 处理消息...
    return ConsumerStatus.SUCCESS; // 或 FAILED / CONSUMER_LATER
});
```

### MqMessage 字段

| 字段 | 说明 |
|------|------|
| `topic` | 主题名称（必填） |
| `tags` | 标签列表，用于过滤（消费者按 tag 正则订阅） |
| `payload` | 消息体（字符串） |
| `bizKey` | 业务键，便于追踪 |
| `shardingKey` | 分片键，用于顺序消息 |

### ConsumerStatus

| 值 | 说明 |
|----|------|
| `SUCCESS` | 消费成功，Broker 确认 |
| `FAILED` | 消费失败，Broker 可重试 |
| `CONSUMER_LATER` | 稍后再消费（延迟重试） |

---

## 消息流程

```
HTTP GET /mq/send
    │
    └─ MqProducerService.send()
          ├─ 构造 MqMessage（topic=DEMO_TOPIC, tags, payload）
          └─ mqProducer.send(message)
                └─ Netty → Broker (9999)
                      └─ Broker 推送 → MqConsumerPush
                            └─ MqConsumerService.listener.consumer()
                                  └─ 打印日志，返回 SUCCESS
```

---

## 演示接口

Base URL: `http://localhost:8089`

| 接口 | 场景 | 说明 |
|------|------|------|
| `GET /mq/send` | 同步发送单条消息 | 默认 payload="Hello MQ!", tag=TAG_A |
| `GET /mq/send-one-way` | 单向发送（不等待确认） | 默认 payload="One-Way Message", tag=TAG_B |
| `GET /mq/send-batch?count=5` | 批量发送 N 条消息 | 默认发送 5 条，topic=DEMO_TOPIC |

---

## 代码结构

```
project9/
├── Project9Application.java
├── controller/
│   └── MqDemoController.java     # 三个演示接口
└── service/
    ├── MqProducerService.java    # 封装 MqProducer，@PostConstruct 启动
    └── MqConsumerService.java    # 封装 MqConsumerPush，@PostConstruct 订阅
```

---

## 常见问题

**Q: 启动报 `Connection refused localhost:9999`？**
A: Broker 未启动。将 `check: false` 可让应用先启动，再手动启动 Broker 后重试发送。

**Q: 消息发送返回 `FAILED`？**
A: 检查 Broker 是否正常运行，以及 `broker-address` 配置是否正确。

**Q: Consumer 收不到消息？**
A: 确认 `subscribe("DEMO_TOPIC", "*")` 的 topic 和 Producer 发送的 topic 一致；Consumer 必须在 Producer 发消息前已完成订阅。

**Q: 多个 Consumer 如何分组消费？**
A: 设置不同的 `group-name`，同一 group 内多个实例竞争消费（负载均衡），不同 group 各自收到全量消息。
