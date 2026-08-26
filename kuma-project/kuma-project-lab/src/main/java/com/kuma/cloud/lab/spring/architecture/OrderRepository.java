package com.kuma.cloud.lab.spring.architecture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据访问层：模拟 Repository，演示分层架构最底层。
 */
public interface OrderRepository {

    void save(String orderId, String status);

    String findStatus(String orderId);

}
