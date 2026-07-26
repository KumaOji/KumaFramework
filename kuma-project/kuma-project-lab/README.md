# KumaFramework Test Lab

这是一个独立的测试实验项目，用于集中保存框架和业务技术验证代码，避免临时测试污染正式业务模块。

## 测试分类

- `transaction`：数据库事务、提交、回滚和变更快照测试
- 后续测试按能力建立独立包，例如 `cache`、`mq`、`lock`

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
