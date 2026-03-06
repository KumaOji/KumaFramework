package com.kuma.boot.sentinel.spi;

//Sentinel 框架对于资源进行保护的时候其实是通过一个一个类这种责任链的模式对保护的资源进行拦截来达到资源保护的作用。
// 而 SlotChainBuilder 就是构建这个拦截器链的类。

import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.SlotChainBuilder;

//它是由 SlotChainProvider 这个类通过 newSlotChain 来加载拦截器默认构建类 DefaultSlotChainBuilder
// 并调用 SlotChainBuilder#build 完成对拦截器链的构建。
// 在它的内部其实也是通过 SPI 来加载 ProcessorSlot 这个拦截器处理类。
//
public class KmcSlotChainBuilder implements SlotChainBuilder {

    @Override
    public ProcessorSlotChain build() {
        return null;
    }
}
