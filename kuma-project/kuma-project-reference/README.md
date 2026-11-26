# Kuma Reference：业务项目模板

参考 blog 的常规分层，用于复制后开发新业务。沿用仓库 Java 25、Spring Boot 和 Gradle 版本管理。

日志、操作审计、指标及 Grafana 演示见 [可观测性说明](OBSERVABILITY.md)。

## 项目结构

```text
kuma-project-reference/
├── build.gradle
├── sql/                          PostgreSQL/MySQL 建表与保留数据的升级脚本
└── src/
    ├── main/
    │   ├── java/com/kuma/cloud/reference/
    │   │   ├── ReferenceApplication.java
    │   │   ├── config/           显式选择框架配置和业务安全策略
    │   │   ├── controller/       HTTP 入口与参数校验
    │   │   ├── service/
    │   │   │   └── impl/         业务处理、事务、Mapper 调用及响应转换
    │   │   ├── mapper/           MyBatis Mapper 接口
    │   │   ├── domain/
    │   │   │   ├── dto/          写入参数 CreateOrderDTO
    │   │   │   ├── qo/           查询参数 OrderPageQO
    │   │   │   ├── vo/           响应 OrderVO，分页复用框架 PageResultRecord
    │   │   │   ├── entity/       数据库行对象 OrderDO
    │   │   │   ├── model/        订单状态、金额计算与商品快照
    │   │   │   └── enums/        数据源范围
    │   └── resources/
    │       ├── bootstrap.yml     应用名称、环境和 Nacos 导入
    └── test/                     单元测试、集成测试及隔离配置
```

业务调用链：`Controller → Service → ServiceImpl → BaseMapperX`，查询条件使用框架的 LambdaQueryWrapper，当前示例无需手写 XML。

Controller 注入 Service 接口，使用 Jakarta Validation 校验 DTO/QO；ServiceImpl 处理业务和事务，返回 VO。Mapper 只负责数据库操作，不直接作为 HTTP 入口。订单状态与金额约束保留在 model，数据库行对象不直接对外返回。

单个实现的数据源支持类直接使用具体类。不额外增加 Repository 转发层、Command/Query、副本输出 DTO、转换层或抽象 Service 基类。

## 框架注解复用

| 使用位置 | 框架注解 | 作用 |
|---|---|---|
| ReferenceApplication | `@KumaBootApplication(oauth2ResourceServer = false)` | 代替直接使用 `@SpringBootApplication`，复用上下文工具、Servlet 扫描和 AOP；本示例显式关闭 OAuth2 资源服务器 |
| OrderController | `@BusinessApi` | 复用统一 `Result<T>` 返回体；仍配合 `@RestController` 和请求映射注解 |
| OrderServiceImpl | `@Manager` | 复用框架基于 `@Service` 的组件注解，保留 Service 接口及实现分层 |
| DataSourceConfiguration | `@EnableDynamicDataSource` | 统一导入动态数据源配置、`#last` 参数解析链和数据源路由组件 |
| MyBatisConfiguration | `@EnableActualSql` | 启动时打印 `KUMA SQL LOG`，开发环境以 `TRACE` 记录 Mapper 方法、SQL 参数、耗时和行数 |
| OrderPageQO.status | `@StringEnums` | 复用字段取值校验，允许 CREATED / CONFIRMED，未传值时不筛选状态 |

`@KumaBootApplication` 默认仍启用 OAuth2，已有项目无需修改。reference 使用显式关闭选项，并由 SecurityConfig 声明匿名访问策略。`@EnableDynamicDataSource` 同时适用于 YAML/Nacos 配置来源；数据库配置来源仍受原有 enabled 配置控制。事务支持继续显式导入，不修改 `AutoConfiguration.imports`。

框架尚无等价组合注解的 `@Configuration`、`@Bean`、请求映射和构造器参数 `@Qualifier` 继续使用 Spring 注解。通用必填、数值范围及格式约束继续使用 Jakarta Validation；框架注解按实际用途使用。

## 已配置的能力

