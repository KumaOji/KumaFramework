package com.kuma.boot.common.chain.core.executor;

import com.kuma.boot.common.chain.core.context.HandlerContext;
import com.kuma.boot.common.chain.core.handler.BaseHandler;
import com.kuma.boot.common.chain.core.registry.ChainRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 责任链执行器，负责执行责任链
 * @param <P> Param类型，表示请求参数
 * @param <R> Response类型，表示响应结果
 */
public class ChainExecutor<P, R> {

    private ChainRegistry<P, R> chainRegistry;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public ChainExecutor() {
    }

    public ChainExecutor(ChainRegistry<P, R> chainRegistry) {
        this.chainRegistry = chainRegistry;
    }

    public ChainExecutor(ChainRegistry<P, R> chainRegistry, ExecutorService executorService) {
        this.chainRegistry = chainRegistry;
        this.executorService = executorService;
    }

    /** 同步执行责任链，返回响应结果 */
    public R execute(String chainId, HandlerContext<P, R> context) throws Exception {
        List<BaseHandler<P, R>> handlers = chainRegistry.buildChain(chainId);

        for (BaseHandler<P, R> handler : handlers) {
            try {
                if (handler.shouldSkip(context)) {
                    continue;
                }
                boolean shouldContinue = handler.doHandle(context);
                handler.onCompleted(context);
                if (!shouldContinue) {
                    break;
                }
            } catch (Exception e) {
                handler.onError(context, e);
                throw e;
            }
        }

        return context.getResponse();
    }

    /** 异步执行责任链 */
    public CompletableFuture<R> executeAsync(String chainId, HandlerContext<P, R> context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(chainId, context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public ChainRegistry<P, R> getChainRegistry() { return chainRegistry; }
    public void setChainRegistry(ChainRegistry<P, R> chainRegistry) { this.chainRegistry = chainRegistry; }

    public ExecutorService getExecutorService() { return executorService; }
    public void setExecutorService(ExecutorService executorService) { this.executorService = executorService; }
}
