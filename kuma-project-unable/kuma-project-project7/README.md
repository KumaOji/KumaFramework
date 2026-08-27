# Project7 — CCSR 分布式配置中心客户端示例

## 项目概述

Project7 演示如何将 Spring Boot 应用接入 `kuma-cloud-ccsr`（Config Center Service Registry）——
Kuma Cloud 自研的分布式配置中心。核心功能：

- 通过 `CcsrService` API 向集群写入、读取、删除配置
- 启动后通过探针自动验证与服务端的连通性
- 理解 gRPC + Raft 双层架构的端口分工

---

## CCSR 框架架构

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                   project7 (Client)                      │
│                                                         │
│  CcsrController ──► CcsrService ──► CcsrClient          │
│                                          │               │
│                               FilterChain (责任链)       │
│                     ValidationFilter → SignFilter        │
│                     → ConvertFilter → InvokerFilter      │
│                                          │               │
│                               GrpcInvoker                │
│                                          │               │
│                               GrpcClient (gRPC Stub)     │
└──────────────────────────────────────────┼──────────────┘
                                           │  gRPC (port 8005)
                                           ▼
┌──────────────────────────────────────────────────────────┐
│               kuma-cloud-ccsr-server                      │
│                                                          │
│  GrpcServer (port 8005)                                  │
│      │                                                   │
│      │ 接收请求 (put / get / delete)                     │
│      ▼                                                   │
│  WriteRequestRpcProcessor / ReadRequestRpcProcessor /    │
│  DeleteRequestRpcProcessor                               │
│      │                                                   │
│      │ 写操作提交到 Raft                                 │
│      ▼                                                   │
│  RaftServer (SOFAJRaft, port 9006)                       │
│      │                                                   │
│      │ Raft 共识 (Leader选举 / 日志复制)                 │
│      ▼                                                   │
│  RaftStateMachine                                        │
│      │                                                   │
│      ▼                                                   │
│  Storage (RocksDB / 内存)                                │
└──────────────────────────────────────────────────────────┘
```

### 端口分工

| 端口 | 归属 | 用途 |
|------|------|------|
| 8005 | GrpcServer | 客户端 gRPC 接入端口（put/get/delete） |
| 9006 | RaftServer | Raft 内部通信端口（= 8005 + portOffset 1001），用于 Leader 选举、日志复制。**客户端不直接连接此端口** |
| 8006 | 节点2 GrpcServer | 三节点集群第二个节点 |
| 9007 | 节点2 RaftServer | 第二个节点 Raft 端口 |
| 8007 | 节点3 GrpcServer | 三节点集群第三个节点 |
| 9008 | 节点3 RaftServer | 第三个节点 Raft 端口 |

> **关键规则**：`cluster-address` 在**客户端**配置中填 gRPC 端口（8005/8006/8007）；
> 在**服务端** `cluster-address` 中填 Raft 端口（9006/9007/9008）。

### 读写一致性

| 操作 | 路径 |
|------|------|
| `PUT` / `DELETE` | 提交到 Raft Leader → Raft 日志复制到多数节点 → 状态机应用 → 响应 |
| `GET` | ReadIndex 线性一致读（ReadOnlySafe），确保读到最新已提交数据 |

---

## 模块说明

CCSR 框架由以下模块组成：

```
kuma-cloud-ccsr/
├── kuma-cloud-ccsr-api/           # Protobuf 生成的 gRPC 类、事件类型定义
├── kuma-cloud-ccsr-common/        # 公共配置（CcsrConfig）、响应码（ResponseCode）、工具类
├── kuma-cloud-ccsr-spi/           # SPI 扩展加载机制（@SPI、@Join、ExtensionLoader）
├── kuma-cloud-ccsr-client/        # 客户端核心（CcsrClient、GrpcClient、FilterChain、Listener）
├── kuma-cloud-ccsr-client-starter/# Spring Boot 自动配置（CcsrService、@EnableCcsrClient）
├── kuma-cloud-ccsr-core/          # 服务端核心（GrpcServer、RaftServer、Storage、Processors）
└── kuma-cloud-ccsr-server-starter/# 服务端 Spring Boot 启动（CcsrServerInitializer、@EnableCcsrServer）
```

---

## 客户端 SDK 详解

### 1. 启动入口：`@EnableCcsrClient`

```java
@Import(CcsrClientAutoConfiguration.class)
public @interface EnableCcsrClient {
    boolean enable() default true;
}
```

`@EnableCcsrClient(enable = true)` 导入 `CcsrClientAutoConfiguration`，该配置类受 `EnableCcsrCondition`
控制（读取 `enable` 属性），只有 `enable=true` 时才实际装配 Bean。

### 2. 自动配置：`CcsrClientAutoConfiguration`

```
@Conditional(EnableCcsrCondition.class)
CcsrClientAutoConfiguration
    │
    ├── ccsrConfig()          ← 绑定 application.yml 中 ccsr.* 前缀属性
    │       └── CcsrConfig（namespace、rpcType、clusterAddress、grpcConfig、raftConfig）
    │
    └── ccsrClient(CcsrConfig)
            │
            ├── GrpcOption.initServers(clusterAddress)   ← 解析服务端地址列表
            ├── CcsrClient.builder(namespace, option).build()  ← 构建客户端，初始化 FilterChain
            └── new CcsrService(ccsrClient)              ← 注册 SPI Listener，暴露为 Spring Bean
