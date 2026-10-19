package com.kuma.cloud.reference.support.datasource;

import com.baomidou.dynamic.datasource.annotation.DS;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/** 独立代理边界；#last 由 Kuma 的 DsLastParamProcessor 解析。 */
@Component
public class ScopedDataSourceInvoker {
    @DS("#last")
    public <T> T invoke(Supplier<T> action, String dataSource) { return action.get(); }
}
