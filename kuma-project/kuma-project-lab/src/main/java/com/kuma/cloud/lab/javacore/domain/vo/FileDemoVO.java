package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * 文件处理综合演示结果。
 */
public record FileDemoVO(
        String workspace,
        List<FileOperationResultVO> operations,
        List<FileEntryVO> entries,
        List<String> ioNotes
) {
}
