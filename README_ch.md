# Kuma Framework

> 基于 Spring Boot 4 / Spring Cloud 2025 的多层企业级框架 Monorepo，提供 60+ 开箱即用的 Auto-Configuration Starter，覆盖数据访问、缓存、安全、消息、可观测等领域。

---

## 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [架构总览](#架构总览)
- [模块清单](#模块清单)
- [快速开始](#快速开始)
- [构建与运行](#构建与运行)
- [配置说明](#配置说明)
- [项目结构](#项目结构)
- [开发规范](#开发规范)

---

## 项目概述

| 属性 | 值 |
|------|-----|
| Group ID | `io.github.kumaoji` |
| 当前版本 | `2026.04.01` |
| 构建工具 | Gradle **9.3.1** + Version Catalog |
| Java 版本 | **JDK 25**（强制，低于此版本构建失败）|
| Spring Boot | 4.0.3 |
| Spring Framework | 7.0.3 |
| Spring Cloud | 2025.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 |

所有子模块均开启 `--enable-preview`，使用 Java 25 预览特性。

---

## 技术栈

### 核心框架

| 分类 | 组件 | 版本 |
|------|------|------|
| Web | Spring Boot Web / WebFlux | 4.0.3 |
| ORM | MyBatis Plus | 3.5.16 |
| ORM | Spring Data JPA + Hibernate | 7.0.0.Final |
| 数据源 | Dynamic DataSource | 4.5.0 |
| 连接池 | Druid | 1.2.27 |
| 缓存 | Redisson / Caffeine / JetCache | 4.1.0 / 3.1.8 / 2.7.8 |
| 安全 | Sa-Token | 1.35.0.RC |
| 安全 | Spring Security + JWT（JJWT） | — / 0.12.6 |
| 消息 | Kafka / RocketMQ / MQTT | 4.1.1 / 2.3.3 / — |
| 分布式事务 | Seata | 2.5.0 |
| 熔断限流 | Sentinel / Resilience4j | 1.8.8 / 2.2.0 |
| 服务注册 | Nacos（Spring Cloud Alibaba） | 2025.1.0.0 |
| 远程调用 | gRPC / Dubbo / Forest / Retrofit | 1.76.0 / 3.3.2 / 1.5.36 / 3.1.6 |
| 可观测 | Micrometer + Prometheus + SkyWalking + OpenTelemetry | — |
| 日志 | Logback + ELK + Loki | — |
| 任务调度 | XXL-Job | 2.4.2 |
| 对象存储 | MinIO | 8.5.14 |
| 文档 | SpringDoc OpenAPI / Knife4j | 3.0.1 / 4.5.0 |
| 工具 | Hutool / MapStruct / Lombok | 5.8.40 / 1.5.5 / 1.18.42 |

---

## 架构总览

```
Myframework（Monorepo）
│
├── kuma-boot-framework        ← Layer 1: 60+ Spring Boot Auto-Configuration Starters
│   └── kuma-boot-starter-*
│
├── kuma-cloud-framework       ← Layer 2: Spring Cloud 扩展 Starters
│   └── kuma-cloud-starter-*
│
├── kuma-project               ← Layer 3: 可运行的 Spring Boot 演示应用
│   ├── kuma-project-project4  ← 全功能演示项目（当前主力）
│   └── kuma-project-blog      ← 博客应用示例
│
├── kuma-bigdata-framework     ← Layer 4: 大数据集成（规划中）
│   └── Flink / Spark / Doris / Hadoop ...
│
└── kuma-other-framework       ← Layer 5: 设计模式 / Gradle 插件（规划中）
```

### Starter 核心依赖链

```
common
  └── core
        ├── web
        │     └── webagg / websocket
        ├── data-datasource
        │     ├── data-mybatis
        │     ├── data-jpa
        │     ├── data-mongodb
        │     └── data-elasticsearch
        ├── cache-common
        │     ├── cache-redis
        │     ├── cache-caffeine
        │     └── cache-jetcache
        └── security-common
              ├── security-spring
              └── security-jstauth
```

---

## 模块清单

### kuma-boot-framework（Spring Boot Starters）

#### 基础层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-common` | 公共工具类、基础 Bean、全局常量 |
| `kuma-boot-starter-core` | 框架核心，AOP、Jackson、日志基础配置 |

#### Web 层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-web` | Web MVC 基础配置、全局异常、参数校验、统一响应 |
| `kuma-boot-starter-webagg` | Web 聚合增强：请求聚合、协议转换 |
| `kuma-boot-starter-websocket` | WebSocket 支持 |
| `kuma-boot-starter-xss` | XSS 过滤（基于 AntiSamy + Jsoup）|
| `kuma-boot-starter-sensitive` | 数据脱敏（手机号、身份证、银行卡等）|

#### 数据访问层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-data-datasource` | 多数据源（Dynamic DataSource + Druid）|
| `kuma-boot-starter-data-mybatis` | MyBatis Plus 增强配置（分页、逻辑删除、枚举）|
| `kuma-boot-starter-data-jpa` | Spring Data JPA + Hibernate + Blaze-Persistence |
| `kuma-boot-starter-data-mongodb` | MongoDB 自动配置 |
| `kuma-boot-starter-data-elasticsearch` | Elasticsearch 自动配置（Easy-ES）|
| `kuma-boot-starter-data-shardingsphere` | ShardingSphere JDBC 分库分表 |
| `kuma-boot-starter-data-p6spy` | SQL 性能分析（p6spy）|
| `kuma-boot-starter-data-common` | 数据层公共组件（分页、排序、枚举处理）|

#### 缓存层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-cache-common` | 缓存抽象层 |
| `kuma-boot-starter-cache-redis` | Redis 缓存（Redisson）|
| `kuma-boot-starter-cache-caffeine` | Caffeine 本地缓存 |
| `kuma-boot-starter-cache-jetcache` | JetCache 两级缓存 |

#### 安全层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-security-common` | 安全公共组件（权限模型、Token 抽象）|
| `kuma-boot-starter-security-spring` | Spring Security 深度集成 |
| `kuma-boot-starter-security-jstauth` | Sa-Token 集成（无状态 JWT 认证）|
| `kuma-boot-starter-totp` | TOTP 双因素认证（Google Authenticator）|
| `kuma-boot-starter-captcha` | 验证码（图形、滑块、点击）|
| `kuma-boot-starter-encrypt` | 接口加解密（RSA / AES）|

#### 消息层

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-mq-common` | 消息公共抽象 |
| `kuma-boot-starter-mq-kafka` | Kafka 生产者/消费者封装 |
| `kuma-boot-starter-mqtt` | MQTT v5 集成（Eclipse Paho）|
| `kuma-boot-starter-eventbus` | 进程内事件总线（Guava EventBus / Disruptor）|

#### 分布式能力

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-lock` | 分布式锁（Redisson）|
| `kuma-boot-starter-idempotent` | 接口幂等（Token + Lua 脚本）|
| `kuma-boot-starter-ratelimit` | 接口限流（Sentinel / Resilience4j）|
| `kuma-boot-starter-retry` | 重试（Spring Retry + Guava Retrying）|
| `kuma-boot-starter-seata` | 分布式事务（Seata AT/TCC）|
| `kuma-boot-starter-sentinel` | 流量控制（Sentinel 独立模式）|
| `kuma-boot-starter-idgenerator` | 分布式 ID（雪花 / UUID / Leaf）|

#### 可观测性

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-actuator` | Actuator 端点增强 |
| `kuma-boot-starter-metrics` | Micrometer 指标采集 |
| `kuma-boot-starter-prometheus` | Prometheus 指标暴露 |
| `kuma-boot-starter-monitor` | 系统监控（CPU、内存、JVM、线程，基于 Oshi）|
| `kuma-boot-starter-tracer` | 链路追踪（Micrometer Tracing + OpenTelemetry）|
| `kuma-boot-starter-skywalking` | SkyWalking Agent 工具集成 |
| `kuma-boot-starter-elk` | ELK 日志（Logstash + GELF）|
| `kuma-boot-starter-logger` | 日志增强（结构化日志 / Loki / Kafka Appender）|

#### 远程调用

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-grpc` | gRPC Server/Client 自动配置 |
| `kuma-boot-starter-client` | HTTP 客户端（Forest / Retrofit）|

#### 业务能力

| 模块 | 说明 |
|------|------|
| `kuma-boot-starter-springdoc` | SpringDoc OpenAPI 基础配置 |
| `kuma-boot-starter-openapi` | OpenAPI 增强（Knife4j 聚合）|
| `kuma-boot-starter-job-common` | 任务调度公共接口 |
| `kuma-boot-starter-job-xxl` | XXL-Job 执行器自动配置 |
| `kuma-boot-starter-oss-common` | 对象存储抽象接口 |
| `kuma-boot-starter-oss-minio` | MinIO 存储实现 |
| `kuma-boot-starter-sms-common` | 短信发送抽象 |
| `kuma-boot-starter-mail` | 邮件发送（Spring Mail + Commons Email）|
| `kuma-boot-starter-office` | Office 文档处理（POI / EasyExcel / Aspose）|
| `kuma-boot-starter-translation` | 多语言国际化翻译 |
| `kuma-boot-starter-threadpool` | 动态线程池（DynamicTp + TTL）|
| `kuma-boot-starter-statemachine` | 状态机（Spring StateMachine）|
| `kuma-boot-starter-flowengine` | 流程引擎集成 |
| `kuma-boot-starter-ddd` | DDD 战术设计基础组件（聚合根、领域事件）|
| `kuma-boot-starter-ip2region` | IP 归属地查询（离线库）|
| `kuma-boot-starter-useragent` | User-Agent 解析（Browscap / YAUAA）|
| `kuma-boot-starter-pinyin` | 汉字转拼音（Pinyin4j）|
| `kuma-boot-starter-dingtalk` | 钉钉机器人告警通知 |
| `kuma-boot-starter-apollo` | Apollo 配置中心集成 |
| `kuma-boot-starter-canal` | Canal 数据库变更监听 |
| `kuma-boot-starter-frp` | FRP 内网穿透工具集成 |
| `kuma-boot-starter-test` | 测试工具集（TestContainers / DataFaker）|

---

### kuma-cloud-framework（Spring Cloud Starters）

| 模块 | 说明 |
|------|------|
| `kuma-cloud-starter-bootstrap` | Spring Cloud 启动引导配置 |
| `kuma-cloud-starter-alibaba` | Nacos 注册/配置中心一站式集成 |
| `kuma-cloud-starter-sentinel` | Spring Cloud Sentinel 集成 |
| `kuma-cloud-starter-seata` | Spring Cloud Seata 分布式事务 |
| `kuma-cloud-starter-stream` | Spring Cloud Stream + RocketMQ |
| `kuma-cloud-starter-gateway` | Spring Cloud Gateway 网关配置 |
| `kuma-cloud-starter-cache` | 分布式缓存 Cloud 扩展 |
| `kuma-cloud-starter-jdbcpool` | 多数据源 JDBC 连接池 Cloud 扩展 |
| `kuma-cloud-starter-netty` | Netty 服务端 Cloud 扩展 |
| `kuma-cloud-starter-kmc` | KMC 加密模块 |

---

## 快速开始

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | **25**（强制）|
| Gradle | **9.3.1**（使用 Wrapper，无需手动安装）|
| MySQL | 8.0+ |
| Redis | 7.x |

### 克隆项目

```bash
git clone https://github.com/kumaoji/Myframework.git
cd Myframework
```

### 配置本地数据库

在本地 MySQL 中创建数据库（kuma-boot-starter-idempotent，kuma-boot-starter-data-mybatis，kuma-cloud-starter-seata）：

```sql
CREATE DATABASE base CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

默认连接信息（添加 `gradle.properties` 修改）：

| 服务 | 地址 | 用户名 | 密码 |
|------|------|----|---|
| MySQL | localhost:3306 | —  | — |
| Redis | localhost:6379 | —  | — |

---

## 构建与运行

### 构建

```bash
# 构建整个项目
./gradlew build

# 构建指定模块
./gradlew :kuma-boot-framework:kuma-boot-starter-core:build

# 跳过测试构建
./gradlew build -x test
```

### 运行演示项目

```bash
# 构建可运行 JAR
./gradlew :kuma-project:kuma-project-project4:bootJar

# 直接启动（热开发模式）
./gradlew :kuma-project:kuma-project-project4:bootRun

# 启动博客应用
./gradlew :kuma-project:kuma-project-blog:bootRun
```

---

## 配置说明

### 配置文件分层

每个 Spring Boot 应用遵循以下配置拆分约定：

| 文件 | 作用 |
|------|------|
| `application.yml` | 应用专属配置，通过 `${placeholder}` 引用 `gradle.properties` 中的值 |
| `application-dev.yml` | 开发环境覆盖配置 |
| `application-framework.yml` | 框架默认配置（由 Starter 通过 Auto-Config 注入）|
| `logback-spring.xml` | 结构化日志配置 |


### 引入 Starter

在子项目的 `build.gradle` 中直接引用：

```groovy
dependencies {
    // 引用框架内部模块
    implementation project(':kuma-boot-framework:kuma-boot-starter-web')
    implementation project(':kuma-boot-framework:kuma-boot-starter-data-mybatis')

    // 引用版本目录中的第三方依赖
    implementation projectLibs.hutool.core
    implementation projectLibs.mybatis.plus
}
```

或在外部项目中通过 Maven Coordinates 引入：

```xml
<dependency>
    <groupId>io.github.kumaoji</groupId>
    <artifactId>kuma-boot-starter-web</artifactId>
    <version>2026.04.01</version>
</dependency>
```

---

## 项目结构

```
Myframework/
├── build.gradle                          # 根项目构建配置
├── settings.gradle                       # 子模块声明
├── gradle.properties                     # 版本、数据库连接等全局属性
├── gradle/
│   ├── libs.versions.toml               # 统一版本目录（Version Catalog）
│   ├── publish-central.gradle           # Maven Central 发布脚本
│   ├── publish-jar.gradle               # 普通 JAR 发布脚本
│   ├── springboot.gradle                # Spring Boot 应用通用配置
│   ├── docker.gradle                    # Docker 构建配置
│   ├── jacoco.gradle                    # 测试覆盖率配置
│   ├── spotless.gradle                  # 代码格式化（Spotless）
│   ├── sonar.gradle                     # SonarQube 静态分析
│   ├── smart-doc.gradle                 # Smart-Doc API 文档生成
│   └── ...                              # 其他复用脚本
│
├── kuma-boot-framework/
│   ├── kuma-boot-starter-common/        # [Layer 0] 公共基础
│   ├── kuma-boot-starter-core/          # [Layer 1] 框架核心
│   ├── kuma-boot-starter-web/           # [Layer 5] Web 层聚合
│   └── kuma-boot-starter-*/             # 其余 50+ Starter
│
├── kuma-cloud-framework/
│   ├── kuma-cloud-starter-bootstrap/    # Cloud 启动引导
│   ├── kuma-cloud-starter-alibaba/      # Nacos 集成
│   └── kuma-cloud-starter-*/            # 其余 Cloud Starter
│
└── kuma-project/
    ├── kuma-project-project4/           # 全功能演示应用
    └── kuma-project-blog/               # 博客示例应用
```

---

## 开发规范

### 新增 Starter

1. 在 `settings.gradle` 的 `frameworkBootSubModules` 列表中追加模块名
2. 在 `kuma-boot-framework/` 下创建目录 `kuma-boot-starter-<feature>/`
3. 创建 `build.gradle`，继承公共配置并声明依赖
4. 在 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册自动配置类
5. 在根 `build.gradle` 的 `publishOrder` 列表中按依赖层次插入模块名

### Gradle 脚本复用

```groovy
// 应用 Spring Boot 应用配置（bootJar / bootBuildImage）
apply from: rootProject.file('gradle/springboot.gradle')
```

### 编译参数

所有子项目自动继承以下编译参数：

| 参数 | 说明 |
|------|------|
| `--enable-preview` | 启用 Java 25 预览特性 |
| `-parameters` | 保留方法参数名（MyBatis / Spring MVC 反射需要）|
| `-Xlint:unchecked` | 未检查操作警告 |
| `-Xlint:deprecation` | 废弃 API 警告 |

---

## License

本项目基于 [Apache License 2.0](LICENSE.txt) 开源。
