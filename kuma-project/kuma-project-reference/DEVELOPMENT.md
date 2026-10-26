# 业务项目开发规范

本规范用于从 reference 复制的新业务项目，统一日常开发写法。框架能力、配置及订单示例见 [README](README.md)。

## 命名与分层

类名使用 UpperCamelCase，方法和字段使用 lowerCamelCase，常量使用 UPPER_SNAKE_CASE。包名使用小写，按下表放置业务代码。

| 位置 | 命名示例 | 职责 |
|---|---|---|
| `controller` | `OrderController` | HTTP 映射、入参校验、调用 Service、设置响应状态与头 |
| `service` | `OrderService` | 定义业务用例接口，参数和返回类型表达业务含义 |
| `service.impl` | `OrderServiceImpl` | 编排业务规则、事务、Mapper 调用及 VO 转换 |
| `mapper` | `OrderMapper` | 数据库读写，复用 `BaseMapperX` |
| `domain.dto` | `CreateOrderDTO` | 创建、修改等写入参数，按操作分别定义 |
| `domain.qo` | `OrderPageQO` | 查询条件，分页继承 `PageQuery` |
| `domain.vo` | `OrderVO` | 对外返回的数据结构 |
| `domain.entity` | `OrderDO` | 数据库字段映射，复用框架实体基类 |
| `domain.model` | `Order`、`ProductSnapshot` | 状态变更、金额计算等业务规则 |
| `domain.enums` | `DataScope` | 有限取值及其明确含义 |
| `config` | `WebConfiguration` | 接入框架配置及声明项目策略 |

调用方向为 `Controller → Service → ServiceImpl → Mapper`。Controller 注入 Service 接口；ServiceImpl 按需调用 model。DO 不直接对外返回，VO 转换集中在 ServiceImpl，model 保持对 HTTP 和数据库的零依赖。

必需依赖使用构造器注入与 `final` 字段；单构造器省略 `@Autowired`，同类型多 Bean 用 `@Qualifier` 区分。沿用 Service 接口与实现分层；单实现的支持类可直接注入具体类。只有存在实际复用需求时才提取公共层或基类。

## 接口与参数

- 资源路径使用 `/api/` 加复数名词，如 `/api/orders`；多词路径使用小写与连字符。
- GET 查询、POST 创建、PUT 整体修改、PATCH 局部修改、DELETE 删除；业务动作使用 `POST /api/orders/{id}/confirm` 等路径。
- 写入参数用 `@RequestBody` DTO，查询条件用 QO，资源标识用路径参数。分页参数统一为 `currentPage`、`pageSize`，排序字段使用白名单。
- DTO/QO 使用 Jakarta Validation 声明必填、长度、格式和数值范围，Controller 使用 `@Valid` 触发校验；路径与独立参数按需声明约束并确保校验生效。业务状态、金额计算等约束由 model 或 Service 保证。
- 金额使用 `BigDecimal` 或框架 `Money`，明确币种、精度与舍入规则；时间统一按 UTC 处理，时间点优先使用 `Instant`，业务当前时间通过注入的 `Clock` 获取。
- 主键沿用框架 Long 类型。接口需明确 Long ID 的 JSON 表达及前端精度约定；当前示例返回数字，新项目如采用字符串应统一配置和文档。

## 响应与异常

Controller 使用 `@RestController`、`@BusinessApi` 和请求映射注解，返回 VO、`PageResultRecord<VO>` 或 `ResponseEntity`，由框架统一包装为 `Result<T>`。Service 返回业务数据，HTTP 状态和响应头由 Controller 处理。

创建成功使用 201，普通成功使用 200；参数错误使用 400、未认证使用 401、无权限使用 403、数据不存在使用 404、状态或版本冲突使用 409、资源繁忙使用 503。异常状态通过框架集中映射。

- 可预期的业务失败抛出框架 `BusinessException`，优先复用 `ResultEnum`，如 `DATA_NOT_EXIST_ERROR`、`REQUEST_OTHER_OPERATION`。
- 新业务码沿用框架 `Code` 和集中 HTTP 状态映射；项目通用错误码和异常处理复用框架已有实现。
- 数据操作结果异常使用 `DataCommonException`；影响行数为 0 时结合业务含义判断，不直接等同于连接故障。
- 保留原始异常原因，交由统一异常处理生成响应。对外消息不得包含 SQL、凭据或堆栈。

## 数据访问与事务

- Mapper 只处理数据库操作。简单查询优先使用 `BaseMapperX`、`LambdaQueryWrapper`；复杂 SQL 按需使用参数绑定的 Mapper SQL/XML。
- 更新、删除必须带明确条件；排序列、数据源和 schema 使用服务端白名单，SQL 值通过参数绑定传入。
- 事务边界放在 ServiceImpl，覆盖完整业务写入；多数据源场景先路由再开启事务，事务内禁止切换数据源。
- 复用框架 `TransactionalUtils` 并设置合适的隔离级别、只读属性与超时。外部 HTTP、消息发送等操作需明确失败处理及一致性方案，避免长时间占用数据库事务。
- 有并发修改风险的实体使用 `@Version`，更新失败返回明确冲突。分页复用 `MpUtils`、`PageUtils` 和 `PageResultRecord`。
- 审计字段沿用框架约定；接入认证后统一关联当前用户，客户端传入的用户 ID 不作为可信操作人。

## 框架、配置与异步

- 启动入口复用 `@KumaBootApplication` 与 `StartupSpringApplication`；ServiceImpl 使用 `@Manager`。框架尚无等价能力时使用标准 Spring/Jakarta 注解。
- 按框架各模块的接入方式启用配置；当前 reference 的 Web、数据访问等配置使用显式导入。新增能力先确认依赖、配置和启用方式，避免重复实现已有功能。
- `bootstrap.yml` 保留应用、环境和 Nacos 连接及导入配置，业务运行参数集中管理。配置文件提交占位变量，真实凭据通过环境变量或受控配置中心提供。
- 普通同步查询可直接返回结果。需要异步处理时复用框架执行器，说明异步目的，明确超时、拒绝和异常处理；通过返回的 Future 观察执行结果，注意事务与线程的边界。
- 日志使用 SLF4J 参数化输出，复用框架 MDC 的请求与链路信息；记录关键业务标识及状态变更，敏感信息脱敏，避免多层重复记录同一异常。

## 复制模板时的业务选择

统一修改模块名、包名、启动类、应用名和配置标识，再替换订单示例。根据实际业务选择数据源、认证及缓存等能力，并同步更新项目说明。

reference 的匿名放行、客户端选择 `scope` 和提交商品价格均用于演示。新项目需明确访问策略、数据范围授权、价格来源，以及创建或状态变更接口的幂等语义。
