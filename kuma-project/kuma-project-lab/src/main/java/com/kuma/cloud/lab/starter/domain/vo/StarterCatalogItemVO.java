package com.kuma.cloud.lab.starter.domain.vo;

/**
 * Starter 目录项。
 */
public record StarterCatalogItemVO(
        String name,
        String category,
        Boolean onClasspath,
        String anchorClass
) {
}