| 能力 | 实现与配置 |
|---|---|
| Nacos 配置和服务注册 | kuma-cloud-starter-alibaba、bootstrap.yml |
| 多数据源 | PostgreSQL 的 referenceMain 池，以及 WSL MySQL 的 referenceMysql 池 |
| 动态数据源 | kuma-boot-starter-data-datasource，DataSourceExecutor、@DS 和 #last 参数解析 |
| Schema 切换 | kuma-boot-starter-data-mybatis 的 SchemaInterceptor，SCHEMA 模式 |
| Web MVC | kuma-boot-starter-web，MVC 配置、参数转换、异步执行、Trace MDC 和 HTTP 交换记录 |
| 日志 | kuma-boot-starter-logger，统一 Logback 输出；Nacos 模板启用 reference DEBUG 日志以查看数据源/schema 路由 |
| MyBatis / MyBatis-Plus | Kuma starter、单一 SqlSessionFactory、BaseMapperX 和条件构造器 |
| Redis | kuma-boot-starter-cache-redis、Lettuce、已确认订单详情缓存与 Actuator 健康检查 |
| 业务线程池 | kuma-boot-starter-core 的异步池、MDC/上下文传递、有界队列、拒绝策略和优雅关闭 |
| Actuator / Prometheus | kuma-boot-starter-actuator、kuma-boot-starter-prometheus，health / info / prometheus 及 Kuma 监控扩展 |
| 操作审计 | kuma-boot-starter-audit-log，创建与确认订单的 `@AuditLog` 示例 |
| 业务与资源指标 | kuma-boot-starter-metrics，`@TimedMethod`、动态数据源连接池和业务线程池指标 |
| 链路追踪 | Spring Boot 4 OpenTelemetry starter，经 WSL Collector 导出到 SkyWalking；可选 SkyWalking Java Agent |

MAIN 对应 PostgreSQL 的 `kuma_reference_main` schema；MYSQL 对应独立 MySQL database `kuma_reference_mysql`。DataScope 白名单映射数据源名与目标 schema/database；框架的 DataSourceExecutor 使用 @DS 切换数据源并设置 TenantContextHolder，SchemaInterceptor 根据当前 JDBC 驱动选择 PostgreSQL 或 MySQL SQL parser，将 Mapper SQL 限定到目标 schema/database。数据库、schema 和表由部署流程预先创建，应用启动时不执行数据库初始化。上下文在调用结束或异常后恢复。

先选择数据源再开启事务，事务内禁止切换到其他数据源。分页 count 与 data 共用 REPEATABLE READ 只读事务。数据源切换和 SQL schema 改写是两项组件能力，不代表主从复制或跨库分布式事务。

JetCache 当前关闭。Kafka、认证和对象存储尚未在本模块配置。

### Redis 连接与订单缓存

开发配置通过 `spring.data.redis` 连接 WSL Redis，默认地址为 `172.23.89.45:31281`、ACL 用户为 `default`、逻辑库为 `0`。Windows 本地启动时通过环境变量注入 `REFERENCE_REDIS_PASSWORD`。容器内可通过 `REFERENCE_REDIS_HOST=redis.base.svc.cluster.local`、`REFERENCE_REDIS_PORT=6379` 和 Redis Secret 连接。地址、端口、用户名和逻辑库分别由 `REFERENCE_REDIS_HOST`、`REFERENCE_REDIS_PORT`、`REFERENCE_REDIS_USERNAME` 和 `REFERENCE_REDIS_DATABASE` 覆盖。设置 `REFERENCE_REDIS_ENABLED=false` 可以关闭订单缓存和 Redis 健康检查。连接和命令超时均为 500 ms。

