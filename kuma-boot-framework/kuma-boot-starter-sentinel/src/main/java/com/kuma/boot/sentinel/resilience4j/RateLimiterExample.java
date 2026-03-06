package com.kuma.boot.sentinel.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.vavr.control.Try;

import java.time.Duration;

public class RateLimiterExample {

    static void main() {
        // 允许每秒2个请求
        RateLimiterConfig limiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(2)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(500)) // 等待超时时间
                .build();

        RateLimiter rateLimiter = RateLimiter.of("paymentApi", limiterConfig);

// 装饰REST调用
//		CheckedFunction0<PaymentResponse> restrictedCall = RateLimiter
//			.decorateCheckedSupplier(rateLimiter, () -> paymentClient.pay(order));
//
//// 尝试调用
//		Try<PaymentResponse> result = Try.of(restrictedCall)
//			.recover(ex -> new PaymentResponse("ERROR", "请求过于频繁"));

    }
}
