package com.kuma.cloud.lab.spring.domain.vo;

import java.util.List;

/**
 * Spring 综合场景测试结果。
 */
public record SpringScenarioVO(
        List<SpringOperationStepVO> steps
) {
}
