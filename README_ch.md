# Kuma Framework

[![Java](https://img.shields.io/badge/Java-25-blue?logo=openjdk)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.0-brightgreen?logo=spring)](https://spring.io/projects/spring-cloud)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-blue?logo=gradle)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](LICENSE.txt)

基于 **Java 25 + Spring Boot 4 + Spring Cloud 2025** 的企业级框架，以 Monorepo 形式提供 **65+ 开箱即用的 Auto-Configuration Starter**，覆盖数据访问、缓存、安全、消息、可观测、分布式能力等各个领域。

---

## 基本信息

| 属性 | 值                                               |
|------|-------------------------------------------------|
| Group ID | `io.github.kumaoji`                             |
| 当前版本 | `2026.05.01`                                    |
| 构建工具 | Gradle 9.3.1 + Gradle Wrapper + Version Catalog |
| Java 版本 | JDK 25（强制，所有模块开启 `--enable-preview`）            |
| Spring Boot | 4.0.3                                           |
| Spring Framework | 7.0.3                                           |
| Spring Cloud | 2025.1.0                                        |
| Spring Cloud Alibaba | 2025.1.0.0                                      |

---

## 模块体系

```
Kumaframework/
├── kuma-boot-framework/      65+ Spring Boot Auto-Configuration Starters
├── kuma-cloud-framework/     10  Spring Cloud Starters
├── kuma-project/             可运行的演示应用
├── kuma-bigdata-framework/   大数据集成（规划中）
└── kuma-other-framework/     设计模式 / 插件（规划中）
```

---

## kuma-boot-framework

Spring Boot Auto-Configuration Starter 集合，按功能领域分组。

### 基础层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-common` | 公共基础：Hutool、Guava、Commons、Jackson、Fastjson2、Kryo、TTL、Groovy 等工具库聚合 |
| `kuma-boot-starter-core` | 框架核心：Spring Boot + Spring Framework + AOP + Reactor，所有 Starter 的基础依赖 |

### Web 层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-web` | Web 聚合：Spring MVC、全局异常、参数校验、统一响应，聚合安全/限流/幂等/缓存等核心能力 |
| `kuma-boot-starter-webagg` | Web 聚合增强：请求聚合、协议转换 |
| `kuma-boot-starter-websocket` | WebSocket 支持 |
| `kuma-boot-starter-xss` | XSS 过滤（AntiSamy + Jsoup）|
| `kuma-boot-starter-sensitive` | 数据脱敏（手机号、身份证、银行卡等）|
| `kuma-boot-starter-encrypt` | 接口加解密（RSA / AES）|

### 数据访问层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-data-common` | 数据层公共组件：分页、排序、枚举处理 |
| `kuma-boot-starter-data-datasource` | 多数据源（Dynamic DataSource + Druid）|
| `kuma-boot-starter-data-mybatis` | MyBatis Plus 增强配置（分页、逻辑删除、枚举）|
| `kuma-boot-starter-data-jpa` | Spring Data JPA + Hibernate + Blaze-Persistence |
| `kuma-boot-starter-data-mongodb` | MongoDB 自动配置 |
| `kuma-boot-starter-data-elasticsearch` | Elasticsearch 自动配置（Easy-ES）|
| `kuma-boot-starter-data-shardingsphere` | ShardingSphere JDBC 分库分表 |
| `kuma-boot-starter-data-p6spy` | SQL 执行性能分析（p6spy）|

### 缓存层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-cache-common` | 缓存抽象层 |
| `kuma-boot-starter-cache-redis` | Redis 缓存（Redisson）|
| `kuma-boot-starter-cache-caffeine` | Caffeine 本地缓存 |
| `kuma-boot-starter-cache-jetcache` | JetCache 两级缓存（本地 + 远程）|

### 安全层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-security-common` | 安全公共组件：权限模型、Token 抽象 |
| `kuma-boot-starter-security-spring` | Spring Security 深度集成 |
| `kuma-boot-starter-security-jstauth` | Sa-Token 集成（无状态 JWT 认证）|
| `kuma-boot-starter-totp` | TOTP 双因素认证（Google Authenticator 兼容）|
| `kuma-boot-starter-captcha` | 验证码（图形、滑块、点击）|

### 消息层

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-mq-common` | 消息公共抽象 |
| `kuma-boot-starter-mq-kafka` | Kafka 生产者/消费者封装 |
| `kuma-boot-starter-mqtt` | MQTT v5 集成（Eclipse Paho）|
| `kuma-boot-starter-eventbus` | 进程内事件总线（Guava EventBus / Disruptor）|

### 分布式能力

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-lock` | 分布式锁（Redisson）|
| `kuma-boot-starter-idempotent` | 接口幂等（Token + Lua 脚本）|
| `kuma-boot-starter-ratelimit` | 接口限流（Sentinel / Resilience4j）|
| `kuma-boot-starter-retry` | 重试（Spring Retry + Guava Retrying）|
| `kuma-boot-starter-seata` | 分布式事务（Seata AT/TCC）|
| `kuma-boot-starter-sentinel` | 流量控制（Sentinel 独立模式）|
| `kuma-boot-starter-idgenerator` | 分布式 ID（雪花 / UUID / Leaf）|

### 可观测性

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-actuator` | Actuator 端点增强 |
| `kuma-boot-starter-metrics` | Micrometer 指标采集 |
| `kuma-boot-starter-prometheus` | Prometheus 指标暴露 |
| `kuma-boot-starter-monitor` | 系统监控（CPU、内存、JVM、线程，基于 Oshi）|
| `kuma-boot-starter-tracer` | 链路追踪（Micrometer Tracing + OpenTelemetry）|
| `kuma-boot-starter-skywalking` | SkyWalking Agent 工具集成 |
| `kuma-boot-starter-elk` | ELK 日志（Logstash + GELF）|
| `kuma-boot-starter-logger` | 日志增强（结构化日志 / Loki / Kafka Appender）|

### 远程调用

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-grpc` | gRPC Server/Client 自动配置 |
| `kuma-boot-starter-client` | HTTP 客户端（Forest / Retrofit）|

### 任务与存储

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-job-common` | 任务调度公共接口 |
| `kuma-boot-starter-job-xxl` | XXL-Job 执行器自动配置 |
| `kuma-boot-starter-oss-common` | 对象存储抽象接口 |
| `kuma-boot-starter-oss-minio` | MinIO 存储实现 |

### API 文档

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-springdoc` | SpringDoc OpenAPI 基础配置 |
| `kuma-boot-starter-openapi` | OpenAPI 增强（Knife4j 聚合）|

### 业务工具

| Starter | 说明 |
|---------|------|
| `kuma-boot-starter-mail` | 邮件发送（Spring Mail + Commons Email）|
| `kuma-boot-starter-sms-common` | 短信发送抽象 |
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

## kuma-cloud-framework

Spring Cloud Starter 集合，在 kuma-boot-framework 基础上提供分布式微服务能力。

| Starter | 说明 |
|---------|------|
| `kuma-cloud-starter-bootstrap` | Spring Cloud 启动引导：bootstrap 上下文、负载均衡、Cloud Commons |
| `kuma-cloud-starter-alibaba` | Nacos 一站式集成：服务注册发现 + 配置中心 |
| `kuma-cloud-starter-sentinel` | Spring Cloud Sentinel：流量控制、熔断降级 |
| `kuma-cloud-starter-seata` | Spring Cloud Seata：分布式事务（AT/TCC）|
| `kuma-cloud-starter-stream` | Spring Cloud Stream + RocketMQ：声明式消息驱动 |
| `kuma-cloud-starter-gateway` | Spring Cloud Gateway：网关路由、过滤器配置 |
| `kuma-cloud-starter-cache` | 分布式缓存 Cloud 扩展 |
| `kuma-cloud-starter-jdbcpool` | 多数据源 JDBC 连接池 Cloud 扩展 |
| `kuma-cloud-starter-netty` | Netty 服务端 Cloud 扩展 |
| `kuma-cloud-starter-kmc` | KMC 国密加密模块 |

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 25（强制）|
| Gradle | 9.3.1（通过 Wrapper 自动下载，无需手动安装）|
| MySQL | 8.0+ |
| Redis | 7.x |

### 克隆项目

```bash
git clone https://github.com/kumaoji/Kumaframework.git
cd Kumaframework
```

### 引入 Starter（项目内）

在子项目的 `build.gradle` 中直接引用：

```groovy
dependencies {
    implementation project(':kuma-boot-framework:kuma-boot-starter-web')
    implementation project(':kuma-boot-framework:kuma-boot-starter-data-mybatis')
}
```

### 引入 Starter（外部项目，Maven）

```xml
<dependency>
    <groupId>io.github.kumaoji</groupId>
    <artifactId>kuma-boot-starter-web</artifactId>
    <version>2026.05.01</version>
</dependency>
```

### 构建命令

```bash
# 构建全部模块
./gradlew build

# 跳过测试
./gradlew build -x test

# 构建指定模块
./gradlew :kuma-boot-framework:kuma-boot-starter-core:build

# 启动演示应用
./gradlew :kuma-project:kuma-project-project4:bootRun
```

---

## 开发规范

### 新增 Starter

1. 在 `settings.gradle` 的 `frameworkBootSubModules` 列表中追加模块名
2. 在 `kuma-boot-framework/` 下创建目录 `kuma-boot-starter-<feature>/`
3. 创建 `build.gradle`，声明依赖
4. 在 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册自动配置类
5. 在根 `build.gradle` 的 `publishOrder` 列表中按依赖层次插入模块名

### 编译参数（全局继承）

| 参数 | 说明 |
|------|------|
| `--enable-preview` | 启用 Java 25 预览特性 |
| `-parameters` | 保留方法参数名（MyBatis / Spring MVC 反射需要）|
| `-Xlint:unchecked` | 未检查操作警告 |
| `-Xlint:deprecation` | 废弃 API 警告 |

---

## License

本项目基于 [Apache License 2.0](LICENSE.txt) 开源。
