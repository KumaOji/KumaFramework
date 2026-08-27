# Project6 — kuma-cloud-rpc 自研 RPC 框架使用示例

## 项目概述

Project6 演示如何使用 `kuma-cloud-rpc`——Kuma Cloud 自研的轻量级 RPC 框架，基于 **Netty TCP 长连接** 实现跨进程方法调用。核心演示内容：

- 用 `ServiceBs` 在 Netty 服务端注册并暴露服务实现
- 用 `ClientBs` 创建 JDK 动态代理，像调用本地方法一样调用远程服务
- 通过 HTTP 接口触发 RPC 调用，端到端验证整个链路

为方便演示，本项目在**同一进程**内同时扮演服务端和客户端。真实场景中两者部署在不同进程/机器上。

---

## RPC 框架模块说明

```
kuma-cloud-rpc/
├── kuma-cloud-rpc-common/        # 公共类：RpcRequest/RpcResponse、InvokeManager、Netty 编解码、序列化
├── kuma-cloud-rpc-server/        # 服务端：ServiceBs、RpcServerHandler、DefaultServiceFactory
├── kuma-cloud-rpc-client/        # 客户端：ClientBs、DefaultReferenceProxy、CallTypeStrategy
├── kuma-cloud-rpc-registry/      # 注册中心接入：自研 nameserver 客户端、Nacos 接入（可选）
├── kuma-cloud-rpc-filter/        # RPC 过滤器（可扩展）
├── kuma-cloud-rpc-serialization/ # 序列化策略（Kryo / JSON / Hessian）
├── kuma-cloud-rpc-metrics/       # 指标采集（可选）
└── kuma-cloud-rpc-core/          # 旧版 Socket/Netty 实现（参考用）
```

---

## 整体架构

### 调用全链路

```
HTTP 请求（浏览器/curl）
    │
    ▼  Spring MVC
HelloController
    │ 调用接口方法（透明 RPC）
    ▼
HelloService（接口）
    │  JDK 动态代理（DefaultReferenceProxy.invoke()）
    ▼
1. 生成 seqId（UUID）
2. 构建 RpcRequest { seqId, serviceId, methodName, paramTypeNames, paramValues, callType, timeout }
3. InvokeManager.addRequest(seqId, timeout)  ← 注册等待槽
4. channel.writeAndFlush(RpcRequest)          ← Netty 异步发送
    │
    │  Netty TCP（端口 9527）
    │  自定义二进制帧编解码
    ▼
RpcServerHandler.channelRead0()
    │
    ▼
DefaultServiceFactory.invoke(serviceId, methodName, paramTypeNames, paramValues)
    │  反射调用
    ▼
HelloServiceImpl.hello(name)   ← 真正的业务逻辑
    │
    ▼  封装 RpcResponse { seqId, result }
channel.writeAndFlush(RpcResponse)
    │
    │  Netty TCP（响应方向）
    ▼
客户端 Handler 收到响应
    └── InvokeManager.addResponse(seqId, response) + notifyAll()
            │
            ▼
5. InvokeManager.getResponse(seqId)  ← wait() 阻塞被唤醒
    │
    ▼
response.result()  →  HTTP 响应
```

### 端口说明

| 端口 | 用途 |
|------|------|
| 8086 | HTTP 服务端口（Spring MVC），接受外部 API 请求 |
| 9527 | Netty TCP 端口，RPC 服务端监听，客户端直连 |
| 7103 | nameserver 端口（可选），服务注册发现，不启动时直连仍正常 |

---

## 关键组件详解

### 1. `ServiceBs`（服务端）

服务端注册器，单例，Builder 模式链式调用：

```java
ServiceBs.getInstance()
    .port(9527)                              // Netty 监听端口
    .register("hello", new HelloServiceImpl()) // serviceId → 实现类
    .registerCenter("127.0.0.1:7103")        // 可选：注册到 nameserver
    .expose();                               // 启动 Netty + 注册到注册中心
```

`expose()` 内部执行三步：

```
expose()
    │
    ├── DefaultServiceFactory.registerServicesLocal(serviceConfigList)
    │       └── serviceId → (impl实例 + Method[] methodMap) 存入本地 Map
    │
    ├── DefaultNettyServer.newInstance(port, RpcServerHandler).asyncRun()
    │       └── Netty ServerBootstrap 绑定端口，异步监听
    │
    └── registerServiceCenter()
            ├── 为每个 registerCenter 建立 Netty 长连接（DefaultNettyClient）
            ├── 通过 RpcServerRegisterHandler 接收注册中心下发的节点变化通知
            └── 向注册中心发送 ServiceEntry { serviceId, ip, port }（可延迟 delay ms）
```

