# Myframework

企业级 Java 微服务开发框架，涵盖 Spring Boot 增强、分布式中间件自研、AI Agent 集成三大核心领域。

## 技术栈

| 项 | 版本 |
|----|------|
| Java | 25（`--enable-preview`） |
| Spring Boot | 4.0.3 |
| Spring Framework | 7.0.3 |
| MyBatis-Plus | 3.5.16 |
| Gradle | 8.x（配置缓存 + 并行编译） |
| Python（AI Agent） | 3.12+ |

## 项目结构

```
Myframework/
├── kuma-boot-framework/          # Spring Boot Starter 库（28 个模块）
├── kuma-cloud-framework/         # 分布式中间件（5 大子系统 + 9 个 Starter）
├── kuma-ai-framework/            # AI Agent（Python / LangGraph）
├── kuma-bigdata-framework/       # 大数据模块
├── kuma-other-framework/         # 设计模式、插件（Gradle/ES/Maven）
├── kuma-project/                 # 演示项目（11 个）
├── gradle/                       # 版本目录 + 20+ 可复用构建脚本
├── build.gradle                  # 根构建配置
├── settings.gradle               # 模块注册（100+ 子模块）
└── gradle.properties             # 本地环境变量（gitignored）
```

## kuma-boot-framework

28 个 Spring Boot Starter，包路径 `com.kuma.boot.*`，通过 `AutoConfiguration.imports` 自动装配。

### 基础核心

| 模块 | 包 | 说明 |
|------|---|------|
| `kuma-boot-starter-common` | `com.kuma.boot.common` | 常量、枚举、异常体系、响应模型、SPI 扩展加载器（Dubbo 风格）、上下文持有者（User/Tenant/Trace） |
| `kuma-boot-starter-core` | `com.kuma.boot.core` | `StartupSpringApplication` 启动器、AOP 工具、责任链模式、自动配置（async/strategy/restclient） |

### Web 与 API

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-web` | 全局异常处理、响应包装、注解（`@BusinessApi`、`@NotAuth`、`@LoginUser`、`@CountTime`）、AOP 切面 |
| `kuma-boot-starter-springdoc` | OpenAPI / Knife4j 自动配置 |
| `kuma-boot-starter-grpc` | gRPC 服务端/客户端，Protobuf 代码生成 |

### 数据持久化

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-data-mybatis` | MyBatis-Plus 配置、字段加密拦截器、审计日志（Javers）、规则引擎（Easy Rules） |
| `kuma-boot-starter-data-datasource` | 动态多数据源（MySQL + PostgreSQL），Druid 连接池 |

### 缓存

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-cache-redis` | Redis 自动配置（Jedis + Lettuce），Redisson 延迟队列，Key 过期事件，Redis Streams |
| `kuma-boot-starter-cache-caffeine` | Caffeine 本地缓存 |
| `kuma-boot-starter-cache-jetcache` | JetCache 多级缓存（L1 本地 + L2 Redis） |

### 安全认证

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-security-spring` | Spring Security OAuth2，多登录方式（账号/验证码/短信/社交），RBAC |
| `kuma-boot-starter-captcha` | 验证码生成（EasyCaptcha、Hutool、ZXing） |
| `kuma-boot-starter-totp` | TOTP 两步验证 |

### 流量控制与分布式协调

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-ratelimit` | 4 种限流算法（固定窗口/滑动窗口/令牌桶/漏桶），支持 Guava、Redis Lua、Redisson |
| `kuma-boot-starter-idempotent` | `@Idempotent` 幂等性保障，Redis / MySQL 后端 |
| `kuma-boot-starter-lock` | `@Lock` 分布式锁，Redisson / Zookeeper / JVM 后端 |

### 消息与工具

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-mq-kafka` | Kafka 生产/消费封装 |
| `kuma-boot-starter-office` | Office 文档生成（Excel: EasyExcel/FastExcel/POI，Word: Aspose，PDF: Spire/Aspose） |
| `kuma-boot-starter-ip2region` | IP 地理定位（ip2region + 百度/高德 API 回退） |
| `kuma-boot-starter-dingtalk` | 钉钉 API 集成 |
| `kuma-boot-starter-sentinel` | Sentinel 熔断降级 |
| `kuma-boot-starter-actuator` | Spring Boot Actuator 配置 |

## kuma-cloud-framework

5 大自研分布式中间件，每个均包含 server / client / dashboard / distribution 完整架构。

| 子系统 | 模块数 | 说明 |
|--------|--------|------|
| **kuma-cloud-tx** | 6 | 分布式事务（2PC：Resource Manager + Transaction Manager + Consistency） |
| **kuma-cloud-job** | 12 | 分布式任务调度（DAG 执行、重试策略、NameServer 服务发现） |
| **kuma-cloud-rpc** | 15 | RPC 框架（服务注册/发现、负载均衡、Kryo/Hessian 序列化、过滤器链） |
| **kuma-cloud-mq** | 13 | 消息队列（Broker、Cluster、Proxy、NameServer） |
| **kuma-cloud-ccsr** | 11 | 配置中心 + 服务注册（类 Nacos，SPI 可扩展） |

