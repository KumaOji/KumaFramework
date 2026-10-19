# Kuma Reference：业务项目模板

参考 blog 的常规分层，用于复制后开发新业务。沿用仓库 Java 25、Spring Boot 和 Gradle 版本管理。

## 项目结构

```text
kuma-project-reference/
├── build.gradle
├── config/nacos/                 Nacos 配置模板
├── requests/reference.http       HTTP 请求示例
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
    │   │   │   ├── vo/           响应 OrderVO、PageVO、SchemaVO
    │   │   │   ├── entity/       数据库行对象 OrderDO
    │   │   │   ├── model/        订单状态、金额计算与商品快照
    │   │   │   └── enums/        数据源范围
    │   │   ├── integration/      外部商品 HTTP 客户端
    │   │   ├── support/          数据源路由和实际连接检查
    │   │   └── exception/        业务异常及统一异常处理
    │   └── resources/
    │       ├── bootstrap.yml     应用名称、环境和 Nacos 导入
    │       ├── mapper/           SQL XML
    │       └── db/migration/     Flyway 迁移
    └── test/                     单元测试、集成测试及隔离配置
```

业务调用链：`Controller → Service → ServiceImpl → Mapper → XML`。

Controller 注入 Service 接口，使用 Jakarta Validation 校验 DTO/QO；ServiceImpl 处理业务和事务，返回 VO。Mapper 只负责数据库操作，不直接作为 HTTP 入口。订单状态与金额约束保留在 model，数据库行对象不直接对外返回。

单个实现的客户端和数据源支持类直接使用具体类。不额外增加 Repository 转发层、Command/Query、副本输出 DTO、转换层或抽象 Service 基类。

## 已配置的能力

| 能力 | 实现与配置 |
|---|---|
| Nacos 配置和服务注册 | kuma-cloud-starter-alibaba、bootstrap.yml |
| PostgreSQL 双 schema | referenceMain / referenceSandbox 两个独立 HikariCP 池 |
| 动态数据源 | Kuma 数据源组件，DataScope 白名单路由 |
| MyBatis / MyBatis-Plus | Kuma starter、单一 SqlSessionFactory、Mapper XML |
| Flyway | 分别迁移两个物理数据源 |
| 业务线程池 | 有界队列、拒绝策略、MDC 恢复和优雅关闭 |
| HTTP 连接池 | WebClient、连接上限、响应及整体超时 |
| Actuator | health / info |

MAIN 对应 `kuma_reference_main`，SANDBOX 对应 `kuma_reference_sandbox`，共用 PostgreSQL `base` 数据库。先选择数据源再开启事务，事务内禁止切换到其他数据源。HTTP 商品查询先于数据库事务执行；分页 count 与 items 共用 REPEATABLE READ 只读事务。

Redis / JetCache 当前禁用；Kafka、认证、对象存储等尚未在本模块配置。传递依赖不代表已接入。

## 启动

运行配置由 Nacos 加载，本地模板为 `config/nacos/kuma-reference-dev.yaml`：

| 配置项 | 默认值 |
|---|---|
| 应用名 | kuma-reference |
| 环境 | dev |
| Namespace ID | base |
| Group | DEFAULT_GROUP |
| Data ID | kuma-reference-dev.yaml |

先确保 Nacos 配置、数据库账户和两个 schema 已准备好。在 IDEA 运行 `ReferenceApplication.main()`，或在仓库根目录执行：

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:bootRun
```

应用地址 `http://127.0.0.1:19092`，健康检查 `/actuator/health`。创建订单需要另启商品 mock：

```powershell
python .\kuma-project\kuma-project-reference\scripts\mock-catalog.py
```

使用 `SPRING_CLOUD_NACOS_SERVER_ADDR` 修改 Nacos 地址，使用 `SPRING_PROFILES_ACTIVE` 切换环境；对应的 Nacos Data ID 需提前配置。运行时 Nacos 导入为必需项，连接池等配置修改后需重启。

新机器的 WSL 开发数据库可通过 `scripts/initialize-wsl.ps1` 初始化独立账户和 schema。配置模板仅保留凭据占位变量，真实凭据由部署环境或受控配置中心提供。

## 接口

打开 `requests/reference.http` 运行示例。

| 方法 | 路径 | 用途 |
|---|---|---|
| POST | /api/orders?scope=MAIN | 创建订单 |
| GET | /api/orders/{id}?scope=MAIN | 查询订单 |
| GET | /api/orders?scope=MAIN&page=1&size=10 | 分页查询 |
| POST | /api/orders/{id}/confirm?scope=MAIN | 确认订单 |
| GET | /api/datasources/current?scope=SANDBOX | 检查实际连接的 schema |

响应保持原接口契约。异常统一为 `application/problem+json`：参数 400、不存在 404、状态或版本冲突 409、商品不存在 422、上游故障 502、资源繁忙 503、上游超时 504。创建接口没有幂等键，重复提交会产生新订单。

## 复制为新项目

1. 复制源码及配置模板，排除 build、日志及真实凭据。
2. 修改模块名、根包、启动类、Gradle mainClass 和 JAR 名称；同步更新 XML 中的完整类名。
3. 在 settings.gradle 注册新模块，更新脚本和请求示例中的模块路径。
4. 分配应用名、端口、Nacos Data ID、数据库账户和 schema，更新 DataScope 与 JDBC currentSchema。
5. 根据业务替换订单示例。已有数据库追加 Flyway 迁移，不修改已执行的迁移。
6. 保留隔离测试配置，验证 test 和 bootJar 后，再检查部署环境的中间件连通性。

## 验证

普通测试关闭 Nacos 配置及服务注册，使用真实本机 HTTP mock 与两套 H2 数据源。H2 只用于测试，不进入生产 JAR。

```powershell
.\gradlew.bat :kuma-project:kuma-project-reference:test
.\gradlew.bat :kuma-project:kuma-project-reference:bootJar
```

真实 WSL PostgreSQL 验证单独运行，会写入 reference 的两个开发 schema：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\kuma-project\kuma-project-reference\scripts\start-wsl.ps1 -Action test
```
