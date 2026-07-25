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
