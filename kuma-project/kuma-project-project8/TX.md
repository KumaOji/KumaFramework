# kuma-cloud-tx 接入说明 — project8

## 架构概览

```
project8 (RM — Resource Manager)
    │
    │  Netty TCP (port 8080)
    ▼
kuma-cloud-tx-server (TM — Transaction Manager)
    │  收集所有子事务状态
    │  全部 commit → 通知各 RM 提交
    │  任一 rollback → 通知各 RM 回滚
```

| 角色 | 模块 | 说明 |
|------|------|------|
| TM（事务管理者） | `kuma-cloud-tx-server` | 独立进程，Netty 监听 **8080**，HTTP 在 **7102** |
| RM（事务参与者） | `kuma-cloud-tx-rm` | 引入此 starter 即成为参与者，启动时自动连接 TM |

---

## 快速启动

### 1. 启动 TM（事务管理者）

运行 `kuma-cloud-tx-server` 的 `TxManagerApplication`，Netty 服务监听 `localhost:8080`。

### 2. 启动本应用

运行 `Project8Application`，启动日志出现：

```
>>>>>>事务参与者启动成功<<<<<<
```

说明已成功连接到 TM。

---

## 启用配置

```yaml
# application-dev.yml
kuma:
  tx:
    enabled: true   # 开启分布式事务 RM，注入 NettyClient 和 AOP 切面
```

`TxRmAutoConfiguration` 通过 `@ConditionalOnProperty` 控制，`enabled=true` 才生效。

---

## 核心注解

```java
@DistributedTransactional(isStart = true)   // 开启全局事务（第一步）
@DistributedTransactional()                 // 中间参与者
@DistributedTransactional(isEnd = true)     // 结束全局事务（最后一步）
```

| 属性 | 说明 |
|------|------|
| `isStart=true` | 向 TM 发送 `create` 命令，生成 `groupId` 并存入 ThreadLocal |
| `isEnd=true` | 向 TM 发送 `add` 命令并携带 `isEnd=true`，TM 收到后做最终裁决 |
| 默认（两者均 false） | 中间参与者，从 ThreadLocal 读取 `groupId`，向 TM 发送 `add` |

> **注意**：被注解的方法返回值类型必须为 `Integer`（AOP 切面强转限制）。

---

## 事务流程（正常提交）

```
HTTP GET /tx/place-order
    │
    ├─ OrderService.createOrder  @DistributedTransactional(isStart=true)
    │     ├─ AOP: 向 TM 发送 create(groupId)
    │     ├─ 执行业务逻辑（创建订单）
    │     └─ AOP: 向 TM 发送 add(groupId, txId, commit, isEnd=false)
    │
    └─ StockService.deductStock  @DistributedTransactional(isEnd=true)
          ├─ AOP: 从 ThreadLocal 读取 groupId
          ├─ 执行业务逻辑（扣减库存）
          └─ AOP: 向 TM 发送 add(groupId, txId, commit, isEnd=true)
                   └─ TM 收到 isEnd=true，判断所有子事务均为 commit → 全局提交
```

---

## 事务流程（回滚）

```
任意步骤抛出异常
    └─ AOP catch: 向 TM 发送 add(groupId, txId, rollback, isEnd=?)
                   └─ TM 检测到 rollback → 全局回滚
```

---

## 演示接口

Base URL: `http://localhost:8088`

| 接口 | 场景 | 预期结果 |
|------|------|---------|
| `GET /tx/place-order` | 正常下单 | 两步均成功，TM 收到全局 commit |
| `GET /tx/place-order-fail-order` | 订单创建失败（第一步异常） | TM 收到 rollback，全局回滚 |
| `GET /tx/place-order-fail-stock` | 库存不足（第二步异常） | 订单已提交但库存回滚（TM 全局回滚） |

---

## 代码结构

```
project8/
├── Project8Application.java
├── controller/
│   └── TxDemoController.java     # 三个演示接口
└── service/
    ├── OrderService.java         # isStart=true，开启全局事务
    └── StockService.java         # isEnd=true，结束全局事务
```

---

## 常见问题

**Q: 启动报 `Connection refused localhost:8080`？**
A: TM（`kuma-cloud-tx-server`）未启动，先启动它再启动 project8。

**Q: 方法返回值不是 Integer 可以用 `@DistributedTransactional` 吗？**
A: 当前 AOP 切面强转 `(Integer) result`，非 Integer 返回值会抛 `ClassCastException`，是框架现阶段的限制。

**Q: 跨服务调用时 ThreadLocal 如何传递 groupId？**
A: 单 JVM 内（同一线程）自动共享。跨服务时需手动将 `KmcTxParticipant.getCurrentGroupId()` 通过请求头或参数传递，在目标服务调用前执行 `KmcTxParticipant.setCurrentGroupId(groupId)`。
