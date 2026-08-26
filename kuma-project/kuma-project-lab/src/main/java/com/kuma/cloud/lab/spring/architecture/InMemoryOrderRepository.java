package com.kuma.cloud.lab.spring.architecture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String orderId, String status) {
        storage.put(orderId, status);
    }

    @Override
    public String findStatus(String orderId) {
        return storage.get(orderId);
    }

}
