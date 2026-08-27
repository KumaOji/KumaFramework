# project5 —— kuma-cloud-job 分布式任务调度示例

## project5 是什么

project5 是 **kuma-cloud-job 分布式任务调度系统**的 **Worker（执行器）** 示例。

kuma-cloud-job 由三个角色组成：

| 角色 | 模块 | 端口 | 职责 |
|------|------|------|------|
| job-nameserver | kuma-cloud-job-nameserver | HTTP:8089 / gRPC:9089 | 服务注册与发现，job-server 向其注册自身地址 |
| job-server | kuma-cloud-job-server | HTTP:8082 / gRPC:9081 | 调度中心，负责任务触发、分发、执行记录 |
| **project5** | kuma-project-project5 | HTTP:8085 / gRPC:9082 | **执行器**，接收 job-server 下发的任务并执行 |

```
job-nameserver (9089)      ← job-server 向其注册
      │
job-server (gRPC:9081)     ← 调度中心，触发并分发任务
      │  gRPC
      ▼
project5 (Worker:9082)     ← 执行器，执行具体业务逻辑
```

---

## 作用

- 演示如何将业务服务接入 kuma-cloud-job，充当 Worker 节点
- 展示两种处理器写法：类级（`BasicProcessor`）和方法级（`@KmcJobHandler`）
- Worker 启动后自动向 job-server 注册，job-server 通过 gRPC 调用 Worker 的处理器方法执行任务

---

## 启动前提

### 1. 数据库注册 appName

job-server 的 `app_info` 表必须有 project5 的记录，否则 Worker 启动时 assertApp 校验失败。

```sql
INSERT INTO app_info (app_name, current_server) VALUES ('project5', '127.0.0.1');
```

### 2. 启动顺序

```
1. 启动 job-nameserver   (端口 9089)
2. 启动 job-server       (HTTP:8082, gRPC:9081)
3. 启动 project5         (HTTP:8085, gRPC:9082)
```

启动成功后日志会出现：

```
[KmcJob] starting ...
GrpcServer started, listening on 9082
[KmcJobSubscribeService] subscribe success
[WorkerHealthReporter] report health status, appId:1, appName:project5, isOverload:false
```

---

## 配置

```yaml
# application-dev.yml
kmcjob:
  worker:
    enabled: true
    app-name: project5           # 须与 app_info 表中的 app_name 一致
    server-address: 127.0.0.1   # job-server 的 IP（不含端口）
    server-port: 9081            # job-server gRPC 端口
    name-server-address: 127.0.0.1:9089  # job-nameserver 地址
```

---

## 两种处理器写法

### 方式一：实现 BasicProcessor 接口（类级处理器）

```java
@Component
public class SimpleJobProcessor implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        // 业务逻辑...
        return new ProcessResult(true, "success");   // true=成功, false=失败
    }
}
```

在 job-server 控制台配置任务时，`processorInfo` 填写 **Bean 名称**：`simpleJobProcessor`

---

### 方式二：@KmcJobHandler 注解（方法级处理器）

```java
@Component
public class MethodJobProcessor {

    @KmcJobHandler(name = "sendNoticeJob")
    public void sendNotice(TaskContext context) {
        // 发送通知业务逻辑
    }

    @KmcJobHandler(name = "cleanCacheJob")
    public void cleanCache(TaskContext context) {
        // 清理缓存业务逻辑
    }
}
```

在 job-server 控制台配置任务时，`processorInfo` 填写 **注解 name 值**：`sendNoticeJob` 或 `cleanCacheJob`

---

## ProcessResult 返回值

```java
new ProcessResult(true, "执行成功")          // 成功，job-server 记录成功
new ProcessResult(false, "执行失败，原因：xxx")  // 失败，job-server 记录失败原因
```

---

## TaskContext 常用方法

| 方法 | 说明 |
|------|------|
| `context.getJobParams()` | 获取控制台配置的任务参数 |
| `context.getJobId()` | 获取任务 ID |
| `context.getInstanceId()` | 获取本次执行实例 ID |

---

## Worker 运行时行为

Worker 注册成功后会持续运行两个后台线程：

- **healthReport**：每 10 秒向 job-server 上报健康状态（当前负载、最大任务数）
  ```
  [WorkerHealthReporter] appId:1, appName:project5, isOverload:false,
  maxLightweightTaskNum:1024, currentLightweightTaskNum:0,
  maxHeavyweightTaskNum:64
  ```
- **ker-heartbeat**：定期发现 job-server 地址，应对 job-server 重启/切换

---

## 文件结构

```
job/
├── SimpleJobProcessor.java   # 类级处理器（实现 BasicProcessor 接口）
└── MethodJobProcessor.java   # 方法级处理器（@KmcJobHandler 注解）
```