`GET /api/orders/{id}` 查询已确认订单后，通过 Kuma Starter 的 `RedisRepository` 写入缓存，后续查询可以直接命中。键按应用环境、数据范围和订单 ID 隔离，默认格式为 `kuma-reference:dev:orders:v1:MAIN:123`，过期时间为 5 分钟，可通过 `REFERENCE_REDIS_KEY_PREFIX` 和 `REFERENCE_REDIS_ORDER_CACHE_TTL` 调整。待确认订单每次从数据库查询，因此确认后首次查询即可读到最新状态。Redis 连接、读写或反序列化失败时，详情查询仍可由数据库完成。创建、确认和分页直接走数据库。Redis 启用后，其连通性纳入 `/actuator/health`。

线程池直接注入框架的 `asyncThreadPoolTaskExecutor`，配置统一使用 `kuma.boot.core.async`。核心参数为 `core-pool-size`、`max-pool-siz`（沿用框架现有拼写）、`queue-capacity`、`rejection-policy` 和 `await-termination-seconds`。reference 使用 `ABORT` 拒绝策略，过载返回 503；框架默认仍为 `CALLER_RUNS`。

Service 和 Controller 的必需依赖使用构造器注入及 `final` 字段。只有一个构造器时不需要 `@Autowired`；多个同类型 Bean 使用参数上的 `@Qualifier` 区分。

## 启动

入口使用 `kuma-boot-starter-core` 的 `StartupSpringApplication`，启用 Kuma Banner、应用属性初始化和初始化器耗时记录。环境仍由 bootstrap.yml、环境变量及命令行控制。

reference 在 config 下通过 `@ImportAutoConfiguration` 选择 core、logger、data-datasource、data-mybatis、web、actuator 所需的配置。配置类只负责接入，具体能力复用框架实现。ExecutionConfiguration 显式引入框架 Clock，MyBatisConfiguration 通过 `@MapperScan` 指定本项目 Mapper。入口使用 `@KumaBootApplication(oauth2ResourceServer = false)` 显式关闭 OAuth2 资源服务；`SecurityConfig` 声明匿名放行策略，接入认证时同步调整启动注解与安全策略。OpenTelemetry starter 通过自身的 `AutoConfiguration.imports` 自动接入。

运行配置由 Nacos 加载，本地模板为 [Nacos 开发配置](../../.nacos-tmp/local/base/kuma-reference-dev.yaml)：

| 配置项 | 默认值 |
|---|---|
| 应用名 | kuma-reference |
| 环境 | dev |
| Namespace ID | base |
| Group | DEFAULT_GROUP |
| Data ID | kuma-reference-dev.yaml |

先确保 Nacos 配置、PostgreSQL schema 和 MySQL database 已准备好。在 IDEA 运行 `ReferenceApplication.main()`，或在仓库根目录执行：

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:bootRun
```

应用地址 `http://127.0.0.1:19092`，健康检查 `/actuator/health`，指标入口 `/actuator/prometheus`。

使用 `SPRING_CLOUD_NACOS_SERVER_ADDR` 修改 Nacos 地址，使用 `SPRING_PROFILES_ACTIVE` 切换环境；对应的 Nacos Data ID 需提前配置。运行时 Nacos 导入为必需项，连接池等配置修改后需重启；本地模板变更后需发布到对应的 Nacos Data ID。

部署环境需提前准备 PostgreSQL schema、MySQL database 及应用账号，并通过 `REFERENCE_PG_*` 和 `REFERENCE_MYSQL_*` 环境变量提供连接信息。表结构见 [PostgreSQL SQL](sql/postgresql/schema.sql) 和 [MySQL SQL](sql/mysql/schema.sql)。

## 接口

| 方法 | 路径 | 用途 |
|---|---|---|
| POST | /api/orders?scope=MAIN | 创建订单 |
| GET | /api/orders/{id}?scope=MAIN | 查询订单 |
| GET | /api/orders?scope=MAIN&currentPage=1&pageSize=10 | 分页查询 |
| POST | /api/orders/{id}/confirm?scope=MAIN | 确认订单 |

创建订单由请求直接提供示例商品信息：

```json
{"sku":"BOOK-001","productName":"示例商品","unitPrice":99.00,"quantity":2}
```

