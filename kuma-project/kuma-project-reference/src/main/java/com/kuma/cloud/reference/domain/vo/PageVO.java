package com.kuma.cloud.reference.domain.vo;

import java.util.List;

public record PageVO<T>(List<T> items, long total, int page, int size) {
    public PageVO { items = List.copyOf(items); }
}
