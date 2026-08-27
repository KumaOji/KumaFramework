# Project8 — 自研分布式事务框架接入演示

## 目录

1. [项目概述](#1-项目概述)
2. [整体架构](#2-整体架构)
3. [模块依赖](#3-模块依赖)
4. [启动配置](#4-启动配置)
5. [核心组件详解](#5-核心组件详解)
   - 5.1 [@DistributedTransactional 注解](#51-distributedtransactional-注解)
   - 5.2 [KmcTransactionalAspect — AOP 切面](#52-kmctransactionalaspect--aop-切面)
   - 5.3 [KmcTxParticipant — 事务参与者核心类](#53-kmctxparticipant--事务参与者核心类)
   - 5.4 [KmcTx — 子事务对象](#54-kmctx--子事务对象)
   - 5.5 [NettyClient — RM 侧网络客户端](#55-nettyclient--rm-侧网络客户端)
   - 5.6 [NettyClientHandler — RM 侧消息处理器](#56-nettyclienthandler--rm-侧消息处理器)
   - 5.7 [NettyServerHandler — TM 侧核心处理器](#57-nettyserverhandler--tm-侧核心处理器)
   - 5.8 [TxRmAutoConfiguration — 自动装配](#58-txrmautoconfiguration--自动装配)
6. [完整事务流程](#6-完整事务流程)
   - 6.1 [正常提交流程](#61-正常提交流程)
   - 6.2 [第一步失败回滚](#62-第一步失败回滚)
   - 6.3 [第二步失败回滚](#63-第二步失败回滚)
7. [通信协议](#7-通信协议)
8. [演示接口](#8-演示接口)
9. [代码结构](#9-代码结构)
10. [设计要点与局限性](#10-设计要点与局限性)

---

## 1. 项目概述

Project8 是一个**自研轻量级分布式事务框架**的接入演示应用，展示如何通过一个简单注解 `@DistributedTransactional` 将多个本地事务纳入同一个全局事务进行协调，实现"要么全部提交，要么全部回滚"的语义。

**演示场景**：电商下单流程，涉及两个独立的本地操作：

| 步骤 | 服务 | 操作 |
|------|------|------|
| 第 1 步 | `OrderService` | 创建订单（全局事务开启者） |
| 第 2 步 | `StockService` | 扣减库存（全局事务结束者） |

两步操作在同一 JVM 内的不同 Service 中执行，通过自研的分布式事务框架保证原子性。

---

## 2. 整体架构

```
┌─────────────────────────────────────────┐
│          project8（RM，端口 8088）         │
│                                         │
│  HTTP 请求                               │
│    │                                    │
│    ▼                                    │
│  TxDemoController                       │
│    │                                    │
│    ├─ OrderService.createOrder()        │
│    │    @DistributedTransactional       │
│    │    (isStart=true)                  │
│    │                                    │
│    └─ StockService.deductStock()        │
│         @DistributedTransactional       │
│         (isEnd=true)                    │
│                                         │
│  KmcTransactionalAspect（AOP 切面）      │
│  KmcTxParticipant（事务状态管理）         │
│  NettyClient（TCP 长连接）               │
└─────────────────────┬───────────────────┘
                      │ Netty TCP（端口 8080）
                      │ JSON 消息
                      ▼
┌─────────────────────────────────────────┐
│    kuma-cloud-tx-server（TM，端口 8080）  │
│                                         │
│  NettyServerHandler                     │
│    ├── 接收 create 命令 → 创建事务组     │
│    ├── 接收 add 命令    → 累积子事务状态 │
│    └── isEnd=true 时 → 做全局裁决：      │
│         有 rollback → 广播 rollback      │
│         全 commit   → 广播 commit        │
└─────────────────────────────────────────┘
```

### 角色说明

| 角色 | 英文缩写 | 模块 | 职责 |
|------|----------|------|------|
| 事务管理者 | TM（Transaction Manager） | `kuma-cloud-tx-server` | 独立进程；收集所有子事务的投票结果，做全局提交/回滚裁决，并将结果广播给所有 RM |
| 事务参与者 | RM（Resource Manager） | `kuma-cloud-tx-rm` | 嵌入业务应用；每个标注 `@DistributedTransactional` 的方法即为一个子事务参与者，执行后向 TM 上报结果 |

---

## 3. 模块依赖

```gradle
// build.gradle
dependencies {
    api project(':kuma-boot-framework:kuma-boot-starter-core')
    api project(':kuma-boot-framework:kuma-boot-starter-core')
    api project(':kuma-boot-framework:kuma-boot-starter-web')

    // 分布式事务 RM starter（核心依赖）
    api project(':kuma-cloud-framework:kuma-cloud-tx:kuma-cloud-tx-rm')

    api projectLibs.spring.boot.starter.web
}
```

`kuma-cloud-tx-rm` 是核心 starter，引入后通过 `TxRmAutoConfiguration` 自动注册：
- AOP 切面（`KmcTransactionalAspect`）
- Netty 客户端（`NettyClient`）
- Web 拦截器（`WebAppConfig` / `RequestInterceptor`）
- Spring 上下文工具（`ApplicationContextProvider`）

---

## 4. 启动配置

### application-dev.yml

```yaml
server:
  port: 8088

spring:
  autoconfigure:
    exclude:
      # 本演示不使用数据库，排除 DataSource 自动配置
      - org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
      - org.springframework.boot.jdbc.autoconfigure.health.DataSourceHealthContributorAutoConfiguration
      # 本演示不使用 Kafka
      - org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration

kuma:
  tx:
    enabled: true   # 开启分布式事务 RM，激活 TxRmAutoConfiguration
```

### 启动顺序

```
第 1 步：启动 TM → 运行 kuma-cloud-tx-server 的 TxManagerApplication
          Netty 监听 localhost:8080

第 2 步：启动 RM → 运行 Project8Application（本应用，端口 8088）
          启动日志出现：>>>>>>事务参与者启动成功<<<<<<
          表示已成功连接 TM
```

---

## 5. 核心组件详解

### 5.1 `@DistributedTransactional` 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedTransactional {
    boolean isStart() default false;  // 是否为全局事务的开启者
    boolean isEnd()   default false;  // 是否为全局事务的结束者
}
```

**三种使用方式**：

| 用法 | 含义 | TM 交互 |
|------|------|---------|
| `@DistributedTransactional(isStart = true)` | 全局事务第一步，负责创建事务组 | 发送 `create` 命令，TM 初始化事务组 |
| `@DistributedTransactional` | 中间参与者（本演示未使用） | 发送 `add` 命令，`isEnd=false` |
| `@DistributedTransactional(isEnd = true)` | 全局事务最后一步，触发 TM 做裁决 | 发送 `add` 命令，`isEnd=true`，TM 收到后判断全局结果 |

**注意**：被注解的方法返回值类型**必须为 `Integer`**，切面会强转 `(Integer) result`。

---

### 5.2 `KmcTransactionalAspect` — AOP 切面

```
位置：kuma-cloud-tx-rm/.../aspect/KmcTransactionalAspect.java
优先级：Ordered.getOrder() = 10000（低优先级，确保 Spring 本地事务切面先执行）
```

切面的完整执行流程：

```
@Around 拦截 @DistributedTransactional 方法
    │
    ├─ 读取注解属性（isStart / isEnd）
    │
    ├─ 获取 groupId
    │   ├─ isStart=true → 调用 KmcTxParticipant.createKmcTransactionalManagerGroup()
    │   │                  生成 UUID 作为 groupId，发送 create 命令给 TM
    │   └─ isStart=false → 从 ThreadLocal 读取已有 groupId
    │
    ├─ 创建子事务对象 KmcTx（带唯一 txId）
    │
    ├─ 执行业务方法 proceedingJoinPoint.proceed()
    │
    ├─ 成功 → KmcTxParticipant.addKmcTransactional(kmcTx, isEnd, commit)
    │          向 TM 发送 add 命令，携带 commit 状态
    │
    └─ 异常 → KmcTxParticipant.addKmcTransactional(kmcTx, isEnd, rollback)
               向 TM 发送 add 命令，携带 rollback 状态
```

**关键设计**：切面在业务方法**执行完**之后才向 TM 上报结果，这意味着业务逻辑的成功/失败决定了子事务的投票方向。

---

### 5.3 `KmcTxParticipant` — 事务参与者核心类

```
位置：kuma-cloud-tx-rm/.../transactional/KmcTxParticipant.java
```

负责维护当前线程的事务上下文，并与 TM 通信。

**ThreadLocal 状态**：

| 变量 | 类型 | 作用 |
|------|------|------|
| `current` | `ThreadLocal<KmcTx>` | 当前线程正在执行的子事务对象 |
| `currentGroupId` | `ThreadLocal<String>` | 当前线程所属的全局事务组 ID |
| `transactionalCount` | `ThreadLocal<Integer>` | 当前线程已创建的子事务数量（自增） |
| `KMC_TRANSACTIONAL_MAP` | `Map<String, KmcTx>` | groupId → KmcTx 映射，供 TM 回调时查找 |

**核心方法**：

```java
// 1. 向 TM 创建事务组，返回 groupId（UUID）
String groupId = KmcTxParticipant.createKmcTransactionalManagerGroup();
// 发送 JSON：{"groupId":"xxx","command":"create"}

// 2. 创建子事务对象（本地记录，尚未上报 TM）
KmcTx kmcTx = KmcTxParticipant.createTransactional(groupId);
// 生成 txId(UUID)，存入 KMC_TRANSACTIONAL_MAP，transactionalCount+1

// 3. 上报子事务结果给 TM
KmcTxParticipant.addKmcTransactional(kmcTx, isEnd, TransactionalType.commit);
// 发送 JSON：{groupId, transactionalId, transactionalType, command:"add",
//             isEnd, transactionalCount}

// 4. 跨服务传递 groupId（单 JVM 内自动共享）
String groupId = KmcTxParticipant.getCurrentGroupId();
KmcTxParticipant.setCurrentGroupId(groupId);  // 目标服务手动设置
```

---

### 5.4 `KmcTx` — 子事务对象

```
位置：kuma-cloud-tx-rm/.../transactional/KmcTx.java
```

```java
public class KmcTx {
    String groupId;           // 所属全局事务组 ID
    String transactionalId;   // 本子事务唯一 ID（UUID）
    TransactionalType transactionalType;  // commit / rollback
    Task task;                // 阻塞/唤醒控制（用于等待 TM 回调）
}
```

`Task` 是一个基于 `CountDownLatch` 或 `wait/notify` 的等待工具。TM 回调 `commit`/`rollback` 时，`NettyClientHandler` 调用 `kmcTx.getTask().signalTask()` 唤醒等待线程，实现事务控制权的挂起与恢复。

---

### 5.5 `NettyClient` — RM 侧网络客户端

```
位置：kuma-cloud-tx-rm/.../netty/NettyClient.java
```

```java
@Component
public class NettyClient implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // Spring Bean 初始化完成后自动连接 TM
        start("localhost", 8080);
        // 输出：>>>>>>事务参与者启动成功<<<<<<
    }

    public void send(JSONObject sendData) {
        client.sendData(sendData);  // 通过 Netty Channel 发送 JSON
    }
}
```

- 实现 `InitializingBean`，**Spring 启动时自动建立与 TM 的 Netty TCP 长连接**
- 连接固定为 `localhost:8080`（TM 地址）
- `send()` 方法供 `KmcTxParticipant` 调用，将 JSON 消息写入 Channel

---

### 5.6 `NettyClientHandler` — RM 侧消息处理器

```
位置：kuma-cloud-tx-rm/.../netty/NettyClientHandler.java
```

负责**接收 TM 下发的全局裁决**（commit 或 rollback）：

```java
@Override
public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
    JSONObject data = JSON.parseObject((String) msg);
    String groupId = data.getString("groupId");
    String command = data.getString("command");

    // 根据 groupId 找到对应的子事务对象
    KmcTx kmcTx = KmcTxParticipant.getKmcTransactional(groupId);

    if ("commit".equals(command)) {
        kmcTx.setTransactionalType(TransactionalType.commit);
    } else {
        kmcTx.setTransactionalType(TransactionalType.rollback);
    }

    // 唤醒等待 TM 裁决的业务线程
    kmcTx.getTask().signalTask();
}
```

TM 广播裁决 → `channelRead` 被触发 → 查找子事务 → 设置最终类型 → 唤醒业务线程。

---

### 5.7 `NettyServerHandler` — TM 侧核心处理器

```
位置：kuma-cloud-tx-server/.../NettyServerHandler.java
```

TM 的核心逻辑，维护三个并发安全的状态表：

| 变量 | 类型 | 含义 |
|------|------|------|
| `transactionTypeMap` | `Map<groupId, List<String>>` | 各事务组收到的子事务投票列表（"commit"/"rollback"） |
| `isEndMap` | `Map<groupId, Boolean>` | 是否已收到结束标记（isEnd=true 的子事务） |
| `transactionCountMap` | `Map<groupId, Integer>` | 该事务组应有的子事务总数 |

**裁决逻辑**：

```java
// 收到 add 命令后
transactionTypeMap.get(groupId).add(transactionType);

// 判断裁决条件：已收到结束标记 AND 已收到的子事务数 == 应有总数
if (isEndMap.get(groupId)
    && transactionCountMap.get(groupId).equals(transactionTypeMap.get(groupId).size())) {

    if (transactionTypeMap.get(groupId).contains("rollback")) {
        // 有任意一个 rollback → 全局回滚
        broadcastToAllRM("rollback");
    } else {
        // 全部 commit → 全局提交
        broadcastToAllRM("commit");
    }
}
```

广播方式：遍历 `ChannelGroup`（所有已连接的 RM 的 Channel），逐一写入裁决结果。

---

### 5.8 `TxRmAutoConfiguration` — 自动装配

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "kuma.tx", name = "enabled",
                       havingValue = "true", matchIfMissing = false)
@ComponentScan("com.kuma.cloud.tx.rm")
public class TxRmAutoConfiguration {
}
```

通过 `@ConditionalOnProperty` 控制，只有在配置文件中设置 `kuma.tx.enabled=true` 时，才会扫描并注册 `com.kuma.cloud.tx.rm` 包下的所有组件，实现**按需开启**，不影响未启用分布式事务的应用。

---

## 6. 完整事务流程

### 6.1 正常提交流程

```
GET /tx/place-order?orderId=O001&productId=P001&quantity=1
          │
          ▼
TxDemoController.placeOrder()
          │
          ├─ [Step 1] OrderService.createOrder(..., failOrder=false)
          │         │
          │   KmcTransactionalAspect 拦截（isStart=true）
          │         │
          │         ├─ KmcTxParticipant.createKmcTransactionalManagerGroup()
          │         │     生成 groupId = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
          │         │     存入 ThreadLocal
          │         │     → 发送 TM：{"groupId":"xxx","command":"create"}
          │         │     TM：创建 transactionTypeMap[groupId] = []
          │         │
          │         ├─ KmcTxParticipant.createTransactional(groupId)
          │         │     生成 txId，transactionalCount = 1
          │         │
          │         ├─ 执行业务：打印订单创建日志
          │         │
          │         └─ KmcTxParticipant.addKmcTransactional(kmcTx, false, commit)
          │               → 发送 TM：{groupId, txId, "commit", "add",
          │                           isEnd=false, transactionalCount=1}
          │               TM：transactionTypeMap[groupId] = ["commit"]
          │               TM：isEndMap[groupId] = false → 不裁决，继续等待
          │
          └─ [Step 2] StockService.deductStock(..., failStock=false)
                    │
              KmcTransactionalAspect 拦截（isEnd=true）
                    │
                    ├─ 从 ThreadLocal 读取 groupId（同一线程，共享上下文）
                    │
                    ├─ KmcTxParticipant.createTransactional(groupId)
                    │     生成 txId，transactionalCount = 2
                    │
                    ├─ 执行业务：打印库存扣减日志
                    │
                    └─ KmcTxParticipant.addKmcTransactional(kmcTx, true, commit)
                          → 发送 TM：{groupId, txId, "commit", "add",
                                      isEnd=true, transactionalCount=2}
                          TM：transactionTypeMap[groupId] = ["commit","commit"]
                          TM：isEndMap[groupId] = true
                          TM：transactionCountMap[groupId] = 2
                          TM：已收到 2 个子事务 == 应有 2 个，且 isEnd=true
                          TM：列表中无 "rollback" → 全局提交
                          TM：广播 {"groupId":"xxx","command":"commit"} 给所有 RM
                          │
                    RM NettyClientHandler 收到 commit
                          → kmcTx.setTransactionalType(commit)
                          → kmcTx.getTask().signalTask() 唤醒等待线程

HTTP 响应：下单成功：orderId=O001, productId=P001
```

---

### 6.2 第一步失败回滚

```
GET /tx/place-order-fail-order
          │
          ├─ [Step 1] OrderService.createOrder(..., failOrder=true)
          │         │
          │   切面拦截（isStart=true）
          │         │
          │         ├─ 创建事务组，发送 create 命令给 TM
          │         ├─ 创建子事务（transactionalCount=1）
          │         ├─ 执行业务 → 抛出 RuntimeException("模拟订单创建失败")
          │         │
          │         └─ catch → addKmcTransactional(kmcTx, false, rollback)
          │               → 发送 TM：{..., "rollback", isEnd=false, count=1}
          │               TM：transactionTypeMap[groupId] = ["rollback"]
          │               TM：isEnd=false → 不裁决
          │
          └─ Controller catch(Exception e) 捕获异常
               StockService 不再执行

注意：由于 Step 1 失败，StockService 未执行，TM 永远收不到 isEnd=true。
TM 的事务组处于"未完成"状态，不会发出裁决通知。
（这是当前框架的一个局限，详见第 10 节）

HTTP 响应：下单失败（已回滚）: 模拟订单创建失败，触发全局事务回滚
```

---

### 6.3 第二步失败回滚

```
GET /tx/place-order-fail-stock
          │
          ├─ [Step 1] OrderService.createOrder(..., failOrder=false)
          │         成功，发送 commit 给 TM（isEnd=false）
          │         TM：["commit"]，等待更多
          │
          └─ [Step 2] StockService.deductStock(..., failStock=true)
                    │
              切面拦截（isEnd=true）
                    │
                    ├─ 创建子事务（transactionalCount=2）
                    ├─ 执行业务 → 抛出 RuntimeException("模拟库存不足")
                    │
                    └─ catch → addKmcTransactional(kmcTx, true, rollback)
                          → 发送 TM：{..., "rollback", isEnd=true, count=2}
                          TM：["commit","rollback"]，isEnd=true，count=2
                          TM：已收到 2 个 == 应有 2 个
                          TM：列表中有 "rollback" → 全局回滚
                          TM：广播 {"command":"rollback"} 给所有 RM

HTTP 响应：下单失败（已回滚）: 模拟库存不足，触发全局事务回滚
```

---

## 7. 通信协议

RM 与 TM 之间通过 **Netty TCP 长连接**传输 JSON 字符串，以 `\n` 换行符作为消息分隔符。

### RM → TM 消息格式

**创建事务组（create）**

```json
{
  "groupId": "550e8400-e29b-41d4-a716-446655440000",
  "command": "create"
}
```

**添加子事务（add）**

```json
{
  "groupId": "550e8400-e29b-41d4-a716-446655440000",
  "transactionalId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
  "transactionalType": "commit",
  "command": "add",
  "isEnd": false,
  "transactionalCount": 1
}
```

| 字段 | 说明 |
|------|------|
| `groupId` | 全局事务组 ID（UUID） |
| `transactionalId` | 当前子事务 ID（UUID） |
| `transactionalType` | `"commit"` 或 `"rollback"` |
| `command` | `"create"` 或 `"add"` |
| `isEnd` | 是否为最后一个子事务，TM 以此判断何时裁决 |
| `transactionalCount` | 当前已创建的子事务总数，TM 用于判断"是否全部到达" |

### TM → RM 消息格式（广播）

```json
{
  "groupId": "550e8400-e29b-41d4-a716-446655440000",
  "command": "commit"
}
```

或

```json
{
  "groupId": "550e8400-e29b-41d4-a716-446655440000",
  "command": "rollback"
}
```

---

## 8. 演示接口

**Base URL**：`http://localhost:8088`

| 接口 | 方法 | 参数 | 场景 | 预期结果 |
|------|------|------|------|---------|
| `/tx/place-order` | GET | `orderId=O001`<br>`productId=P001`<br>`quantity=1` | 正常下单 | 两步均成功，TM 裁决 commit，响应"下单成功" |
| `/tx/place-order-fail-order` | GET | `orderId=O002`<br>`productId=P001`<br>`quantity=1` | 第一步（订单）失败 | Step1 抛出异常，上报 rollback，控制器返回"下单失败（已回滚）" |
| `/tx/place-order-fail-stock` | GET | `orderId=O003`<br>`productId=P001`<br>`quantity=1` | 第二步（库存）失败 | Step1 成功，Step2 失败，TM 收到 rollback，全局回滚，响应"下单失败（已回滚）" |

---

## 9. 代码结构

```
kuma-project-project8/
├── build.gradle                         # 依赖配置（含 kuma-cloud-tx-rm）
├── TX.md                                # 旧版接入说明（简要）
├── README.md                            # 本文档
└── src/main/
    ├── java/com/kuma/cloud/project8/
    │   ├── Project8Application.java     # 启动入口，扫描 com.kuma.cloud.tx.rm
    │   ├── controller/
    │   │   └── TxDemoController.java    # 三个演示 HTTP 接口
    │   └── service/
    │       ├── OrderService.java        # @DistributedTransactional(isStart=true)
    │       └── StockService.java        # @DistributedTransactional(isEnd=true)
    └── resources/
        ├── application.yml              # 激活 dev profile
        └── application-dev.yml          # 端口 8088，kuma.tx.enabled=true

kuma-cloud-framework/kuma-cloud-tx/
├── kuma-cloud-tx-common/                # 公共常量
├── kuma-cloud-tx-rm/                    # RM starter（项目引用此模块）
│   └── src/main/java/com/kuma/cloud/tx/rm/
│       ├── annotation/
│       │   └── DistributedTransactional.java    # 核心注解
│       ├── aspect/
│       │   ├── KmcTransactionalAspect.java      # 分布式事务 AOP 切面
│       │   └── KmcDataSourceAspect.java         # 数据源切面（本地事务控制）
│       ├── transactional/
│       │   ├── KmcTx.java                       # 子事务对象
│       │   ├── KmcTxParticipant.java            # 事务参与者核心逻辑
│       │   └── TransactionalType.java           # commit / rollback 枚举
│       ├── netty/
│       │   ├── NettyClient.java                 # Netty TCP 客户端
│       │   ├── NettyClientHandler.java          # 接收 TM 裁决
│       │   └── ClientInitializer.java           # Channel Pipeline 初始化
│       ├── interceptor/
│       │   ├── WebAppConfig.java                # 注册 HTTP 拦截器
│       │   ├── RequestInterceptor.java          # 从请求头传递 groupId（跨服务）
│       │   └── GroupIdRespAdvice.java           # 响应时携带 groupId
│       ├── util/
│       │   ├── ApplicationContextProvider.java  # Spring 上下文静态工具
│       │   ├── HttpClient.java                  # HTTP 工具
│       │   └── Task.java                        # 阻塞/唤醒控制
│       └── autoconfigure/
│           └── TxRmAutoConfiguration.java       # 自动装配入口
└── kuma-cloud-tx-server/                # TM 独立服务
    └── src/main/java/com/kuma/cloud/tx/server/
        ├── TxManagerApplication.java            # TM 启动入口
        ├── NettyServer.java                     # Netty 服务端（监听 8080）
        ├── ServerInitializer.java               # Pipeline 初始化
        └── NettyServerHandler.java              # 核心裁决逻辑
```

---

## 10. 设计要点与局限性

### 设计要点

**1. AOP 切面优先级**

`KmcTransactionalAspect.getOrder() = 10000`，优先级数值越大越晚执行。这确保分布式事务切面在 Spring 本地事务切面（`@Transactional`，默认优先级更高）**之外**包裹执行，即 Spring 先开启/提交本地事务，分布式切面再向 TM 上报结果。

```
外层（先进后出）：KmcTransactionalAspect（order=10000，低优先级，最外层）
内层（后进先出）：Spring @Transactional（高优先级，在内层管理本地事务）
```

**2. ThreadLocal 传播**

同一 HTTP 请求线程内，`currentGroupId`、`transactionalCount` 通过 `ThreadLocal` 自动共享，无需在 Service 间手动传递 groupId。跨服务调用时需通过 HTTP 请求头传递（`RequestInterceptor` / `GroupIdRespAdvice` 提供支持）。

**3. TM 裁决条件**

TM 同时满足以下两个条件才触发裁决：
- 已收到 `isEnd=true` 的子事务（`isEndMap[groupId] = true`）
- 已收到的子事务数量 == 上报的应有总数（`transactionalCount`）

这防止了 TM 在所有子事务结果未到齐时就做出错误裁决。

---

### 局限性

| 问题 | 说明 |
|------|------|
| **返回值限制** | 切面强转 `(Integer) result`，被注解的方法返回值必须为 `Integer`，否则抛 `ClassCastException` |
| **第一步失败时 TM 不裁决** | 若 `isStart=true` 的方法抛异常，控制器提前退出，`isEnd=true` 的方法不会执行，TM 永远收不到结束信号，事务组挂在 TM 内存中（需要超时清理机制，目前未实现） |
| **广播给所有 RM** | TM 收到裁决后广播给 `ChannelGroup` 中**所有**已连接的 RM（包括其他应用），而不是只通知本次全局事务相关的 RM，存在误触发风险 |
| **TM 地址硬编码** | `NettyClient` 写死连接 `localhost:8080`，不支持配置化 |
| **单点 TM** | TM 无高可用，宕机后 RM 无法上报，所有分布式事务均会超时/悬挂 |
| **无持久化** | TM 的事务状态全在内存（`ConcurrentHashMap`），重启即丢失 |
| **本地事务实际不回滚** | 当前演示未真正接入数据库，`@Transactional` 未生效，TM 的 commit/rollback 指令仅更新 `KmcTx.transactionalType`，实际数据库操作需配合 `KmcDataSourceAspect` 和 `KmcConnection` 实现 |
