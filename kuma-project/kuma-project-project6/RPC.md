# kuma-cloud-rpc 使用说明

## 整体结构

project6 在**同一进程**内同时扮演 RPC 服务端和客户端，通过 HTTP 接口触发 RPC 调用，便于演示。真实场景中服务端与客户端部署在不同进程/机器。

```
HTTP 请求
    │
    ▼
HelloController          ← Spring MVC，对外暴露 HTTP 接口
    │ 调用代理对象
    ▼
HelloService（接口）      ← 客户端只依赖接口，不依赖实现
    │ JDK 动态代理
    ▼
RpcBootstrap.helloService()
    │ Netty TCP（端口 9527）
    ▼
HelloServiceImpl         ← 服务端真正的实现类
```

---

## 文件说明

| 文件 | 作用 |
|------|------|
| `api/HelloService.java` | RPC 服务接口，客户端与服务端共用 |
| `service/HelloServiceImpl.java` | RPC 服务实现，由服务端持有 |
| `rpc/RpcBootstrap.java` | 启动服务端 + 初始化客户端代理 |
| `controller/HelloController.java` | HTTP 入口，调用客户端代理 |
| `application-dev.yml` | RPC 端口、直连地址等配置 |

---

## 使用步骤

### 第一步：定义接口

```java
// api/HelloService.java
public interface HelloService {
    String hello(String name);
    int add(int a, int b);
}
```

客户端和服务端必须持有**相同的接口**。真实项目中通常把接口单独打成 jar 包供双方依赖。

### 第二步：实现服务（服务端）

```java
// service/HelloServiceImpl.java
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

注意：**不需要** `@Service`，由 RPC 框架自己管理实例。

### 第三步：启动服务端，初始化客户端代理

```java
// rpc/RpcBootstrap.java  （implements ApplicationRunner，Spring 启动后自动执行）
@Override
public void run(ApplicationArguments args) throws Exception {
    // 1. 启动 Netty 服务端，注册服务实现
    ServiceBs.getInstance()
        .port(9527)
        .register("hello", new HelloServiceImpl())
        .expose();

    // 2. 稍等 Netty 绑定完成
    Thread.sleep(500);

    // 3. 初始化客户端代理（直连模式，无需注册中心）
    this.helloService = ClientBs.<HelloService>newInstance()
        .serviceId("hello")
        .serviceInterface(HelloService.class)
        .addresses("127.0.0.1:9527")
        .timeout(5000)
        .subscribe(false)   // false = 直连，不走注册中心
        .reference();       // 返回 JDK 动态代理对象
}
```

关键参数：
- `register("hello", impl)` — `"hello"` 是服务唯一标识，客户端必须与此一致
- `subscribe(false)` — 直连模式，不依赖 nameserver
- `.reference()` — 返回接口的动态代理，调用方法时自动走 RPC

### 第四步：在 Controller 中调用

```java
// controller/HelloController.java
@GetMapping("/hello")
public String hello(@RequestParam(defaultValue = "World") String name) {
    HelloService service = rpcBootstrap.helloService();
    return service.hello(name);   // 透明 RPC，写法与本地调用完全相同
}
```

### 第五步：配置

```yaml
# application-dev.yml
rpc:
  server:
    port: 9527                       # Netty 监听端口
  client:
    server-address: 127.0.0.1:9527  # 直连地址
    timeout: 5000                    # 调用超时（ms）
```

---

## 调用链内部原理

```
service.hello("World")
    │
    ▼  JDK 动态代理
DefaultReferenceProxy.invoke()
    ├─ 生成 seqId（UUID）
    ├─ 构建 RpcRequest { serviceId, methodName, paramTypeNames, paramValues }
    ├─ invokeManager.addRequest(seqId)   ← 注册等待槽
    ├─ channel.writeAndFlush(request)    ─────────────────────→  服务端收到请求
    │                                                              DefaultServiceFactory.invoke()
    │                                                              └─ methodMap 查找 Method
    │                                                              └─ method.invoke(impl, args)
    │                                                              └─ writeAndFlush(response)
    └─ invokeManager.getResponse(seqId)  ←──────────────────────  客户端 Handler 收到响应
         └─ wait() 阻塞                       addResponse(seqId, response) + notifyAll()
         └─ 被唤醒，返回 response.result()
```

通信协议为自定义二进制帧（Netty）：

```
┌──────────┬───────────┬─────────────────┬──────────┬───────────┐
│ Magic 4B │ 包类型 4B │ 序列化类型 4B   │ 数据长度 4B │ 数据     │
│ CAFEBABE │ REQ/RESP  │ Kryo/JSON/Hessian│          │ (变长)    │
└──────────┴───────────┴─────────────────┴──────────┴───────────┘
```

---

## 注意事项

- **nameserver 可选**：不启动 nameserver 时服务注册失败，但直连调用（`subscribe(false)`）仍正常工作
- **同进程演示**：服务端与客户端共用同一个 `InvokeManager` 实例，`seqId` 必须唯一，否则响应无法对应到请求
- **线程阻塞**：同步调用模式下，HTTP 线程会在 `getResponse()` 处阻塞，直到服务端响应或超时（5000ms）
