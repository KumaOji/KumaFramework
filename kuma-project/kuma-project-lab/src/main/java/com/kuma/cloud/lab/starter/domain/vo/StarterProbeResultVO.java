package com.kuma.cloud.lab.starter.domain.vo;

import com.kuma.cloud.lab.starter.support.StarterProbeStatus;

import java.util.List;
import java.util.Map;

/**
 * 单个 Starter 探测结果。
 */
public record StarterProbeResultVO(
        String name,
        String category,
        String description,
        StarterProbeStatus status,
        String message,
        List<StarterProbeStepVO> steps,
        Map<String, Object> details
) {
}