**服务方法查找**：`DefaultServiceFactory` 在注册时通过反射扫描 serviceImpl 的所有方法，
以 `methodName + paramTypeNames` 为 key 构建索引，调用时直接 `Method.invoke(impl, args)`，
无需每次 `Class.getMethod()`。

### 2. `ClientBs<T>`（客户端）

客户端引用配置类，Builder 模式：

```java
HelloService proxy = ClientBs.<HelloService>newInstance()
    .serviceId("hello")                    // 必须与服务端 register() 的 id 一致
    .serviceInterface(HelloService.class)  // 用于创建 JDK 动态代理
    .addresses("127.0.0.1:9527")          // 直连地址（subscribe=false 时必填）
    .timeout(5000)                         // 调用超时（ms），默认 60000ms
    .subscribe(false)                      // false=直连，true=从注册中心订阅
    .callType(CallTypeEnum.SYNC)           // 调用方式，默认 SYNC
    .failType(FailTypeEnum.FAIL_OVER)      // 失败策略，默认 FAIL_OVER
    .reference();                          // 返回 HelloService 接口的动态代理对象
```

`reference()` 内部执行：

```
reference()
    │
    ├── ClientRegisterManager.initServerChannelFutureList()
    │       ├── subscribe=false → 直接解析 addresses，为每个地址建立 Netty 长连接
    │       └── subscribe=true  → 连接 registerCenter，订阅 serviceId 的地址列表
    │
    ├── buildServiceProxyContext()
    │       └── 将所有配置打包为 ServiceContext（serviceId、接口类型、invokeManager、callType...）
    │
    └── DefaultReferenceProxy.proxy()
            └── Proxy.newProxyInstance(classLoader, [HelloService.class], proxy)
                    → 返回 HelloService 动态代理对象
```

### 3. `DefaultReferenceProxy`（JDK 动态代理）

每次通过代理调用方法时，`invoke()` 被触发：

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 1. 检查客户端状态（ENABLE）
    // 2. 执行拦截器 before()
    // 3. 构建 RpcRequest
    DefaultRpcRequest rpcRequest = new DefaultRpcRequest();
    rpcRequest.serviceId(...)      // 服务标识
             .methodName(...)      // 方法名
             .paramTypeNames(...)  // 参数类型名（用于服务端反射查找方法）
             .paramValues(args)    // 参数值
             .returnType(...)      // 返回类型
             .timeout(...)         // 超时
             .callType(...);       // 调用方式

    // 4. 调用 RemoteInvokeService.remoteInvoke(context)
    //    → 选择 Channel（负载均衡）→ channel.writeAndFlush(rpcRequest)
    //    → 按 callType 等待结果（SYNC: InvokeManager.getResponse() 阻塞）
    // 5. 执行拦截器 after()
    return result;
}
```

### 4. `InvokeManager`（同步等待机制）

`InvokeManager` 是客户端同步调用的核心：

```
addRequest(seqId, timeoutMs)
    └── requestMap.put(seqId, expireTime)  ← 注册等待槽

channel.writeAndFlush(request)             ← 异步发送

getResponse(seqId)                         ← HTTP 线程在此阻塞
    └── synchronized(this) { this.wait(remainingMs) }

    ─── 服务端响应到达 ───

addResponse(seqId, response)               ← 由 Netty IO 线程调用
    └── responseMap.put(seqId, response)
    └── synchronized(this) { this.notifyAll() }  ← 唤醒阻塞线程

getResponse(seqId) 被唤醒
    └── responseMap.remove(seqId)
    └── return response
