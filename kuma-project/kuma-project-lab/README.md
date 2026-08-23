# KumaFramework Test Lab

这是一个独立的测试实验项目，用于集中保存框架和业务技术验证代码，避免临时测试污染正式业务模块。

## 测试分类

- `transaction`：数据库事务、提交、回滚和变更快照测试
- `binlog`：MySQL Row Binlog 变更捕获测试
- `kafka`：Kafka 消息生产与消费测试
- `redis`：String / Hash / List / Set 读写与 TTL 验证
- `vector`：向量库写入、相似度检索、元数据过滤与删除验证
- `leetcode`：算法练习题在线运行与内置用例验证
- `starter`：统一探测与冒烟测试各 `kuma-boot-starter-*` 依赖
- `jni`：通过 JNI 调用 C 实现整数运算、字符串处理与数组求和
- 后续测试按能力建立独立包，例如 `lock`

## 事务测试

1. 在 MySQL 数据库执行 `src/main/resources/sql/transaction-test.sql`。
2. 配置根项目 `gradle.properties` 中的 `mysql.url`、`mysql.username` 和 `mysql.password`。
3. 启动 `LabApplication`。
4. 请求 `POST /api/lab/transaction/transfer`。

## MySQL Binlog

黑盒变更捕获需要 MySQL 开启 Row Binlog，MySQL 配置应包含：

```ini
[mysqld]
server-id=1
log-bin=mysql-bin
binlog-format=ROW
binlog-row-image=FULL
```

修改配置后需重启 MySQL。可使用以下 SQL 验证当前实例：

```sql
SHOW VARIABLES
WHERE Variable_name IN ('log_bin', 'binlog_format', 'binlog_row_image', 'server_id');
```

期望值为 `log_bin=ON`、`binlog_format=ROW`、`binlog_row_image=FULL`。

请求示例：

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 100,
  "failAfterDebit": false,
  "rollbackAfterExecution": true
}
```

返回结果包含是否回滚、修改行的前后快照，以及按执行顺序记录的 SQL、参数和影响行数。

Controller 不直接依赖 JDBC 实现，转账逻辑通过 `TransactionBlackBox` 黑盒协议执行。

## Redis 测试

1. 启动本地 Redis，默认连接 `127.0.0.1:6379`。
2. 在根项目 `gradle.properties` 中配置 `redis.host`、`redis.port`、`redis.database`（可选 `redis.password`）。
3. 启动 `LabApplication`。
4. 请求 `POST /api/lab/redis/scenario`，一次性验证 String / Hash / List / Set 与 TTL 行为。

手动测试示例：

```json
POST /api/lab/redis/string
{
  "key": "demo",
  "value": "hello",
  "ttlSeconds": 60
}
```

实验 key 默认带前缀 `lab:redis:`，避免污染业务数据。

## 向量数据库测试

基于 `kuma-boot-starter-data-vector` 的 `VectorStore` 抽象，默认使用内存实现（零外部依赖），也可切换为 Qdrant。

1. 启动 `LabApplication`（默认 `kuma.boot.data.vector.type=memory`）。
2. 请求 `POST /api/lab/vector/scenario`，一次性验证写入、相似度检索、元数据过滤与删除。

手动测试示例：

```json
POST /api/lab/vector/upsert
{
  "id": "doc-1",
  "content": "KumaFramework 向量检索实验",
  "metadata": {
    "source": "lab"
  }
}
```

```json
POST /api/lab/vector/search
{
  "query": "向量检索",
  "topK": 5,
  "minScore": 0.0
}
```

也可显式传入向量（维度需与配置一致，默认 4）：

```json
POST /api/lab/vector/search
{
  "queryVector": [0.9, 0.1, 0.0, 0.0],
  "topK": 3,
  "filter": {
    "category": "language"
  }
}
```

其他接口：

- `GET /api/lab/vector/status`：查看 provider、集合名与文档数量
- `DELETE /api/lab/vector/{id}`：按 ID 删除
- `DELETE /api/lab/vector/collection`：重置实验集合

切换 Qdrant 时，在 `application.yml` 中修改：

```yaml
kuma:
  boot:
    data:
      vector:
        type: qdrant
        qdrant:
          host: localhost
          http-port: 6333
```

          http-port: 6333
```

## LeetCode 练习

