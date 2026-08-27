# CCSR 接入说明 — project7

## 什么是 CCSR

CCSR（Config Center Service Registry）是 Kuma Cloud 自研的配置中心，核心特性：

- **gRPC 通信**：客户端与服务端通过 gRPC 进行配置读写。
- **Raft 一致性**：服务端内嵌 SOFAJRaft，通过 Raft 协议保证多节点一致性。
- **实时推送**：客户端订阅后，配置变更通过 gRPC 长连接实时推送，无需轮询。
- **命名空间隔离**：通过 `namespace` 实现多租户配置隔离。

---

## 架构说明

```
project7 (Client)
    │
    │  gRPC (port 8005)
    ▼
CcsrServer (gRPC + Raft)
    │
    │  Raft 内部通信 (port = gRPC端口 + portOffset)
    │  默认: 8005 + 1001 = 9006（Raft选举/日志复制端口）
    ▼
  RocksDB / 内存存储
```

| 端口 | 用途 |
|------|------|
| 8005 | gRPC 服务端口，客户端连接此端口进行配置读写 |
| 9006 | Raft 内部端口（8005 + portOffset 1001），用于 Leader 选举和日志复制，客户端**不需要**连接 |

---

## 快速启动

### 1. 启动服务端

运行 `kuma-cloud-ccsr-server` 模块的 `CcsrServerApplication`。

服务端配置（`application.yml`）关键项：

```yaml
ccsr:
  port: 8005            # gRPC 服务端口
  namespace: default
  raft-config:
    single-node: true   # 单节点模式（本地开发）
    port-offset: 1001   # Raft端口 = gRPC端口 + portOffset = 9006
  cluster-address:
    - 127.0.0.1:9006    # Raft 集群地址（注意：是 Raft 端口，不是 gRPC 端口）
```

### 2. 启动客户端

运行本模块 `Project7Application`，服务端启动后客户端自动建立连接。

客户端配置（`application-dev.yml`）：

```yaml
ccsr:
  rpc-type: grpc
  namespace: default
  cluster-address:
    - 127.0.0.1:8005    # 客户端填 gRPC 端口
    - 127.0.0.1:8006
    - 127.0.0.1:8007
```

---

## 启动日志说明

| 日志 | 含义 |
|------|------|
| `[CCSR] 连接验证成功 ✓  code=2000` | 探针 key 存在，连接正常 |
| `[CCSR] 连接验证成功 ✓  code=4042` | 探针 key 不存在（正常，服务端已响应） |
| `[CCSR] 连接验证失败 ✗  code=5005` | namespace 配置错误，检查 `ccsr.namespace` |
| `[CCSR] 连接验证异常` | 服务端未启动或 `cluster-address` 地址有误 |
| `配置订阅连接断开，将在 Xs 后进行第 N 次重连` | 订阅流断开，客户端按指数退避（1→2→4→…→60s）自动重连 |

---

## API 接口

Base URL: `http://localhost:8087`

### 写入配置

```http
PUT /ccsr/config?group=default_group&dataId=default_data_id&content=hello
```

### 读取配置

```http
GET /ccsr/config?group=default_group&dataId=default_data_id
```

### 删除配置

```http
GET /ccsr/config/delete?group=default_group&dataId=default_data_id
```

---

## 代码结构

```
project7/
├── Project7Application.java          # 启动类，@EnableCcsrClient 开启客户端
├── controller/
│   └── CcsrController.java           # 配置读写 REST 接口
├── dto/
│   └── TextConfig.java               # 配置数据 DTO，实现 ConfigData 接口
└── runner/
    └── CcsrConnectionVerifier.java   # 启动后探针验证连通性
```

### TextConfig

配置数据必须实现 `ConfigData` 接口：

```java
@Data
public class TextConfig implements ConfigData {
    private String content;

    @Override
    public String key() {
        return "text";   // 唯一标识此配置类型的 key
    }
}
```

### CcsrService 用法

```java
// 注入
@Autowired
private CcsrService ccsrService;

// 构建 Payload（namespace 不传时自动使用 ccsr.namespace 配置）
Payload payload = Payload.builder()
        .group("my_group")
        .dataId("my_data_id")
        .configData(new TextConfig("hello world"))  // GET/DELETE 不需要
        .build();

// 写入
Response putResp = ccsrService.request(payload, EventType.PUT);

// 读取
Response getResp = ccsrService.request(payload, EventType.GET);

// 删除
Response delResp = ccsrService.request(payload, EventType.DELETE);
```

---

## 配置变更监听（推送）

客户端与服务端建立 gRPC 长连接订阅，服务端 PUT/DELETE 后会广播至所有订阅同一 namespace 的客户端。

实现监听器：

```java
@Component
public class MyConfigListener extends AbstractConfigListener {

    @Override
    public void onChange(Metadata metadata, EventType eventType) {
        System.out.println("配置变更: " + metadata.getDataId() + " -> " + metadata.getContent());
    }

    @Override
    public boolean isMatch(Metadata metadata) {
        return "my_group".equals(metadata.getGroup());
    }
}
```

---

## 常见问题

**Q: 连接到 8007、8006 失败后才连到 8005，正常吗？**
A: 正常。`ServiceDiscovery` 按 `cluster-address` 列表轮询选择，未启动的节点 TCP 连接失败后会通过指数退避重连，最终连到存活节点。

**Q: 日志中一直出现 `UNAVAILABLE: io exception` 并重连？**
A: 客户端订阅流（`subscribe`）在连接断开时会自动重连，且重连间隔按 1→2→4→…→60s 指数增长。如果服务端正常运行，重连成功后此日志消失。

**Q: Raft 日志中出现 `fsync` 警告？**
A: Windows 系统不支持对目录执行 `fsync`，是正常现象，已在服务端日志配置中抑制。
