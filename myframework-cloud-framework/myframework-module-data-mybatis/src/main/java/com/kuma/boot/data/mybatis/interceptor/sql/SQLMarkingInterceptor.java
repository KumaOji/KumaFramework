package com.kuma.boot.data.mybatis.interceptor.sql;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * SQLMarkingInterceptor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})
})
public class SQLMarkingInterceptor implements Interceptor {

    /**
     * 默认线程栈数组下标
     */
    private static final int DEFAULT_INDEX = 2;

    /**
     * 是否开启SQL染色标记
     */
    @Value("sql.marking.enable")
    private boolean enabled;

    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        if (!enabled) {
            return invocation.proceed();
        }

        try {
            // 1. 找到 StatementHandler（SQL 执行时，StatementHandler 的实际类型为 RoutingStatementHandler）
            RoutingStatementHandler routingStatementHandler = getRoutingStatementHandler(invocation.getTarget());

            if (routingStatementHandler != null) {
                // 其中 delegate 是实际类型的 StatementHandler （静态代理模式），获取到实际的 StatementHandler
                StatementHandler delegate = getFieldValue(
                        RoutingStatementHandler.class, routingStatementHandler, "delegate", StatementHandler.class
                );
                // 2. 找到 StatementHandler 之后便能拿到 SQL 相关信息，现在对 SQL 信息打标即可
                marking(delegate);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }

        return invocation.proceed();
    }

    private RoutingStatementHandler getRoutingStatementHandler( Object target )
            throws NoSuchFieldException, IllegalAccessException {
        // 如果被代理，那么一直找到具体被代理的对象
        while (Proxy.isProxyClass(target.getClass())) {
            target = Proxy.getInvocationHandler(target);
        }
        while (target instanceof Plugin) {
            Plugin plugin = (Plugin) target;
            target = getFieldValue(Plugin.class, plugin, "target", Object.class);
        }
        // 找到了 RoutingStatementHandler
        if (target instanceof RoutingStatementHandler) {
            return (RoutingStatementHandler) target;
        }

        return null;
    }

    /**
     * 打标 1. 要清楚的知道被执行的 SQL 是定义在 Mapper 中的哪条 2. 要清楚的知道这条 SQL 被执行时方法的调用栈
     */
    private void marking( StatementHandler delegate ) throws NoSuchFieldException, IllegalAccessException {
        BoundSql boundSql = delegate.getBoundSql();
        // 实际的 SQL
        String sql = boundSql.getSql().trim();
        // 只对 select 打标
        if (StringUtils.containsIgnoreCase(sql, "select")) {
            // 获取其基类中的 MappedStatement 即定义的 SQL 声明对象，获取它的 ID 值表示它是哪条 SQL
            MappedStatement mappedStatement = getFieldValue(
                    BaseStatementHandler.class, delegate, "mappedStatement", MappedStatement.class
            );
            String mappedStatementId = mappedStatement.getId();
            // 方法调用栈
            String trace = trace();

            // 按顺序创建打标的内容
            LinkedHashMap<String, Object> markingMap = new LinkedHashMap<>();
            markingMap.put("STATEMENT_ID", mappedStatementId);
            markingMap.put("STACK_TRACE", trace);
            String marking = "[SQLMarking] ".concat(markingMap.toString());

            // 打标
            sql = String.format(" /* %s */ %s", marking, sql);

            // 反射更新
            Field field = getField(BoundSql.class, "sql");
            field.set(boundSql, sql);
        }
    }

    /**
     * 获取某类型 clazz 某对象 object 下某字段 fieldName 的值 fieldClass
     */
    private <T> T getFieldValue( Class<?> clazz, Object object, String fieldName, Class<T> fieldClass )
            throws NoSuchFieldException, IllegalAccessException {
        // 获取到目标类的字段
        Field field = getField(clazz, fieldName);
        return fieldClass.cast(field.get(object));
    }

    private String trace() {
        StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        if (stackTraceArray.length <= DEFAULT_INDEX) {
            return EMPTY;
        }
        LinkedList<String> methodInfoList = new LinkedList<>();
        for (int i = stackTraceArray.length - 1 - DEFAULT_INDEX; i >= DEFAULT_INDEX; i--) {
            StackTraceElement stackTraceElement = stackTraceArray[i];
            String className = stackTraceElement.getClassName();
            if (!className.startsWith("com.your.package") || className.contains("FastClassBySpringCGLIB")
                    || className.contains("EnhancerBySpringCGLIB") || stackTraceElement.getMethodName()
                    .contains("lambda$")
            ) {
                continue;
            }
            // 过滤拦截器相关
            if (className.contains("Interceptor") || className.contains("Aspect")) {
                continue;
            }

            // 只拼接类和方法，不拼接文件名和行号
            String methodInfo = String.format("%s#%s",
                    className.substring(className.lastIndexOf('.') + 1),
                    stackTraceElement.getMethodName()
            );
            methodInfoList.add(methodInfo);
        }

        if (methodInfoList.isEmpty()) {
            return EMPTY;
        }

        // 格式化结果
        StringJoiner stringJoiner = new StringJoiner(" ==> ");
        for (String method : methodInfoList) {
            stringJoiner.add(method);
        }
        return stringJoiner.toString();
    }

    private Field getField( Class<?> clazz, String fieldName ) throws NoSuchFieldException {
        Field field;
        String cacheKey = String.format("%s.%s", clazz.getName(), fieldName);
        if (FIELD_CACHE.containsKey(cacheKey)) {
            field = FIELD_CACHE.get(cacheKey);
        } else {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            FIELD_CACHE.put(cacheKey, field);
        }
        return field;
    }

}
