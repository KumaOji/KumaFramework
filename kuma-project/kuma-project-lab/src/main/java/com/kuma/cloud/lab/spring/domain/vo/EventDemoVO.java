package com.kuma.cloud.lab.spring.domain.vo;

import java.util.List;

/**
 * 事件发布与监听结果。
 */
public record EventDemoVO(
        String publishedEvent,
        List<String> handledEvents
) {
}
