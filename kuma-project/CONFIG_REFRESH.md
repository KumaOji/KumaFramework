# Blog、UAA、Gateway 配置热更新

三个服务的 `bootstrap.yml` 均通过 `spring.config.import` 导入 Nacos 配置，并设置
`refreshEnabled=true`。构建并部署本次代码后，在对应 namespace、group、dataId 发布配置，
应用通过 Spring Cloud 刷新事件应用新值，无需手动调用 Actuator refresh。
直接编辑仓库中的 YAML 文件不会向运行中的应用推送配置；命令行参数等优先级更高的配置仍会覆盖 Nacos。

| 服务 | 本次覆盖的运行时配置 |
| --- | --- |
| Blog | `blog.oauth2.*`、`blog.cookie-secure`、`blog.token-expire-seconds`、`blog.music.base-path`；JWT JWK 地址、issuer、audience；OAuth2 客户端注册信息和 provider 地址；MVC 异步请求超时 |
| UAA | `kuma.uaa` 登录限制、验证码、MFA；issuer 与授权服务器安全链；签名密钥来源；新签发令牌使用的有效期和 Refresh Token 轮换策略；配置中声明的客户端 |
| Gateway | `kuma.cloud.gateway` 总开关、鉴权开关、免鉴权路径、CORS、IP 黑白名单、日志开关与参数、身份透传参数、限流键参数；JWT JWK 地址、issuer、audience、签名算法 |

配置对象和需要重新构建的对象使用 refresh scope。安全链通过接口代理重新创建，避免仅更新属性、
但实际过滤器仍保留旧规则。Gateway 的 CORS 在请求时读取配置，并在安全过滤器之前处理预检；
IP 规则在请求内构建，不共享可变匹配器列表。日志和 IP 过滤器始终注册，在请求时判断开关，
因此启动时关闭的功能也能在发布配置后开启。

UAA 修改 `kuma.uaa.clients` 时，会同步配置中仍存在的客户端；单纯修改其他配置不会覆盖后台维护的客户端。
已存在客户端的密钥只有在 Nacos 中配置的密钥本身变化时才更新。从列表移除客户端不会删除数据库记录，
如需撤销客户端请使用管理 API。全局 token 配置在客户端查询时应用，覆盖后台创建的客户端。
管理员初始化配置仍仅用于启动时创建账号，不通过配置刷新重置已有账号密码。

刷新对后续请求和新创建的数据生效：不会修改已经签发 JWT 的 `exp`，也不会重写已有 Cookie、Redis TTL、
已上传文件位置或正在执行的异步请求超时。切换 issuer / 签名密钥需要同步消费方配置；本实现不保留旧签名密钥。

端口、应用名、活动 profile、Nacos 连接位置、数据库连接池及未列出的第三方 starter 初始化配置，
仍按对应组件的生命周期管理，不能据此认定所有 Spring Boot 配置均可热更新。

验证命令（仓库根目录）：

```powershell
.\gradlew.bat :kuma-project:kuma-project-blog:test :kuma-project:kuma-project-uaa:test :kuma-project:kuma-project-gateway:test
```

刷新测试在同一个应用上下文、同一份已注入的引用中变更配置并触发 Spring Cloud 刷新，
验证 Cookie / OAuth2 客户端、授权服务器 issuer / token TTL、网关鉴权 / 白名单 / CORS，以及实际 JWT 验签。
测试不连接或修改实际 Nacos、MySQL、Redis。
