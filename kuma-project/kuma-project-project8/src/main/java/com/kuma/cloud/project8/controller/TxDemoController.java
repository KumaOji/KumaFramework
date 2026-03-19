package com.kuma.cloud.project8.controller;

import com.kuma.cloud.project8.service.OrderService;
import com.kuma.cloud.project8.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分布式事务演示接口
 *
 * <p>下单流程：
 * <ol>
 *   <li>OrderService.createOrder — isStart=true，开启全局事务，向 TM 发送 "create"</li>
 *   <li>StockService.deductStock — isEnd=true，结束全局事务，向 TM 发送 "add" + isEnd=true</li>
 * </ol>
 * TM 收到 isEnd=true 的 "add" 命令后，判断所有子事务状态，决定全局提交或回滚。
 * </p>
 */
@RestController
@RequestMapping("/tx")
@RequiredArgsConstructor
public class TxDemoController {

    private final OrderService orderService;
    private final StockService stockService;

    /**
     * 正常下单（全局事务提交）
     *
     * <pre>GET /tx/place-order?orderId=O001&productId=P001&quantity=2</pre>
     */
    @GetMapping("/place-order")
    public String placeOrder(
            @RequestParam(defaultValue = "O001") String orderId,
            @RequestParam(defaultValue = "P001") String productId,
            @RequestParam(defaultValue = "1")   int quantity) {
        try {
            orderService.createOrder(orderId, productId, quantity, false);
            stockService.deductStock(productId, quantity, false);
            return "下单成功：orderId=" + orderId + ", productId=" + productId;
        } catch (Exception e) {
            return "下单失败（已回滚）: " + e.getMessage();
        }
    }

    /**
     * 模拟订单创建失败（第一步失败 → 全局回滚）
     *
     * <pre>GET /tx/place-order-fail-order</pre>
     */
    @GetMapping("/place-order-fail-order")
    public String placeOrderFailOnOrder(
            @RequestParam(defaultValue = "O002") String orderId,
            @RequestParam(defaultValue = "P001") String productId,
            @RequestParam(defaultValue = "1")   int quantity) {
        try {
            orderService.createOrder(orderId, productId, quantity, true);   // 抛出异常
            stockService.deductStock(productId, quantity, false);
            return "下单成功";
        } catch (Exception e) {
            return "下单失败（已回滚）: " + e.getMessage();
        }
    }

    /**
     * 模拟库存不足失败（第二步失败 → 全局回滚）
     *
     * <pre>GET /tx/place-order-fail-stock</pre>
     */
    @GetMapping("/place-order-fail-stock")
    public String placeOrderFailOnStock(
            @RequestParam(defaultValue = "O003") String orderId,
            @RequestParam(defaultValue = "P001") String productId,
            @RequestParam(defaultValue = "1")   int quantity) {
        try {
            orderService.createOrder(orderId, productId, quantity, false);
            stockService.deductStock(productId, quantity, true);   // 抛出异常
            return "下单成功";
        } catch (Exception e) {
            return "下单失败（已回滚）: " + e.getMessage();
        }
    }
}
