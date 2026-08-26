package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * Java 基础知识综合场景测试结果。
 */
public record JavaCoreScenarioVO(
        List<JavaCoreOperationStepVO> steps
) {
}
