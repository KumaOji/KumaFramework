# kuma-boot-starter-data-migration

> Kumaframework 数据库版本管理模块 —— 统一封装 **Flyway** 与 **Liquibase**。

数据库 Schema/数据的版本化迁移是工程化项目的标配能力（与代码一同进入 CI/CD、可追溯、可回滚）。
本模块在 Spring Boot 4 原生 Flyway / Liquibase 自动装配之上，提供一个**统一开关**与**引擎选择**，
让业务工程一行依赖即可获得迁移能力，并能用同一套配置在两种引擎之间切换。

## 一、依赖引入

```gradle
dependencies {
    implementation 'io.github.kumaoji:kuma-boot-starter-data-migration'
}
```

模块已传递引入：

| 能力 | 依赖 |
| --- | --- |
| Flyway 引擎 + 自动装配 | `spring-boot-starter-flyway` |
| Flyway 方言扩展 | `flyway-mysql`、`flyway-database-postgresql` |
| Liquibase 引擎 + 自动装配 | `spring-boot-starter-liquibase` |

> 版本随 Spring Boot 4.0.3 BOM 对齐：Flyway `11.14.1`，Liquibase `5.0.1`。
> 其它数据库方言（Oracle、SQL Server 等）按需自行追加对应 `flyway-database-*` 依赖。

## 二、统一配置

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `kuma.boot.data.migration.enabled` | `true` | 总开关，关闭时同时禁用 Flyway 与 Liquibase |
| `kuma.boot.data.migration.type` | `auto` | 引擎选择：`auto` / `flyway` / `liquibase` |
| `kuma.boot.data.migration.banner` | `true` | 是否打印模块启动横幅 |

引擎自身的细粒度参数仍沿用 Spring Boot 原生命名空间（`spring.flyway.*` / `spring.liquibase.*`）。

`type` 的翻译规则（由 `DataMigrationEnvironmentPostProcessor` 在启动早期完成）：

- `auto`：Flyway 与 Liquibase 均交由 Spring Boot 原生条件判断 —— 谁存在迁移脚本/变更日志谁生效；
- `flyway`：自动设置 `spring.liquibase.enabled=false`；
- `liquibase`：自动设置 `spring.flyway.enabled=false`。

> 派生属性以最低优先级注入，用户在 `application.yml` 中显式配置的
> `spring.flyway.enabled` / `spring.liquibase.enabled` 始终优先生效。

## 三、使用方式

### 方式 A：Flyway（推荐用于以 SQL 为主的团队）

```yaml
kuma:
  boot:
    data:
      migration:
        type: flyway
spring:
  flyway:
    baseline-on-migrate: true        # 已有库首次接入时建立基线
    locations: classpath:db/migration
```

迁移脚本放在 `src/main/resources/db/migration/`，命名遵循 `V<版本>__<描述>.sql`：

```
src/main/resources/db/migration/
├── V1__init_schema.sql
└── V2__add_user_index.sql
```

```sql
-- V1__init_schema.sql
CREATE TABLE t_user (
    id      BIGINT       NOT NULL PRIMARY KEY,
    name    VARCHAR(64)  NOT NULL,
    created TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 方式 B：Liquibase（推荐用于多数据库、强回滚需求）

```yaml
kuma:
  boot:
    data:
      migration:
        type: liquibase
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

```yaml
# src/main/resources/db/changelog/db.changelog-master.yaml
databaseChangeLog:
  - changeSet:
      id: 1
      author: kuma
      changes:
        - createTable:
            tableName: t_user
            columns:
              - column: { name: id,   type: BIGINT,      constraints: { primaryKey: true, nullable: false } }
              - column: { name: name, type: VARCHAR(64), constraints: { nullable: false } }
```

### 方式 C：彻底关闭

```yaml
kuma:
  boot:
    data:
      migration:
        enabled: false
```

## 四、注意事项

- 迁移在应用启动、`DataSource` 就绪后执行，请确保已引入并正确配置数据源
  （如 `kuma-boot-starter-data-datasource`）。
- `auto` 模式下若同时存在 Flyway 脚本与 Liquibase changelog，两者都会执行；
  生产环境建议显式指定 `type` 避免歧义。
- 多数据源场景下，Spring Boot 原生迁移默认作用于主数据源，
  如需对指定数据源迁移请参考 Spring Boot 文档配置自定义 `Flyway`/`SpringLiquibase` Bean。
