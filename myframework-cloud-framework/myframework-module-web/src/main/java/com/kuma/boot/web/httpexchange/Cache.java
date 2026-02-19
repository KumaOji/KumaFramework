/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopProxyUtils
 */
package com.kuma.boot.web.httpexchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.aop.framework.AopProxyUtils;

final class Cache {
    private static final Map<Class<?>, Object> classToInstance = new ConcurrentHashMap();
    private static final Map<ClientId, Object> clientIdToHttpClient = new ConcurrentHashMap<ClientId, Object>();

    private Cache() {
    }

    public static void addClient(Object client) {
        classToInstance.put(AopProxyUtils.ultimateTargetClass((Object)client), client);
    }

    public static Map<Class<?>, Object> getClients() {
        return Map.copyOf(classToInstance);
    }

    public static <T> T getHttpClient(ClientId clientId, Supplier<T> supplier) {
        return (T)clientIdToHttpClient.computeIfAbsent(clientId, k -> supplier.get());
    }

    public static void clear() {
        classToInstance.clear();
        clientIdToHttpClient.clear();
    }

    record ClientId(HttpExchangeProperties.Channel channel, HttpExchangeProperties.ClientType clientType) {
    }
}