```

### 3. 核心 Bean：`CcsrService`

```java
public class CcsrService implements DisposableBean {
    public Response request(Payload request, EventType eventType) {
        request.setEventType(eventType);
        return ccsrClient.request(request);
    }
}
```

- `request()` 是唯一的公开 API，通过 `EventType`（`PUT` / `GET` / `DELETE`）区分操作类型
- `destroy()` 在 Spring 容器关闭时优雅释放 gRPC 连接（3 秒超时）

### 4. FilterChain（责任链）

`CcsrClient.buildChain()` 构建四级过滤器：

```
Payload 请求
    │
    ▼
ValidationFilter   ← 参数校验（namespace、group、dataId 非空等）
    │
    ▼
SignFilter         ← 签名（TODO：待实现，当前透传）
    │
    ▼
ConvertFilter      ← 将 Payload 转换为 gRPC Protobuf 请求对象
                     (MetadataWriteRequest / MetadataReadRequest / MetadataDeleteRequest)
    │
    ▼
InvokerFilter      ← 选择协议（当前仅 gRPC），调用 GrpcInvoker
    │
    ▼
GrpcClient         ← 选取服务端节点（负载均衡），发送 gRPC 请求
    │
    ▼
Response（gRPC 响应）
```

### 5. `GrpcClient`

`GrpcClient` 继承 `GrpcConnection`，持有 gRPC 的 `blockingStub` 和 `futureStub`：

```java
public Response request(Message request) {
    if (request instanceof MetadataWriteRequest)  → blockingStub.put(request)
    if (request instanceof MetadataDeleteRequest) → blockingStub.delete(request)
    if (request instanceof MetadataReadRequest)   → blockingStub.get(request)
}
```

- 同步调用使用 `blockingStub`（`request()` 方法）
- 异步调用使用 `futureStub`（`requestFuture()` 方法），返回 `RequestFuture<Response>`

### 6. `Payload`（请求载体）

```java
@Data @Builder
public class Payload {
    String namespace;      // 命名空间（不填时由 ConvertFilter 注入配置值）
    String group;          // 配置分组
    String tag;            // 标签（可选）
    String dataId;         // 配置唯一 ID
    ConfigData configData; // 配置内容（PUT 时必填，GET/DELETE 不需要）
    MetadataType type;     // 元数据类型
    Map<String, String> ext; // 扩展字段
    Long gmtCreate;        // 创建时间（框架自动填充）
    Long gmtModified;      // 修改时间（框架自动填充）
    EventType eventType;   // PUT / GET / DELETE（由 CcsrService.request() 设置）
}
```

---

## 服务端核心详解

### GrpcServer 启动流程

`CcsrServerInitializer` 监听 `ContextRefreshedEvent`：

```
ContextRefreshedEvent
    │
    ├── rpcServer.start()        ← GrpcServer: 绑定端口，注册 SPI GrpcService 实现
    │       └── NettyServerBuilder.forPort(8005).addService(services).build().start()
    │
    └── raftServer.init(config).start()  ← RaftServer: 初始化 Raft 节点
