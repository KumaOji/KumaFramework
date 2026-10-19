package com.kuma.boot.data.datasource.dynamic.mybatisplus;

import com.baomidou.dynamic.datasource.annotation.DS;
import java.util.function.Supplier;

/** 独立的 AOP 边界，使用 Kuma 的 #last 参数解析器选择数据源。 */
public class DataSourceInvoker {
    @DS("#last")
    public <T> T invoke(Supplier<T> action, String dataSource) {
        return action.get();
    }
}
