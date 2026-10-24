# Kuma OpenTelemetry starter

Spring Boot 4 的 Micrometer Tracing、OpenTelemetry SDK 与 OTLP 导出统一入口。
框架内部依赖 `spring-boot-starter-opentelemetry`，业务模块使用：

```gradle
implementation project(':kuma-boot-framework:kuma-boot-starter-otel')
```

独立发布使用时，依赖坐标为 `io.github.kumaoji:kuma-boot-starter-otel`，
与框架版本保持一致。SDK 和桥接版本由 Spring Boot BOM 对齐。

## Kuma 配置

```yaml
kuma:
  boot:
    otel:
      enabled: true
      endpoint: http://localhost:4318/v1/traces
      timeout: 10s
      compression: gzip
      sampling:
        probability: 0.1
      # headers:
      #   X-Scope-OrgID: tenant
```

`enabled` 控制 Kuma 属性映射，默认为 `false`。开启后，启动阶段将采样配置映射到
`management.tracing.*`，将 endpoint、timeout、compression、headers 映射到 Boot 4 的
`management.opentelemetry.tracing.export.otlp.*`。环境处理器在 Config Data 加载后执行，
自动配置通过 `AutoConfiguration.imports` 注册，无需额外扫描或手动导入。

## 原生配置与优先级

仍可直接使用原生配置；显式的 `management.*` 值优先于 Kuma 映射值：

```yaml
management:
  tracing:
    enabled: true
    sampling:
      probability: 0.1
  opentelemetry:
    tracing:
      export:
        otlp:
          endpoint: http://localhost:4318/v1/traces
  otlp:
    metrics:
      export:
        enabled: false
```

Blog、UAA、Gateway 已有的原生配置可以继续使用，无需同时设置 Kuma 开关。
关闭 Kuma 映射不会覆盖或关闭显式的原生配置。停用原生 Tracing 时使用
`management.tracing.enabled=false`。

官方 starter 也包含 OTLP 指标注册表；若指标由 Prometheus 拉取，可像上例一样关闭
OTLP metrics 导出。现有 Loki 日志、SkyWalking Java Agent 及其启动参数分别管理。

验证命令：

```powershell
.\gradlew.bat :kuma-boot-framework:kuma-boot-starter-otel:test --console=plain
```

测试覆盖属性映射、原生配置优先级、自动发现、实际 HTTP/protobuf Span 导出和 gzip 请求。

参考：[Spring Boot 4 Tracing](https://docs.spring.io/spring-boot/4.0/reference/actuator/tracing.html)。
