package com.kuma.cloud.lab.spring.domain.vo;

import java.util.List;

/**
 * 分层架构调用链演示结果。
 */
public record ArchitectureDemoVO(
        String orderId,
        String finalStatus,
        List<String> callChain
) {
}
