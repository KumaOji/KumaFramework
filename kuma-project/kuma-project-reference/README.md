# Kuma Reference：业务项目模板

参考 blog 的常规分层，用于复制后开发新业务。沿用仓库 Java 25、Spring Boot 和 Gradle 版本管理。

## 项目结构

```text
kuma-project-reference/
├── build.gradle
├── config/nacos/                 Nacos 配置模板
├── scripts/                      本机初始化、启动和演示
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
| 业务线程池 | kuma-boot-starter-core 的异步池、MDC/上下文传递、有界队列、拒绝策略和优雅关闭 |
| Actuator | kuma-boot-starter-actuator，health / info 及 Kuma 监控扩展 |
| 链路追踪 | Spring Boot 4 OpenTelemetry starter，经 WSL Collector 导出到 SkyWalking；启动脚本加载 SkyWalking Java Agent |

MAIN 对应 PostgreSQL 的 `kuma_reference_main` schema；MYSQL 对应独立 MySQL database `kuma_reference_mysql`。DataScope 白名单映射数据源名与目标 schema/database；框架的 DataSourceExecutor 使用 @DS 切换数据源并设置 TenantContextHolder，SchemaInterceptor 根据当前 JDBC 驱动选择 PostgreSQL 或 MySQL SQL parser，将 Mapper SQL 限定到目标 schema/database。数据库、schema 和表由部署流程预先创建，应用启动时不执行数据库初始化。上下文在调用结束或异常后恢复。

先选择数据源再开启事务，事务内禁止切换到其他数据源。分页 count 与 data 共用 REPEATABLE READ 只读事务。数据源切换和 SQL schema 改写是两项组件能力，不代表主从复制或跨库分布式事务。

Redis / JetCache 当前禁用；Kafka、认证、对象存储等尚未在本模块配置。传递依赖不代表已接入。

业务模块通过 Kuma starter 获取 AOP、JDBC 等能力，不重复声明对应的 Spring Boot starter。数据库初始化由部署流程负责，应用不在启动时创建或迁移数据库结构。

线程池直接注入框架的 `asyncThreadPoolTaskExecutor`，配置统一使用 `kuma.boot.core.async`。核心参数为 `core-pool-size`、`max-pool-siz`（沿用框架现有拼写）、`queue-capacity`、`rejection-policy` 和 `await-termination-seconds`。reference 使用 `ABORT` 拒绝策略，过载返回 503；框架默认仍为 `CALLER_RUNS`。

普通异步任务复用 core 的线程池。独立的 `kuma-boot-starter-threadpool` 提供 DynamicTP 能力，当前模板未启用其演示线程池。线程池等运行参数统一由 Nacos 管理，配置模板见 `config/nacos/kuma-reference-dev.yaml`；bootstrap.yml 只保留应用名称、环境和 Nacos 连接、导入及注册配置。已发布的 Nacos 配置需同步使用 `kuma.boot.core.async`。

Service 和 Controller 的必需依赖使用构造器注入及 `final` 字段。只有一个构造器时不需要 `@Autowired`；多个同类型 Bean 使用参数上的 `@Qualifier` 区分。

## 启动

入口使用 `kuma-boot-starter-core` 的 `StartupSpringApplication`，启用 Kuma Banner、应用属性初始化和初始化器耗时记录。环境仍由 bootstrap.yml、环境变量及命令行控制。

框架保持显式接入设计：reference 在 config 下通过 `@ImportAutoConfiguration` 选择 core、logger、data-datasource、data-mybatis、web、actuator 所需的配置，不通过 `AutoConfiguration.imports` 随依赖自动启用。配置类只负责接入，具体能力复用框架实现。ExecutionConfiguration 显式引入框架 Clock，MyBatisConfiguration 通过 `@MapperScan` 指定本项目 Mapper。现有 `@KumaBootApplication` 同时开启 OAuth2 资源服务，因此模板继续使用 `@SpringBootApplication`；`SecurityConfig` 保留业务的放行策略，接入认证时修改该策略。

运行配置由 Nacos 加载，本地模板为 `config/nacos/kuma-reference-dev.yaml`：

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

应用地址 `http://127.0.0.1:19092`，健康检查 `/actuator/health`。

### OpenTelemetry 和 SkyWalking

Nacos 模板已配置 `management.tracing` 和 Boot 4 的 `management.opentelemetry.tracing.export.otlp`。默认发送到 `http://localhost:4318/v1/traces`，对应 WSL 已转发的 OTEL Collector；Collector 再转发到 SkyWalking OAP。开发环境默认采样率为 1.0，可通过 `REFERENCE_TRACING_SAMPLING` 调整，通过 `REFERENCE_OTLP_ENDPOINT` 修改接收地址，通过 `REFERENCE_TRACING_ENABLED` 控制追踪。

