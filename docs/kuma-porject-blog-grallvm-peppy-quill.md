# GraalVM Native Image 可行性分析 — kuma-project-blog

## Context

用户希望评估将 `kuma-project-blog` 编译为 GraalVM Native Image 的可行性。该模块是一个完整的博客后端系统，使用 Spring Boot 4.0.3 + Java 25，依赖多个自研 kuma-boot-framework starters。GraalVM 插件 `0.10.4` 已在 settings.gradle 注册但尚未在任何 project 中 apply。

---

## 一、结论（先说结论）

**技术上可行，但工程量较大，短期内不建议直接用于生产。**

| 维度 | 评估 |
|------|------|
| 技术可行性 | ✅ 可行（Spring Boot 4.x 官方支持 AOT/Native） |
| 框架层就绪度 | ⚠️ 部分就绪（约 30%） |
| 应用层就绪度 | ⚠️ 部分就绪（约 50%） |
| 预估工期 | 40–60 小时（主要在 kuma-boot-framework 层） |
| 推荐优先级 | 中（先解决框架层，再推应用层） |

---

## 二、现状盘点

### 2.1 基础条件（已具备）

- **GraalVM 插件**：`settings.gradle:23` 已注册 `org.graalvm.buildtools.native:0.10.4`
- **Spring Boot 4.0.3**：官方内置 AOT 引擎，自动处理 `@Component`、`@Configuration` 等标准组件
- **Java 25 + `--enable-preview`**：GraalVM 24.x Community Edition 支持 Java 24，EE 版支持更新 JDK
- **部分 RuntimeHintsRegistrar 已实现**：
  - `FrpRuntimeHintsRegistrar`：注册 FRP 二进制资源 ✅
  - `Ip2regionRuntimeHintsRegistrar`：注册 IP 数据库文件 ✅
  - `HttpExchangeRuntimeHintsRegistrar`：代码已写，**但未用 `@ImportRuntimeHints` 注册** ⚠️

### 2.2 关键障碍

#### 障碍 A：MyBatis-Plus 动态代理（严重）

- `@MapperScan` 使用**通配符模式**（`com.kuma.cloud.*.biz.mapper`），AOT 编译期无法解析通配符扫描
- 所有 7 个 Mapper 接口（ArticleMapper、UserMapper 等）通过 `Proxy.newProxyInstance()` 创建动态代理
- `MyBatisPlus 3.5.16` 官方对 Native Image 的支持处于实验阶段，需额外 hints
- 影响：**没有 mapper，所有数据库操作都会在 native image 运行时崩溃**

#### 障碍 B：kuma-boot-starter-security-spring 无 hints（严重）

- 自定义认证过滤器链（Account/Email/Captcha/MFA 等 8+ 种登录方式）全部依赖反射
- JWT decoder、BCrypt 等处理器需要反射注册
- 自定义 `@Authorize` 注解 + `@EnableMethodSecurity` 的 AOP 代理需要声明
- 影响：**认证/授权在 native image 中大概率失效**

#### 障碍 C：ProcessBuilder 动态进程（中等）

- `ServiceServiceImpl.java`：启动 LibreTranslate Python 进程（翻译功能）
- `ToolServiceImpl.java`：执行 `RePKG.exe`（macOS 包解析工具）
- ProcessBuilder 本身在 native image 中可用，但依赖的外部进程与 native image 无关，**功能受限是预期行为**

#### 障碍 D：自定义 AOP 注解无 hints（高）

- `@Caching`、`@LimitRate`、`@LogExecutionElapsed`、`@ValidateParameter`、`@CountTime` 等自研注解通过 AOP 代理实现
- 无反射 hints 时，AOT 可能无法生成正确的代理类

#### 障碍 E：`KmcRuntimeHints` 是空壳（低）

- `kuma-boot-starter-core` 已有 `KmcRuntimeHints.java`，但内容仅打日志，未注册任何 hints
- `spring.factories` 中也未注册该类

#### 障碍 F：Java 25 的 GraalVM 兼容性（中等）

- GraalVM Community 24.x 仅支持到 Java **24**；支持 Java 25 需要 GraalVM **25.x**（2025 年 Q3/Q4 发布）
- 如果必须用 Java 25 preview 特性，需等 GraalVM 25 发布或降级到 Java 21/24

---

## 三、兼容性矩阵