Cloud Starter 集成模块：`bootstrap`、`seata`、`kmc`、`sentinel`、`stream`、`alibaba`、`cache`、`jdbcpool`、`netty`。

### 模块依赖链

```
common → api → spi → core → client / server → consistency / metrics → dashboard → distribution
```

## kuma-ai-framework

基于 **LangGraph + FastAPI** 的 AI Agent 系统（Python）。

- **多模型**：OpenAI、Anthropic、DeepSeek、Google Gemini
- **中间件**：13+ 组件（循环检测、记忆管理、工具错误处理、图片查看等）
- **多通道**：Slack、Telegram、钉钉、gRPC
- **流式输出**：SSE 实时推送
- **状态持久化**：SQLite / PostgreSQL 检查点

## kuma-project（演示项目）

| 项目 | 演示内容 |
|------|----------|
| project1 / project2 | 最小启动骨架 |
| project3 | Gradle 插件集成（jacoco / spotbugs / checkstyle / pmd / sonar） |
| project4 | 全功能演示：安全 + 双数据源 + 缓存 + Kafka + 锁 + Office |
| project5 | 分布式任务调度（kuma-cloud-job） |
| project6 | RPC 调用（kuma-cloud-rpc） |
| project7 | 配置中心（kuma-cloud-ccsr） |
| project8 | 分布式事务（kuma-cloud-tx） |
| project9 | 消息队列（kuma-cloud-mq） |
| project20 | gRPC 调用 Python AI Agent |
| blog | 完整业务应用（OAuth2 + MySQL + Redis + MyBatis + SpringDoc） |

## 核心架构模式

### SPI 扩展机制

Dubbo 风格的 `ExtensionLoader`，支持 `@SPI`、`@Adaptive`、`@Activate`、`@Wrapper`、`@Order` 注解，贯穿全框架实现可插拔策略。

### 应用启动

```java
StartupSpringApplication
    .setKmcBanner()
    .setKmcProfileIfNotExists("dev")
    .setKmcApplicationProperty("app-name")
    .run(args);
```

### AOP 注解驱动

`@Limit`（限流）、`@Lock`（分布式锁）、`@Idempotent`（幂等）、`@CountTime`（计时）、`@Caching`（缓存）— 声明式使用，框架自动切面拦截。

### 上下文传播

`UserContextHolder`、`TenantContextHolder`、`TraceContextHolder` 基于 `TransmittableThreadLocal`，确保异步线程安全传递。

### 自动装配

所有框架模块通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册，属性驱动 + 条件注解控制装配。

## 快速开始

### 环境要求

- JDK 25+
- Gradle 8.x（使用项目自带 Wrapper）
- MySQL 8.x + Redis 6.x（可选，按需启用）

### 本地配置

创建 `gradle.properties`（已 gitignore）：

```properties
version=1.0.0
group=com.kuma.cloud

mysql.url=jdbc:mysql://localhost:3306/base?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
mysql.username=root
mysql.password=123456
mysql.hikari.minimum-idle=1
mysql.hikari.maximum-pool-size=10
mysql.hikari.connection-timeout=10000

postgresql.url=jdbc:postgresql://localhost:5432/base
postgresql.username=postgres
postgresql.password=123456

redis.host=localhost
redis.port=6379
redis.database=0
redis.jedis.pool.max-active=8
redis.jedis.pool.max-idle=8
redis.jedis.pool.min-idle=0
```

### 构建

```bash
# 构建整个项目
./gradlew build

# 构建指定模块
./gradlew :kuma-boot-framework:kuma-boot-starter-common:build

# 运行演示项目
./gradlew :kuma-project:kuma-project-project1:bootRun

# 跳过测试
./gradlew build -x test
```

## 构建工具集

`gradle/` 目录提供 20+ 可复用构建脚本：

| 脚本 | 用途 |
|------|------|
| `springboot.gradle` | Spring Boot 应用配置 |
| `docker.gradle` / `jib.gradle` | 容器化打包 |
| `graalvm.gradle` | GraalVM Native Image |
| `jacoco.gradle` | 代码覆盖率 |
| `spotbugs.gradle` / `checkstyle.gradle` / `pmd.gradle` | 静态分析 |
| `sonar.gradle` | SonarQube 集成 |
| `smart-doc.gradle` | API 文档生成 |
| `shadow.gradle` | Fat JAR 打包 |
| `proguard.gradle` | 代码混淆 |
| `publish-jar.gradle` / `publish-bom.gradle` | Maven 发布 |

## 许可证

[Apache License 2.0](LICENSE.txt)
