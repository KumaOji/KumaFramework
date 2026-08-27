package com.kuma.cloud.project8.service;

import com.kuma.cloud.tx.rm.annotation.DistributedTransactional;
import com.kuma.cloud.tx.rm.transactional.KmcTxParticipant;
import org.springframework.stereotype.Service;

/**
 * 订单服务
 * <p>
 * 作为全局事务的第一步（isStart=true），负责创建订单并开启全局事务组。
 * AOP 拦截后会向 TM 发送 "create" 命令创建事务组。
 * </p>
 */
@Service
public class OrderService {

    /**
     * 创建订单
     *
     * @param orderId   订单ID
     * @param productId 商品ID
     * @param quantity  数量
     * @param failOrder 是否模拟订单失败（用于测试回滚）
     * @return 1=成功，-1=失败
     */
    @DistributedTransactional(isStart = true)
    public Integer createOrder(String orderId, String productId, int quantity, boolean failOrder) {
        System.out.printf("[OrderService] 创建订单: orderId=%s, productId=%s, qty=%d, groupId=%s%n",
                orderId, productId, quantity, KmcTxParticipant.getCurrentGroupId());

        if (failOrder) {
            throw new RuntimeException("模拟订单创建失败，触发全局事务回滚");
        }

        System.out.printf("[OrderService] 订单 %s 创建成功%n", orderId);
        return 1;
    }
}
