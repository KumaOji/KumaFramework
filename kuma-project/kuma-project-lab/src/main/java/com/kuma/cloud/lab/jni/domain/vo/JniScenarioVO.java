package com.kuma.cloud.lab.jni.domain.vo;

import java.util.List;

/**
 * JNI 场景测试结果。
 */
public record JniScenarioVO(
        boolean libraryLoaded,
        String libraryPath,
        String osArch,
        List<JniOperationStepVO> steps
) {
}
