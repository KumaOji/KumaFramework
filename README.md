# Kuma Framework

**English** | [简体中文](README_ZH.md)

[![Java](https://img.shields.io/badge/Java-25-blue?logo=openjdk)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.0-brightgreen?logo=spring)](https://spring.io/projects/spring-cloud)
[![Gradle](https://img.shields.io/badge/Gradle-9.6.1-blue?logo=gradle)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](LICENSE.txt)

An enterprise-grade framework built on **Java 25 + Spring Boot 4 + Spring Cloud 2025**, organized as a monorepo with **96 Spring Boot starters** and **12 Spring Cloud starters** for data access, caching, security, messaging, observability, distributed systems, testing, and more.

---

## Overview

| Attribute | Value |
|-----------|-------|
| Group ID | `io.github.kumaoji` |
| Current Version | `2026.07.01` |
| Build Tool | Gradle 9.6.1 + Gradle Wrapper + Version Catalog |
| Java Version | JDK 25 (required, all modules enable `--enable-preview`) |
| Spring Boot | 4.0.3 |
| Spring Framework | 7.0.3 |
| Spring Cloud | 2025.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 |

---

## Module Layout

```
Kumaframework/
├── kuma-boot-framework/      96 Spring Boot Auto-Configuration Starters
├── kuma-cloud-framework/     12 Spring Cloud Starters
├── kuma-project/              4 included runnable/sample projects
├── kuma-ai-framework/         AI integrations (incubating)
├── kuma-bigdata-framework/    Big-data integrations (incubating)
└── kuma-other-framework/      Design patterns and build/IDE plugins (incubating)
```

---

## kuma-boot-framework

A collection of Spring Boot Auto-Configuration Starters, grouped by functional domain.

### Foundation

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-common` | Common foundation: aggregates utility libraries such as Hutool, Guava, Commons, Jackson, Fastjson2, Kryo, TTL, Groovy |
| `kuma-boot-starter-core` | Framework core: Spring Boot + Spring Framework + AOP + Reactor; the base dependency for all starters |

### Web

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-web` | Web aggregation: Spring MVC, global exceptions, parameter validation, unified responses; bundles security/rate-limit/idempotency/cache capabilities |
| `kuma-boot-starter-webagg` | Web aggregation enhancements: request aggregation, protocol conversion |
| `kuma-boot-starter-websocket` | WebSocket support |
| `kuma-boot-starter-webflux` | Reactive WebFlux support |
| `kuma-boot-starter-graphql` | Spring GraphQL integration |
| `kuma-boot-starter-mcp` | Model Context Protocol integration |
| `kuma-boot-starter-sse` | Server-Sent Events support |
| `kuma-boot-starter-xss` | XSS filtering (AntiSamy + Jsoup) |
| `kuma-boot-starter-sensitive` | Data masking (phone, ID card, bank card, etc.) |
| `kuma-boot-starter-encrypt` | API encryption/decryption (RSA / AES) |

### Data Access

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-data-common` | Common data-layer components: pagination, sorting, enum handling |
| `kuma-boot-starter-data-datasource` | Multi-datasource (Dynamic DataSource + Druid) |
| `kuma-boot-starter-data-mybatis` | MyBatis Plus enhancements (pagination, logical delete, enum mapping) |
| `kuma-boot-starter-data-jpa` | Spring Data JPA + Hibernate + Blaze-Persistence |
| `kuma-boot-starter-data-mongodb` | MongoDB auto-configuration |
| `kuma-boot-starter-data-elasticsearch` | Elasticsearch auto-configuration (Easy-ES) |
| `kuma-boot-starter-data-shardingsphere` | ShardingSphere JDBC sharding |
| `kuma-boot-starter-data-p6spy` | SQL execution profiling (p6spy) |
| `kuma-boot-starter-data-migration` | Database migration support |
| `kuma-boot-starter-data-vector` | Vector database integration |

### Cache

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-cache-common` | Cache abstraction layer |
| `kuma-boot-starter-cache-redis` | Redis cache (Redisson) |
| `kuma-boot-starter-cache-caffeine` | Caffeine local cache |
| `kuma-boot-starter-cache-jetcache` | JetCache two-tier cache (local + remote) |

### Security

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-security-common` | Security common components: permission model, token abstraction |
| `kuma-boot-starter-security-spring` | Deep Spring Security integration |
| `kuma-boot-starter-security-jstauth` | Sa-Token integration (stateless JWT authentication) |
| `kuma-boot-starter-security-satoken` | Sa-Token security auto-configuration |
| `kuma-boot-starter-totp` | TOTP two-factor authentication (Google Authenticator compatible) |
| `kuma-boot-starter-captcha` | CAPTCHA (image, slider, click-based) |
| `kuma-boot-starter-session` | Distributed session support |
| `kuma-boot-starter-sign` | Request signing and signature verification |

### Messaging

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-mq-common` | Messaging common abstraction |
| `kuma-boot-starter-mq-kafka` | Kafka producer/consumer wrappers |
| `kuma-boot-starter-mq-rabbitmq` | RabbitMQ integration |
| `kuma-boot-starter-mq-rocketmq` | RocketMQ integration |
| `kuma-boot-starter-mqtt` | MQTT v5 integration (Eclipse Paho) |
| `kuma-boot-starter-eventbus` | In-process event bus (Guava EventBus / Disruptor) |

### Distributed Capabilities

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-lock` | Distributed lock (Redisson) |
| `kuma-boot-starter-idempotent` | API idempotency (Token + Lua script) |
| `kuma-boot-starter-ratelimit` | API rate limiting (Sentinel / Resilience4j) |
| `kuma-boot-starter-retry` | Retry support (Spring Retry + Guava Retrying) |
| `kuma-boot-starter-seata` | Distributed transactions (Seata AT/TCC) |
| `kuma-boot-starter-sentinel` | Traffic control (Sentinel standalone mode) |
| `kuma-boot-starter-idgenerator` | Distributed ID (Snowflake / UUID / Leaf) |

### Observability

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-actuator` | Actuator endpoint enhancements |
| `kuma-boot-starter-metrics` | Micrometer metrics collection |
| `kuma-boot-starter-prometheus` | Prometheus metrics exposure |
| `kuma-boot-starter-monitor` | System monitoring (CPU, memory, JVM, threads — based on Oshi) |
| `kuma-boot-starter-tracer` | Distributed tracing (Micrometer Tracing + OpenTelemetry) |
| `kuma-boot-starter-skywalking` | SkyWalking agent toolkit integration |
| `kuma-boot-starter-elk` | ELK logging (Logstash + GELF) |
| `kuma-boot-starter-logger` | Logging enhancements (structured logs / Loki / Kafka appender) |
| `kuma-boot-starter-audit-log` | Application audit logging |
| `kuma-boot-starter-otel` | OpenTelemetry integration |

### Remote Invocation

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-grpc` | gRPC Server/Client auto-configuration |
| `kuma-boot-starter-client` | HTTP clients (Forest / Retrofit) |

### Diagnostics & Performance

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-arthas` | Arthas diagnostics integration |
| `kuma-boot-starter-jprofiler` | JProfiler integration helpers |
| `kuma-boot-starter-jmeter` | Embedded Apache JMeter performance testing |
| `kuma-boot-starter-jacoco` | JaCoCo coverage tooling |

### Scheduling & Storage

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-job-common` | Job scheduling common interfaces |
| `kuma-boot-starter-job-quartz` | Quartz scheduling integration |
| `kuma-boot-starter-job-powerjob` | PowerJob worker integration |
| `kuma-boot-starter-job-xxl` | XXL-Job executor auto-configuration |
| `kuma-boot-starter-oss-common` | Object storage abstraction interface |
| `kuma-boot-starter-oss-minio` | MinIO storage implementation |
| `kuma-boot-starter-oss-aliyun` | Alibaba Cloud OSS implementation |
| `kuma-boot-starter-oss-cos` | Tencent Cloud COS implementation |
| `kuma-boot-starter-oss-qiniu` | Qiniu object storage implementation |
| `kuma-boot-starter-file-transfer` | File-transfer capabilities |

### API Documentation

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-springdoc` | SpringDoc OpenAPI base configuration |
| `kuma-boot-starter-openapi` | OpenAPI enhancements (Knife4j aggregation) |

### Business Utilities

| Starter | Description |
|---------|-------------|
| `kuma-boot-starter-mail` | Email delivery (Spring Mail + Commons Email) |
| `kuma-boot-starter-sms-common` | SMS delivery abstraction |
| `kuma-boot-starter-sms-aliyun` | Alibaba Cloud SMS implementation |
| `kuma-boot-starter-sms-tencent` | Tencent Cloud SMS implementation |
| `kuma-boot-starter-ai` | Spring AI integration |
| `kuma-boot-starter-office` | Office document processing (POI / EasyExcel / Aspose) |
| `kuma-boot-starter-translation` | Multilingual i18n translation |
| `kuma-boot-starter-threadpool` | Dynamic thread pools (DynamicTp + TTL) |
| `kuma-boot-starter-statemachine` | State machine (Spring StateMachine) |
| `kuma-boot-starter-flowengine` | Workflow engine integration |
| `kuma-boot-starter-ddd` | DDD tactical components (Aggregate Root, Domain Events) |
| `kuma-boot-starter-ip2region` | IP geolocation lookup (offline database) |
| `kuma-boot-starter-useragent` | User-Agent parsing (Browscap / YAUAA) |
| `kuma-boot-starter-pinyin` | Chinese-to-Pinyin conversion (Pinyin4j) |
| `kuma-boot-starter-dingtalk` | DingTalk bot alert notifications |
| `kuma-boot-starter-apollo` | Apollo configuration center integration |
| `kuma-boot-starter-canal` | Canal database change-log subscription |
| `kuma-boot-starter-frp` | FRP intranet penetration toolkit |
| `kuma-boot-starter-i18n` | Internationalization auto-configuration |
| `kuma-boot-starter-multi-tenant` | Multi-tenant application support |
| `kuma-boot-starter-test` | Testing toolkit (TestContainers / DataFaker) |

### Core Dependency Chain

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

Spring Cloud Starter collection, providing distributed microservice capabilities on top of kuma-boot-framework.

| Starter | Description |
|---------|-------------|
| `kuma-cloud-starter-bootstrap` | Spring Cloud bootstrap: bootstrap context, load balancer, Cloud Commons |
| `kuma-cloud-starter-alibaba` | Nacos all-in-one integration: service discovery + configuration center |
| `kuma-cloud-starter-sentinel` | Spring Cloud Sentinel: traffic control, circuit breaking |
| `kuma-cloud-starter-seata` | Spring Cloud Seata: distributed transactions (AT/TCC) |
| `kuma-cloud-starter-stream` | Spring Cloud Stream + RocketMQ: declarative message-driven model |
| `kuma-cloud-starter-gateway` | Spring Cloud Gateway: route and filter configuration |
| `kuma-cloud-starter-cache` | Distributed cache Cloud extensions |
| `kuma-cloud-starter-jdbcpool` | Multi-datasource JDBC connection pool Cloud extensions |
| `kuma-cloud-starter-netty` | Netty server Cloud extensions |
| `kuma-cloud-starter-kmc` | KMC (Chinese SM-series) cryptography module |
| `kuma-cloud-starter-openfeign` | Declarative HTTP clients with Spring Cloud OpenFeign |
| `kuma-cloud-starter-dubbo` | Apache Dubbo RPC integration |

---

## Getting Started

### Requirements

| Tool | Version |
|------|---------|
| JDK | 25 (required) |
| Gradle | 9.6.1 (downloaded automatically by Wrapper — no manual install needed) |
| MySQL | 8.0+ |
| Redis | 7.x |

### Clone

```bash
git clone https://github.com/kumaoji/Kumaframework.git
cd Kumaframework
```

### Use a Starter (within this project)

Reference it directly in a sub-project's `build.gradle`:

```groovy
dependencies {
    implementation project(':kuma-boot-framework:kuma-boot-starter-web')
    implementation project(':kuma-boot-framework:kuma-boot-starter-data-mybatis')
}
```

### Use a Starter (external project, Maven)

```xml
<dependency>
    <groupId>io.github.kumaoji</groupId>
    <artifactId>kuma-boot-starter-web</artifactId>
    <version>2026.07.01</version>
</dependency>
```

### Build Commands

```bash
# Build everything
./gradlew build

# Skip tests
./gradlew build -x test

# Build a specific module
./gradlew :kuma-boot-framework:kuma-boot-starter-core:build

# Launch the demo application
./gradlew :kuma-project:kuma-project-project4:bootRun

# Run the blog example
./gradlew :kuma-project:kuma-project-blog:bootRun
```

---

## Development Guide

### Adding a New Starter

1. Append the module name to the `frameworkBootSubModules` list in `settings.gradle`
2. Create the directory `kuma-boot-starter-<feature>/` under `kuma-boot-framework/`
3. Create a `build.gradle` and declare dependencies
4. Register the auto-configuration class in `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
5. Insert the module name into the `publishOrder` list in the root `build.gradle` according to its dependency layer

### Compiler Flags (inherited globally)

| Flag | Description |
|------|-------------|
| `--enable-preview` | Enables Java 25 preview features |
| `-parameters` | Preserves method parameter names (required by MyBatis / Spring MVC reflection) |
| `-Xlint:unchecked` | Warns about unchecked operations |
| `-Xlint:deprecation` | Warns about deprecated APIs |

---

## License

This project is open-sourced under the [Apache License 2.0](LICENSE.txt).
