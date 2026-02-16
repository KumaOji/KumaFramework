package cn.kuma.blog.framework.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 配置 dynamic-datasource 多数据源
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DataSourceConfig {

    // ==================== mysql 数据源配置 ===================
    @Value("${spring.datasource.dynamic.datasource.mysql.url:jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&useServerPrepStmts=true&cachePrepStmts=true&allowPublicKeyRetrieval=true}")
    private String mysqlUrl;

    @Value("${spring.datasource.dynamic.datasource.mysql.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.dynamic.datasource.mysql.password:123456}")
    private String mysqlPassword;

    @Value("${spring.datasource.dynamic.datasource.mysql.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriverClassName;

    @Value("${spring.datasource.dynamic.datasource.mysql.hikari.minimum-idle:1}")
    private int mysqlMinimumIdle;

    @Value("${spring.datasource.dynamic.datasource.mysql.hikari.maximum-pool-size:20}")
    private int mysqlMaximumPoolSize;

    @Value("${spring.datasource.dynamic.datasource.mysql.hikari.connection-timeout:30000}")
    private long mysqlConnectionTimeout;

    @Value("${spring.datasource.dynamic.datasource.mysql.hikari.idle-timeout:600000}")
    private long mysqlIdleTimeout;

    @Value("${spring.datasource.dynamic.datasource.mysql.hikari.max-lifetime:1800000}")
    private long mysqlMaxLifetime;

    /**
     * 创建 mysql 数据源
     * 配置参数从 application.yml 读取，如果未配置则使用默认值
     */
    @Bean("mysql")
    @Primary
    public DataSource mysqlDataSource() {
        HikariConfig config = new HikariConfig();
        // 数据库连接配置
        config.setJdbcUrl(mysqlUrl);
        config.setUsername(mysqlUsername);
        config.setPassword(mysqlPassword);
        config.setDriverClassName(mysqlDriverClassName);

        // HikariCP 连接池配置
        config.setMinimumIdle(mysqlMinimumIdle);
        config.setMaximumPoolSize(mysqlMaximumPoolSize);
        config.setConnectionTimeout(mysqlConnectionTimeout);
        config.setIdleTimeout(mysqlIdleTimeout);
        config.setMaxLifetime(mysqlMaxLifetime);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("HikariCP-mysql");
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    // ==================== postgresql 数据源配置 ===================
    @Value("${spring.datasource.dynamic.datasource.postgresql.url:jdbc:postgresql://localhost:5432/postgres}")
    private String postgresqlUrl;

    @Value("${spring.datasource.dynamic.datasource.postgresql.username:postgres}")
    private String postgresqlUsername;

    @Value("${spring.datasource.dynamic.datasource.postgresql.password:postgres}")
    private String postgresqlPassword;

    @Value("${spring.datasource.dynamic.datasource.postgresql.driver-class-name:org.postgresql.Driver}")
    private String postgresqlDriverClassName;

    @Value("${spring.datasource.dynamic.datasource.postgresql.hikari.minimum-idle:1}")
    private int postgresqlMinimumIdle;

    @Value("${spring.datasource.dynamic.datasource.postgresql.hikari.maximum-pool-size:20}")
    private int postgresqlMaximumPoolSize;

    @Value("${spring.datasource.dynamic.datasource.postgresql.hikari.connection-timeout:30000}")
    private long postgresqlConnectionTimeout;

    @Value("${spring.datasource.dynamic.datasource.postgresql.hikari.idle-timeout:600000}")
    private long postgresqlIdleTimeout;

    @Value("${spring.datasource.dynamic.datasource.postgresql.hikari.max-lifetime:1800000}")
    private long postgresqlMaxLifetime;

    /**
     * 创建 postgresql 数据源
     * 配置参数从 application.yml 读取，如果未配置则使用默认值
     */
    @Bean("postgresql")
    public DataSource postgresqlDataSource() {
        HikariConfig config = new HikariConfig();
        // 数据库连接配置
        config.setJdbcUrl(postgresqlUrl);
        config.setUsername(postgresqlUsername);
        config.setPassword(postgresqlPassword);
        config.setDriverClassName(postgresqlDriverClassName);

        // HikariCP 连接池配置
        config.setMinimumIdle(postgresqlMinimumIdle);
        config.setMaximumPoolSize(postgresqlMaximumPoolSize);
        config.setConnectionTimeout(postgresqlConnectionTimeout);
        config.setIdleTimeout(postgresqlIdleTimeout);
        config.setMaxLifetime(postgresqlMaxLifetime);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("HikariCP-postgresql");
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

}
