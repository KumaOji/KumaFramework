package com.kuma.boot.data.datasource.dynamic.mybatisplus;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.kuma.boot.common.holder.TenantContextHolder;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 通用数据源执行入口；调用方应在 action 内开启事务。 */
public class DataSourceExecutor {
    private final DataSource dataSource;
    private final DataSourceInvoker invoker;

    public DataSourceExecutor(DataSource dataSource, DataSourceInvoker invoker) {
        this.dataSource = dataSource;
        this.invoker = invoker;
    }

    public <T> T execute(String name, Supplier<T> action) {
        Objects.requireNonNull(name, "dataSource");
        Objects.requireNonNull(action, "action");
        if (!(dataSource instanceof DynamicRoutingDataSource routing)) {
            throw new IllegalStateException("当前数据源未启用动态路由");
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && routing.getDataSource(DynamicDataSourceContextHolder.peek()) != routing.getDataSource(name)) {
            throw new IllegalStateException("不能在已开启的事务内切换数据源，请在事务之前选择数据源");
        }
        return invoker.invoke(action, name);
    }

    /** 数据源与 SCHEMA 租户上下文在同一执行边界内设置，并在异常后恢复。 */
    public <T> T execute(String name, String tenant, Supplier<T> action) {
        Objects.requireNonNull(tenant, "tenant");
        String previous = TenantContextHolder.getTenant();
        TenantContextHolder.setTenant(tenant);
        try {
            return execute(name, action);
        } finally {
            if (previous == null) TenantContextHolder.clear();
            else TenantContextHolder.setTenant(previous);
        }
    }
}
