package com.kuma.boot.sentinel.spi;

//它的作用是在调用资源之前，对资源进行保护增强，有点类似于 Spring AOP 的概念

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

//它是由 DefaultSlotChainBuilder 进行触发加载的。在我们进行资源规则保护的时候是调用 SphU.entry("规则名称") 与 entry.exit()
// 这两个方法来包裹资源就可以了。SphU.entry("规则名称") 这个其实是 Sentinel 简化了我们的使用以及对概念的理解。
// 而 ProcessorSlot 这个接口里面定义的方法才是真正对资源保护的处理。这个章节我们只需要分析 SPI 接口的加载，
// 对于 Sentinel 对于保护资源的拦截的处理
public class KmcProcessorSlot implements ProcessorSlot {
    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, Object o, int i, boolean b, Object... objects) throws Throwable {

    }

    @Override
    public void fireEntry(Context context, ResourceWrapper resourceWrapper, Object o, int i, boolean b, Object... objects) throws Throwable {

    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int i, Object... objects) {

    }

    @Override
    public void fireExit(Context context, ResourceWrapper resourceWrapper, int i, Object... objects) {

    }
}
