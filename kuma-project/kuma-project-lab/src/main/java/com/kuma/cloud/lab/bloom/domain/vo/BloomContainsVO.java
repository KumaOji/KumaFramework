package com.kuma.cloud.lab.bloom.domain.vo;

/**
 * 布隆过滤器查询结果。
 */
public record BloomContainsVO(
        String value,
        boolean mightContain,
        String interpretation
) {
}
