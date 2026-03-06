package com.kuma.boot.sentinel.resilience4j;

import com.kuma.boot.common.exception.BusinessException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class RetryExample {

    static void main() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3) // 最大尝试3次
                .waitDuration(Duration.ofMillis(500)) // 重试间隔
                .retryExceptions(TimeoutException.class) // 只重试超时异常
                .ignoreExceptions(BusinessException.class) // 忽略业务异常
                .build();

        Retry retry = Retry.of("emailService", retryConfig);

// 装饰发送邮件逻辑
//		CheckedFunction0<String> retryableSend = Retry
//			.decorateCheckedSupplier(retry, () -> emailService.send(userEmail));
//
//// 执行并记录结果
//		Try<String> result = Try.of(retryableSend)
//			.onSuccess(res -> log.info("邮件发送成功"))
//			.onFailure(ex -> log.error("最终发送失败", ex));

    }
}
