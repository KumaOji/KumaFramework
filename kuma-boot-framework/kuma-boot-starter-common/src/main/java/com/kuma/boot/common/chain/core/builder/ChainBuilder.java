package com.kuma.boot.common.chain.core.builder;

import com.kuma.boot.common.chain.core.executor.ChainExecutor;
import com.kuma.boot.common.chain.core.handler.BaseHandler;
import com.kuma.boot.common.chain.core.handler.Ordered;
import com.kuma.boot.common.chain.core.registry.ChainRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 责任链流式构建器，无 Spring 依赖的手动注册入口。
 *
 * <pre>{@code
 * ChainExecutor<OrderReq, OrderResp> executor =
 *     ChainBuilder.<OrderReq, OrderResp>forChain("orderChain")
 *         .add(new ValidateHandler())   // 实现 Ordered 自动排序
 *         .add(new StockHandler(), 20)  // 显式指定顺序
 *         .add(new PayHandler(), 30)
 *         .build();
 *
 * OrderResp result = executor.execute("orderChain", new HandlerContext<>(req, null));
 * }</pre>
 */
public class ChainBuilder<P, R> {

    private final String chainId;
    private final List<HandlerEntry<P, R>> entries = new ArrayList<>();
    private ExecutorService executorService;

    private ChainBuilder(String chainId) {
        this.chainId = chainId;
    }

    public static <P, R> ChainBuilder<P, R> forChain(String chainId) {
        return new ChainBuilder<>(chainId);
    }

    /** 添加处理者，顺序由处理者实现 {@link Ordered} 决定，未实现则默认为 0 */
    public ChainBuilder<P, R> add(BaseHandler<P, R> handler) {
        int order = handler instanceof Ordered ? ((Ordered) handler).getOrder() : 0;
        entries.add(new HandlerEntry<>(handler, order));
        return this;
    }

    /** 添加处理者并显式指定顺序，数值越小优先级越高 */
    public ChainBuilder<P, R> add(BaseHandler<P, R> handler, int order) {
        entries.add(new HandlerEntry<>(handler, order));
        return this;
    }

    /** 指定异步执行使用的线程池，不设置则默认使用 CPU 核心数 × 2 的固定线程池 */
    public ChainBuilder<P, R> executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ChainExecutor<P, R> build() {
        ChainRegistry<P, R> registry = new ChainRegistry<>();
        entries.stream()
                .sorted(Comparator.comparingInt(e -> e.order))
                .forEach(e -> registry.registerHandler(chainId, e.handler));
        ExecutorService es = executorService != null
                ? executorService
                : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        return new ChainExecutor<>(registry, es);
    }

    private static final class HandlerEntry<P, R> {
        final BaseHandler<P, R> handler;
        final int order;

        HandlerEntry(BaseHandler<P, R> handler, int order) {
            this.handler = handler;
            this.order = order;
        }
    }
}