```

### RaftServer 启动流程

```
RaftServer.init(config)
    │
    ├── initPeerId()          ← 获取本机 IP + port（= grpcPort + portOffset）
    ├── initConfiguration()   ← 解析 clusterAddress，注册到 NodeManager
    ├── initCliServices()     ← 初始化 CliService（用于 refreshLeader 等管理操作）
    └── new RaftNodeMonitor() ← 节点健康监控

RaftServer.startServer()
    │
    ├── RaftHelper.initServer()      ← 创建 Bolt RpcServer（Raft 内部 RPC 通信）
    ├── startMultiRaftGroup()        ← 为每个 RaftGroup 创建节点
    │       └── createRaftGroup(groupId)
    │               ├── initDirectory()        ← 初始化日志、快照存储目录
    │               ├── new RaftStateMachine() ← 状态机（负责 apply 写操作到 Storage）
    │               └── RaftGroupService.start() ← 启动 Raft 节点
    │
    └── scheduleLeaderRefresh()      ← 定时刷新 RouteTable Leader 信息
            └── refreshRouteTable() → GlobalEventBus.post(LeaderRefreshEvent)
```

### 写操作流程（PUT）

```
gRPC: MetadataWriteRequest
    ▼
WriteRequestRpcProcessor
    ▼
RequestDispatcher → WriteRequestHandler
    ▼
检查当前节点是否为 Leader
    ├── 是 Leader → Task 提交 RaftServer.apply()
    │               → Raft 日志复制 → 多数节点确认
    │               → RaftStateMachine.onApply()
    │               → Storage.put(key, value)
    │               → Response(success=true, code=2000)
    │
    └── 非 Leader → Response(code=3002, REDIRECT, leader地址)
                     → 客户端重定向到 Leader 节点重试
```

### 读操作流程（GET）

```
gRPC: MetadataReadRequest
    ▼
ReadRequestRpcProcessor
    ▼
ReadRequestHandler
    ▼
Node.readIndex()   ← SOFAJRaft ReadOnlySafe 线性一致读
    ▼
ReadIndexClosure.run()
    ▼
Storage.get(key)
    ▼
Response(success=true, data=JSON序列化内容)
```

---

## 配置监听（实时推送）

### 工作原理

服务端每次 PUT/DELETE 后，通过 gRPC 服务端流（Server Streaming）将变更推送给所有已订阅的客户端。
客户端 `GrpcClient` 建立订阅流时持续监听，连接断开后按指数退避（1 → 2 → 4 → ... → 60s）自动重连。

### 实现步骤

**第一步**：定义配置数据类，实现 `ConfigData`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextConfig implements ConfigData {
    private String content;

    // 默认实现返回类名（"TextConfig"），可覆盖为自定义 key
    // @Override public String key() { return "my_text_config"; }
}
```

**第二步**：实现监听器，继承 `AbstractConfigListener<T>`

```java
@Join   // SPI 标注，框架启动时通过 SpiExtensionFactory 自动发现
public class MyConfigListener extends AbstractConfigListener<TextConfig> {

    @Override
    public void receive(String dataStr, TextConfig data, EventType eventType) {
        System.out.println("收到变更 eventType=" + eventType + ", content=" + data.getContent());
    }
}
```

**第三步**：在 SPI 文件中注册（`META-INF/services/com.kuma.cloud.ccsr.client.listener.ConfigListener`）

```
com.example.MyConfigListener
```

### 内部注册机制