| 组件 | Native Image 兼容性 | 所需工作 |
|------|--------|---------|
| Spring Boot 4.0.3 核心 | ✅ 官方支持 | 无 |
| Spring MVC + Tomcat | ✅ 官方支持 | 无 |
| Spring Security 7 | ✅ 官方支持（标准用法） | 注册自定义 filter 的 hints |
| Spring Data JPA | ✅ 支持 | 注册 Entity 反射 hints |
| MyBatis-Plus 3.5.16 | ⚠️ 实验性 | 大量 hints + 重构 MapperScan |
| Redis (Jedis 7) | ✅ 支持 | 无 |
| MySQL Connector 8 | ✅ 支持 | 无 |
| Hutool 5.8.x | ⚠️ 部分支持 | 避免用到 ClassUtil 等反射工具 |
| Jackson | ✅ 支持 | 注册自定义 TypeHandler hints |
| SpringDoc/Swagger | ⚠️ 有限支持 | Native image 通常不包含 Swagger UI |
| Lombok | ✅ 编译期处理，无影响 | 无 |
| p6spy | ⚠️ 可能问题 | 仅 dev 环境，可排除 |
| LibreTranslate 进程 | ✅ ProcessBuilder 可用 | 翻译功能依赖外部 Python 进程，与 native 无关 |
| RePKG.exe 执行 | ✅ ProcessBuilder 可用 | 仅 Windows，外部工具调用 |
| Spring Cloud Bootstrap | ⚠️ 需验证 | kuma-cloud-starter-bootstrap 未评估 |

---

## 四、实施路线（如决定推进）

### Phase 1：使框架层支持 AOT（约 30h）

1. **修复 `HttpExchangeRuntimeHintsRegistrar`**（1h）
   - 在 `ActuatorHttpExchangeAutoConfiguration` 加 `@ImportRuntimeHints(HttpExchangeRuntimeHintsRegistrar.class)`
   - 文件：`kuma-boot-starter-web/.../ActuatorHttpExchangeAutoConfiguration.java`

2. **充实 `KmcRuntimeHints`**（2h）
   - 注册核心 config properties 类的反射 hints
   - 在 `CoreAutoConfiguration` 加 `@ImportRuntimeHints(KmcRuntimeHints.class)`
   - 文件：`kuma-boot-starter-core/.../aot/KmcRuntimeHints.java`

3. **MyBatis-Plus RuntimeHintsRegistrar**（8h，最复杂）
   - 创建 `MybatisPlusRuntimeHintsRegistrar`，注册 `MapperProxyFactory`、`BaseMapper`、所有 TypeHandler 的反射
   - 将 `@MapperScan` 通配符改为显式包路径
   - 文件：`kuma-boot-starter-data-mybatis/.../MybatisPlusAutoConfiguration.java`

4. **Spring Security RuntimeHintsRegistrar**（10h）
   - 创建 `SecurityRuntimeHintsRegistrar`，注册自定义 filter、JWT decoder、AuthProvider 反射
   - 文件：`kuma-boot-starter-security-spring/.../SecurityAuthenticationAutoConfiguration.java`

5. **Web AOP RuntimeHintsRegistrar**（5h）
   - 为 `@Caching`, `@LimitRate` 等自定义 AOP 注解注册 hints

6. **资源 hints**（2h）
   - 确保 MyBatis XML mapper 文件（`resources/mapper/*.xml`）、`logback-spring.xml`、SQL 初始化文件可被 native image 访问

7. **去除或 profile-guard p6spy**（2h）

### Phase 2：blog 应用层适配（约 15h）

> **仓库现状**：已在 `kuma-project-blog` 接入 `org.graalvm.buildtools.native` + `gradle/graalvm.gradle`；`BlogApplication` 已改为可执行 JAR 启动（不再继承 `SpringBootServletInitializer`）；应用侧 `BlogRuntimeHintsRegistrar` 已注册实体/VO/`@Authorize` 链路与 blog 安全类型。步骤 4 需本机用 GraalVM Agent 跑 jar 生成配置后人工审核；步骤 5 需在 GraalVM JDK 下执行 `nativeCompile` 并做冒烟测试。

1. **在 `build.gradle` 中 apply GraalVM 插件**（1h）
   ```groovy
   plugins {
       id 'org.graalvm.buildtools.native'
   }
   graalvmNative {
       binaries {
           main {
               buildArgs.add('--initialize-at-build-time=org.slf4j')
               buildArgs.add('-H:+ReportExceptionStackTraces')
           }
       }
   }
   ```
   文件：`kuma-project/kuma-project-blog/build.gradle`