在仓库根目录执行 `./kuma-project/kuma-project-reference/scripts/start-wsl.ps1` 会同时加载数据库环境变量和 SkyWalking Agent。Agent 优先使用 `SKYWALKING_AGENT_JAR` 指定的路径；本机未设置时复用 lab 已解压的 `kuma-project/kuma-project-lab/build/tools/skywalking-agent/skywalking-agent.jar`。复制模板到新项目时需另外准备完整 Agent 目录，包含 plugins、activations 和 config。

直接使用 Gradle 启动时可传入 `-PskywalkingAgent=<Agent JAR 绝对路径>`，可选 `-PskywalkingBackend=localhost:11800`。IDEA 则在 ReferenceApplication 的 VM options 中加入：

```text
-javaagent:<Agent JAR 绝对路径>
-Dskywalking.agent.service_name=kuma-reference
-Dskywalking.collector.backend_service=localhost:11800
```

SkyWalking UI 为 `http://localhost:12880`。请求订单接口后，业务日志中的 `OtlpTraceId` 应有 traceId/spanId，加载 Agent 后请求线程的 `TID` 应有值；两套追踪的 ID 可能不同。启动日志没有请求上下文时仍可能为空。只使用 OpenTelemetry 导出也可以在 SkyWalking 查看追踪，但不会自动填充 SkyWalking toolkit 的 `%tid`。

WebConfiguration 显式引入 WebMdcAutoConfiguration，由框架统一注册 TraceFilter、TenantFilter、VersionFilter，均支持 Servlet 异步请求。`kmc-tenant-id` 请求头或同名参数会写入日志 MDC，未提供时使用框架默认租户 `1`；`kmc-request-version` 请求头会写入 RequestVersion，未提供时为空。可通过 `kuma.boot.web.filter.trace=false`、`kuma.boot.web.filter.tenant=false` 或 `kuma.boot.web.filter.version=false` 关闭对应过滤器。项目提供对应 Filter Bean 或 FilterRegistrationBean 时，框架会退让，避免重复注册。日志中的请求租户用于关联上下文，订单数据库路由仍由 DataScope 白名单控制。

三个过滤器同时处理 REQUEST 和 ASYNC 分派：异步任务的结果或异常回到 Servlet 线程时，重新建立 MDC，并在该次分派结束后清理。框架生成的 TraceId 和解析后的请求租户保存在请求属性中，因此初始请求与异步异常日志使用同一个关联 ID，后续请求不会沿用上次的租户或版本。

core 的异步池会组合框架上下文装饰器与应用提供的 TaskDecorator。reference 提供 SkyWalking RunnableWrapper 装饰器，保证其业务异步任务延续请求的 SkyWalking 链路；未加载 Agent 时该包装器不会生成 SkyWalking ID。Klog 字段读取 MDC 的 `tl`，当前框架中的 KlogAutoConfiguration 和 KlogIdGenerator 未实现实际 Klog 接入，因此该字段仍为空。

使用 `SPRING_CLOUD_NACOS_SERVER_ADDR` 修改 Nacos 地址，使用 `SPRING_PROFILES_ACTIVE` 切换环境；对应的 Nacos Data ID 需提前配置。运行时 Nacos 导入为必需项，连接池等配置修改后需重启；本地模板变更后需发布到对应的 Nacos Data ID。

WSL 开发环境可运行 `scripts/initialize-wsl.ps1` 初始化 PostgreSQL schema。MySQL database 与 `kuma-reference-mysql` Kubernetes Secret 需由部署环境预先准备；`scripts/start-wsl.ps1` 会从 Secret 加载两种数据库的环境变量。配置模板仅保留凭据占位变量，真实凭据由部署环境或受控配置中心提供。

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

1. 复制源码及配置模板，排除 build、日志及真实凭据。
2. 修改模块名、根包、启动类、Gradle mainClass 和 JAR 名称。
3. 在 settings.gradle 注册新模块，更新脚本和请求示例中的模块路径。
4. 分配应用名、端口、Nacos Data ID、数据库账户和 schema，更新 DataScope 与 JDBC currentSchema。
5. 根据业务替换订单示例，并由部署流程预先创建并维护所需的数据库、schema 和表。
6. 保留隔离测试配置，验证 test 和 bootJar 后，再检查部署环境的中间件连通性。

## 验证

普通测试关闭 Nacos 配置及服务注册，通过 HTTP 调用本机应用并使用两套隔离的 H2 数据源。H2 只用于测试，不进入生产 JAR。

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:test
.\gradlew.bat :kuma-project:kuma-project-reference:bootJar
```

真实 WSL 验证单独运行，会访问 PostgreSQL schema，并启动 MySQL 数据源：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\kuma-project\kuma-project-reference\scripts\start-wsl.ps1 -Action test
```
