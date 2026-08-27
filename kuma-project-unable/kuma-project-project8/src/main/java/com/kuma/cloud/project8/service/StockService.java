package com.kuma.cloud.project8.service;

import com.kuma.cloud.tx.rm.annotation.DistributedTransactional;
import com.kuma.cloud.tx.rm.transactional.KmcTxParticipant;
import org.springframework.stereotype.Service;

/**
 * 库存服务
 * <p>
 * 作为全局事务的最后一步（isEnd=true），负责扣减库存并结束全局事务组。
 * AOP 拦截后会向 TM 发送 "add" 命令，携带 isEnd=true，TM 收到后决定全局提交或回滚。
 * </p>
 */
@Service
public class StockService {

    /**
     * 扣减库存
     *
     * @param productId  商品ID
     * @param quantity   数量
     * @param failStock  是否模拟库存不足（用于测试回滚）
     * @return 1=成功，-1=失败
     */
    @DistributedTransactional(isEnd = true)
    public Integer deductStock(String productId, int quantity, boolean failStock) {
        System.out.printf("[StockService] 扣减库存: productId=%s, qty=%d, groupId=%s%n",
                productId, quantity, KmcTxParticipant.getCurrentGroupId());

        if (failStock) {
            throw new RuntimeException("模拟库存不足，触发全局事务回滚");
        }

        System.out.printf("[StockService] 商品 %s 库存扣减 %d 成功%n", productId, quantity);
        return 1;
    }
}