这是演示持久化和事务的模板接口，正式业务的价格来源应由业务规则确定。业务接口的成功和异常响应统一复用框架 `Result<T>`，包含 `status`、`code`、`message`、`data`、`timestamp`、`version`、`requestId`。`requestId` 与响应头 `kmc-trace-id` 一致。成功时 `status=SUCCESS`，订单数据位于 `data`；失败时 `status=FAILURE`，校验详情位于 `data.fieldErrors`、`data.globalErrors` 或 `data.parameterErrors`，没有详情时 `data=null`。HTTP 状态为：创建 201、参数 400、不存在 404、状态或版本冲突 409、资源繁忙 503。创建接口没有幂等键，重复提交会产生新订单。

`WebConfiguration` 显式导入框架 `WebResponseAutoConfiguration`，Controller 标记 `@BusinessApi` 后自动包装成功响应；框架异常处理将同一 Controller 的错误转换为 `Result`，保留原错误码、公开消息和 HTTP 状态。Controller 继续返回 VO、分页对象或 `ResponseEntity`，业务层不负责 HTTP 包装，已经返回的 `Result` 不会重复包装。Actuator 保持自身的响应协议；不通过 `AutoConfiguration.imports` 注册这些配置。

### 复用 framework 业务错误码

业务错误只使用 common starter 的 `ResultEnum`、`Code` 和 `BusinessException`，不另建项目级通用错误码枚举或异常处理器。通用错误直接抛出 framework 异常：

```java
throw new BusinessException(ResultEnum.BAD_REQUEST);
```

订单直接抛出框架 `BusinessException`，不再声明项目异常子类或 `@ResponseStatus`：

| 场景 | 复用的 ResultEnum | HTTP 状态 |
|---|---|---|
| 订单不存在 | DATA_NOT_EXIST_ERROR | 404 |
| 状态或版本冲突 | REQUEST_OTHER_OPERATION | 409 |

通过 `new BusinessException(ResultEnum.DATA_NOT_EXIST_ERROR, "订单不存在：" + id)` 传递错误码和业务说明。web starter 的 HttpStatusMapper 集中映射现有错误码对应的 HTTP 状态，显式配置的状态映射仍优先。未知业务码默认 500，不通过解析错误码数字猜测 HTTP 状态。响应 code 保留框架 Code 的完整字符串，业务 message 原样返回，内部异常使用配置的通用错误提示。

reference 设置 `kuma.boot.web.error.handling.default-error-code-strategy=RESULT_ENUM`，参数解析、字段校验、金额精度和溢出错误也返回 ResultEnum.BAD_REQUEST 的完整错误码。未知内部错误使用 INNER_ERROR；字段错误保留字段及校验提示。其他应用的默认策略保持原样。复制模板后优先复用已有 ResultEnum；新的业务码沿用框架 Code 和集中状态映射。

数据操作结果不符合预期时复用 data-common 的 `DataCommonException`，例如 `new DataCommonException(ResultEnum.FAILED, "订单插入未生效")`。它支持自定义消息、框架错误码及原始 cause，不把影响行数为 0 误判为数据库连接故障。异常保留诊断消息，对外仍遵循内部错误提示配置；数据库驱动实际抛出的异常不在业务层重复包装。

## 框架复用与数据结构

