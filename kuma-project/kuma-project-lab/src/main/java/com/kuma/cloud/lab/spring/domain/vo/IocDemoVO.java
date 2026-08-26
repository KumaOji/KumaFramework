package com.kuma.cloud.lab.spring.domain.vo;

/**
 * IOC 注入演示结果。
 */
public record IocDemoVO(
        String primaryRendererType,
        String plainRendererType,
        String primaryOutput,
        String plainOutput,
        int prototypeInstanceA,
        int prototypeInstanceB,
        boolean prototypeInstancesDifferent
) {
}
