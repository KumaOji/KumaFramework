package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * 类加载演示结果。
 */
public record ClassLoaderDemoVO(
        List<ClassLoaderEntryVO> hierarchy,
        List<String> loadingPhases
) {
}
