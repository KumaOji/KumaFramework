# Java 接入 KumaAgent gRPC 服务

## 1. 启动 Python gRPC 服务器

```bash
cd kuma-ai-framework/kuma-ai-agent/kuma-agent
uv run python app/grpc_server.py --port 50051
```

---

## 2. Java 项目配置（Gradle）

### build.gradle

```groovy
plugins {
    id 'java'
    id 'com.google.protobuf' version '0.9.4'
}

dependencies {
    // gRPC runtime
    implementation 'io.grpc:grpc-netty-shaded:1.62.2'
    implementation 'io.grpc:grpc-protobuf:1.62.2'
    implementation 'io.grpc:grpc-stub:1.62.2'

    // protobuf runtime
    implementation 'com.google.protobuf:protobuf-java:3.25.3'

    // 编译期注解处理器（生成存根需要）
    compileOnly 'org.apache.tomcat:annotations-api:6.0.53'
}

protobuf {
    protoc {
        artifact = 'com.google.protobuf:protoc:3.25.3'
    }
    plugins {
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.62.2'
        }
    }
    generateProtoTasks {
        all()*.plugins {
            grpc {}
        }
    }
}

// 告诉 IDEA / 编译器去哪里找生成的 Java 源码
sourceSets {
    main {
        java {
            srcDirs 'build/generated/source/proto/main/grpc'
            srcDirs 'build/generated/source/proto/main/java'
        }
        proto {
            srcDir 'src/main/proto'
        }
    }
}
```

### Spring Boot 项目的 build.gradle（Maven 同理）

```groovy
// 在 Spring Boot 项目中额外添加
implementation 'net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE'
```

---

## 3. 放置 proto 文件

将 `proto/kuma_agent.proto` 复制到 Java 项目：

```
src/
└── main/
    └── proto/
        └── kuma_agent.proto   ← 直接复制，无需修改
```

执行 `./gradlew generateProto` 或直接 `./gradlew build`，会自动生成：

```
build/generated/source/proto/main/
├── grpc/com/kuma/ai/agent/grpc/KumaAgentServiceGrpc.java
└── java/com/kuma/ai/agent/grpc/
    ├── ChatRequest.java
    ├── ChatResponse.java
    ├── StreamToken.java
    └── ...
```

---

## 4. Java 调用示例

### 4.1 阻塞式对话（Blocking Chat）

```java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.kuma.ai.agent.grpc.*;

public class KumaAgentClient {

    private final KumaAgentServiceGrpc.KumaAgentServiceBlockingStub stub;
    private final ManagedChannel channel;

    public KumaAgentClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()          // 开发环境不加密；生产用 .useTransportSecurity()
                .build();
        stub = KumaAgentServiceGrpc.newBlockingStub(channel);
    }

    /** 单次阻塞对话 */
    public String chat(String message, String threadId) {
        ChatRequest req = ChatRequest.newBuilder()
                .setMessage(message)
                .setThreadId(threadId != null ? threadId : "")
                .build();
        ChatResponse resp = stub.chat(req);
        System.out.println("ThreadId: " + resp.getThreadId());
        System.out.println("Title:    " + resp.getTitle());
        return resp.getResponse();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        KumaAgentClient client = new KumaAgentClient("127.0.0.1", 50051);

        // 第一轮
        String answer1 = client.chat("你好，我叫张三，是一名 Java 工程师", "thread-001");
        System.out.println("回复: " + answer1);

        // 第二轮（同一 thread_id，保持上下文）
        String answer2 = client.chat("我叫什么名字？", "thread-001");
        System.out.println("回复: " + answer2);

        client.shutdown();
    }
}
```

### 4.2 流式对话（Streaming Chat）

```java
import io.grpc.stub.StreamObserver;
import com.kuma.ai.agent.grpc.*;
import java.util.concurrent.CountDownLatch;

public class StreamingExample {

    public static void streamChat(String host, int port) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        // 流式 RPC 必须用异步 stub
        KumaAgentServiceGrpc.KumaAgentServiceStub asyncStub =
                KumaAgentServiceGrpc.newStub(channel);

        CountDownLatch done = new CountDownLatch(1);
        StringBuilder fullResponse = new StringBuilder();

        ChatRequest req = ChatRequest.newBuilder()
                .setMessage("讲一个关于程序员的笑话")
                .setThreadId("stream-001")
                .build();

        asyncStub.chatStream(req, new StreamObserver<StreamToken>() {
            @Override
            public void onNext(StreamToken token) {
                if (!token.getDone()) {
                    // 逐 token 打印（不换行）
                    System.out.print(token.getDelta());
                    System.out.flush();
                    fullResponse.append(token.getDelta());
                } else {
                    // 最后一条：包含完整回复和标题
                    System.out.println();
                    System.out.println("\n--- 完整回复 ---");
                    System.out.println(token.getResponse());
                    System.out.println("Thread: " + token.getThreadId());
                    System.out.println("Title:  " + token.getTitle());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("流式对话出错: " + t.getMessage());
                done.countDown();
            }

            @Override
            public void onCompleted() {
                done.countDown();
            }
        });

        done.await();
        channel.shutdown();
    }
}
```

### 4.3 Spring Boot 集成（推荐）

```java
// application.yml
// grpc:
//   client:
//     kuma-agent:
//       address: 'static://127.0.0.1:50051'
//       negotiationType: PLAINTEXT

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import com.kuma.ai.agent.grpc.*;

@Service
public class KumaAgentService {

    @GrpcClient("kuma-agent")
    private KumaAgentServiceGrpc.KumaAgentServiceBlockingStub blockingStub;

    @GrpcClient("kuma-agent")
    private KumaAgentServiceGrpc.KumaAgentServiceStub asyncStub;

    public String chat(String message, String threadId) {
        ChatRequest req = ChatRequest.newBuilder()
                .setMessage(message)
                .setThreadId(threadId)
                .build();
        return blockingStub.chat(req).getResponse();
    }
}
```

---

## 5. 线程（会话）管理

```java
// 列出所有线程
ListThreadsResponse threads = stub.listThreads(
        ListThreadsRequest.newBuilder().build());
threads.getThreadsList().forEach(t ->
    System.out.printf("  %s  title=%s  msgs=%d%n",
        t.getThreadId(), t.getTitle(), t.getMessageCount()));

// 删除线程
stub.deleteThread(
    DeleteThreadRequest.newBuilder().setThreadId("thread-001").build());

// 查看 Agent 配置
GetConfigResponse cfg = stub.getConfig(GetConfigRequest.newBuilder().build());
System.out.println("Model: " + cfg.getModel());
System.out.println("Tools: " + cfg.getToolsList());
```

---

## 6. 连接配置参考

| 场景 | 配置 |
|------|------|
| 本机开发 | `127.0.0.1:50051` + `usePlaintext()` |
| 局域网 | `192.168.x.x:50051` + `usePlaintext()` |
| 生产环境 | TLS 证书 + `useTransportSecurity()` |
| 超时控制 | `stub.withDeadlineAfter(30, TimeUnit.SECONDS)` |
| 连接池 | `ManagedChannelBuilder` 默认已做连接复用 |
