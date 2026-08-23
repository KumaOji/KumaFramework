package com.kuma.cloud.lab.starter.domain.vo;

/**
 * Starter 探测步骤。
 */
public record StarterProbeStepVO(
        String action,
        boolean success,
        String detail
) {
}