```
CcsrService 构造时
    └── SpiExtensionFactory.getExtensions(ConfigListener.class)
            └── 加载所有 @Join 标注的 ConfigListener 实现
                    └── listener.register()
                            └── AbstractConfigListener.register()
                                    └── ConfigListenerManager.registerListener(type, this)
                                            └── 以 ConfigData.key() 为索引存入 listenerMap

服务端推送变更时
    └── ConfigListenerManager.fireEvent(metadata, eventType)
            └── listenerMap.get(metadata.getDataKey())
                    └── GsonUtils.fromJson(content, dataClass)
                            └── listener.receive(dataStr, configData, eventType)
```

---

## 配置说明

### `application-dev.yml`（project7 客户端）

```yaml
server:
  port: 8087                   # HTTP 服务端口

ccsr:
  rpc-type: grpc               # 通信协议（当前仅支持 grpc）
  namespace: default           # 命名空间，用于多租户隔离
  cluster-address:             # 填 gRPC 端口（不是 Raft 端口）
    - 127.0.0.1:8005
    - 127.0.0.1:8006
    - 127.0.0.1:8007
```

### `CcsrConfig` 完整属性

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `ccsr.namespace` | `default` | 命名空间 |
| `ccsr.rpc-type` | `grpc` | 通信类型（grpc / http / raft / websocket） |
| `ccsr.port` | `8000` | 服务端监听端口 |
| `ccsr.local-mode` | `false` | 本地模式（强制使用 127.0.0.1） |
| `ccsr.cluster-address` | `[]` | 集群节点地址列表 |
| `ccsr.grpc-config.max-inbound-message-size` | `10MB` | gRPC 最大入站消息大小 |
| `ccsr.grpc-config.keep-alive-time` | `10000ms` | gRPC Keepalive 间隔 |
| `ccsr.raft-config.port-offset` | `1001` | Raft 端口 = gRPC 端口 + portOffset |
| `ccsr.raft-config.election-timeout` | `1000ms` | Raft 选举超时 |
| `ccsr.raft-config.rpc-request-timeout` | `5000ms` | RPC 请求超时 |
| `ccsr.raft-config.snapshot-interval-secs` | `1800s` | 快照间隔（30分钟） |
| `ccsr.raft-config.single-node` | `false` | 单节点模式 |

---

## 响应码说明

| 响应码 | 枚举 | 含义 |
|--------|------|------|
| 2000 | `SUCCESS` | 操作成功 |
| 3002 | `REDIRECT` | 非 Leader，需重定向到 Leader |
| 4001 | `GROUP_NOT_FOUND` | Raft Group 不存在 |
| 4041 | `INSTANCE_NOT_FOUND` | 服务实例未找到 |
| 4042 | `DATA_NOT_EXIST` | 数据不存在（GET 时 key 未写入） |
| 4002 | `NO_LEADER` | 集群无 Leader（选举中） |
| 5000 | `SYSTEM_ERROR` | 服务端内部错误 |
| 5003 | `REQUEST_TIMEOUT` | 请求超时 |
| 5004 | `PARAM_INVALID` | 参数非法 |
| 5005 | `CLIENT_ERROR` | 客户端错误（如 namespace 不匹配） |

---

## Project7 代码结构

```
kuma-project-project7/
├── src/main/java/com/kuma/cloud/project7/
│   ├── Project7Application.java            # 启动类
│   ├── controller/
│   │   └── CcsrController.java             # 配置读写 REST 接口
│   ├── dto/
│   │   └── TextConfig.java                 # 配置数据 DTO
│   └── runner/
│       └── CcsrConnectionVerifier.java     # 启动后连通性探针
├── src/main/resources/
│   └── application-dev.yml                 # 开发环境配置
├── build.gradle                            # Gradle 构建配置
└── CCSR.md                                 # 接入说明（快速参考）
```

