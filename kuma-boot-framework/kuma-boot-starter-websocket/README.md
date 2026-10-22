# Kuma WebSocket starter

STOMP 保持显式接入设计：应用通过 `@ImportAutoConfiguration(MessageWebSocketAutoConfiguration.class)` 选择该能力，并配置 `enabled=true` 启用；不通过 `AutoConfiguration.imports` 随依赖自动加载。启用后提供端点注册、SockJS、简单消息代理、心跳调度器及 `WebSocketMessageSender`，无需 Redis。

```yaml
kuma:
  boot:
    websocket:
      stomp:
        enabled: true
        endpoint: /ws/chat
        broker-prefixes: [/topic]
        application-destination-prefixes: [/app]
        allowed-origin-patterns: ["https://example.com"]
        sockjs-enabled: true
        native-endpoint-enabled: false
```

`allowed-origin-patterns` 默认为空（同源）；SockJS 和原生端点默认均开启，至少保留一种。默认订阅前缀为 `/broadcast`、`/personal`，应用前缀为 `/app`，用户前缀为 `/user`。`heartbeat-interval` 为毫秒，默认 10000，设为 0 关闭心跳；调度器由 Spring 创建和关闭。传输限制配置为 `message-size-limit`、`send-buffer-size-limit`、`send-time-limit`，默认分别为 10240 字节、10240 字节、10000 毫秒。

## 业务握手扩展

应用提供 `WebSocketStompEndpointCustomizer` Bean 注入业务拦截器或握手处理器；支持 `Ordered` / `@Order`，在 `.withSockJS()` 前执行，并应用于每个已启用的传输端点。

```java
@Bean
WebSocketStompEndpointCustomizer chatEndpoint(ChatHandshakeInterceptor interceptor) {
    return endpoint -> endpoint.addInterceptors(interceptor);
}
```

默认不强制框架 Principal，应用负责自己的身份及权限策略，因此既能接入公开聊天室，也能使用已有的 Spring Security 身份。若启用 `principal-authentication-enabled=true`，应用必须提供实际校验 Token 的 `BearerTokenResolver` Bean；不会默认使用把 Token 当作 openId 的示例解析器。框架会安装会话握手和入站身份拦截器，缺少身份的消息不会进入业务处理。

## 可选 Redis 用户推送

Redis/Redisson 为编译期可选依赖，不随基础 STOMP 自动接入。需要现有的 `WebSocketClusterProcessor` 时，应用应自行引入 Redis/Redisson、提供 `RedissonClient`，并配置 `cluster-enabled=true`。此组件用于现有的跨实例用户推送 API，不会把简单消息代理的所有 `/topic` 广播自动同步到其他实例。

本次自动配置入口只启用 STOMP；模块中的原生 JSR-356、普通 JSON WebSocket 等实现不随之自动加载。

## Blog 接入示例

Blog 依赖本 starter，在 `WebSocketConfig` 显式引入 MessageWebSocketAutoConfiguration，并声明 Cookie JWT/黑名单握手拦截器及端点扩展，不再自行实现 Broker 配置。通过 `bootstrap.yml` 导入 `blog-websocket.properties` 加载开关和聊天室默认值，保留 `/ws/chat` SockJS 端点、`/topic` 订阅和 `/app` 消息前缀。应用、环境变量或 Nacos 配置可覆盖这些默认值。

应用保留显式导入配置的方式，并使用功能开关控制是否启用。曾依赖旧的内置 Principal/Redis 行为的项目，需明确开启对应功能并提供相应 Bean。框架身份模式不再把客户端 `openid` 查询参数当作可信身份，也不在日志输出 Token。

## 验证

```powershell
.\gradlew.bat :kuma-boot-framework:kuma-boot-starter-websocket:test :kuma-project:kuma-project-blog:test
```
