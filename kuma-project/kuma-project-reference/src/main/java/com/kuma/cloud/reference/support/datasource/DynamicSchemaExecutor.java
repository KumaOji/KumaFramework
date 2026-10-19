package com.kuma.cloud.reference.support.datasource;

import com.kuma.cloud.reference.domain.enums.DataScope;
import java.util.Objects;
import java.util.function.Supplier;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Component;

@Component
public class DynamicSchemaExecutor {
    private final ScopedDataSourceInvoker invoker;
    public DynamicSchemaExecutor(ScopedDataSourceInvoker invoker) { this.invoker = invoker; }

    public <T> T execute(DataScope scope, Supplier<T> action) {
        Objects.requireNonNull(scope);
        String current = DynamicDataSourceContextHolder.peek();
        current = current == null ? DataScope.MAIN.dataSource() : current;
        if (TransactionSynchronizationManager.isActualTransactionActive() && !current.equals(scope.dataSource())) {
            throw new IllegalStateException("不能在已开启的事务内切换数据源，请在事务之前选择 scope");
        }
        return invoker.invoke(action, scope.dataSource());
    }
}
