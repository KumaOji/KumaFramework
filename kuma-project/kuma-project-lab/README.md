# KumaFramework Test Lab

这是一个独立的测试实验项目，用于集中保存框架和业务技术验证代码，避免临时测试污染正式业务模块。

## 测试分类

- `transaction`：数据库事务、提交、回滚和变更快照测试
- `binlog`：MySQL Row Binlog 变更捕获测试
- `kafka`：Kafka 消息生产与消费测试
- `redis`：String / Hash / List / Set 读写与 TTL 验证
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

## JNI / C 语言测试

通过 JNI 调用 `src/main/c/lab_math.c` 中编译出的动态库，验证 Java 与 C 的互操作。

1. 编译 C 动态库：

```bash
./gradlew :kuma-project:kuma-project-lab:compileNative
```

Windows 若未安装 GCC，构建会自动下载便携版 TinyCC 到 `build/tools/tcc`；Linux / macOS 需本机安装 `gcc`。

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