题解源码位于 `src/main/java/com/kuma/cloud/leetcode/`，按四位题号分包（如 `p0001`）。可通过 HTTP 接口浏览题目、运行题解并执行内置测试用例。

1. 启动 `LabApplication`。
2. 查看题目列表：`GET /api/lab/leetcode/problems`
3. 运行全部内置测试：`POST /api/lab/leetcode/scenario`

手动运行示例（第 1 题两数之和）：

```json
POST /api/lab/leetcode/run/1
{
  "input": {
    "nums": [2, 7, 11, 15],
    "target": 9
  }
}
```

其他接口：

- `GET /api/lab/leetcode/problems/{number}`：查看单题信息
- `POST /api/lab/leetcode/problems/{number}/test`：运行单题内置测试

新增一道题：复制 `p0001` 的 `Solution.java` 与 `SolutionTest.java` 目录，改名为对应题号包，并在 `LeetCodeBasicProblemRunnersConfiguration` 或 `LeetCodeStructureProblemRunnersConfiguration` 中注册 runner。

本地单元测试：

```bash
gradlew :kuma-project:kuma-project-lab:test
```

只运行一道题：

```bash
gradlew :kuma-project:kuma-project-lab:test --tests "com.kuma.cloud.leetcode.p0001.SolutionTest"
```

## Starter 测试

集中探测与验证 `kuma-boot-starter-*` 依赖是否引入、Bean 是否就绪，并对已注册探测器执行冒烟测试。

1. 启动 `LabApplication`。
2. 查看 Starter 目录：`GET /api/lab/starter/catalog`
3. 查看当前 classpath 已引入的 Starter：`GET /api/lab/starter/catalog/on-classpath`
4. 查看已注册探测器诊断结果：`GET /api/lab/starter/probes`
5. 一键场景测试：`POST /api/lab/starter/scenario`

单个 Starter 测试示例：

```text
GET  /api/lab/starter/kuma-boot-starter-core/diagnose
POST /api/lab/starter/kuma-boot-starter-cache-redis/smoke-test
```

扩展方式：在 `com.kuma.cloud.lab.starter.probe.impl` 下新增 `StarterProbe` 实现类即可自动注册。

默认已内置以下探测器：

- `kuma-boot-starter-common`
- `kuma-boot-starter-core`
- `kuma-boot-starter-web`
- `kuma-boot-starter-data-datasource`
- `kuma-boot-starter-cache-redis`
- `kuma-boot-starter-data-vector`
- `kuma-boot-starter-springdoc`

## JNI / C 语言测试

通过 JNI 调用 `src/main/c/lab_math.c` 中编译出的动态库，验证 Java 与 C 的互操作。

1. 编译 C 动态库：

```bash
./gradlew :kuma-project:kuma-project-lab:compileNative
```

Windows 优先使用本机 `gcc`；仅当系统未安装 GCC 时，构建才会自动下载便携版 TinyCC 到 `build/tools/tcc`。Linux / macOS 需本机安装 `gcc`。

2. 启动 `LabApplication`。
3. 请求 `POST /api/lab/jni/scenario`，一次性验证 add / multiply / greet / sumArray。

手动测试示例：

```json
POST /api/lab/jni/add
{
  "left": 21,
  "right": 21
}
```

```json
POST /api/lab/jni/greet
{
  "name": "KumaFramework"
}
```

IDE 直启时若提示找不到动态库，可先执行 `compileNative`，或在 VM options 中设置：

```text
-Djava.library.path=kuma-project/kuma-project-lab/build/native
```

## Kafka 测试

1. 确保 Kafka 集群可访问，并在 `gradle.properties` 中配置 `kafka.bootstrap-servers`。
2. 预先创建测试 topic（默认 `kuma-lab-test`），或确保 broker 允许自动创建 topic。
3. 在 `application.yml` 中将 `kuma.lab.kafka.enabled` 设为 `true`。
4. 启动 `LabApplication`。
5. 发送测试消息：`POST /api/lab/kafka/send`

请求示例：

```json
{
  "key": "demo-1",
  "message": "hello kafka"
}
```

6. 查看监听器状态：`GET /api/lab/kafka/status`
7. 查看最近消息：`GET /api/lab/kafka/messages?limit=100&direction=CONSUMED`

返回结果包含 topic、partition、offset，以及内存中缓存的最近生产/消费消息。