```

超时由 `InvokeTimeoutCheckThread` 定时扫描 requestMap，过期后写入超时响应并唤醒等待方。

### 5. 调用类型（`CallTypeEnum`）

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| `SYNC` | 同步阻塞，等待服务端响应后返回 | 默认，绝大多数场景 |
| `ONE_WAY` | 单向发送，不关心结果，不等待 | 日志、埋点等不需要返回值的操作 |
| `ASYNC` | 异步调用，发送后立即返回，结果通过 Future 获取 | 高并发、非阻塞场景 |
| `CALLBACK` | 回调方式，通过注册的 Callback 处理结果 | 事件驱动场景 |

### 6. 失败策略（`FailTypeEnum`）

| 策略 | 说明 |
|------|------|
| `FAIL_OVER` | 失败自动重试（默认），重试次数可配 |
| `FAIL_FAST` | 失败立即抛出异常，不重试 |

---

## 通信协议

客户端与服务端之间使用 **自定义二进制帧**（Netty Pipeline 编解码）：

```
┌──────────────┬─────────────┬──────────────────┬──────────────┬───────────────┐
│  Magic 4B    │  包类型 4B  │  序列化类型 4B   │  数据长度 4B  │  数据（变长）  │
│  固定魔数    │ REQ / RESP  │ Kryo/JSON/Hessian │              │  序列化对象    │
└──────────────┴─────────────┴──────────────────┴──────────────┴───────────────┘
```

- **Magic**：固定魔数，用于识别合法包，防止粘包/半包时误解析
- **包类型**：`PackageType.REQUEST_PACK(726571)` / `RESPONSE_PACK(726573)`
- **序列化**：默认 Kryo（高性能二进制），可选 JSON（调试友好）、Hessian（跨语言）
- **长度字段**：Netty `LengthFieldBasedFrameDecoder` 处理 TCP 粘包/半包

### RpcRequest 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `seqId` | String | UUID，用于客户端响应路由（InvokeManager 的 key） |
| `serviceId` | String | 服务唯一标识，服务端据此查找实现 |
| `methodName` | String | 方法名 |
| `paramTypeNames` | List\<String\> | 参数类型全类名，服务端据此精确定位重载方法 |
| `paramValues` | Object[] | 参数值（需可序列化） |
| `returnType` | Class | 返回类型 |
| `timeout` | long | 超时时间（ms） |
| `callType` | CallTypeEnum | 调用方式 |

---

## 名称服务器（可选）

`nameserver`（`kuma-cloud-rpc-nameserver`，端口 7103）提供服务注册与发现：

```
服务端注册流程：
    ServiceBs.registerCenter("127.0.0.1:7103")
        → 建立 Netty 长连接到 nameserver
        → 发送 ServiceEntry { serviceId="hello", ip, port=9527 }
        → nameserver 持久化并广播给所有已订阅的客户端

客户端发现流程（subscribe=true）：
    ClientBs.subscribe(true).registerCenter("127.0.0.1:7103")
        → 建立长连接到 nameserver
        → 发送订阅请求 { serviceId="hello" }
        → nameserver 返回服务端地址列表
        → 后续节点变化实时推送（REGISTER_CENTER_ADD/REMOVE_NOTIFY）
```

**project6 中使用直连模式（`subscribe=false`）**，nameserver 不启动时直连调用完全正常。
仅当需要动态服务发现时才需要启动 nameserver。

---

## Project6 代码结构

```
kuma-project-project6/
├── src/main/java/com/kuma/cloud/project6/
│   ├── Project6Application.java           # 启动类
│   ├── api/
│   │   └── HelloService.java              # RPC 服务接口（客户端/服务端共用）
│   ├── service/
│   │   └── HelloServiceImpl.java          # RPC 服务实现（服务端持有）
│   ├── rpc/
│   │   └── RpcBootstrap.java              # ApplicationRunner：启动 Netty 服务端 + 初始化客户端代理
│   ├── controller/
│   │   └── HelloController.java           # HTTP 接口，通过 RPC 代理调用服务
│   └── config/
│       └── AppConfig.java                 # Spring 配置（占位）
├── src/main/resources/
│   ├── application.yml                    # 激活 profile
│   └── application-dev.yml                # RPC 端口、地址、超时配置
├── build.gradle                           # Gradle 依赖
└── RPC.md                                 # 快速参考文档
```

### `Project6Application`

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project6"})
public class Project6Application {
    public static void main(String[] args) {
        new StartupSpringApplication(Project6Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project6")
                .run(args);
    }
}
```

### `HelloService`（接口）

```java
public interface HelloService {
    String hello(String name);   // 问候方法
    int add(int a, int b);       // 求和方法（演示基本类型参数）
}
```

真实项目中，此接口通常单独打成 `api.jar`，服务端和客户端分别依赖。

### `HelloServiceImpl`（服务端实现）

```java
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name + "! (from kuma-cloud-rpc server)";
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
```