- `OrderPageQO extends PageQuery`，默认 `currentPage=1`、`pageSize=20`，页码至少为 1，每页 1–100 条；`page`、`size` 仍可作为参数别名。
- 分页由框架 `MpUtils`、分页拦截器和 `PageUtils.toResult` 完成，HTTP 响应为 `Result<PageResultRecord<OrderVO>>`：外层 `data` 内包含 `totalSize`、`totalPage`、`currentPage`、`pageSize`、`data`（订单列表）。删除原先的 `PageVO`；超出最后一页返回空列表并保留总数。排序只允许 `createdAt`、`id`、`sku`，方向为 asc/desc。分页插件根据当前 JDBC 连接识别方言，不固定 MySQL。
- `OrderDO extends MpSuperEntity<Long>`，`OrderMapper extends BaseMapperX<OrderDO>`，复用插入、按 ID 查询、审计字段和 `updateById`。确认订单通过 `@Version` 与框架乐观锁插件检查旧版本，不再手写版本更新 SQL。ID 为数据库自增 Long，`create_date`、`modify_date`、`created_user`、`modify_user` 对齐框架。不同数据源可能存在同一个数字 ID，订单定位需同时包含 scope。
- Service 复用修复后的 `TransactionalUtils`，每次执行使用独立事务定义，继承统一超时。先路由，再执行事务；分页 count 和 data 保留同一 REPEATABLE READ 只读事务。框架 `TransactionAutoConfiguration` 同步和异步失败保留原始原因，异步 Task API 返回可观察的 CompletableFuture。
- UTC `Clock` 和异步线程池由 core 提供，业务或测试可提供对应 Bean 替换；SkyWalking 装饰器由 logger 提供。
- 金额直接复用 common 的 `Money`，不再维护 reference 自己的金额类。框架 Money 为不可变对象，转换金额时拒绝精度损失，运算时检查溢出。示例币种为 CNY、单价非负，订单总金额上限为 `92233720368547758.07` 元，数据库约束同步检查该范围；数量和订单状态仍由业务模型约束。

### 表结构升级

新库执行 `sql/postgresql/schema.sql` 或 `sql/mysql/schema.sql`。这些脚本也用于集成测试建表，应用启动不会自动执行 DDL。

已有 UUID 订单表使用对应目录的 `migrate-uuid-orders.sql` 一次性升级：原 UUID 保留在可空唯一列 `legacy_id`，增加 Long 自增主键，并将 `created_at` 改为 `create_date`、补齐审计字段和金额范围约束。既有订单须满足上述金额范围；迁移不会截断或舍入金额。旧进程停止后执行升级，再启动新版本；MySQL DDL 隐式提交。这些脚本只针对本模板之前的单表结构。

接口变化：订单 ID 从 UUID 字符串改为 Long 数字，分页 JSON 改为框架字段，原 UUID 链接不再作为新 ID 使用。已有数据可通过 `legacy_id` 查询对应的新 ID。Nacos 须同步开启 `kuma.boot.data.mybatis.mybatis-plus.interceptor.pagination.enabled=true`、`optimistic-locker.enabled=true` 和 `auto-fill.enabled=true`，仓库模板已更新。本轮未对正在运行的数据库执行升级脚本。

## 复制为新项目

模板允许匿名访问、客户端选择 `scope` 和提交商品价格。用于实际业务前，需明确访问控制、数据范围授权、价格来源和写入接口的幂等策略。

1. 复制源码及配置模板，排除 build、日志及真实凭据。
2. 修改模块名、根包、启动类、Gradle mainClass 和 JAR 名称。
3. 在 settings.gradle 注册新模块，更新文档命令和请求示例中的模块路径。
4. 分配应用名、端口、Nacos Data ID、数据库账户和 schema，更新 DataScope 与 JDBC currentSchema。
5. 根据业务替换订单示例，并由部署流程预先创建并维护所需的数据库、schema 和表。
6. 保留隔离测试配置，验证 test 和 bootJar 后，再检查部署环境的中间件连通性。

## 验证

普通测试关闭 Nacos 配置及服务注册，通过 HTTP 调用本机应用并使用两套隔离的 H2 数据源。H2 只用于测试，不进入生产 JAR。

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:test
.\gradlew.bat :kuma-project:kuma-project-reference:bootJar
```

通过 `REFERENCE_REDIS_TEST_PASSWORD` 提供真实 Redis 测试密码后，可以验证 Actuator 连通性、确认后缓存及过期时间。测试使用随机键前缀和 H2 订单数据。

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:test --tests '*RedisReferenceApplicationTest.cachesOnlyConfirmedOrdersWithScopeAndExpiry'
```

设置上述数据库环境变量后，可单独运行真实 WSL 测试。测试连接 PostgreSQL 和 MySQL。

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:wslTest
```
