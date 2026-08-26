package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * Mark Word 综合演示结果。
 */
public record MarkWordDemoVO(
        String vmDetails,
        List<MarkWordStateVO> states,
        String klassLayout,
        List<String> notes
) {
}
