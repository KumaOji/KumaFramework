package com.kuma.cloud.lab.starter.domain.vo;

import java.util.List;

/**
 * Starter 场景测试结果。
 */
public record StarterScenarioVO(
        int total,
        int passed,
        int failed,
        int skipped,
        List<StarterProbeResultVO> results
) {
}