### `Project7Application`

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project7"})
@EnableCcsrClient(enable = true)   // 开启 CCSR 客户端自动配置
public class Project7Application {
    public static void main(String[] args) {
        // Protobuf 兼容性标志（针对旧版生成代码的 JDK 25 兼容性）
        System.setProperty("com.google.protobuf.use_unsafe_pre22_gencode", "true");

        new StartupSpringApplication(Project7Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project7")
                .run(args);
    }
}
```

### `CcsrController`

三个端点，统一使用 `CcsrService.request(Payload, EventType)` 接口：

```java
@PutMapping("/config")
String putConfig(group, dataId, content)
    └── Payload.builder().group().dataId().configData(new TextConfig(content)).build()
    └── ccsrService.request(payload, EventType.PUT)

@GetMapping("/config")
String getConfig(group, dataId)
    └── Payload.builder().group().dataId().build()
    └── ccsrService.request(payload, EventType.GET)

@GetMapping("/config/delete")
String deleteConfig(group, dataId)
    └── Payload.builder().group().dataId().build()
    └── ccsrService.request(payload, EventType.DELETE)
```

### `TextConfig`

```java
public class TextConfig implements ConfigData {
    private String content;
    // ConfigData.key() 默认返回 "TextConfig"（类名）
}
```

`ConfigData` 接口的 `key()` 方法是监听器路由的唯一索引：
当服务端推送变更时，`ConfigListenerManager` 以 `metadata.getDataKey()` 查找对应的监听器。
默认实现返回类的简单类名（`this.getClass().getSimpleName()`），可按需覆盖。

### `CcsrConnectionVerifier`

实现 `ApplicationRunner`，在 Spring 容器完全启动后执行：

```java
@Override
public void run(ApplicationArguments args) {
    // 发送 GET 探针请求（probe_group/probe_data_id）
    Response response = ccsrService.request(payload, EventType.GET);

    // 成功(2000) 或 数据不存在(4042) → 均表示网络连通，服务端已响应
    boolean connected = response.getSuccess()
            || response.getCode() == ResponseCode.DATA_NOT_EXIST.getCode();
}
```

---

## 快速启动

### 前置条件

启动 CCSR 服务端（`kuma-cloud-ccsr-server`），监听端口 8005/8006/8007。

服务端 `application.yml` 关键配置：
```yaml
ccsr:
  port: 8005
  namespace: default
  raft-config:
    single-node: true      # 单机开发模式
    port-offset: 1001      # Raft端口 = 8005 + 1001 = 9006
  cluster-address:
    - 127.0.0.1:9006       # 服务端 cluster-address 填 Raft 端口
```

### 启动 Project7

```bash
./gradlew :kuma-project:kuma-project-project7:bootRun
```

或在 IDE 中直接运行 `Project7Application.main()`。

### 验证连通性

查看启动日志：
```
[CCSR] 开始验证与 CCSR 服务端的连通性...
[CCSR] 连接验证成功 ✓  code=4042, msg=Data not exist
```

code=4042 表示探针 key 未写入，属于正常状态，说明网络已连通。

---

## API 接口

**Base URL**: `http://localhost:8087`

### 写入配置

```http
PUT /ccsr/config?group=default_group&dataId=default_data_id&content=hello
```

**参数**：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `group` | `default_group` | 配置分组 |
| `dataId` | `default_data_id` | 配置 ID |
| `content` | 必填 | 配置内容字符串 |

**返回**：gRPC Response 的 toString，成功示例：
```
success: true  code: 2000  msg: "success"
```

### 读取配置

```http
GET /ccsr/config?group=default_group&dataId=default_data_id
```

**返回示例**（已写入时）：
```
success: true  code: 2000  data: "{\"content\":\"hello\"}"
```

**返回示例**（未写入时）：
```
success: false  code: 4042  msg: "Data not exist"
```

### 删除配置

```http
GET /ccsr/config/delete?group=default_group&dataId=default_data_id
```

---

## 启动日志说明

| 日志内容 | 含义 | 处理建议 |
|----------|------|----------|
| `[CCSR] 连接验证成功 ✓  code=2000` | 探针 key 已存在，连接正常 | 无需处理 |
| `[CCSR] 连接验证成功 ✓  code=4042` | 探针 key 未写入，连接正常 | 无需处理（正常状态） |
| `[CCSR] 连接验证失败 ✗  code=5005` | `namespace` 配置与服务端不匹配 | 检查 `ccsr.namespace` 是否与服务端一致 |
| `[CCSR] 连接验证异常` | 服务端未启动或地址有误 | 检查服务端是否启动，检查 `cluster-address` |
| `UNAVAILABLE: io exception` + 重连日志 | gRPC 订阅流断开，正在按指数退避重连 | 服务端启动后自动恢复，无需干预 |
| `配置订阅连接断开，将在 Xs 后进行第 N 次重连` | 客户端订阅流断开，即将重连 | 服务端启动后自动恢复 |
| `触发 leader 刷新【开始/成功】 for group: ...` | Raft 定时刷新 Leader 信息 | 正常日志，无需处理 |

---

## SPI 扩展机制

CCSR 客户端使用 `@SPI` + `@Join` 注解实现插件化扩展：

```java
// 注解说明
@SPI                    // 标注接口为 SPI 扩展点
@Join(order = 1, isSingleton = true)  // 标注实现类，order 决定优先级

// 加载方式
SpiExtensionFactory.getExtensions(ConfigListener.class)  // 加载所有实现
SpiExtensionFactory.getExtension("grpc", RpcClient.class) // 按名称加载
```

当前主要扩展点：

| 扩展点接口 | 现有实现 | 说明 |
|-----------|---------|------|
| `RpcClient` | `GrpcClient` | gRPC 传输层 |
| `RpcServer` | `GrpcServer`、`RaftServer` | 服务端（gRPC + Raft） |
| `ConfigListener` | 用户自定义 | 配置变更监听 |
| `GrpcService` | `MetadataServiceImpl` | gRPC 服务实现注册 |
| `LoadBalancer` | `RandomLoadBalancer`、`RoundRobinLoadBalancer` | 客户端负载均衡 |

---

## 常见问题

**Q: 客户端配置多个地址（8005/8006/8007），只有一个服务端节点，会报错吗？**

A: 不会导致应用崩溃。`GrpcClient` 在连接失败时会在地址列表中轮询，未启动的节点连接失败后按指数退避重连。
启动时日志会显示连接 8006/8007 失败，但最终连到 8005 后正常工作。

**Q: `cluster-address` 填错了（服务端填了 gRPC 端口，客户端填了 Raft 端口）会怎样？**

A: 典型表现：服务端启动时报 Raft 节点无法加入集群；客户端请求超时。
核心规则：客户端配置填 **gRPC 端口**（8005），服务端 Raft 配置填 **Raft 端口**（8005 + 1001 = 9006）。

**Q: Windows 下 Raft 日志中出现 `fsync` 相关警告？**

A: Windows 不支持对目录执行 `fsync`，是正常现象，不影响功能。

**Q: 连续 PUT 后立即 GET，是否能读到最新值？**

A: 能。服务端使用 SOFAJRaft `ReadOnlySafe`（ReadIndex）实现线性一致读，
GET 请求发出时会等待 Raft 确认当前节点读到的数据不落后于最新已提交日志，然后再返回。

**Q: 如何扩展支持新的配置类型监听？**

A: 三步：① 创建实现 `ConfigData` 的 DTO 类；② 创建继承 `AbstractConfigListener<YourConfig>` 的监听器并加 `@Join`；
③ 在 `META-INF/services/com.kuma.cloud.ccsr.client.listener.ConfigListener` 文件中注册全类名。

**Q: `GrpcOption.initServers()` 如何工作？**

A: 解析 `cluster-address` 字符串列表（格式 `host:port`），转换为 `ServerAddress` 对象列表，
存入 `GrpcOption`，供 `GrpcConnection` 建立连接时使用。负载均衡策略（随机/轮询）在此基础上选取目标节点。
