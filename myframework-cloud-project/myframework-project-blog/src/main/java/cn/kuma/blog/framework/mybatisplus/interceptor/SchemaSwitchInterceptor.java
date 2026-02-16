package cn.kuma.blog.framework.mybatisplus.interceptor;

import cn.kuma.blog.framework.mybatisplus.util.SchemaContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Schema 切换拦截器
 * 在执行 SQL 前自动切换 schema（数据库），支持动态切换
 * 适用于 MySQL 等支持 USE DATABASE 的数据库
 * 使用方式：请求头 X-Schema: blog_source，或代码内 {@link SchemaContext#setSchema(String)} / {@link SchemaContext#withSchema(String, Runnable)}
 *
 * @author Kuma
 * @version 1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SchemaSwitchInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SchemaSwitchInterceptor.class);

    /**
     * 是否启用 schema 切换（可通过配置关闭）
     */
    private boolean enabled = true;

    /**
     * 默认 schema，请求结束后恢复为该库（与数据源 URL 中库名一致，如 blog）
     */
    private String defaultSchema;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enabled) {
            return invocation.proceed();
        }

        Executor executor = (Executor) invocation.getTarget();
        Connection connection = null;
        try {
            connection = executor.getTransaction().getConnection();
        } catch (SQLException e) {
            logger.warn("获取数据库连接失败，跳过 schema 切换: {}", e.getMessage());
            return invocation.proceed();
        }
        if (connection == null || connection.isClosed()) {
            return invocation.proceed();
        }

        String targetSchema = SchemaContext.hasSchema() ? SchemaContext.getSchema() : null;
        if (targetSchema != null) {
            targetSchema = targetSchema.trim();
        }
        if (targetSchema == null || targetSchema.isEmpty()) {
            targetSchema = defaultSchema;
        }
        boolean switched = false;
        if (targetSchema != null && !targetSchema.isEmpty()) {
            String current = getCurrentSchema(connection);
            if (!targetSchema.equals(current)) {
                try {
                    switchSchema(connection, targetSchema);
                    switched = true;
                    logger.debug("动态切换 schema: {} -> {}", current, targetSchema);
                } catch (SQLException e) {
                    logger.error("切换 schema 失败: {}，错误: {}", targetSchema, e.getMessage(), e);
                }
            }
        }

        try {
            return invocation.proceed();
        } finally {
            if (switched && defaultSchema != null && !defaultSchema.isEmpty()) {
                try {
                    switchSchema(connection, defaultSchema);
                    logger.debug("恢复默认 schema: {}", defaultSchema);
                } catch (SQLException e) {
                    logger.warn("恢复默认 schema 失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 切换 schema
     */
    private void switchSchema(Connection connection, String schema) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // MySQL 使用 USE 语句切换数据库
            String sql = "USE `" + schema.replace("`", "``") + "`";
            statement.execute(sql);
        }
    }

    /**
     * 获取当前连接的 schema
     */
    private String getCurrentSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             var rs = statement.executeQuery("SELECT DATABASE()")) {
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String enabledStr = properties.getProperty("enabled", "true");
        this.enabled = Boolean.parseBoolean(enabledStr);
        String defaultSchema = properties.getProperty("defaultSchema");
        if (defaultSchema != null && !defaultSchema.trim().isEmpty()) {
            this.defaultSchema = defaultSchema.trim();
        }
    }

    /**
     * 设置默认 schema（恢复用）。若通过 setProperties 配置则无需调用。
     */
    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema == null ? null : defaultSchema.trim();
    }
}
