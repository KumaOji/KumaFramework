package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * 类加载器信息条目。
 */
public record ClassLoaderEntryVO(
        String target,
        String loaderType,
        String parentLoaderType,
        String note
) {
}