注意：不需要 Spring `@Service`，由 RPC 框架的 `DefaultServiceFactory` 直接管理实例。

### `RpcBootstrap`

```java
@Component
public class RpcBootstrap implements ApplicationRunner {

    @Value("${rpc.server.port:9527}")
    private int serverPort;

    @Value("${rpc.client.server-address:127.0.0.1:9527}")
    private String clientServerAddress;

    @Value("${rpc.client.timeout:5000}")
    private long clientTimeout;

    private volatile HelloService helloService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startRpcServer();        // 1. 启动 Netty 服务端
        Thread.sleep(500);       // 2. 等待 Netty 异步绑定完成
        initRpcClient();         // 3. 初始化客户端代理
    }

    private void startRpcServer() {
        ServiceBs serviceBs = (ServiceBs) ServiceBs.getInstance()
                .port(serverPort)
                .register(SERVICE_ID, new HelloServiceImpl());
        if (nameserverAddress != null && !nameserverAddress.isBlank()) {
            serviceBs.registerCenter(nameserverAddress);
        }
        serviceBs.expose();
    }

    private void initRpcClient() {
        this.helloService = ClientBs.<HelloService>newInstance()
                .serviceId(SERVICE_ID)
                .serviceInterface(HelloService.class)
                .addresses(clientServerAddress)
                .timeout(clientTimeout)
                .subscribe(false)   // 直连模式，不走注册中心
                .reference();
    }
}
```

**为什么 `Thread.sleep(500)`？**

`DefaultNettyServer.asyncRun()` 是异步的（`ServerBootstrap.bind().sync()` 在独立线程中执行）。
`sleep(500)` 是为了确保 Netty 端口绑定完成后再创建客户端连接，避免客户端连接时服务端还未就绪。

### `HelloController`

```java
@RestController
@RequestMapping("/rpc")
public class HelloController {

    private final RpcBootstrap rpcBootstrap;

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        HelloService service = rpcBootstrap.helloService();
        if (service == null) {
            return "RPC client not ready, please try again later.";
        }
        return service.hello(name);  // 透明 RPC，写法与本地调用完全相同
    }

    @GetMapping("/add")
    public String add(@RequestParam int a, @RequestParam int b) {
        HelloService service = rpcBootstrap.helloService();
        if (service == null) {
            return "RPC client not ready, please try again later.";
        }
        return a + " + " + b + " = " + service.add(a, b);
    }
}
```

---

## 配置说明

### `application-dev.yml`

```yaml
server:
  port: 8086                             # HTTP 服务端口

rpc:
  server:
    port: 9527                           # Netty 服务端监听端口
  client:
    server-address: 127.0.0.1:9527      # 直连地址（host:port，多个用逗号分隔）
    timeout: 5000                        # RPC 调用超时（ms）
  # nameserver:
  #   address: 127.0.0.1:7103           # 可选：注册中心地址
```

### `RpcBootstrap` 配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `rpc.server.port` | `9527` | Netty 服务端端口 |
| `rpc.nameserver.address` | 空 | nameserver 地址（空时跳过注册） |
| `rpc.client.server-address` | `127.0.0.1:9527` | 客户端直连地址 |
| `rpc.client.timeout` | `5000` | 调用超时（ms） |

---

## 快速启动

```bash
./gradlew :kuma-project:kuma-project-project6:bootRun
```

或在 IDE 中运行 `Project6Application.main()`。

启动后日志示例：
```
[RPC] Starting server on port 9527 ...
server register local finish.
server service start finish.
[RPC] Server exposed on port 9527.
[RPC] Connecting client to 127.0.0.1:9527 ...
[RPC] Client connected to 127.0.0.1:9527.
```

---

## API 接口

**Base URL**: `http://localhost:8086`

### 调用 hello 方法

```http
GET /rpc/hello?name=Alice
```

**返回示例**：
```
Hello, Alice! (from kuma-cloud-rpc server)
```

**默认 name=World**：
```http
GET /rpc/hello
→ Hello, World! (from kuma-cloud-rpc server)
```

### 调用 add 方法

```http
GET /rpc/add?a=3&b=5
```

**返回示例**：
```
3 + 5 = 8
```

---

## 同进程模式的特殊性

Project6 在同一 JVM 进程内同时运行服务端和客户端，这在真实场景中是不常见的。这带来一些注意事项：