2. **Blog 特定 RuntimeHints**（4h）
   - 注册所有 Entity 类（Article、User、Music 等 8 个）的反射 hints
   - 注册自定义 `@Authorize` 注解处理器
   - 注册 blog domain/vo/* 类序列化 hints

3. **处理 `SpringBootServletInitializer` 继承**（1h）
   - Native image 不支持 WAR 部署，`BlogApplication extends SpringBootServletInitializer` 需改为纯 JAR 模式
   - 可用 `@ConditionalOnNotNativeImage` 条件跳过 WAR 初始化器
   - 文件：`kuma-project-blog/.../BlogApplication.java`

4. **GraalVM Agent 采集**（4h）
   ```bash
   # 用 agent 模式运行，收集反射/资源访问记录
   java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
        -jar blog.jar --spring.profiles.active=dev
   # 然后手动审核并精简生成的 config json
   ```

5. **native 编译 + 冒烟测试**（5h）
   ```bash
   ./gradlew :kuma-project:kuma-project-blog:nativeCompile
   ```

### Phase 3：验证（约 5h）

```bash
# 1. 健康检查
curl http://localhost:9000/api/actuator/health

# 2. 登录（JWT 令牌）
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"<redacted>"}'

# 3. 文章列表（MyBatis 查询）
curl http://localhost:9000/api/article/list

# 4. 创建文章（Spring Security 鉴权）
curl -X POST http://localhost:9000/api/article \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"test","content":"test content"}'

# 5. Redis 缓存命中验证（重复调用对比响应时间）
```

---

## 五、风险与限制

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|---------|
| GraalVM 不支持 Java 25 preview | 高 | 严重 | 降级到 Java 21 LTS 或等 GraalVM 25 |
| MyBatis-Plus hints 不完整导致运行时崩溃 | 中 | 严重 | 用 GraalVM Agent 生成配置 + 手动补充 |
| Spring Cloud Bootstrap 兼容性未知 | 中 | 中 | 在 native profile 下禁用 Cloud 模块 |
| Swagger/SpringDoc 在 native image 中功能受限 | 高 | 低 | Native image 用于生产，不暴露 Swagger |
| 编译时间过长（native 编译约 5–15 min） | 确定 | 低 | CI/CD 中单独编译，不影响开发速度 |

---

## 六、关键文件索引

| 文件 | 用途 |
|------|------|
| `kuma-project/kuma-project-blog/build.gradle` | GraalVM 插件 + `graalvm.gradle` + `nativeCompile` |
| `kuma-project-blog/.../aot/BlogRuntimeHintsRegistrar.java` | 博客实体/VO/`@Authorize`/安全类型 AOT hints |
| `kuma-boot-starter-core/.../aot/KmcRuntimeHints.java` | Core 配置属性 hints |
| `kuma-boot-starter-data-mybatis/.../MybatisPlusAutoConfiguration.java` | MyBatis hints + 显式 MapperScan |
| `kuma-boot-starter-security-spring/.../Oauth2ResourceAutoConfiguration.java` | Security spring hints 注册 |
| `kuma-boot-starter-web/.../HttpExchangeRuntimeHintsRegistrar.java` | HTTP Exchange hints |
| `kuma-project-blog/.../BlogApplication.java` | `@ImportRuntimeHints`、纯 JAR 启动类 |
| `gradle/graalvm.gradle` | Native 构建参数（需子模块先声明 graalvm 插件） |
| `settings.gradle`（`pluginManagement.plugins`） | GraalVM 插件版本 |

---

## 七、替代方案

| 方案 | 描述 | 适用场景 |
|------|------|---------|
| **CRaC（Checkpoint/Restore）** | JVM 快照恢复，启动极快，无需 native 编译 | 不想改代码，只要快速启动 |
| **Spring Boot AOT 预处理（仍用 JVM）** | 仅做 AOT 预处理，JVM 模式运行，启动速度提升 ~30% | 最小化改动 |
| **GraalVM JIT 模式** | 用 GraalVM JDK 替换 JVM，但仍运行字节码 | 几乎零改造，部分性能提升 |
| **Native Image（本方案）** | 完整编译为原生可执行文件，启动 <1s，内存减少 60% | 容器化部署、Serverless |

**推荐**：如果目标是容器快速启动和内存降低，选 Native Image；如果只是想优化启动速度且不想大量改框架，先试 CRaC 或 AOT 预处理模式。
