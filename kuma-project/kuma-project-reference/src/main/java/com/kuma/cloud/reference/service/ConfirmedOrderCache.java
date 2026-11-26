package com.kuma.cloud.reference.service;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.model.Order;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import java.time.Duration;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ConfirmedOrderCache {
    private static final Logger log = LoggerFactory.getLogger(ConfirmedOrderCache.class);

    private final ObjectProvider<RedisRepository> repositories;
    private final JsonMapper json;
    private final Duration ttl;
    private final String prefix;

    public ConfirmedOrderCache(ObjectProvider<RedisRepository> repositories, JsonMapper json,
            @Value("${reference.redis.order-cache-ttl:5m}") Duration ttl,
            @Value("${reference.redis.key-prefix:kuma-reference}") String prefix) {
        this.repositories = repositories;
        this.json = json;
        this.ttl = ttl;
        this.prefix = prefix;
    }

    public OrderVO get(Long id, DataScope scope, Supplier<OrderVO> loadOrder) {
        var redis = repositories.getIfAvailable();
        if (redis == null) {
            return loadOrder.get();
        }

        String key = prefix + ":orders:v1:" + scope.name() + ":" + id;
        try {
            Object cached = redis.opsForValue().get(key);
            if (cached instanceof String value) {
                OrderVO order = json.readValue(value, OrderVO.class);
                if (order != null && id.equals(order.id()) && isConfirmed(order)) {
                    return order;
                }
            }
        } catch (DataAccessException | SerializationException | JacksonException failure) {
            log.debug("Could not read order cache key {}", key, failure);
        }

        OrderVO order = loadOrder.get();
        if (isConfirmed(order)) {
            try {
                redis.opsForValue().set(key, json.writeValueAsString(order), ttl);
            } catch (DataAccessException | SerializationException | JacksonException failure) {
                log.debug("Could not populate order cache key {}", key, failure);
            }
        }
        return order;
    }

    private static boolean isConfirmed(OrderVO order) {
        return Order.Status.CONFIRMED.name().equals(order.status());
    }
}
