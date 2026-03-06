package com.kuma.boot.sentinel.resilience4j;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.function.Supplier;

public class CircuitBreakerExample {

    static void main() {
        // 1. 配置熔断器
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 失败率阈值50%
                .waitDurationInOpenState(Duration.ofMillis(1000)) // 熔断后1秒进入半开
                //.ringBufferSizeInHalfOpenState(2) // 半开状态允许2个请求
                //.ringBufferSizeInClosedState(4) // 关闭状态环形缓冲区大小
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of("inventoryService", config);

// 2. 用熔断器保护服务调用
//		Supplier<Integer> decoratedSupplier = CircuitBreaker
//			.decorateSupplier(circuitBreaker, () -> inventoryService.getStock(itemId));
//
//// 3. 调用并处理可能异常
//		try {
//			Integer stock = decoratedSupplier.get();
//		} catch (CallNotPermittedException e) {
//			// 熔断打开时的快速失败
//			log.error("服务熔断中！请稍后重试");
//		} catch (Exception e) {
//			// 业务异常处理
//		}

    }
}
