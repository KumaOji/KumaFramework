package cn.kuma.blog.framework.mybatisplus.util;

/**
 * Schema 上下文工具类
 * 用于在同一数据源下动态切换 schema（数据库）
 * 使用 ThreadLocal 存储当前线程的 schema，确保线程安全
 *
 * @author Kuma
 * @version 1.0
 */
@SuppressWarnings("all")
public class SchemaContext {

    /**
     * ThreadLocal 存储当前线程的 schema
     */
    private static final ThreadLocal<String> SCHEMA_CONTEXT = new ThreadLocal<>();

    /**
     * 默认 schema（如果没有设置，使用默认值）
     */
    private static final String DEFAULT_SCHEMA = null;

    /**
     * 设置当前线程的 schema
     *
     * @param schema schema 名称
     */
    public static void setSchema(String schema) {
        if (schema != null && !schema.trim().isEmpty()) {
            SCHEMA_CONTEXT.set(schema.trim());
        } else {
            clearSchema();
        }
    }

    /**
     * 获取当前线程的 schema
     *
     * @return schema 名称，如果未设置则返回 null
     */
    public static String getSchema() {
        return SCHEMA_CONTEXT.get();
    }

    /**
     * 清除当前线程的 schema
     */
    public static void clearSchema() {
        SCHEMA_CONTEXT.remove();
    }

    /**
     * 判断是否设置了 schema
     *
     * @return 如果设置了 schema 返回 true，否则返回 false
     */
    public static boolean hasSchema() {
        return SCHEMA_CONTEXT.get() != null;
    }

    /**
     * 在指定 schema 下执行代码（执行完后自动清除）
     *
     * @param schema schema 名称
     * @param runnable 要执行的代码
     */
    public static void withSchema(String schema, Runnable runnable) {
        String oldSchema = getSchema();
        try {
            setSchema(schema);
            runnable.run();
        } finally {
            if (oldSchema != null) {
                setSchema(oldSchema);
            } else {
                clearSchema();
            }
        }
    }
}