| 特性 | 说明 |
|------|------|
| 共用 JVM | 客户端抛出的异常（如调用超时）会被同一进程捕获，不影响服务端 |
| 不共用 InvokeManager | `ServiceBs` 的 `InvokeManager` 和 `ClientBs` 的 `InvokeManager` 是独立实例，`seqId` 路由不会混淆 |
| 网络仍经过 TCP | 即使同进程，调用依然走 `localhost:9527` TCP，不走内存直连，性能测试结果与真实跨进程场景有差异 |

---

## 扩展能力

`kuma-cloud-rpc` 框架设计为可扩展，以下能力可按需接入：

| 能力 | 扩展点 | 说明 |
|------|--------|------|
| 自定义序列化 | `BaseSerialization` | 替换 Kryo/JSON/Hessian |
| 负载均衡 | `LoadBalance` | 随机（`RandomLoadBalancer`）、轮询（`RoundRobinLoadBalance`）、自定义 |
| 拦截器 | `RpcInterceptor` | `before()` / `after()` 钩子，用于链路追踪、耗时统计 |
| 过滤器 | `RpcFilter` | `CommonFilter` 接口，链式过滤 |
| 失败策略 | `FailStrategy` | `FAIL_OVER`（重试）/ `FAIL_FAST`（快速失败）/ 自定义 |
| 泛化调用 | `ClientBs.generic(true)` | 无需依赖接口 jar，通过 `GenericReferenceProxy` 调用 |
| 注册中心 | `ServiceRegistry` | 接入自研 nameserver 或 Nacos |

### 泛化调用示例

```java
// 无需持有 HelloService 接口
GenericReferenceProxy proxy = (GenericReferenceProxy) ClientBs.newInstance()
        .serviceId("hello")
        .generic(true)
        .addresses("127.0.0.1:9527")
        .reference();

Object result = proxy.invoke("hello", List.of("java.lang.String"), new Object[]{"Alice"});
```

### 自定义拦截器

```java
ClientBs.<HelloService>newInstance()
        .rpcInterceptor(new RpcInterceptor() {
            @Override
            public void before(RpcInterceptorContext context) {
                System.out.println("调用前: params=" + Arrays.toString(context.params()));
            }
            @Override
            public void after(RpcInterceptorContext context) {
                System.out.println("调用后: result=" + context.result());
            }
        })
        ...
```

---

## 常见问题

**Q: `RPC client not ready, please try again later.` 是什么原因？**

A: `RpcBootstrap.run()` 中先启动服务端（`startRpcServer()`），等待 500ms 后才初始化客户端代理。
如果在这 500ms 内就发送 HTTP 请求，`helloService()` 返回 null。稍等片刻再重试即可。

**Q: 为什么 `Thread.sleep(500)` 而不用更可靠的方式等待端口就绪？**

A: 简化演示。生产场景可监听 Netty `ChannelFuture.addListener()`，在绑定成功回调后再初始化客户端。

**Q: nameserver 不启动会报错吗？**

A: 不会崩溃。`ServiceBs.expose()` 中连接 nameserver 失败时打印 WARN 日志并跳过（`continue`），
Netty 服务端依然正常绑定，客户端直连调用完全正常。

**Q: 服务端和客户端在不同进程，如何修改？**

A:
- 服务端进程：只运行 `ServiceBs.getInstance().port(9527).register(...).expose()`
- 客户端进程：持有 `HelloService` 接口 jar，运行 `ClientBs.newInstance()...reference()`，`addresses` 填服务端实际 IP:Port
- 两个进程不需要有任何代码依赖，只需要共享接口定义

**Q: 如何支持多个服务端节点的负载均衡？**

A: `addresses()` 支持多个地址（逗号分隔），客户端按配置的负载均衡策略选取节点：

```java
ClientBs.<HelloService>newInstance()
        .addresses("192.168.1.1:9527,192.168.1.2:9527,192.168.1.3:9527")
        .loadBalance(LoadBalances.roundRobin())
        ...
```

**Q: RPC 调用超时后，服务端还会继续执行吗？**

A: 是的。超时是客户端侧的行为——`InvokeManager.getResponse()` 超时后返回超时响应，HTTP 线程提前返回。
服务端的 `method.invoke()` 可能还在执行，执行完后 `writeAndFlush(response)` 到客户端，
但客户端已移除该 seqId 的等待槽，响应会被直接丢弃。
