package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * 文件操作结果。
 */
public record FileOperationResultVO(
        String operation,
        String absolutePath,
        long sizeBytes,
        String contentPreview,
        String note
) {
}
