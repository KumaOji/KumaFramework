# PostgreSQL 数据源切换示例

本项目使用 `dynamic-datasource` 实现多数据源切换，支持在运行时动态切换 MySQL 和 PostgreSQL。

## 配置说明

### 1. 数据源配置（application-dev.yml）

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        mysql:
          url: jdbc:mysql://localhost:3306/blog
          username: root
          password: 123456
          driver-class-name: com.mysql.cj.jdbc.Driver
        postgresql:
          url: jdbc:postgresql://localhost:5432/postgres
          username: postgres
          password: postgres
          driver-class-name: org.postgresql.Driver
```

### 2. 数据源 Bean 配置

在 `DataSourceConfig` 中已经配置了两个数据源：
- `mysql`: 默认数据源（@Primary）
- `postgresql`: PostgreSQL 数据源

## 使用方法

### 方法1：在方法上使用 @DS 注解

```java
@Service
public class YourService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 切换到 PostgreSQL
    @DS("postgresql")
    public List<Map<String, Object>> queryFromPostgreSQL() {
        String sql = "SELECT current_database() as database";
        return jdbcTemplate.queryForList(sql);
    }
    
    // 使用默认数据源（MySQL）
    public List<Map<String, Object>> queryFromMySQL() {
        String sql = "SELECT DATABASE() as database";
        return jdbcTemplate.queryForList(sql);
    }
}
```

### 方法2：在类上使用 @DS 注解

```java
@Service
@DS("postgresql")  // 整个类都使用 PostgreSQL
public class PostgreSQLService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<Map<String, Object>> getAllTables() {
        String sql = "SELECT table_name FROM information_schema.tables";
        return jdbcTemplate.queryForList(sql);
    }
}
```

### 方法3：在 MyBatis Mapper 上使用 @DS 注解

```java
@Mapper
@DS("postgresql")
public interface PostgreSQLMapper {
    
    @Select("SELECT * FROM your_table")
    List<YourEntity> selectAll();
    
    // 也可以单独在方法上指定
    @DS("mysql")
    @Select("SELECT * FROM mysql_table")
    List<YourEntity> selectFromMySQL();
}
```

### 方法4：混合使用

```java
@Service
@DS("postgresql")  // 类级别默认使用 PostgreSQL
public class MixedService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 使用类级别的 PostgreSQL
    public List<Map<String, Object>> queryFromPostgreSQL() {
        return jdbcTemplate.queryForList("SELECT current_database()");
    }
    
    // 覆盖类级别配置，切换到 MySQL
    @DS("mysql")
    public List<Map<String, Object>> queryFromMySQL() {
        return jdbcTemplate.queryForList("SELECT DATABASE()");
    }
}
```

## 优先级说明

1. **方法级别** > **类级别** > **默认数据源**
2. 如果方法上有 `@DS` 注解，使用方法上的配置
3. 如果方法上没有，但类上有 `@DS` 注解，使用类上的配置
4. 如果都没有，使用默认数据源（MySQL，因为配置了 @Primary）

## 测试接口

启动应用后，可以访问以下接口测试：

- `GET /example/postgresql/version` - 查询 PostgreSQL 版本
- `GET /example/postgresql/tables` - 查询 PostgreSQL 表列表
- `GET /example/postgresql/users` - 查询 PostgreSQL 用户
- `GET /example/postgresql/mixed` - 混合数据源查询示例
- `GET /example/postgresql/default` - 默认数据源查询

## 注意事项

1. **事务管理**：`@DS` 注解需要在事务外部使用，或者在同一个事务中使用相同的数据源
2. **连接池配置**：确保 PostgreSQL 连接池配置正确
3. **数据库驱动**：确保项目中已引入 PostgreSQL 驱动依赖
4. **SQL 语法差异**：MySQL 和 PostgreSQL 的 SQL 语法有差异，需要注意兼容性

## 常见问题

### Q: 如何查看当前使用的数据源？
A: 可以在日志中查看，或者执行 `SELECT current_database()`（PostgreSQL）或 `SELECT DATABASE()`（MySQL）

### Q: 事务中能切换数据源吗？
A: 不建议在事务中切换数据源，可能会导致事务管理问题。建议在事务外部切换，或者在同一个事务中使用相同的数据源。

### Q: 如何配置多个 PostgreSQL 数据源？
A: 在 `DataSourceConfig` 中添加多个 PostgreSQL Bean，使用不同的名称，然后在 `@DS` 注解中指定对应的名称。
