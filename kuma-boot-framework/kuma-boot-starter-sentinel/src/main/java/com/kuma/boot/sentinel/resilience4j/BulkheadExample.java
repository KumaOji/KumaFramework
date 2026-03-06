package com.kuma.boot.sentinel.resilience4j;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;

import java.time.Duration;

public class BulkheadExample {

    static void main() {
        // 配置线程池舱壁（还有信号量模式）
        BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                .maxConcurrentCalls(5) // 最大并发数
                .maxWaitDuration(Duration.ofMillis(100)) // 进入队列最大等待时间
                .build();

        Bulkhead bulkhead = Bulkhead.of("imageService", bulkheadConfig);

// 使用CompletableFuture异步保护
//		Supplier<CompletableFuture<Image>> asyncSupplier = () ->
//			imageProcessor.renderHighRes(image);
//
//		Supplier<CompletableFuture<Image>> decorated = Bulkhead
//			.decorateSupplier(bulkhead, asyncSupplier);
//
//// 执行并处理拒绝
//		decorated.get().exceptionally(ex -> {
//			if (ex instanceof BulkheadFullException) {
//				return Image.placeholder(); // 返回降级图片
//			}
//			return null;
//		});

    }
}
