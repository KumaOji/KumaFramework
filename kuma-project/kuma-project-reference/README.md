# Kuma Reference：业务项目模板

参考 blog 的常规分层，用于复制后开发新业务。沿用仓库 Java 25、Spring Boot 和 Gradle 版本管理。

## 项目结构

```text
kuma-project-reference/
├── build.gradle
├── config/nacos/                 Nacos 配置模板
├── scripts/                      本机初始化、启动和演示
└── src/
    ├── main/
    │   ├── java/com/kuma/cloud/reference/
    │   │   ├── ReferenceApplication.java
    │   │   ├── config/           中间件配置、配置属性、线程池和连接池
    │   │   ├── controller/       HTTP 入口与参数校验
    │   │   ├── service/
    │   │   │   └── impl/         业务处理、事务、Mapper 调用及响应转换
    │   │   ├── mapper/           MyBatis Mapper 接口
    │   │   ├── domain/
    │   │   │   ├── dto/          写入参数 CreateOrderDTO
    │   │   │   ├── qo/           查询参数 OrderPageQO
    │   │   │   ├── vo/           响应 OrderVO、PageVO
    │   │   │   ├── entity/       数据库行对象 OrderDO
    │   │   │   ├── model/        订单状态、金额计算与商品快照
    │   │   │   └── enums/        数据源范围
    │   │   ├── support/          实际数据库连接检查
    │   │   └── exception/        业务异常及统一异常处理
    │   └── resources/
    │       ├── bootstrap.yml     应用名称、环境和 Nacos 导入
    │       ├── mapper/           SQL XML
    │       └── db/migration/     Flyway 迁移
    └── test/                     单元测试、集成测试及隔离配置
```

业务调用链：`Controller → Service → ServiceImpl → Mapper → XML`。

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
| MyBatis / MyBatis-Plus | Kuma starter、单一 SqlSessionFactory、Mapper XML |
| Flyway | kuma-boot-starter-data-migration，选择 FLYWAY，分别迁移两个物理数据源 |
| 业务线程池 | kuma-boot-starter-core 的异步池、MDC/上下文传递、有界队列、拒绝策略和优雅关闭 |
| Actuator | kuma-boot-starter-actuator，health / info 及 Kuma 监控扩展 |

MAIN 对应 PostgreSQL 的 `kuma_reference_main` schema；MYSQL 对应独立 MySQL database `kuma_reference_mysql`。DataScope 白名单映射数据源名与目标 schema/database；框架的 DataSourceExecutor 使用 @DS 切换数据源并设置 TenantContextHolder，SchemaInterceptor 根据当前 JDBC 驱动选择 PostgreSQL 或 MySQL SQL parser，将 Mapper SQL 限定到目标 schema/database。Flyway 分别迁移两个物理数据源。上下文在调用结束或异常后恢复。

先选择数据源再开启事务，事务内禁止切换到其他数据源。分页 count 与 items 共用 REPEATABLE READ 只读事务。数据源切换和 SQL schema 改写是两项组件能力，不代表主从复制或跨库分布式事务。

Redis / JetCache 当前禁用；Kafka、认证、对象存储等尚未在本模块配置。传递依赖不代表已接入。

业务模块通过 Kuma starter 获取 AOP、JDBC 和数据库迁移能力，不重复声明对应的 Spring Boot starter。底层仍由 Kuma starter 组合 Spring Boot 与第三方库。Flyway 按物理数据源分别迁移，关闭默认单数据源迁移及 Liquibase，避免两种引擎同时运行。

线程池直接注入框架的 `asyncThreadPoolTaskExecutor`，配置统一使用 `kuma.boot.core.async`。核心参数为 `core-pool-size`、`max-pool-siz`（沿用框架现有拼写）、`queue-capacity`、`rejection-policy` 和 `await-termination-seconds`。reference 使用 `ABORT` 拒绝策略，过载返回 503；框架默认仍为 `CALLER_RUNS`。

普通异步任务复用 core 的线程池。独立的 `kuma-boot-starter-threadpool` 提供 DynamicTP 能力，当前模板未启用其演示线程池。线程池等运行参数统一由 Nacos 管理，配置模板见 `config/nacos/kuma-reference-dev.yaml`；bootstrap.yml 只保留应用名称、环境和 Nacos 连接、导入及注册配置。已发布的 Nacos 配置需同步使用 `kuma.boot.core.async`。

Service 和 Controller 的必需依赖使用构造器注入及 `final` 字段。只有一个构造器时不需要 `@Autowired`；多个同类型 Bean 使用参数上的 `@Qualifier` 区分。

## 启动

入口使用 `kuma-boot-starter-core` 的 `StartupSpringApplication`，启用 Kuma Banner、应用属性初始化和初始化器耗时记录。环境仍由 bootstrap.yml、环境变量及命令行控制。

启动类使用 core 的启动入口，WebConfiguration 显式导入 web starter 的 MVC 和 HTTP 交换记录配置。现有 `@KumaBootApplication` 同时开启 OAuth2 资源服务；本模板尚未配置认证，保留 `@SpringBootApplication` 并排除传递依赖带来的默认登录与管理端点安全配置。新项目接入认证时应移除这些排除项并配置对应安全组件。

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

使用 `SPRING_CLOUD_NACOS_SERVER_ADDR` 修改 Nacos 地址，使用 `SPRING_PROFILES_ACTIVE` 切换环境；对应的 Nacos Data ID 需提前配置。运行时 Nacos 导入为必需项，连接池等配置修改后需重启；本地模板变更后需发布到对应的 Nacos Data ID。

WSL 开发环境可运行 `scripts/initialize-wsl.ps1` 初始化 PostgreSQL schema。MySQL database 与 `kuma-reference-mysql` Kubernetes Secret 需由部署环境预先准备；`scripts/start-wsl.ps1` 会从 Secret 加载两种数据库的环境变量。配置模板仅保留凭据占位变量，真实凭据由部署环境或受控配置中心提供。

## 接口

| 方法 | 路径 | 用途 |
|---|---|---|
| POST | /api/orders?scope=MAIN | 创建订单 |
| GET | /api/orders/{id}?scope=MAIN | 查询订单 |
| GET | /api/orders?scope=MAIN&page=1&size=10 | 分页查询 |
| POST | /api/orders/{id}/confirm?scope=MAIN | 确认订单 |

创建订单由请求直接提供示例商品信息：

```json
{"sku":"BOOK-001","productName":"示例商品","unitPrice":99.00,"quantity":2}
```

这是演示持久化和事务的模板接口，正式业务的价格来源应由业务规则确定。异常统一为 `application/problem+json`：参数 400、不存在 404、状态或版本冲突 409、资源繁忙 503。创建接口没有幂等键，重复提交会产生新订单。

## 复制为新项目

1. 复制源码及配置模板，排除 build、日志及真实凭据。
2. 修改模块名、根包、启动类、Gradle mainClass 和 JAR 名称；同步更新 XML 中的完整类名。
3. 在 settings.gradle 注册新模块，更新脚本和请求示例中的模块路径。
4. 分配应用名、端口、Nacos Data ID、数据库账户和 schema，更新 DataScope 与 JDBC currentSchema。
5. 根据业务替换订单示例。已有数据库追加 Flyway 迁移，不修改已执行的迁移。
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
