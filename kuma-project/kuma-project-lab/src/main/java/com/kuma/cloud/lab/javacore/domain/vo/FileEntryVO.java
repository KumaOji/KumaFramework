package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * 工作目录中的文件条目。
 */
public record FileEntryVO(
        String relativePath,
        boolean directory,
        long sizeBytes
) {
}
