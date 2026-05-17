package com.kuma.boot.core.chain.core.registry;

import com.kuma.boot.core.chain.core.handler.BaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 责任链注册器，负责注册和管理处理者
 * @param <P> Param类型，表示请求参数
 * @param <R> Response类型，表示响应结果
 */
public class ChainRegistry<P, R> {

    private Map<String, List<BaseHandler<P, R>>> handlerMap = new HashMap<>();

    public void registerHandler(String chainId, BaseHandler<P, R> handler) {
        handlerMap.computeIfAbsent(chainId, k -> new ArrayList<>()).add(handler);
    }

    public List<BaseHandler<P, R>> buildChain(String chainId) {
        return handlerMap.getOrDefault(chainId, new ArrayList<>());
    }

    public Set<String> getAllChainIds() {
        return handlerMap.keySet();
    }

    public void clear() {
        handlerMap.clear();
    }

    public Map<String, List<BaseHandler<P, R>>> getHandlerMap() { return handlerMap; }
    public void setHandlerMap(Map<String, List<BaseHandler<P, R>>> handlerMap) { this.handlerMap = handlerMap; }
}
